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

import com.saasovation.common.CommonTestCase;
import com.saasovation.common.persistence.PersistenceManagerProvider;

public class EventStoreContractTest extends CommonTestCase {

    public EventStoreContractTest() {
        super();
    }

    public void testAllStoredEventsBetween() throws Exception {
        EventStore eventStore = this.eventStore();

        long totalEvents = eventStore.countStoredEvents();

        assertEquals(totalEvents, eventStore.allStoredEventsBetween(1, totalEvents).size());

        assertEquals(10, eventStore.allStoredEventsBetween(totalEvents - 9, totalEvents).size());
    }

    public void testAllStoredEventsSince() throws Exception {
        EventStore eventStore = this.eventStore();

        long totalEvents = eventStore.countStoredEvents();

        assertEquals(totalEvents, eventStore.allStoredEventsSince(0).size());

        assertEquals(0, eventStore.allStoredEventsSince(totalEvents).size());

        assertEquals(10, eventStore.allStoredEventsSince(totalEvents - 10).size());
    }

    public void testAppend() throws Exception {
        EventStore eventStore = this.eventStore();

        long numberOfEvents = eventStore.countStoredEvents();

        TestableDomainEvent domainEvent = new TestableDomainEvent(10001, "testDomainEvent");

        StoredEvent storedEvent = eventStore.append(domainEvent);

        assertTrue(eventStore.countStoredEvents() > numberOfEvents);
        assertEquals(eventStore.countStoredEvents(), numberOfEvents + 1);

        assertNotNull(storedEvent);

        TestableDomainEvent reconstitutedDomainEvent = storedEvent.toDomainEvent();

        assertNotNull(reconstitutedDomainEvent);
        assertEquals(domainEvent.id(), reconstitutedDomainEvent.id());
        assertEquals(domainEvent.name(), reconstitutedDomainEvent.name());
        assertEquals(domainEvent.occurredOn(), reconstitutedDomainEvent.occurredOn());
    }

    public void testCountStoredEvents() throws Exception {
        EventStore eventStore = this.eventStore();

        long numberOfEvents = eventStore.countStoredEvents();

        TestableDomainEvent lastDomainEvent = null;

        for (int idx = 0; idx < 10; ++idx) {
            TestableDomainEvent domainEvent = new TestableDomainEvent(10001 + idx, "testDomainEvent" + idx);

            lastDomainEvent = domainEvent;

            eventStore.append(domainEvent);
        }

        assertEquals(numberOfEvents + 10, eventStore.countStoredEvents());

        numberOfEvents = eventStore.countStoredEvents();

        assertEquals(1, eventStore.allStoredEventsBetween(numberOfEvents, numberOfEvents + 1000).size());

        StoredEvent storedEvent = eventStore.allStoredEventsBetween(numberOfEvents, numberOfEvents).get(0);

        assertNotNull(storedEvent);

        TestableDomainEvent reconstitutedDomainEvent = storedEvent.toDomainEvent();

        assertNotNull(reconstitutedDomainEvent);
        assertEquals(lastDomainEvent.id(), reconstitutedDomainEvent.id());
        assertEquals(lastDomainEvent.name(), reconstitutedDomainEvent.name());
        assertEquals(lastDomainEvent.occurredOn(), reconstitutedDomainEvent.occurredOn());
    }

    public void testStoredEvent() throws Exception {
        EventStore eventStore = this.eventStore();

        TestableDomainEvent domainEvent = new TestableDomainEvent(10001, "testDomainEvent");

        StoredEvent storedEvent = eventStore.append(domainEvent);

        assertNotNull(storedEvent);

        TestableDomainEvent reconstitutedDomainEvent = storedEvent.toDomainEvent();

        assertNotNull(reconstitutedDomainEvent);
        assertEquals(domainEvent.id(), reconstitutedDomainEvent.id());
        assertEquals(domainEvent.name(), reconstitutedDomainEvent.name());
        assertEquals(domainEvent.occurredOn(), reconstitutedDomainEvent.occurredOn());
    }

    private EventStore eventStore() {
        EventStore eventStore = new MockEventStore(new PersistenceManagerProvider() {});

        assertNotNull(eventStore);

        return eventStore;
    }
}
