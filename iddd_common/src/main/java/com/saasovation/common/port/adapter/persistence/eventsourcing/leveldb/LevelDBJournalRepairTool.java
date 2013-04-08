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

import java.util.Map.Entry;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;

public class LevelDBJournalRepairTool {

    private static final int CONTIGUOUS_MISSING_KEY_SAFE_COUNT = 100000;

    private DB database;
    private long lastConfirmedSequence;

    public LevelDBJournalRepairTool(DB aDatabase) {
        super();

        this.setDatabase(aDatabase);
    }

    public long lastConfirmedSequence() {
        return this.lastConfirmedSequence;
    }

    public void repairDatabase() {

        if (!this.requiresRepair()) {
            return;
        }

        System.out.println("REPAIRING EVENT JOURNAL...");

        boolean cleanUpMode = false;
        int contiguousMissingKeys = 0;
        boolean done = false;
        long lastContiguousConfirmedKey = 0;

        for (long journalSequence = 1; !done; ++journalSequence) {
            byte[] journalKey =
                    (JournalKeyProvider.ES_JOURNAL_PREFIX_KEY
                    + journalSequence).getBytes();

            byte[] rawJournalValue =
                    this.database()
                        .get(journalKey);

            if (rawJournalValue == null) {
                cleanUpMode = true;

                if (++contiguousMissingKeys >= CONTIGUOUS_MISSING_KEY_SAFE_COUNT) {
                    done = true;
                }

            } else {
                if (!cleanUpMode) {
                    lastContiguousConfirmedKey = journalSequence;
                } else {
                    contiguousMissingKeys = 0;

                    LoggedJournalEntry loggedJournalEntry =
                            new LoggedJournalEntry(
                                    journalSequence,
                                    null,
                                    new String(rawJournalValue));

                    String streamKey = loggedJournalEntry.nextMetadataValue();

                    WriteBatch batch = this.database().createWriteBatch();

                    try {
                        batch.delete(streamKey.getBytes());
                        batch.delete(journalKey);
                        this.database().write(batch);

                        System.out.println(
                                "Repaired journal entry: "
                                + journalSequence
                                + " and stream entry: "
                                + streamKey);

                    } catch (Throwable t) {
                        System.out.println(
                                "Could not repair journal entry: "
                                + journalSequence
                                + " and stream entry: "
                                + streamKey);
                    } finally {
                        try {
                            batch.close();
                        } catch (Throwable t) {
                            // ignore
                        }
                    }
                }
            }
        }

        this.setLastConfirmedSequence(lastContiguousConfirmedKey);
    }

    private DB database() {
        return this.database;
    }

    private void setDatabase(DB aDatabase) {
        this.database = aDatabase;
    }

    private void setLastConfirmedSequence(long aLastConfirmedSequence) {

        if (aLastConfirmedSequence > 0) {
            System.out.println(
                    "REPAIRED JOURNAL HAS VALID SEQUENCE: " + aLastConfirmedSequence);
        }

        this.lastConfirmedSequence = aLastConfirmedSequence;
    }

    private boolean requiresRepair() {
        boolean requiresRepair = false;

        DBIterator iterator = this.database().iterator();

        iterator.seekToFirst();

        if (iterator.hasNext()) {
            requiresRepair = true;

            Entry<byte[],byte[]> entry = iterator.next();

            System.out.println("REPAIR NEEDED: KEY FOUND: " + new String(entry.getKey()));

        } else {

            System.out.println("NOT REPARING: Database is empty.");
        }

        return requiresRepair;
    }
}
