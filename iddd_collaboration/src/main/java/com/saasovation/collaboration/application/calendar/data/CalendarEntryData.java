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

package com.saasovation.collaboration.application.calendar.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CalendarEntryData {

    private int alarmAlarmUnits;
    private String alarmAlarmUnitsType;
    private String calendarEntryId;
    private String calendarId;
    private String description;
    private Set<CalendarEntryInviteeData> invitees;
    private String location;
    private String ownerEmailAddress;
    private String ownerIdentity;
    private String ownerName;
    private Date repetitionEnds;
    private String repetitionType;
    private String tenantId;
    private Date timeSpanBegins;
    private Date timeSpanEnds;

    public CalendarEntryData() {
        super();

        this.setInvitees(new HashSet<CalendarEntryInviteeData>(0));
    }

    public int getAlarmAlarmUnits() {
        return this.alarmAlarmUnits;
    }

    public void setAlarmAlarmUnits(int alarmAlarmUnits) {
        this.alarmAlarmUnits = alarmAlarmUnits;
    }

    public String getAlarmAlarmUnitsType() {
        return this.alarmAlarmUnitsType;
    }

    public void setAlarmAlarmUnitsType(String alarmAlarmUnitsType) {
        this.alarmAlarmUnitsType = alarmAlarmUnitsType;
    }

    public String getCalendarEntryId() {
        return this.calendarEntryId;
    }

    public void setCalendarEntryId(String calendarEntryId) {
        this.calendarEntryId = calendarEntryId;
    }

    public String getCalendarId() {
        return this.calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<CalendarEntryInviteeData> getInvitees() {
        return this.invitees;
    }

    public void setInvitees(Set<CalendarEntryInviteeData> invitees) {
        this.invitees = invitees;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerEmailAddress() {
        return this.ownerEmailAddress;
    }

    public void setOwnerEmailAddress(String ownerEmailAddress) {
        this.ownerEmailAddress = ownerEmailAddress;
    }

    public String getOwnerIdentity() {
        return this.ownerIdentity;
    }

    public void setOwnerIdentity(String ownerIdentity) {
        this.ownerIdentity = ownerIdentity;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Date getRepetitionEnds() {
        return this.repetitionEnds;
    }

    public void setRepetitionEnds(Date repetitionEnds) {
        this.repetitionEnds = repetitionEnds;
    }

    public String getRepetitionType() {
        return this.repetitionType;
    }

    public void setRepetitionType(String repetitionType) {
        this.repetitionType = repetitionType;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Date getTimeSpanBegins() {
        return this.timeSpanBegins;
    }

    public void setTimeSpanBegins(Date timeSpanBegins) {
        this.timeSpanBegins = timeSpanBegins;
    }

    public Date getTimeSpanEnds() {
        return this.timeSpanEnds;
    }

    public void setTimeSpanEnds(Date timeSpanEnds) {
        this.timeSpanEnds = timeSpanEnds;
    }

}
