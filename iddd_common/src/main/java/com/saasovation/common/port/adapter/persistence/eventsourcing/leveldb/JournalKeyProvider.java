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

public class JournalKeyProvider {

    protected static final String ES_JOURNAL_PREFIX_KEY = "ES_J:";
    protected static final String ES_KEY_DELIMITER = ":";
    protected static final String ES_REFERENCE_PREFIX_KEY = "ES_R:";

    public String compositeReferenceKeyFrom(String aKeyPart1, String aKeyPart2) {
        String referenceKey =
                this.referenceKeyFrom(
                        aKeyPart1
                        + ES_KEY_DELIMITER
                        + aKeyPart2);

        return referenceKey;
    }

    public String firstKeyPart(String aCompositeKey) {
        return aCompositeKey.substring(0, aCompositeKey.indexOf(ES_KEY_DELIMITER));
    }

    public String lastKeyPart(String aCompositeKey) {
        return aCompositeKey.substring(aCompositeKey.lastIndexOf(ES_KEY_DELIMITER) + 1);
    }

    public String nextReferenceKey() {
        throw new UnsupportedOperationException("Must be implemented by subclass.");
    }

    public String primaryResourceName() {
        throw new UnsupportedOperationException("Must be implemented by subclass.");
    }

    public String referenceKeyFrom(String aKey) {
        String referenceKey =
                ES_REFERENCE_PREFIX_KEY
                + aKey;

        return referenceKey;
    }
}
