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

package com.saasovation.collaboration.application.calendar;

import java.util.Collection;

import com.saasovation.collaboration.application.ApplicationTest;
import com.saasovation.collaboration.application.calendar.data.CalendarData;
import com.saasovation.collaboration.application.calendar.data.CalendarSharerData;
import com.saasovation.collaboration.domain.model.DomainRegistry;
import com.saasovation.collaboration.domain.model.calendar.Calendar;
import com.saasovation.collaboration.domain.model.calendar.CalendarSharer;
import com.saasovation.collaboration.domain.model.collaborator.Participant;

public class CalendarQueryServiceTest extends ApplicationTest {

    public CalendarQueryServiceTest() {
        super();
    }

    public void testQueryAllCalendars() throws Exception {
        Calendar[] calendars = this.calendarAggregates();

        for (Calendar calendar : calendars) {
            DomainRegistry.calendarRepository().save(calendar);
        }

        Collection<CalendarData> queriedCalendars =
                calendarQueryService.allCalendarsDataOfTenant(calendars[0].tenant().id());

        assertNotNull(queriedCalendars);
        assertFalse(queriedCalendars.isEmpty());
        assertEquals(calendars.length, queriedCalendars.size());

        for (CalendarData calendarData : queriedCalendars) {
            assertNotNull(calendarData);
            assertEquals(calendars[0].tenant().id(), calendarData.getTenantId());
            assertNotNull(calendarData.getSharers());
            assertFalse(calendarData.getSharers().isEmpty());
        }
    }

    public void testQueryCalendar() throws Exception {
        Calendar calendar = this.calendarAggregate();

        CalendarSharer sharerZoe = new CalendarSharer(
                new Participant("zoe", "Zoe Doe", "zoe@saasovation.com"));

        calendar.shareCalendarWith(sharerZoe);

        CalendarSharer sharerJoe = new CalendarSharer(
                new Participant("joe", "Joe Smith", "joe@saasovation.com"));

        calendar.shareCalendarWith(sharerJoe);

        DomainRegistry.calendarRepository().save(calendar);

        CalendarData calendarData =
                calendarQueryService.calendarDataOfId(
                        calendar.tenant().id(),
                        calendar.calendarId().id());

        assertNotNull(calendarData);
        assertEquals(calendar.calendarId().id(), calendarData.getCalendarId());
        assertEquals(calendar.description(), calendarData.getDescription());
        assertEquals(calendar.name(), calendarData.getName());
        assertEquals(calendar.owner().emailAddress(), calendarData.getOwnerEmailAddress());
        assertEquals(calendar.owner().identity(), calendarData.getOwnerIdentity());
        assertEquals(calendar.owner().name(), calendarData.getOwnerName());
        assertEquals(calendar.tenant().id(), calendarData.getTenantId());
        assertNotNull(calendarData.getSharers());
        assertFalse(calendarData.getSharers().isEmpty());
        assertEquals(2, calendarData.getSharers().size());

        for (CalendarSharerData sharer : calendarData.getSharers()) {
            if (sharer.getParticipantIdentity().equals("zoe")) {
                assertEquals(calendar.calendarId().id(), sharer.getCalendarId());
                assertEquals(sharerZoe.participant().emailAddress(), sharer.getParticipantEmailAddress());
                assertEquals(sharerZoe.participant().identity(), sharer.getParticipantIdentity());
                assertEquals(sharerZoe.participant().name(), sharer.getParticipantName());
            } else {
                assertEquals(calendar.calendarId().id(), sharer.getCalendarId());
                assertEquals(sharerJoe.participant().emailAddress(), sharer.getParticipantEmailAddress());
                assertEquals(sharerJoe.participant().identity(), sharer.getParticipantIdentity());
                assertEquals(sharerJoe.participant().name(), sharer.getParticipantName());
            }
        }
    }
}
