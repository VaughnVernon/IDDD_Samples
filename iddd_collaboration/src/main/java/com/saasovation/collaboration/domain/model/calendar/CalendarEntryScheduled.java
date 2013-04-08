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

import java.util.Date;
import java.util.Set;

import com.saasovation.collaboration.domain.model.collaborator.Owner;
import com.saasovation.collaboration.domain.model.collaborator.Participant;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEvent;

public class CalendarEntryScheduled implements DomainEvent {

    private Alarm alarm;
    private CalendarEntryId calendarEntryId;
    private CalendarId calendarId;
    private String description;
    private int eventVersion;
    private Set<Participant> invitees;
    private String location;
    private Date occurredOn;
    private Owner owner;
    private Repetition repetition;
    private Tenant tenant;
    private TimeSpan timeSpan;

    public CalendarEntryScheduled(
            Tenant aTenant,
            CalendarId aCalendarId,
            CalendarEntryId aCalendarEntryId,
            String aDescription,
            String aLocation,
            Owner anOwner,
            TimeSpan aTimeSpan,
            Repetition aRepetition,
            Alarm anAlarm,
            Set<Participant> anInvitees) {

        super();

        this.alarm = anAlarm;
        this.calendarEntryId = aCalendarEntryId;
        this.calendarId = aCalendarId;
        this.description = aDescription;
        this.eventVersion = 1;
        this.invitees = anInvitees;
        this.location = aLocation;
        this.occurredOn = new Date();
        this.owner = anOwner;
        this.repetition = aRepetition;
        this.tenant = aTenant;
        this.timeSpan = aTimeSpan;
    }

    public Alarm alarm() {
        return this.alarm;
    }

    public CalendarEntryId calendarEntryId() {
        return this.calendarEntryId;
    }

    public CalendarId calendarId() {
        return this.calendarId;
    }

    public String description() {
        return this.description;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    public Set<Participant> invitees() {
        return this.invitees;
    }

    public String location() {
        return this.location;
    }

    @Override
    public Date occurredOn() {
        return this.occurredOn;
    }

    public Owner owner() {
        return this.owner;
    }

    public Repetition repetition() {
        return this.repetition;
    }

    public Tenant tenant() {
        return this.tenant;
    }

    public TimeSpan timeSpan() {
        return this.timeSpan;
    }
}
