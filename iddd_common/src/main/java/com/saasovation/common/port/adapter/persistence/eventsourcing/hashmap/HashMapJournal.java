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

package com.saasovation.common.port.adapter.persistence.eventsourcing.hashmap;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import com.saasovation.common.event.sourcing.*;

public class HashMapJournal {

    protected static final String ES_METADATA_DELIMITER = "#";

    private static final String ES_JOURNAL_SEQUENCE_KEY =
            (JournalKeyProvider.ES_JOURNAL_PREFIX_KEY+"0");

    private static HashMapJournal instance;

    private Map<String,String> database;
    private AtomicLong journalSequence;

    public static HashMapJournal initializeInstance() {
        if (instance == null) {
            instance = new HashMapJournal();
        } else {

            // for test
            if (instance.database() == null) {
                instance.openDatabase();
            }
        }

        return instance;
    }

    public static HashMapJournal instance() {
        if (instance == null) {
            throw new IllegalStateException("There is no LevelDBJournalProvider instance.");
        }

        return initializeInstance();
    }

    public void close() {
        if (instance != null) {
            try {
                this.saveJournalSequence();

                this.database = null;

            } catch (Throwable t) {
                throw new EventStoreException(
                        "Cannot close HashMap database because: "
                                + t.getMessage(),
                        t);
            } finally {
                instance = null;
            }
        }
    }

    public void logEntries(LoggableJournalEntry[] aJournalEntries) {

        try {
            for (LoggableJournalEntry journalEntry : aJournalEntries) {

                long journalSequence = this.nextJournalSequence();

                this.confirmNonExistingReference(journalEntry.referenceKey());

                String jounralKey =
                        JournalKeyProvider.ES_JOURNAL_PREFIX_KEY
                        + journalSequence;

                String referenceKey =
                        journalEntry.referenceKey();

                String journalValue =
                        this.valueWithMetadata(
                                journalEntry.value(),
                                referenceKey);

                // journal entry points to reference

                this.database().put(jounralKey, journalValue);

                // reference points to journal entry

                this.database().put(referenceKey, ""+journalSequence);
            }

        } catch (Throwable t) {
            throw new EventStoreAppendException(
                    "Could not append to journal because: "
                            + t.getMessage(),
                    t);
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

            String journalValue = this.database().get(journalKey);

            if (journalValue != null) {

                LoggedJournalEntry loggedJournalEntry =
                    new LoggedJournalEntry(
                            journalSequence,
                            null,
                            journalValue);

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


        this.setJournalSequence(0L);
    }

    public List<LoggedJournalEntry> referencedLoggedJournalEntries(
            JournalKeyProvider aReferenceKeyProvider) {

        List<LoggedJournalEntry> entries = new ArrayList<LoggedJournalEntry>();

        boolean done = false;

        while (!done) {
            String referenceKey = aReferenceKeyProvider.nextReferenceKey();

            String rawJournalSequenceValue =
                    this.database().get(referenceKey);

            if (rawJournalSequenceValue != null) {
                long journalSequence =
                        Long.parseLong(new String(rawJournalSequenceValue));

                String journalKey =
                        JournalKeyProvider.ES_JOURNAL_PREFIX_KEY
                        + journalSequence;

                String rawJournalValue =
                        this.database().get(journalKey);

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

    private HashMapJournal() {
        super();

        this.openDatabase();
    }

    private boolean cacheJournalSequence() {
        boolean cached = false;

        String journalSequenceValue =
                this.database().get(ES_JOURNAL_SEQUENCE_KEY);

        if (journalSequenceValue != null) {
            this.setJournalSequence(
                    Long.parseLong(new String(journalSequenceValue)));

            // only a successful close() will save the journal sequence.
            // a missing journal sequence on open indicates the need for
            // a repair (unless the database is empty).

            this.database().remove(ES_JOURNAL_SEQUENCE_KEY);

            cached = true;

        } else {
            this.setJournalSequence(0L);
        }

        return cached;
    }

    private void confirmNonExistingReference(String aReferenceKey) {
        // this implementation will not stand up to race conditions

        if (this.database().get(aReferenceKey) != null) {
            throw new EventStoreAppendException("Journal concurrency violation.");
        }
    }

    private Map<String,String> database() {
        return this.database;
    }

    private void setDatabase(Map<String,String> aDatabase) {
        this.database = aDatabase;
    }

    private long nextJournalSequence() {
        long nextJournalSequence = this.journalSequence.incrementAndGet();

        return nextJournalSequence;
    }

    private void setJournalSequence(long aJournalSequence) {
        this.journalSequence =
                new AtomicLong(aJournalSequence);
    }

    private void openDatabase() {

        try {
            this.setDatabase(new HashMap<String,String>());

            if (!this.cacheJournalSequence()) {
                journalSequence = new AtomicLong(0);
            }

        } catch (Throwable t) {
            throw new EventStoreException(
                    "Cannot open HashMap database because: "
                        + t.getMessage(),
                    t);
        }
    }

    private void saveJournalSequence() {
        this.database().put(
                ES_JOURNAL_SEQUENCE_KEY,
                ""+journalSequence.get());
    }
}
