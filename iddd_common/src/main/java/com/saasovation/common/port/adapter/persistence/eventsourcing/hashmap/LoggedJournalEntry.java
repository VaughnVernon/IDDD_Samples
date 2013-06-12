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

public class LoggedJournalEntry {

    private int currentMetadataIndex;
    private long journalSequence;
    private String referenceKey;
    private String value;

    public LoggedJournalEntry(
            long aJournalSequence,
            String aReferenceKey,
            String aValue) {

        super();

        this.setCurrentMetadataIndex(0);
        this.setJournalSequence(aJournalSequence);
        this.setReferenceKey(aReferenceKey);
        this.setValue(aValue);
    }

    public void discardNextMetadataValue() {
        this.nextMetadataValue();
    }

    public long journalSequence() {
        return this.journalSequence;
    }

    public String nextMetadataValue() {
        String metadataValue = null;

        String value = this.value();

        int nextIndex =
                value.indexOf(
                        HashMapJournal.ES_METADATA_DELIMITER,
                        0);

        if (nextIndex != -1) {
            metadataValue = value.substring(0, nextIndex);

            this.setCurrentMetadataIndex(nextIndex + 1);
        }

        return metadataValue;
    }

    public String referenceKey() {
        return this.referenceKey;
    }

    public String value() {
        int index = this.currentMetadataIndex();

        this.setValue(this.value.substring(index));

        this.setCurrentMetadataIndex(0);

        return this.value;
    }

    private int currentMetadataIndex() {
        return this.currentMetadataIndex;
    }

    private void setCurrentMetadataIndex(int anIndex) {
        this.currentMetadataIndex = anIndex;
    }

    private void setJournalSequence(long aJournalSequence) {
        this.journalSequence = aJournalSequence;
    }

    private void setReferenceKey(String aReferenceKey) {
        this.referenceKey = aReferenceKey;
    }

    private void setValue(String aValue) {
        this.value = aValue;
    }
}
