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

public class Calendar extends EventSourcedRootEntity {

    private CalendarId calendarId;
    private String description;
    private String name;
    private Owner owner;
    private Set<CalendarSharer> sharedWith;
    private Tenant tenant;

    public Calendar(
            Tenant aTenant,
            CalendarId aCalendarId,
            String aName,
            String aDescription,
            Owner anOwner,
            Set<CalendarSharer> aSharedWith) {

        this();

        this.assertArgumentNotNull(aTenant, "The tenant must be provided.");
        this.assertArgumentNotNull(aCalendarId, "The calendar id must be provided.");
        this.assertArgumentNotEmpty(aName, "The name must be provided.");
        this.assertArgumentNotEmpty(aDescription, "The description must be provided.");
        this.assertArgumentNotNull(anOwner, "The owner must be provided.");

        if (aSharedWith == null) {
            aSharedWith = new HashSet<CalendarSharer>(0);
        }

        this.apply(new CalendarCreated(aTenant, aCalendarId, aName,
                aDescription, anOwner, aSharedWith));
    }

    public Calendar(List<DomainEvent> anEventStream, int aStreamVersion) {
        super(anEventStream, aStreamVersion);
    }

    public Set<CalendarSharer> allSharedWith() {
        return Collections.unmodifiableSet(this.sharedWith());
    }

    public CalendarId calendarId() {
        return this.calendarId;
    }

    public void changeDescription(String aDescription) {
        this.assertArgumentNotEmpty(aDescription, "The description must be provided.");

        this.apply(new CalendarDescriptionChanged(
                this.tenant(), this.calendarId(), this.name(), aDescription));
    }

    public String description() {
        return this.description;
    }

    public String name() {
        return this.name;
    }

    public Owner owner() {
        return this.owner;
    }

    public void rename(String aName) {
        this.assertArgumentNotEmpty(aName, "The name must be provided.");

        this.apply(new CalendarRenamed(
                this.tenant(), this.calendarId(), aName, this.description()));
    }

    public CalendarEntry scheduleCalendarEntry(
            CalendarIdentityService aCalendarIdentityService,
            String aDescription,
            String aLocation,
            Owner anOwner,
            TimeSpan aTimeSpan,
            Repetition aRepetition,
            Alarm anAlarm,
            Set<Participant> anInvitees) {

        CalendarEntry calendarEntry =
                new CalendarEntry(
                        this.tenant(),
                        this.calendarId(),
                        aCalendarIdentityService.nextCalendarEntryId(),
                        aDescription,
                        aLocation,
                        anOwner,
                        aTimeSpan,
                        aRepetition,
                        anAlarm,
                        anInvitees);

        return calendarEntry;
    }

    public void shareCalendarWith(CalendarSharer aCalendarSharer) {
        this.assertArgumentNotNull(aCalendarSharer, "The calendar sharer must be provided.");

        if (!this.sharedWith().contains(aCalendarSharer)) {
            this.apply(new CalendarShared(this.tenant(), this.calendarId(),
                    this.name(), aCalendarSharer));
        }
    }

    public void unshareCalendarWith(CalendarSharer aCalendarSharer) {
        this.assertArgumentNotNull(aCalendarSharer, "The calendar sharer must be provided.");

        if (this.sharedWith().contains(aCalendarSharer)) {
            this.apply(new CalendarUnshared(this.tenant(), this.calendarId(),
                    this.name(), aCalendarSharer));
        }
    }

    public Tenant tenant() {
        return this.tenant;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Calendar typedObject = (Calendar) anObject;
            equalObjects =
                this.tenant().equals(typedObject.tenant()) &&
                this.calendarId().equals(typedObject.calendarId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
                + (87213 * 73)
                + this.tenant().hashCode()
                + this.calendarId().hashCode();

            return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Calendar [calendarId=" + calendarId + ", description=" + description + ", name=" + name + ", owner=" + owner
                + ", sharedWith=" + sharedWith + ", tenant=" + tenant + "]";
    }

    protected Calendar() {
        super();
    }

    protected void when(CalendarCreated anEvent) {
        this.setCalendarId(anEvent.calendarId());
        this.setDescription(anEvent.description());
        this.setName(anEvent.name());
        this.setOwner(anEvent.owner());
        this.setSharedWith(anEvent.sharedWith());
        this.setTenant(anEvent.tenant());
    }

    protected void when(CalendarDescriptionChanged anEvent) {
        this.setDescription(anEvent.description());
    }

    protected void when(CalendarRenamed anEvent) {
        this.setName(anEvent.name());
    }

    protected void when(CalendarShared anEvent) {
        this.sharedWith().add(anEvent.calendarSharer());
    }

    protected void when(CalendarUnshared anEvent) {
        this.sharedWith().remove(anEvent.calendarSharer());
    }

    private void setCalendarId(CalendarId calendarId) {
        this.calendarId = calendarId;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setOwner(Owner owner) {
        this.owner = owner;
    }

    private Set<CalendarSharer> sharedWith() {
        return this.sharedWith;
    }

    private void setSharedWith(Set<CalendarSharer> sharedWith) {
        this.sharedWith = sharedWith;
    }

    private void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
