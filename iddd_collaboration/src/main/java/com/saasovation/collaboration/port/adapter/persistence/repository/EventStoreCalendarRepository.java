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

import com.saasovation.collaboration.domain.model.calendar.Calendar;
import com.saasovation.collaboration.domain.model.calendar.CalendarId;
import com.saasovation.collaboration.domain.model.calendar.CalendarRepository;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.collaboration.port.adapter.persistence.EventStoreProvider;
import com.saasovation.common.event.sourcing.EventStream;
import com.saasovation.common.event.sourcing.EventStreamId;

public class EventStoreCalendarRepository
        extends EventStoreProvider
        implements CalendarRepository {

    public EventStoreCalendarRepository() {
        super();
    }

    @Override
    public Calendar calendarOfId(Tenant aTenant, CalendarId aCalendarId) {
        // snapshots not currently supported; always use version 1

        EventStreamId eventId = new EventStreamId(aTenant.id(), aCalendarId.id());

        EventStream eventStream = this.eventStore().eventStreamSince(eventId);

        Calendar calendar = new Calendar(eventStream.events(), eventStream.version());

        return calendar;
    }

    @Override
    public CalendarId nextIdentity() {
        return new CalendarId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public void save(Calendar aCalendar) {
        EventStreamId eventId =
                new EventStreamId(
                        aCalendar.tenant().id(),
                        aCalendar.calendarId().id(),
                        aCalendar.mutatedVersion());

        this.eventStore().appendWith(eventId, aCalendar.mutatingEvents());
    }
}
