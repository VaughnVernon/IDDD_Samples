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

package com.saasovation.collaboration.domain.model.calendar;

import java.util.Iterator;
import java.util.TreeSet;

import com.saasovation.collaboration.domain.model.DomainRegistry;
import com.saasovation.collaboration.domain.model.DomainTest;
import com.saasovation.collaboration.domain.model.collaborator.Owner;
import com.saasovation.collaboration.domain.model.collaborator.Participant;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.domain.model.DomainEventSubscriber;

public class CalendarTest extends DomainTest {

    private CalendarEntry calendarEntry;
    private CalendarEntryId calendarEntryId;

    public CalendarTest() {
        super();
    }

    public void testCreateCalendar() throws Exception {

        Calendar calendar = this.calendarAggregate();

        assertEquals("John Doe's Calendar", calendar.name());
        assertEquals("John Doe's everyday work calendar.", calendar.description());
        assertEquals("jdoe", calendar.owner().identity());

        DomainRegistry.calendarRepository().save(calendar);

        expectedEvents(1);
        expectedEvent(CalendarCreated.class);

        expectedNotifications(1);
        expectedNotification(CalendarCreated.class);
    }

    public void testCalendarChangeDescription() throws Exception {

        Calendar calendar = this.calendarAggregate();

        calendar.changeDescription("A changed description.");

        assertEquals("A changed description.", calendar.description());

        DomainRegistry.calendarRepository().save(calendar);

        expectedEvents(2);
        expectedEvent(CalendarCreated.class);
        expectedEvent(CalendarDescriptionChanged.class);

        expectedNotifications(2);
        expectedNotification(CalendarCreated.class);
        expectedNotification(CalendarDescriptionChanged.class);
    }

    public void testRenameCalendar() throws Exception {

        Calendar calendar = this.calendarAggregate();

        calendar.rename("A different name.");

        assertEquals("A different name.", calendar.name());

        DomainRegistry.calendarRepository().save(calendar);

        expectedEvents(2);
        expectedEvent(CalendarCreated.class);
        expectedEvent(CalendarRenamed.class);

        expectedNotifications(2);
        expectedNotification(CalendarCreated.class);
        expectedNotification(CalendarRenamed.class);
    }

    public void testCalendarSharesWithUnshare() throws Exception {

        Calendar calendar = this.calendarAggregate();

        assertTrue(calendar.allSharedWith().isEmpty());

        calendar.shareCalendarWith(
                new CalendarSharer(
                        new Participant("zdoe", "Zoe Doe", "zdoe@saasovation.com")));

        calendar.shareCalendarWith(
                new CalendarSharer(
                        new Participant("jdoe", "John Doe", "jdoe@saasovation.com")));

        assertFalse(calendar.allSharedWith().isEmpty());

        CalendarSharer sharer = calendar.allSharedWith().iterator().next();

        calendar.unshareCalendarWith(sharer);

        assertFalse(calendar.allSharedWith().isEmpty());

        DomainRegistry.calendarRepository().save(calendar);

        expectedEvents(4);
        expectedEvent(CalendarCreated.class);
        expectedEvent(CalendarShared.class, 2);
        expectedEvent(CalendarUnshared.class);

        expectedNotifications(4);
        expectedNotification(CalendarCreated.class);
        expectedNotification(CalendarShared.class, 2);
        expectedNotification(CalendarUnshared.class);
    }

    public void testCalendarShares() throws Exception {

        Calendar calendar = this.calendarAggregate();

        assertTrue(calendar.allSharedWith().isEmpty());

        calendar.shareCalendarWith(
                new CalendarSharer(
                        new Participant("zdoe", "Zoe Doe", "zdoe@saasovation.com")));

        calendar.shareCalendarWith(
                new CalendarSharer(
                        new Participant("jdoe", "John Doe", "jdoe@saasovation.com")));

        assertFalse(calendar.allSharedWith().isEmpty());

        DomainRegistry.calendarRepository().save(calendar);

        expectedEvents(3);
        expectedEvent(CalendarCreated.class);
        expectedEvent(CalendarShared.class, 2);

        expectedNotifications(3);
        expectedNotification(CalendarCreated.class);
        expectedNotification(CalendarShared.class, 2);
    }

    public void testScheduleCalendarEntry() throws Exception {

        DomainEventPublisher
            .instance()
            .subscribe(new DomainEventSubscriber<CalendarEntryScheduled>() {
                public void handleEvent(CalendarEntryScheduled aDomainEvent) {
                    calendarEntryId = aDomainEvent.calendarEntryId();
                }
                public Class<CalendarEntryScheduled> subscribedToEventType() {
                    return CalendarEntryScheduled.class;
                }
            });

        CalendarEntry calendarEntry = this.calendarEntryAggregate();

        DomainRegistry.calendarEntryRepository().save(calendarEntry);

        assertNotNull(calendarEntryId);

        expectedEvents(2);
        expectedEvent(CalendarCreated.class);
        expectedEvent(CalendarEntryScheduled.class);

        expectedNotifications(2);
        expectedNotification(CalendarCreated.class);
        expectedNotification(CalendarEntryScheduled.class);
    }

    public void testCalendarEntryChangeDescription() throws Exception {

        CalendarEntry calendarEntry = this.calendarEntryAggregate();

        calendarEntry.changeDescription("A changed description.");

        assertEquals("A changed description.", calendarEntry.description());

        DomainRegistry.calendarEntryRepository().save(calendarEntry);

        expectedEvents(3);
        expectedEvent(CalendarCreated.class);
        expectedEvent(CalendarEntryScheduled.class);
        expectedEvent(CalendarEntryDescriptionChanged.class);

        expectedNotifications(3);
        expectedNotification(CalendarCreated.class);
        expectedNotification(CalendarEntryScheduled.class);
        expectedNotification(CalendarEntryDescriptionChanged.class);
    }

    public void testInviteToCalendarEntry() throws Exception {

        CalendarEntry calendarEntry = this.calendarEntryAggregate();

        assertTrue(calendarEntry.allInvitees().isEmpty());

        Participant invitee1 = new Participant("jdoe", "John Doe", "jdoe@saasovation.com");

        calendarEntry.invite(invitee1);

        assertFalse(calendarEntry.allInvitees().isEmpty());
        assertEquals(1, calendarEntry.allInvitees().size());
        assertEquals(invitee1, calendarEntry.allInvitees().iterator().next());

        calendarEntry.uninvite(invitee1);

        assertTrue(calendarEntry.allInvitees().isEmpty());

        Participant invitee2 = new Participant("tsmith", "Tom Smith", "tsmith@saasovation.com");

        calendarEntry.invite(invitee1);
        calendarEntry.invite(invitee2);

        assertFalse(calendarEntry.allInvitees().isEmpty());
        assertEquals(2, calendarEntry.allInvitees().size());

        Iterator<Participant> iterator = calendarEntry.allInvitees().iterator();
        Participant participant1 = iterator.next();
        Participant participant2 = iterator.next();

        assertTrue(participant1.equals(invitee1) || participant1.equals(invitee2));
        assertTrue(participant2.equals(invitee1) || participant2.equals(invitee2));

        calendarEntry.uninvite(invitee1);

        assertFalse(calendarEntry.allInvitees().isEmpty());

        calendarEntry.uninvite(invitee2);

        assertTrue(calendarEntry.allInvitees().isEmpty());

        DomainRegistry.calendarEntryRepository().save(calendarEntry);

        expectedEvents(8);
        expectedEvent(CalendarCreated.class);
        expectedEvent(CalendarEntryScheduled.class);
        expectedEvent(CalendarEntryParticipantInvited.class, 3);
        expectedEvent(CalendarEntryParticipantUninvited.class, 3);

        expectedNotifications(8);
        expectedNotification(CalendarCreated.class);
        expectedNotification(CalendarEntryScheduled.class);
        expectedNotification(CalendarEntryParticipantInvited.class, 3);
        expectedNotification(CalendarEntryParticipantUninvited.class, 3);
    }

    public void testRelocateCaledarEntry() throws Exception {

        CalendarEntry calendarEntry = this.calendarEntryAggregate();

        calendarEntry.relocate("A changed location.");

        assertEquals("A changed location.", calendarEntry.location());

        DomainRegistry.calendarEntryRepository().save(calendarEntry);

        expectedEvents(3);
        expectedEvent(CalendarCreated.class);
        expectedEvent(CalendarEntryScheduled.class);
        expectedEvent(CalendarEntryRelocated.class);

        expectedNotifications(3);
        expectedNotification(CalendarCreated.class);
        expectedNotification(CalendarEntryScheduled.class);
        expectedNotification(CalendarEntryRelocated.class);
    }

    public void testRescheduleCalendarEntry() throws Exception {

        CalendarEntry calendarEntry = this.calendarEntryAggregate();

        TimeSpan timeSpan = this.oneWeekAroundTimeSpan();
        Repetition repetition = Repetition.doesNotRepeatInstance(timeSpan.ends());

        calendarEntry.reschedule(
                "A changed description.",
                "A changed location.",
                timeSpan,
                repetition,
                this.oneHourBeforeAlarm());

        assertEquals("A changed description.", calendarEntry.description());
        assertEquals("A changed location.", calendarEntry.location());
        assertEquals(this.oneWeekAroundTimeSpan(), calendarEntry.timeSpan());
        assertEquals(repetition, calendarEntry.repetition());
        assertEquals(this.oneHourBeforeAlarm(), calendarEntry.alarm());

        calendarEntry.reschedule(
                "A changed description.",
                "A changed location.",
                this.oneWeekAroundTimeSpan(),
                Repetition.indefinitelyRepeatsInstance(RepeatType.Weekly),
                this.oneHourBeforeAlarm());

        assertEquals("A changed description.", calendarEntry.description());
        assertEquals("A changed location.", calendarEntry.location());
        assertEquals(this.oneWeekAroundTimeSpan(), calendarEntry.timeSpan());
        assertEquals(Repetition.indefinitelyRepeatsInstance(RepeatType.Weekly), calendarEntry.repetition());
        assertEquals(this.oneHourBeforeAlarm(), calendarEntry.alarm());

        DomainRegistry.calendarEntryRepository().save(calendarEntry);

        expectedEvents(6);
        expectedEvent(CalendarCreated.class);
        expectedEvent(CalendarEntryScheduled.class);
        expectedEvent(CalendarEntryDescriptionChanged.class, 1);
        expectedEvent(CalendarEntryRelocated.class, 1);
        expectedEvent(CalendarEntryRescheduled.class, 2);

        expectedNotifications(6);
        expectedNotification(CalendarCreated.class);
        expectedNotification(CalendarEntryScheduled.class);
        expectedNotification(CalendarEntryDescriptionChanged.class);
        expectedNotification(CalendarEntryRelocated.class, 1);
        expectedNotification(CalendarEntryRescheduled.class, 2);
    }

    protected Calendar calendarAggregate() {

        Tenant tenant = new Tenant("01234567");

        Calendar calendar =
            new Calendar(
                    tenant,
                    DomainRegistry.calendarRepository().nextIdentity(),
                    "John Doe's Calendar",
                    "John Doe's everyday work calendar.",
                    new Owner("jdoe", "John Doe", "jdoe@saasovation.com"),
                    new TreeSet<CalendarSharer>());

        return calendar;
    }

    protected CalendarEntry calendarEntryAggregate() {

        Calendar calendar = this.calendarAggregate();

        DomainRegistry.calendarRepository().save(calendar);

        calendarEntry =
            calendar.scheduleCalendarEntry(
                    DomainRegistry.calendarIdentityService(),
                    "A Doctor Checkup.",
                    "Family Practice Offices",
                    new Owner("jdoe", "John Doe", "jdoe@saasovation.com"),
                    this.tomorrowOneHourTimeSpan(),
                    this.weeklyRepetition(),
                    this.oneHourBeforeAlarm(),
                    new TreeSet<Participant>());

        return calendarEntry;
    }

    protected Repetition weeklyRepetition() {

        return new Repetition(
                RepeatType.Weekly,
                this.tomorrowThroughOneYearLaterTimeSpan().ends());
    }

    protected Alarm oneHourBeforeAlarm() {

        return new Alarm(AlarmUnitsType.Hours, 1);
    }

    protected TimeSpan oneWeekAroundTimeSpan() {

        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        int idx = 0;
        for ( ; idx < 3; ++idx) {
            if (cal1.get(java.util.Calendar.DATE) == 1) {
                break;
            }
            cal1.roll(java.util.Calendar.DATE, false);
        }
        cal1.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal1.clear(java.util.Calendar.MINUTE);
        cal1.clear(java.util.Calendar.SECOND);
        cal1.clear(java.util.Calendar.MILLISECOND);

        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        int currentDate = cal2.get(java.util.Calendar.DATE);
        int currentMonth = cal2.get(java.util.Calendar.MONTH);
        int total = 7 - idx - 1;
        for (idx = 0; idx < total; ++idx) {
            cal2.roll(java.util.Calendar.DATE, true);
            if (cal2.get(java.util.Calendar.DATE) < currentDate) {
                cal2.roll(java.util.Calendar.MONTH, true);
                if (cal2.get(java.util.Calendar.MONTH) < currentMonth) {
                    cal2.roll(java.util.Calendar.YEAR, true);
                }
            }
        }
        cal2.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal2.clear(java.util.Calendar.MINUTE);
        cal2.clear(java.util.Calendar.SECOND);
        cal2.clear(java.util.Calendar.MILLISECOND);

//        System.out.println("oneWeekAround: Begins: " + cal1.getTime() + " Ends: " + cal2.getTime());

        return new TimeSpan(cal1.getTime(), cal2.getTime());
    }

    protected TimeSpan oneDayPriorTimeSpan() {

        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        cal1.roll(java.util.Calendar.DATE, false);
        cal1.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal1.clear(java.util.Calendar.MINUTE);
        cal1.clear(java.util.Calendar.SECOND);
        cal1.clear(java.util.Calendar.MILLISECOND);

        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal2.setTime(cal1.getTime());
        cal2.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal2.set(java.util.Calendar.MINUTE, 59);
        cal2.set(java.util.Calendar.SECOND, 59);

//        System.out.println("oneDayPrior: Begins: " + cal1.getTime() + " Ends: " + cal2.getTime());

        return new TimeSpan(cal1.getTime(), cal2.getTime());
    }

    protected TimeSpan tomorrowOneHourTimeSpan() {

        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        cal1.roll(java.util.Calendar.DATE, true);
        cal1.clear(java.util.Calendar.MINUTE);
        cal1.clear(java.util.Calendar.SECOND);
        cal1.clear(java.util.Calendar.MILLISECOND);

        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal2.setTime(cal1.getTime());
        cal2.roll(java.util.Calendar.HOUR_OF_DAY, true);

        if (cal1.get(java.util.Calendar.HOUR_OF_DAY) > cal2.get(java.util.Calendar.HOUR_OF_DAY)) {
            cal2.roll(java.util.Calendar.DATE, true);
        }

//        System.out.println("tomorrowOneHour: Begins: " + cal1.getTime() + " Ends: " + cal2.getTime());

        return new TimeSpan(cal1.getTime(), cal2.getTime());
    }

    protected TimeSpan tomorrowThroughOneYearLaterTimeSpan() {

        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        cal1.roll(java.util.Calendar.DATE, true);
        cal1.clear(java.util.Calendar.MINUTE);
        cal1.clear(java.util.Calendar.SECOND);
        cal1.clear(java.util.Calendar.MILLISECOND);

        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal2.setTime(cal1.getTime());
        cal2.roll(java.util.Calendar.YEAR, true);

//        System.out.println("tomorrowThroughOneYearLater: Begins: " + cal1.getTime() + " Ends: " + cal2.getTime());

        return new TimeSpan(cal1.getTime(), cal2.getTime());
    }
}
