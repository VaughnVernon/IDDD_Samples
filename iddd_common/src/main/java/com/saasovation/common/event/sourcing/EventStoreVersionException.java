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

public class EventStoreVersionException extends EventStoreException {

    private static final long serialVersionUID = 1L;

    public EventStoreVersionException(String aMessage) {
        super(aMessage);
    }

    public EventStoreVersionException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }
}
