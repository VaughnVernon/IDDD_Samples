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

package com.saasovation.common.event.sourcing;

public final class EventStreamId {

    private String streamName;
    private int streamVersion;

    public EventStreamId(String aStreamName) {
        this(aStreamName, 1);
    }

    public EventStreamId(String aStreamName, int aStreamVersion) {
        super();

        this.setStreamName(aStreamName);
        this.setStreamVersion(aStreamVersion);
    }

    public EventStreamId(String aStreamNameSegment1, String aStreamNameSegment2) {
        this(aStreamNameSegment1, aStreamNameSegment2, 1);
    }

    public EventStreamId(String aStreamNameSegment1, String aStreamNameSegment2, int aStreamVersion) {
        this(aStreamNameSegment1 + ":" + aStreamNameSegment2, aStreamVersion);
    }

    public String streamName() {
        return this.streamName;
    }

    public int streamVersion() {
        return this.streamVersion;
    }

    public EventStreamId withStreamVersion(int aStreamVersion) {
        return new EventStreamId(this.streamName(), aStreamVersion);
    }

    private void setStreamName(String aStreamName) {
        this.streamName = aStreamName;
    }

    private void setStreamVersion(int aStreamVersion) {
        this.streamVersion = aStreamVersion;
    }
}
