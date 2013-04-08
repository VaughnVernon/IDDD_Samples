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

import com.saasovation.collaboration.application.calendar.data.CalendarCommandResult;
import com.saasovation.collaboration.domain.model.calendar.Alarm;
import com.saasovation.collaboration.domain.model.calendar.AlarmUnitsType;
import com.saasovation.collaboration.domain.model.calendar.Calendar;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntry;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntryRepository;
import com.saasovation.collaboration.domain.model.calendar.CalendarId;
import com.saasovation.collaboration.domain.model.calendar.CalendarIdentityService;
import com.saasovation.collaboration.domain.model.calendar.CalendarRepository;
import com.saasovation.collaboration.domain.model.calendar.CalendarSharer;
import com.saasovation.collaboration.domain.model.calendar.RepeatType;
import com.saasovation.collaboration.domain.model.calendar.Repetition;
import com.saasovation.collaboration.domain.model.calendar.TimeSpan;
import com.saasovation.collaboration.domain.model.collaborator.CollaboratorService;
import com.saasovation.collaboration.domain.model.collaborator.Owner;
import com.saasovation.collaboration.domain.model.collaborator.Participant;
import com.saasovation.collaboration.domain.model.tenant.Tenant;

public class CalendarApplicationService {

    private CalendarRepository calendarRepository;
    private CalendarEntryRepository calendarEntryRepository;
    private CalendarIdentityService calendarIdentityService;
    private CollaboratorService collaboratorService;

    public CalendarApplicationService(
            CalendarRepository aCalendarRepository,
            CalendarEntryRepository aCalendarEntryRepository,
            CalendarIdentityService aCalendarIdentityService,
            CollaboratorService aCollaboratorService) {

        super();

        this.calendarRepository = aCalendarRepository;
        this.calendarEntryRepository = aCalendarEntryRepository;
        this.calendarIdentityService = aCalendarIdentityService;
        this.collaboratorService = aCollaboratorService;
    }

    public void changeCalendarDescription(
            String aTenantId,
            String aCalendarId,
            String aDescription) {

        Tenant tenant = new Tenant(aTenantId);

        Calendar calendar =
                this.calendarRepository()
                    .calendarOfId(
                            tenant,
                            new CalendarId(aCalendarId));

        calendar.changeDescription(aDescription);

        this.calendarRepository().save(calendar);
    }

    public void createCalendar(
            String aTenantId,
            String aName,
            String aDescription,
            String anOwnerId,
            Set<String> aParticipantsToSharedWith,
            CalendarCommandResult aCalendarCommandResult) {

        Tenant tenant = new Tenant(aTenantId);

        Owner owner = this.collaboratorService().ownerFrom(tenant, anOwnerId);

        Set<CalendarSharer> sharers = this.sharersFrom(tenant, aParticipantsToSharedWith);

        Calendar calendar =
                new Calendar(
                        tenant,
                        this.calendarRepository.nextIdentity(),
                        aName,
                        aDescription,
                        owner,
                        sharers);

        this.calendarRepository().save(calendar);

        aCalendarCommandResult.resultingCalendarId(calendar.calendarId().id());
    }

    public void renameCalendar(
            String aTenantId,
            String aCalendarId,
            String aName) {

        Tenant tenant = new Tenant(aTenantId);

        Calendar calendar =
                this.calendarRepository()
                    .calendarOfId(
                            tenant,
                            new CalendarId(aCalendarId));

        calendar.rename(aName);

        this.calendarRepository().save(calendar);
    }

    public void scheduleCalendarEntry(
            String aTenantId,
            String aCalendarId,
            String aDescription,
            String aLocation,
            String anOwnerId,
            Date aTimeSpanBegins,
            Date aTimeSpanEnds,
            String aRepeatType,
            Date aRepeatEndsOnDate,
            String anAlarmType,
            int anAlarmUnits,
            Set<String> aParticipantsToInvite,
            CalendarCommandResult aCalendarCommandResult) {

        Tenant tenant = new Tenant(aTenantId);

        Calendar calendar =
                this.calendarRepository()
                    .calendarOfId(
                            tenant,
                            new CalendarId(aCalendarId));

        CalendarEntry calendarEntry =
                calendar.scheduleCalendarEntry(
                    this.calendarIdentityService(),
                    aDescription,
                    aLocation,
                    this.collaboratorService().ownerFrom(tenant, anOwnerId),
                    new TimeSpan(aTimeSpanBegins, aTimeSpanEnds),
                    new Repetition(RepeatType.valueOf(aRepeatType), aRepeatEndsOnDate),
                    new Alarm(AlarmUnitsType.valueOf(anAlarmType), anAlarmUnits),
                    this.inviteesFrom(tenant, aParticipantsToInvite));

        this.calendarEntryRepository().save(calendarEntry);

        aCalendarCommandResult.resultingCalendarId(aCalendarId);
        aCalendarCommandResult.resultingCalendarEntryId(calendarEntry.calendarEntryId().id());
    }

    public void shareCalendarWith(
            String aTenantId,
            String aCalendarId,
            Set<String> aParticipantsToSharedWith) {

        Tenant tenant = new Tenant(aTenantId);

        Calendar calendar =
                this.calendarRepository()
                    .calendarOfId(
                            tenant,
                            new CalendarId(aCalendarId));

        for (CalendarSharer sharer : this.sharersFrom(tenant, aParticipantsToSharedWith)) {
            calendar.shareCalendarWith(sharer);
        }

        this.calendarRepository().save(calendar);
    }

    public void unshareCalendarWith(
            String aTenantId,
            String aCalendarId,
            Set<String> aParticipantsToUnsharedWith) {

        Tenant tenant = new Tenant(aTenantId);

        Calendar calendar =
                this.calendarRepository()
                    .calendarOfId(
                            tenant,
                            new CalendarId(aCalendarId));

        for (CalendarSharer sharer : this.sharersFrom(tenant, aParticipantsToUnsharedWith)) {
            calendar.unshareCalendarWith(sharer);
        }

        this.calendarRepository().save(calendar);
    }

    private CalendarRepository calendarRepository() {
        return this.calendarRepository;
    }

    private CalendarEntryRepository calendarEntryRepository() {
        return this.calendarEntryRepository;
    }

    private CalendarIdentityService calendarIdentityService() {
        return this.calendarIdentityService;
    }

    private CollaboratorService collaboratorService() {
        return this.collaboratorService;
    }

    private Set<Participant> inviteesFrom(
            Tenant aTenant,
            Set<String> aParticipantsToInvite) {

        Set<Participant> invitees = new HashSet<Participant>();

        for (String participatnId : aParticipantsToInvite) {
            Participant participant =
                    this.collaboratorService().participantFrom(aTenant, participatnId);

            invitees.add(participant);
        }

        return invitees;
    }

    private Set<CalendarSharer> sharersFrom(
            Tenant aTenant,
            Set<String> aParticipantsToSharedWith) {

        Set<CalendarSharer> sharers =
                new HashSet<CalendarSharer>(aParticipantsToSharedWith.size());

        for (String participatnId : aParticipantsToSharedWith) {
            Participant participant =
                    this.collaboratorService().participantFrom(aTenant, participatnId);

            sharers.add(new CalendarSharer(participant));
        }

        return sharers;
    }
}
