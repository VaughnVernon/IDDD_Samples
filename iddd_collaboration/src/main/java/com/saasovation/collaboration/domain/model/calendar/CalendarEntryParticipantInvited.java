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

import com.saasovation.collaboration.domain.model.collaborator.Participant;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEvent;

public class CalendarEntryParticipantInvited extends DomainEvent {

    private CalendarEntryId calendarEntryId;
    private CalendarId calendarId;
    private Participant participant;
    private Tenant tenant;

    public CalendarEntryParticipantInvited(
	    Tenant aTenant,
	    CalendarId aCalendarId,
	    CalendarEntryId aCalendarEntryId,
	    Participant aParticipant) {

	super();

	this.calendarEntryId = aCalendarEntryId;
	this.calendarId = aCalendarId;
	this.participant = aParticipant;
	this.tenant = aTenant;
    }

    public CalendarEntryId calendarEntryId() {
	return this.calendarEntryId;
    }

    public CalendarId calendarId() {
	return this.calendarId;
    }

    public Participant participant() {
	return this.participant;
    }

    public Tenant tenant() {
	return this.tenant;
    }
}
