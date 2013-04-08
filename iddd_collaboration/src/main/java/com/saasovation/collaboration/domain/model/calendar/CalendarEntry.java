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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.saasovation.collaboration.domain.model.collaborator.Owner;
import com.saasovation.collaboration.domain.model.collaborator.Participant;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.EventSourcedRootEntity;

public class CalendarEntry extends EventSourcedRootEntity {

    private Alarm alarm;
    private CalendarEntryId calendarEntryId;
    private CalendarId calendarId;
    private String description;
    private Set<Participant> invitees;
    private String location;
    private Owner owner;
    private Repetition repetition;
    private Tenant tenant;
    private TimeSpan timeSpan;

    public CalendarEntry(List<DomainEvent> anEventStream, int aStreamVersion) {
        super(anEventStream, aStreamVersion);
    }

    public Alarm alarm() {
        return this.alarm;
    }

    public Set<Participant> allInvitees() {
        return Collections.unmodifiableSet(this.invitees());
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

    public String location() {
        return this.location;
    }

    public Owner owner() {
        return this.owner;
    }

    public Repetition repetition() {
        return this.repetition;
    }

    public void changeDescription(String aDescription) {
        if (aDescription != null) {
            aDescription = aDescription.trim();
            if (!aDescription.isEmpty() && !this.description().equals(aDescription)) {
                this.apply(new CalendarEntryDescriptionChanged(
                        this.tenant(), this.calendarId(), this.calendarEntryId(),
                        aDescription));
            }
        }
    }

    public void invite(Participant aParticipant) {
        this.assertArgumentNotNull(aParticipant, "The participant must be provided.");

        if (!this.invitees().contains(aParticipant)) {
            this.apply(new CalendarEntryParticipantInvited(
                    this.tenant(), this.calendarId(), this.calendarEntryId(),
                    aParticipant));
        }
    }

    public void relocate(String aLocation) {
        if (aLocation != null) {
            aLocation = aLocation.trim();
            if (!aLocation.isEmpty() && !this.location().equals(aLocation)) {
                this.apply(new CalendarEntryRelocated(
                        this.tenant(), this.calendarId(), this.calendarEntryId(),
                        aLocation));
            }
        }
    }

    public void reschedule(
            String aDescription,
            String aLocation,
            TimeSpan aTimeSpan,
            Repetition aRepetition,
            Alarm anAlarm) {

        this.assertArgumentNotNull(anAlarm, "The alarm must be provided.");
        this.assertArgumentNotNull(aRepetition, "The repetition must be provided.");
        this.assertArgumentNotNull(aTimeSpan, "The time span must be provided.");

        if (aRepetition.repeats().isDoesNotRepeat()) {
            aRepetition = Repetition.doesNotRepeatInstance(aTimeSpan.ends());
        }

        this.assertTimeSpans(aRepetition, aTimeSpan);

        this.changeDescription(aDescription);
        this.relocate(aLocation);

        this.apply(new CalendarEntryRescheduled(
                this.tenant(), this.calendarId(), this.calendarEntryId(),
                aTimeSpan, aRepetition, anAlarm));
    }

    public Tenant tenant() {
        return this.tenant;
    }

    public TimeSpan timeSpan() {
        return this.timeSpan;
    }

    public void uninvite(Participant aParticipant) {
        this.assertArgumentNotNull(aParticipant, "The participant must be provided.");

        if (this.invitees().contains(aParticipant)) {
            this.apply(new CalendarEntryParticipantUninvited(
                    this.tenant(), this.calendarId(), this.calendarEntryId(),
                    aParticipant));
        }
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            CalendarEntry typedObject = (CalendarEntry) anObject;
            equalObjects =
                this.tenant().equals(typedObject.tenant()) &&
                this.calendarId().equals(typedObject.calendarId()) &&
                this.calendarEntryId().equals(typedObject.calendarEntryId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
                + (5439 * 79)
                + this.tenant().hashCode()
                + this.calendarId().hashCode()
                + this.calendarEntryId().hashCode();

            return hashCodeValue;
    }

    @Override
    public String toString() {
        return "CalendarEntry [alarm=" + alarm + ", calendarEntryId=" + calendarEntryId + ", calendarId=" + calendarId
                + ", description=" + description + ", invitees=" + invitees + ", location=" + location + ", owner=" + owner
                + ", repetition=" + repetition + ", tenant=" + tenant + ", timeSpan=" + timeSpan + "]";
    }

    protected CalendarEntry(
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

        this();

        this.assertArgumentNotNull(anAlarm, "The alarm must be provided.");
        this.assertArgumentNotNull(aCalendarEntryId, "The calendar entry id must be provided.");
        this.assertArgumentNotNull(aCalendarId, "The calendar id must be provided.");
        this.assertArgumentNotEmpty(aDescription, "The description must be provided.");
        this.assertArgumentNotEmpty(aLocation, "The location must be provided.");
        this.assertArgumentNotNull(anOwner, "The owner must be provided.");
        this.assertArgumentNotNull(aRepetition, "The repetition must be provided.");
        this.assertArgumentNotNull(aTenant, "The tenant must be provided.");
        this.assertArgumentNotNull(aTimeSpan, "The time span must be provided.");

        if (aRepetition.repeats().isDoesNotRepeat()) {
            aRepetition = Repetition.doesNotRepeatInstance(aTimeSpan.ends());
        }

        this.assertTimeSpans(aRepetition, aTimeSpan);

        if (anInvitees == null) {
            anInvitees = new HashSet<Participant>(0);
        }

        this.apply(new CalendarEntryScheduled(aTenant, aCalendarId, aCalendarEntryId, aDescription,
                aLocation, anOwner, aTimeSpan, aRepetition, anAlarm, anInvitees));
    }

    protected CalendarEntry() {
        super();
    }

    protected void when(CalendarEntryDescriptionChanged anEvent) {
        this.setDescription(anEvent.description());
    }

    protected void when(CalendarEntryParticipantInvited anEvent) {
        this.invitees().add(anEvent.participant());
    }

    protected void when(CalendarEntryRelocated anEvent) {
        this.setLocation(anEvent.location());
    }

    protected void when(CalendarEntryRescheduled anEvent) {
        this.setAlarm(anEvent.alarm());
        this.setRepetition(anEvent.repetition());
        this.setTimeSpan(anEvent.timeSpan());
    }

    protected void when(CalendarEntryScheduled anEvent) {
        this.setAlarm(anEvent.alarm());
        this.setCalendarEntryId(anEvent.calendarEntryId());
        this.setCalendarId(anEvent.calendarId());
        this.setDescription(anEvent.description());
        this.setInvitees(anEvent.invitees());
        this.setLocation(anEvent.location());
        this.setOwner(anEvent.owner());
        this.setRepetition(anEvent.repetition());
        this.setTenant(anEvent.tenant());
        this.setTimeSpan(anEvent.timeSpan());
    }

    protected void when(CalendarEntryParticipantUninvited anEvent) {
        this.invitees().remove(anEvent.participant());
    }

    private void setAlarm(Alarm anAlarm) {
        this.alarm = anAlarm;
    }

    private void assertTimeSpans(Repetition aRepetition, TimeSpan aTimeSpan) {
        if (aRepetition.repeats().isDoesNotRepeat()) {
            this.assertArgumentEquals(
                    aTimeSpan.ends(),
                    aRepetition.ends(),
                    "Non-repeating entry must end with time span end.");
        } else {
            this.assertArgumentFalse(
                    aTimeSpan.ends().after(aRepetition.ends()),
                    "Time span must end when or before repetition ends.");
        }
    }

    private void setCalendarEntryId(CalendarEntryId aCalendarEntryId) {
        this.calendarEntryId = aCalendarEntryId;
    }

    private void setCalendarId(CalendarId aCalendarId) {
        this.calendarId = aCalendarId;
    }

    private void setDescription(String aDescription) {
        this.description = aDescription;
    }

    private Set<Participant> invitees() {
        return this.invitees;
    }

    private void setInvitees(Set<Participant> anInvitees) {
        this.invitees = anInvitees;
    }

    private void setLocation(String aLocation) {
        this.location = aLocation;
    }

    private void setOwner(Owner anOwner) {
        this.owner = anOwner;
    }

    private void setRepetition(Repetition aRepetition) {
        this.repetition = aRepetition;
    }

    private void setTenant(Tenant aTenant) {
        this.tenant = aTenant;
    }

    private void setTimeSpan(TimeSpan aTimeSpan) {
        this.timeSpan = aTimeSpan;
    }
}
