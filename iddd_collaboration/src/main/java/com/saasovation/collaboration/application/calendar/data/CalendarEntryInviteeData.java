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

public class CalendarEntryInviteeData {

    private String calendarEntryId;
    private String participantEmailAddress;
    private String participantIdentity;
    private String participantName;
    private String tenantId;

    public CalendarEntryInviteeData() {
        super();
    }

    public String getCalendarEntryId() {
        return this.calendarEntryId;
    }

    public void setCalendarEntryId(String calendarEntryId) {
        this.calendarEntryId = calendarEntryId;
    }

    public String getParticipantEmailAddress() {
        return this.participantEmailAddress;
    }

    public void setParticipantEmailAddress(String participantEmailAddress) {
        this.participantEmailAddress = participantEmailAddress;
    }

    public String getParticipantIdentity() {
        return this.participantIdentity;
    }

    public void setParticipantIdentity(String participantIdentity) {
        this.participantIdentity = participantIdentity;
    }

    public String getParticipantName() {
        return this.participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
