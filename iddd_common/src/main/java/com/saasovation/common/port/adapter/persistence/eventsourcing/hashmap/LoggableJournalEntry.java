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

public class LoggableJournalEntry {

    private String primaryResourceName;
    private String referenceKey;
    private String value;

    public LoggableJournalEntry(
            String aValue,
            String aReferenceKey,
            String aPrimaryResourceName) {

        super();

        this.setPrimaryResourceName(aPrimaryResourceName);
        this.setReferenceKey(aReferenceKey);
        this.setValue(aValue);
    }

    public String primaryResourceName() {
        return this.primaryResourceName;
    }

    public String referenceKey() {
        return this.referenceKey;
    }

    public String value() {
        return this.value;
    }

    private void setPrimaryResourceName(String aPrimaryResourceName) {
        this.primaryResourceName = aPrimaryResourceName;
    }

    private void setReferenceKey(String aReferenceKey) {
        this.referenceKey = aReferenceKey;
    }

    private void setValue(String aValue) {
        this.value = aValue;
    }
}
