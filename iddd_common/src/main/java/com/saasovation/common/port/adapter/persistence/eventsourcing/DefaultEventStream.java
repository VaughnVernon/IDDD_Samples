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

package com.saasovation.common.port.adapter.persistence.eventsourcing;

import java.util.List;

import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.event.sourcing.EventStream;

public class DefaultEventStream implements EventStream {

    private List<DomainEvent> events;
    private int version;

    public DefaultEventStream(List<DomainEvent> anEventsList, int aVersion) {
        super();

        this.setEvents(anEventsList);
        this.setVersion(aVersion);
    }

    @Override
    public List<DomainEvent> events() {
        return this.events;
    }

    @Override
    public int version() {
        return this.version;
    }

    private void setEvents(List<DomainEvent> anEventsList) {
        this.events = anEventsList;
    }

    private void setVersion(int aVersion) {
        this.version = aVersion;
    }
}
