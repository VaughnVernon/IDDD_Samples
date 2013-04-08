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

package com.saasovation.collaboration.application;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.saasovation.collaboration.StorageCleaner;
import com.saasovation.collaboration.application.calendar.CalendarApplicationService;
import com.saasovation.collaboration.application.calendar.CalendarEntryApplicationService;
import com.saasovation.collaboration.application.calendar.CalendarEntryQueryService;
import com.saasovation.collaboration.application.calendar.CalendarQueryService;
import com.saasovation.collaboration.application.forum.DiscussionApplicationService;
import com.saasovation.collaboration.application.forum.DiscussionQueryService;
import com.saasovation.collaboration.application.forum.ForumApplicationService;
import com.saasovation.collaboration.application.forum.ForumQueryService;
import com.saasovation.collaboration.application.forum.PostApplicationService;
import com.saasovation.collaboration.application.forum.PostQueryService;
import com.saasovation.collaboration.domain.model.DomainRegistry;
import com.saasovation.collaboration.domain.model.calendar.Alarm;
import com.saasovation.collaboration.domain.model.calendar.AlarmUnitsType;
import com.saasovation.collaboration.domain.model.calendar.Calendar;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntry;
import com.saasovation.collaboration.domain.model.calendar.CalendarSharer;
import com.saasovation.collaboration.domain.model.calendar.RepeatType;
import com.saasovation.collaboration.domain.model.calendar.Repetition;
import com.saasovation.collaboration.domain.model.calendar.TimeSpan;
import com.saasovation.collaboration.domain.model.collaborator.Author;
import com.saasovation.collaboration.domain.model.collaborator.CollaboratorService;
import com.saasovation.collaboration.domain.model.collaborator.Creator;
import com.saasovation.collaboration.domain.model.collaborator.Moderator;
import com.saasovation.collaboration.domain.model.collaborator.Owner;
import com.saasovation.collaboration.domain.model.collaborator.Participant;
import com.saasovation.collaboration.domain.model.forum.Discussion;
import com.saasovation.collaboration.domain.model.forum.Forum;
import com.saasovation.collaboration.domain.model.forum.Post;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.port.adapter.persistence.ConnectionProvider;

public abstract class ApplicationTest extends TestCase {

    protected ApplicationContext applicationContext;
    protected CalendarApplicationService calendarApplicationService;
    protected CalendarEntryApplicationService calendarEntryApplicationService;
    protected CalendarEntryQueryService calendarEntryQueryService;
    protected CalendarQueryService calendarQueryService;
    protected CollaboratorService collaboratorService;
    protected DataSource dataSource;
    protected DiscussionApplicationService discussionApplicationService;
    protected DiscussionQueryService discussionQueryService;
    protected ForumApplicationService forumApplicationService;
    protected ForumQueryService forumQueryService;
    protected PostApplicationService postApplicationService;
    protected PostQueryService postQueryService;

    private StorageCleaner storageCleaner;

    protected ApplicationTest() {
        super();
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

        CalendarEntry calendarEntry =
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

    protected CalendarEntry[] calendarEntryAggregates() {

        Calendar calendar = this.calendarAggregate();

        DomainRegistry.calendarRepository().save(calendar);

        Set<Participant> invitees = new TreeSet<Participant>();
        invitees.add(new Participant("zoe", "Zoe Doe", "zoe@saasovation.com"));

        CalendarEntry calendarEntry1 =
            calendar.scheduleCalendarEntry(
                    DomainRegistry.calendarIdentityService(),
                    "A Doctor Checkup",
                    "Family Practice Offices",
                    new Owner("jdoe", "John Doe", "jdoe@saasovation.com"),
                    this.daysFromNowOneHourTimeSpan(1),
                    Repetition.doesNotRepeatInstance(new Date()),
                    this.oneHourBeforeAlarm(),
                    invitees);

        CalendarEntry calendarEntry2 =
            calendar.scheduleCalendarEntry(
                    DomainRegistry.calendarIdentityService(),
                    "A Break Replacement",
                    "Breaks R Us",
                    new Owner("jdoe", "John Doe", "jdoe@saasovation.com"),
                    this.daysFromNowOneHourTimeSpan(2),
                    Repetition.doesNotRepeatInstance(new Date()),
                    this.oneHourBeforeAlarm(),
                    invitees);

        CalendarEntry calendarEntry3 =
            calendar.scheduleCalendarEntry(
                    DomainRegistry.calendarIdentityService(),
                    "Dinner with Family",
                    "Burritos Grandes",
                    new Owner("jdoe", "John Doe", "jdoe@saasovation.com"),
                    this.daysFromNowOneHourTimeSpan(3),
                    Repetition.doesNotRepeatInstance(new Date()),
                    this.oneHourBeforeAlarm(),
                    invitees);

        return new CalendarEntry[] { calendarEntry1, calendarEntry2, calendarEntry3 };
    }

    protected Calendar[] calendarAggregates() {

        Tenant tenant = new Tenant("01234567");

        Set<CalendarSharer> invitees = new TreeSet<CalendarSharer>();
        invitees.add(new CalendarSharer(new Participant("zoe", "Zoe Doe", "zoe@saasovation.com")));

        Calendar calendar1 =
                new Calendar(
                        tenant,
                        DomainRegistry.calendarRepository().nextIdentity(),
                        "John Doe's Calendar",
                        "John Doe's everyday work calendar.",
                        new Owner("jdoe", "John Doe", "jdoe@saasovation.com"),
                        invitees);

        Calendar calendar2 =
                new Calendar(
                        tenant,
                        DomainRegistry.calendarRepository().nextIdentity(),
                        "Zoe Doe's Calendar",
                        "Zoe Doe's awesome person calendar.",
                        new Owner("zoe", "Zoe Doe", "zoe@saasovation.com"),
                        invitees);

        Calendar calendar3 =
                new Calendar(
                        tenant,
                        DomainRegistry.calendarRepository().nextIdentity(),
                        "Joe Smith's Calendar",
                        "Joe Smith's know-everything calendar.",
                        new Owner("joe", "Joe Smith", "joe@saasovation.com"),
                        invitees);

        return new Calendar[] { calendar1, calendar2, calendar3 };
    }

    protected Discussion discussionAggregate(Forum aForum) {

        Discussion discussion = aForum.startDiscussionFor(
                DomainRegistry.forumIdentityService(),
                new Author("jdoe", "John Doe", "jdoe@saasovation.com"),
                "All About DDD",
                UUID.randomUUID().toString().toUpperCase());

        return discussion;
    }

    protected Discussion[] discussionAggregates(Forum aForum) {

        Discussion discussion1 = aForum.startDiscussionFor(
                DomainRegistry.forumIdentityService(),
                new Author("jdoe", "John Doe", "jdoe@saasovation.com"),
                "All About DDD",
                UUID.randomUUID().toString().toUpperCase());

        Discussion discussion2 = aForum.startDiscussionFor(
                DomainRegistry.forumIdentityService(),
                new Author("zoe", "Zoe Doe", "zoe@saasovation.com"),
                "I Already Know That, Too",
                UUID.randomUUID().toString().toUpperCase());

        Discussion discussion3 = aForum.startDiscussionFor(
                DomainRegistry.forumIdentityService(),
                new Author("joe", "Joe Smith", "joe@saasovation.com"),
                "I've Forgotten More Than Zoe Knows",
                UUID.randomUUID().toString().toUpperCase());

        return new Discussion[] { discussion1, discussion2, discussion3 };
    }

    protected Forum forumAggregate() {

        Tenant tenant = new Tenant("01234567");

        Forum forum =
            new Forum(
                    tenant,
                    DomainRegistry.forumRepository().nextIdentity(),
                    new Creator("jdoe", "John Doe", "jdoe@saasovation.com"),
                    new Moderator("jdoe", "John Doe", "jdoe@saasovation.com"),
                    "John Doe Does DDD",
                    "A set of discussions about DDD for anonymous developers.",
                    UUID.randomUUID().toString().toUpperCase());

        return forum;
    }

    protected Forum[] forumAggregates() {

        Tenant tenant = new Tenant("01234567");

        Forum forum1 =
            new Forum(
                    tenant,
                    DomainRegistry.forumRepository().nextIdentity(),
                    new Creator("jdoe", "John Doe", "jdoe@saasovation.com"),
                    new Moderator("jdoe", "John Doe", "jdoe@saasovation.com"),
                    "John Doe Does DDD",
                    "A set of discussions about DDD for anonymous developers.",
                    UUID.randomUUID().toString().toUpperCase());

        Forum forum2 =
                new Forum(
                        tenant,
                        DomainRegistry.forumRepository().nextIdentity(),
                        new Creator("zdoe", "Zoe Doe", "zoe@saasovation.com"),
                        new Moderator("zoe", "Zoe Doe", "jdoe@saasovation.com"),
                        "Zoe Doe Knows DDD",
                        "Discussions about how ubiquitous Zoe's knows is.",
                        UUID.randomUUID().toString().toUpperCase());

        Forum forum3 =
                new Forum(
                        tenant,
                        DomainRegistry.forumRepository().nextIdentity(),
                        new Creator("joe", "Joe Smith", "joe@saasovation.com"),
                        new Moderator("joe", "Joe Smith", "joe@saasovation.com"),
                        "Joe Owns DDD",
                        "Discussions about Joe's Values.",
                        UUID.randomUUID().toString().toUpperCase());

        return new Forum[] { forum1, forum2, forum3 };
    }

    protected Post postAggregate(Discussion aDiscussion) {

        Post post = aDiscussion.post(
                DomainRegistry.forumIdentityService(),
                new Author("jdoe", "John Doe", "jdoe@saasovation.com"),
                "I Am All About DDD",
                "That's what I'm talk'n about.");

        return post;
    }

    protected Post[] postAggregates(Discussion aDiscussion) {

        Post post1 = aDiscussion.post(
                DomainRegistry.forumIdentityService(),
                new Author("jdoe", "John Doe", "jdoe@saasovation.com"),
                "I Am All About DDD",
                "That's what I'm talk'n about.");

        Post post2 = aDiscussion.post(
                DomainRegistry.forumIdentityService(),
                new Author("zoe", "Zoe Doe", "zoe@saasovation.com"),
                "RE: I Am All About DDD",
                "No, no, no. That's what *I'm* talk'n about.");

        Post post3 = aDiscussion.post(
                DomainRegistry.forumIdentityService(),
                new Author("joe", "Joe Smith", "joe@saasovation.com"),
                "RE: I Am All About DDD",
                "Did I mention that I've forgotten more than you will ever know?");

        return new Post[] { post1, post2, post3 };
    }

    protected Repetition weeklyRepetition() {

        return new Repetition(
                RepeatType.Weekly,
                this.tomorrowThroughOneYearLaterTimeSpan().ends());
    }

    protected Alarm oneHourBeforeAlarm() {

        return new Alarm(AlarmUnitsType.Hours, 1);
    }

    protected Date beginningOfDay(Date aDate) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(aDate);
        cal.clear(java.util.Calendar.HOUR_OF_DAY);
        cal.clear(java.util.Calendar.MINUTE);
        cal.clear(java.util.Calendar.SECOND);
        cal.clear(java.util.Calendar.MILLISECOND);

        return cal.getTime();
    }

    protected Date endOfDay(Date aDate) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(aDate);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);
        cal.clear(java.util.Calendar.MILLISECOND);

        return cal.getTime();
    }

    protected TimeSpan daysFromNowOneHourTimeSpan(int aNumberOfDays) {

        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        for (int idx = 0; idx < aNumberOfDays; ++idx) {
            cal1.roll(java.util.Calendar.DATE, true);
        }

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

    protected void setUp() throws Exception {

        System.out.println(">>>>>>>>>>>>>>>>>>>> " + this.getName());

        DomainEventPublisher.instance().reset();

        if (applicationContext == null) {

            // order of load is important. the test beans in
            // applicationContext-collaboration-test.xml will
            // replace the standard ones in the standard
            // applicationContext-collaboration.xml definitions.
            // allows for mocking some heavy interfaces.

            applicationContext =
                    new ClassPathXmlApplicationContext(
                            new String[] {
                                    "applicationContext-collaboration.xml",
                                    "applicationContext-collaboration-test.xml" });
        }

        if (dataSource == null) {
            dataSource = (DataSource) applicationContext.getBean("collaborationDataSource");
        }

        calendarApplicationService = (CalendarApplicationService) applicationContext.getBean("calendarApplicationService");
        calendarQueryService = (CalendarQueryService) applicationContext.getBean("calendarQueryService");

        calendarEntryApplicationService = (CalendarEntryApplicationService) applicationContext.getBean("calendarEntryApplicationService");
        calendarEntryQueryService = (CalendarEntryQueryService) applicationContext.getBean("calendarEntryQueryService");

        collaboratorService = (CollaboratorService) applicationContext.getBean("collaboratorService");

        discussionApplicationService = (DiscussionApplicationService) applicationContext.getBean("discussionApplicationService");
        discussionQueryService = (DiscussionQueryService) applicationContext.getBean("discussionQueryService");

        forumApplicationService = (ForumApplicationService) applicationContext.getBean("forumApplicationService");
        forumQueryService = (ForumQueryService) applicationContext.getBean("forumQueryService");

        postApplicationService = (PostApplicationService) applicationContext.getBean("postApplicationService");
        postQueryService = (PostQueryService) applicationContext.getBean("postQueryService");

        storageCleaner = new StorageCleaner(this.dataSource);

        super.setUp();
    }

    protected void tearDown() throws Exception {

        storageCleaner.clean();

        ConnectionProvider.closeConnection();

        System.out.println("<<<<<<<<<<<<<<<<<<<< (done)");

        super.tearDown();
    }
}
