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

package com.saasovation.common.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.saasovation.common.AssertionConcern;
import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.persistence.PersistenceManagerProvider;

public class MockEventStore extends AssertionConcern implements EventStore {

    private static final long START_ID = 789;

    private List<StoredEvent> storedEvents;

    public MockEventStore(PersistenceManagerProvider aPersistenceManagerProvider) {
        super();

        this.assertArgumentNotNull(
                aPersistenceManagerProvider,
                "PersistenceManagerProvider is not valid; must not be null.");

        // always start with at least 21 events

        this.storedEvents = new ArrayList<StoredEvent>();

        int numberOfStoredEvents =
                Calendar
                    .getInstance()
                    .get(Calendar.MILLISECOND) + 1; // 1-1000

        if (numberOfStoredEvents < 21) {
            numberOfStoredEvents = 21;
        }

        for (int idx = 0; idx < numberOfStoredEvents; ++idx) {
            StoredEvent storedEvent = this.newStoredEvent(START_ID + idx, idx + 1);

            this.storedEvents.add(storedEvent);
        }
    }

    @Override
    public List<StoredEvent> allStoredEventsBetween(long aLowStoredEventId, long aHighStoredEventId) {
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
        return this.allStoredEventsBetween(aStoredEventId + 1, this.countStoredEvents());
    }

    @Override
    public StoredEvent append(DomainEvent aDomainEvent) {
        String eventSerialization =
                EventSerializer.instance().serialize(aDomainEvent);

        StoredEvent storedEvent =
                new StoredEvent(
                        aDomainEvent.getClass().getName(),
                        aDomainEvent.occurredOn(),
                        eventSerialization);

        storedEvent.setEventId(this.storedEvents.size() + 1);

        this.storedEvents.add(storedEvent);

        return storedEvent;
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public long countStoredEvents() {
        return this.storedEvents.size();
    }

    private StoredEvent newStoredEvent(long domainEventId, long storedEventId) {
        EventSerializer serializer = EventSerializer.instance();

        DomainEvent event = new TestableDomainEvent(domainEventId, "name" + domainEventId);
        String serializedEvent = serializer.serialize(event);
        StoredEvent storedEvent = new StoredEvent(event.getClass().getName(), event.occurredOn(), serializedEvent);
        storedEvent.setEventId(storedEventId);

        return storedEvent;
    }
}
