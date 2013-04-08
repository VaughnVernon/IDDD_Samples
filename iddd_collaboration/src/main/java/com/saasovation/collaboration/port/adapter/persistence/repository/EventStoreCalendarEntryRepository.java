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

package com.saasovation.collaboration.port.adapter.persistence.repository;

import java.util.UUID;

import com.saasovation.collaboration.domain.model.calendar.CalendarEntry;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntryId;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntryRepository;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.collaboration.port.adapter.persistence.EventStoreProvider;
import com.saasovation.common.event.sourcing.EventStream;
import com.saasovation.common.event.sourcing.EventStreamId;

public class EventStoreCalendarEntryRepository
        extends EventStoreProvider
        implements CalendarEntryRepository {

    public EventStoreCalendarEntryRepository() {
        super();
    }

    @Override
    public CalendarEntry calendarEntryOfId(Tenant aTenant, CalendarEntryId aCalendarEntryId) {
        // snapshots not currently supported; always use version 1

        EventStreamId eventId = new EventStreamId(aTenant.id(), aCalendarEntryId.id());

        EventStream eventStream = this.eventStore().eventStreamSince(eventId);

        CalendarEntry calendarEntry = new CalendarEntry(eventStream.events(), eventStream.version());

        return calendarEntry;
    }

    @Override
    public CalendarEntryId nextIdentity() {
        return new CalendarEntryId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public void save(CalendarEntry aCalendarEntry) {
        EventStreamId eventId =
                new EventStreamId(
                        aCalendarEntry.tenant().id(),
                        aCalendarEntry.calendarEntryId().id(),
                        aCalendarEntry.mutatedVersion());

        this.eventStore().appendWith(eventId, aCalendarEntry.mutatingEvents());
    }
}
