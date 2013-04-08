//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.saasovation.common.port.adapter.persistence.eventsourcing.leveldb;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.Iq80DBFactory;

import com.saasovation.common.event.sourcing.EventStoreAppendException;
import com.saasovation.common.event.sourcing.EventStoreException;

public class LevelDBJournal {

    protected static final String ES_METADATA_DELIMITER = "#";

    private static final byte[] ES_JOURNAL_SEQUENCE_KEY =
            (JournalKeyProvider.ES_JOURNAL_PREFIX_KEY+"0").getBytes();

    private static LevelDBJournal instance;
    private static Map<String, Object> lock = new HashMap<String, Object>();

    private DB database;
    private String databasePath;
    private AtomicLong journalSequence;

    public static LevelDBJournal initializeInstance(String aDirectoryPath) {
        synchronized (lock) {
            if (instance == null) {
                instance = new LevelDBJournal(aDirectoryPath);
            } else {

                // for test
                if (instance.database() == null) {
                    instance.openDatabase(aDirectoryPath);
                }
            }
        }

        return instance;
    }

    public static LevelDBJournal instance() {
        if (instance == null) {
            throw new IllegalStateException("There is no LevelDBJournalProvider instance.");
        }

        return initializeInstance(instance.databasePath());
    }

    public void close() {
        synchronized (lock) {
            if (instance != null) {
                try {
                    this.saveJournalSequence();

                    this.database().close();

                } catch (Throwable t) {
                    throw new EventStoreException(
                            "Cannot clsoe LevelDB database: "
                                    + this.databasePath()
                                    + " because: "
                                    + t.getMessage(),
                            t);
                } finally {
                    instance = null;
                }
            }
        }
    }

    public String databasePath() {
        return this.databasePath;
    }

    public void logEntries(LoggableJournalEntry[] aJournalEntries) {

        WriteBatch batch = this.database().createWriteBatch();

        try {
            synchronized (this.lockFor(aJournalEntries[0].primaryResourceName())) {
                for (LoggableJournalEntry journalEntry : aJournalEntries) {

                    long journalSequence = this.nextJournalSequence();

                    this.confirmNonExistingReference(journalEntry.referenceKey());

                    String jounralKey =
                            JournalKeyProvider.ES_JOURNAL_PREFIX_KEY
                            + journalSequence;

                    String referenceKey =
                            journalEntry.referenceKey();

                    byte[] journalSequenceBytes = (""+journalSequence).getBytes();

                    String journalValue =
                            this.valueWithMetadata(
                                    journalEntry.value(),
                                    referenceKey);

                    // journal entry points to reference

                    batch.put(
                            jounralKey.getBytes(),
                            journalValue.getBytes());

                    // reference points to journal entry

                    batch.put(
                            referenceKey.getBytes(),
                            journalSequenceBytes);
                }

                this.database().write(batch);
            }

        } catch (Throwable t) {
            throw new EventStoreAppendException(
                    "Could not append to journal because: "
                            + t.getMessage(),
                    t);
        } finally {
            try {
                batch.close();
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    public List<LoggedJournalEntry> loggedJournalEntriesSince(
            long aJournalSequence) {

        List<LoggedJournalEntry> entries = new ArrayList<LoggedJournalEntry>();

        boolean done = false;

        for (long journalSequence = aJournalSequence + 1; !done; ++journalSequence) {
            String journalKey =
                    JournalKeyProvider.ES_JOURNAL_PREFIX_KEY
                    + journalSequence;

            byte[] rawJournalValue =
                    this.database()
                        .get(journalKey.getBytes());

            if (rawJournalValue != null) {

                LoggedJournalEntry loggedJournalEntry =
                    new LoggedJournalEntry(
                            journalSequence,
                            null,
                            new String(rawJournalValue));

                // discard the reference key
                loggedJournalEntry.discardNextMetadataValue();

                entries.add(loggedJournalEntry);
            } else {
                done = true;
            }
        }

        return entries;
    }

    public void purge() {

        DBIterator iterator = this.database().iterator();

        try {
            iterator.seekToFirst();

            while (iterator.hasNext()) {
                Entry<byte[],byte[]> entry = iterator.next();

                this.database().delete(entry.getKey());
            }

        } catch (Throwable t) {
            throw new EventStoreException(
                    "Cannot purge journal LevelDB database: "
                        + this.databasePath()
                        + " because: "
                        + t.getMessage(),
                    t);
        } finally {
            try {
                iterator.close();
            } catch (Throwable t) {
                // ignore
            }

            this.setJournalSequence(0L);
        }
    }

    public List<LoggedJournalEntry> referencedLoggedJournalEntries(
            JournalKeyProvider aReferenceKeyProvider) {

        List<LoggedJournalEntry> entries = new ArrayList<LoggedJournalEntry>();

        boolean done = false;

        while (!done) {
            String referenceKey = aReferenceKeyProvider.nextReferenceKey();

            byte[] rawJournalSequenceValue =
                    this.database().get(referenceKey.getBytes());

            if (rawJournalSequenceValue != null) {
                long journalSequence =
                        Long.parseLong(new String(rawJournalSequenceValue));

                String journalKey =
                        JournalKeyProvider.ES_JOURNAL_PREFIX_KEY
                        + journalSequence;

                byte[] rawJournalValue =
                        this.database()
                            .get(journalKey.getBytes());

                LoggedJournalEntry loggedJournalEntry =
                    new LoggedJournalEntry(
                            journalSequence,
                            referenceKey,
                            new String(rawJournalValue));

                // discard the stream key
                loggedJournalEntry.discardNextMetadataValue();

                entries.add(loggedJournalEntry);

            } else {
                done = true;
            }
        }

        return entries;
    }

    public String valueWithMetadata(String aValue, String aMetadata) {
        String valueWithMetadata =
                aMetadata + ES_METADATA_DELIMITER + aValue;

        return valueWithMetadata;
    }

    private LevelDBJournal(String aDirectoryPath) {
        super();

        this.openDatabase(aDirectoryPath);
    }

    private boolean cacheJournalSequence() {
        boolean cached = false;

        byte[] journalSequenceValue =
                this.database().get(ES_JOURNAL_SEQUENCE_KEY);

        if (journalSequenceValue != null) {
            this.setJournalSequence(
                    Long.parseLong(new String(journalSequenceValue)));

            // only a successful close() will save the journal sequence.
            // a missing journal sequence on open indicates the need for
            // a repair (unless the database is empty).

            this.database().delete(ES_JOURNAL_SEQUENCE_KEY);

            cached = true;

        } else {
            this.setJournalSequence(0L);
        }

        return cached;
    }

    private void confirmNonExistingReference(String aReferenceKey) {
        // this implementation will not stand up to race conditions

        if (this.database().get(aReferenceKey.getBytes()) != null) {
            throw new EventStoreAppendException("Journal concurrency violation.");
        }
    }

    private DB database() {
        return this.database;
    }

    private void setDatabase(DB aDatabase) {
        this.database = aDatabase;
    }

    private void setDatabasePath(String aDatabasePath) {
        this.databasePath = aDatabasePath;
    }

    private Object lockFor(String aPrimaryResourceName) {
        // need a reaper to remove the lock after some
        // size threshold and LRU

        synchronized (lock) {
            Object resourceLock = lock.get(aPrimaryResourceName);

            if (resourceLock == null) {
                resourceLock = new Object();

                lock.put(aPrimaryResourceName, resourceLock);
            }

            return resourceLock;
        }
    }

    private long nextJournalSequence() {
        long nextJournalSequence = this.journalSequence.incrementAndGet();

        return nextJournalSequence;
    }

    private void setJournalSequence(long aJournalSequence) {
        this.journalSequence =
                new AtomicLong(aJournalSequence);
    }

    private void openDatabase(String aDirectoryPath) {

        try {
            this.setDatabasePath(aDirectoryPath);

            DBFactory factory = new Iq80DBFactory();

            Options options = new Options();

            options.createIfMissing(true);

            this.setDatabase(factory.open(new File(aDirectoryPath), options));

            if (!this.cacheJournalSequence()) {
                this.repair();
            }

        } catch (Throwable t) {
            throw new EventStoreException(
                    "Cannot open LevelDB database: "
                        + aDirectoryPath
                        + " because: "
                        + t.getMessage(),
                    t);
        }
    }

    private void repair() {
        LevelDBJournalRepairTool repairTool =
                new LevelDBJournalRepairTool(this.database());

        repairTool.repairDatabase();

        long lastConfirmedKey = repairTool.lastConfirmedSequence();

        if (lastConfirmedKey > 0) {
            this.setJournalSequence(lastConfirmedKey);
        }
    }

    private void saveJournalSequence() {

        byte[] journalSequenceBytes = (""+journalSequence.get()).getBytes();

        this.database().put(
                ES_JOURNAL_SEQUENCE_KEY,
                journalSequenceBytes);
    }
}
