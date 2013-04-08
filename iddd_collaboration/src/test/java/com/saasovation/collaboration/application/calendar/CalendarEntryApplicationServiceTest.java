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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.saasovation.collaboration.application.ApplicationTest;
import com.saasovation.collaboration.domain.model.DomainRegistry;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntry;
import com.saasovation.collaboration.domain.model.collaborator.Participant;

public class CalendarEntryApplicationServiceTest extends ApplicationTest {

    public CalendarEntryApplicationServiceTest() {
        super();
    }

    public void testChangeCalendarEntryDescription() throws Exception {

        CalendarEntry calendarEntry = this.calendarEntryAggregate();

        DomainRegistry.calendarEntryRepository().save(calendarEntry);

        calendarEntryApplicationService
            .changeCalendarEntryDescription(
                    calendarEntry.tenant().id(),
                    calendarEntry.calendarEntryId().id(),
                    "A changed calendar entry description.");

        CalendarEntry changedCalendarEntry =
                DomainRegistry
                    .calendarEntryRepository()
                    .calendarEntryOfId(
                            calendarEntry.tenant(),
                            calendarEntry.calendarEntryId());

        assertNotNull(changedCalendarEntry);
        assertEquals("A changed calendar entry description.", changedCalendarEntry.description());
    }

    public void testInviteCalendarEntryParticipant() throws Exception {

        CalendarEntry calendarEntry = this.calendarEntryAggregate();

        DomainRegistry.calendarEntryRepository().save(calendarEntry);

        Set<String> invitees = new HashSet<String>(0);
        invitees.add("participant1");
        invitees.add("participant2");
        invitees.add("participant3");

        calendarEntryApplicationService
            .inviteCalendarEntryParticipant(
                    calendarEntry.tenant().id(),
                    calendarEntry.calendarEntryId().id(),
                    invitees);

        CalendarEntry changedCalendarEntry =
                DomainRegistry
                    .calendarEntryRepository()
                    .calendarEntryOfId(
                            calendarEntry.tenant(),
                            calendarEntry.calendarEntryId());

        assertNotNull(changedCalendarEntry);
        assertEquals(3, changedCalendarEntry.allInvitees().size());

        for (Participant invitee : changedCalendarEntry.allInvitees()) {
            assertTrue(invitee.identity().equals("participant1") ||
                       invitee.identity().equals("participant2") ||
                       invitee.identity().equals("participant3"));
        }
    }

    public void testRelocateCalendarEntry() throws Exception {

        CalendarEntry calendarEntry = this.calendarEntryAggregate();

        DomainRegistry.calendarEntryRepository().save(calendarEntry);

        calendarEntryApplicationService
            .relocateCalendarEntry(
                    calendarEntry.tenant().id(),
                    calendarEntry.calendarEntryId().id(),
                    "A changed calendar entry location.");

        CalendarEntry changedCalendarEntry =
                DomainRegistry
                    .calendarEntryRepository()
                    .calendarEntryOfId(
                            calendarEntry.tenant(),
                            calendarEntry.calendarEntryId());

        assertNotNull(changedCalendarEntry);
        assertEquals("A changed calendar entry location.", changedCalendarEntry.location());
    }

    public void testRescheduleCalendarEntry() throws Exception {

        CalendarEntry calendarEntry = this.calendarEntryAggregate();

        DomainRegistry.calendarEntryRepository().save(calendarEntry);

        Date now = new Date();
        Date nextWeek = new Date(now.getTime() + (86400000L * 7L));
        Date nextWeekAndOneHour = new Date(nextWeek.getTime() + (1000 * 60 * 60));

        calendarEntryApplicationService
            .rescheduleCalendarEntry(
                    calendarEntry.tenant().id(),
                    calendarEntry.calendarEntryId().id(),
                    "A changed description.",
                    "A changed location.",
                    nextWeek,
                    nextWeekAndOneHour,
                    "DoesNotRepeat",
                    nextWeekAndOneHour,
                    "Hours",
                    8);

        CalendarEntry changedCalendarEntry =
                DomainRegistry
                    .calendarEntryRepository()
                    .calendarEntryOfId(
                            calendarEntry.tenant(),
                            calendarEntry.calendarEntryId());

        assertNotNull(changedCalendarEntry);
        assertEquals("A changed description.", changedCalendarEntry.description());
        assertEquals("A changed location.", changedCalendarEntry.location());
        assertEquals(nextWeek, changedCalendarEntry.timeSpan().begins());
        assertEquals(nextWeekAndOneHour, changedCalendarEntry.timeSpan().ends());
        assertTrue(changedCalendarEntry.repetition().repeats().isDoesNotRepeat());
        assertTrue(changedCalendarEntry.alarm().alarmUnitsType().isHours());
        assertEquals(8, changedCalendarEntry.alarm().alarmUnits());
    }

    public void testUninviteCalendarEntryParticipant() throws Exception {

        CalendarEntry calendarEntry = this.calendarEntryAggregate();

        DomainRegistry.calendarEntryRepository().save(calendarEntry);

        Set<String> invitees = new HashSet<String>(0);
        invitees.add("participant1");
        invitees.add("participant2");
        invitees.add("participant3");

        calendarEntryApplicationService
            .inviteCalendarEntryParticipant(
                    calendarEntry.tenant().id(),
                    calendarEntry.calendarEntryId().id(),
                    invitees);

        Set<String> uninvitees = new HashSet<String>(invitees);
        assertTrue(uninvitees.remove("participant2"));

        calendarEntryApplicationService
            .uninviteCalendarEntryParticipant(
                calendarEntry.tenant().id(),
                calendarEntry.calendarEntryId().id(),
                uninvitees);

        CalendarEntry changedCalendarEntry =
                DomainRegistry
                    .calendarEntryRepository()
                    .calendarEntryOfId(
                            calendarEntry.tenant(),
                            calendarEntry.calendarEntryId());

        assertNotNull(changedCalendarEntry);
        assertEquals(1, changedCalendarEntry.allInvitees().size());
        assertEquals("participant2", changedCalendarEntry.allInvitees().iterator().next().identity());
    }
}
