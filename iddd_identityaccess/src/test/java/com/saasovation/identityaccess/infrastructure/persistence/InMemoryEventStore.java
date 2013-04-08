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

package com.saasovation.identityaccess.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;

import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.event.EventSerializer;
import com.saasovation.common.event.EventStore;
import com.saasovation.common.event.StoredEvent;
import com.saasovation.common.persistence.CleanableStore;

public class InMemoryEventStore implements EventStore, CleanableStore {

    private List<StoredEvent> storedEvents;

    public InMemoryEventStore() {
        super();

        this.storedEvents = new ArrayList<StoredEvent>();
    }

    @Override
    public List<StoredEvent> allStoredEventsBetween(
            long aLowStoredEventId,
            long aHighStoredEventId) {
        List<StoredEvent> events = new ArrayList<StoredEvent>();

        for (StoredEvent storedEvent : this.storedEvents) {
            if (storedEvent.eventId() >= aLowStoredEventId && storedEvent.eventId() <= aHighStoredEventId) {
                events.add(storedEvent);
            }
        }

        return events;
    }

    @Override
    public List<StoredEvent> allStoredEventsSince(long aStoredEventId) {
        List<StoredEvent> events = new ArrayList<StoredEvent>();

        for (StoredEvent storedEvent : this.storedEvents) {
            if (storedEvent.eventId() > aStoredEventId) {
                events.add(storedEvent);
            }
        }

        return events;
    }

    @Override
    public synchronized StoredEvent append(DomainEvent aDomainEvent) {
        String eventSerialization =
                EventSerializer.instance().serialize(aDomainEvent);

        StoredEvent storedEvent =
                new StoredEvent(
                        aDomainEvent.getClass().getName(),
                        aDomainEvent.occurredOn(),
                        eventSerialization,
                        this.storedEvents.size() + 1);

        this.storedEvents.add(storedEvent);

        return storedEvent;
    }

    @Override
    public void close() {
        this.clean();
    }

    @Override
    public long countStoredEvents() {
        return this.storedEvents.size();
    }

    @Override
    public void clean() {
        this.storedEvents = new ArrayList<StoredEvent>();
    }
}
