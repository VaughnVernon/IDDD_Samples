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

import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEvent;

public class CalendarDescriptionChanged extends DomainEvent {

    private CalendarId calendarId;
    private String description;
    private String name;
    private Tenant tenant;

    public CalendarDescriptionChanged(
	    Tenant aTenant,
	    CalendarId aCalendarId,
	    String aName,
	    String aDescription) {

	super();

	this.calendarId = aCalendarId;
	this.description = aDescription;
	this.name = aName;
	this.tenant = aTenant;
    }

    public CalendarId calendarId() {
	return this.calendarId;
    }

    public String description() {
	return this.description;
    }

    public String name() {
	return this.name;
    }

    public Tenant tenant() {
	return this.tenant;
    }
}
