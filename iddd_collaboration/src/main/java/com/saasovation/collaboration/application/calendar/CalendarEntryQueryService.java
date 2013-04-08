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

import java.util.Collection;
import java.util.Date;

import javax.sql.DataSource;

import com.saasovation.collaboration.application.calendar.data.CalendarEntryData;
import com.saasovation.common.port.adapter.persistence.AbstractQueryService;
import com.saasovation.common.port.adapter.persistence.JoinOn;

public class CalendarEntryQueryService extends AbstractQueryService {

    public CalendarEntryQueryService(DataSource aDataSource) {
        super(aDataSource);
    }

    public CalendarEntryData calendarEntryDataOfId(String aTenantId, String aCalendarEntryId) {
        return this.queryObject(
                CalendarEntryData.class,
                "select "
                +  "entry.calendar_entry_id, entry.alarm_alarm_units, entry.alarm_alarm_units_type, "
                +  "entry.calendar_id, entry.description, entry.location, "
                +  "entry.owner_email_address, entry.owner_identity, entry.owner_name, "
                +  "entry.repetition_ends, entry.repetition_type, entry.tenant_id, "
                +  "entry.time_span_begins, entry.time_span_ends, "
                +  "invitee.calendar_entry_id as o_invitees_calendar_entry_id, "
                +  "invitee.participant_email_address as o_invitees_participant_email_address, "
                +  "invitee.participant_identity as o_invitees_participant_identity, "
                +  "invitee.participant_name as o_invitees_participant_name, "
                +  "invitee.tenant_id as o_invitees_tenant_id "
                + "from tbl_vw_calendar_entry as entry left outer join tbl_vw_calendar_entry_invitee as invitee "
                + " on entry.calendar_entry_id = invitee.calendar_entry_id "
                + "where entry.tenant_id = ? and entry.calendar_entry_id = ?",
                new JoinOn("calendar_entry_id", "o_invitees_calendar_entry_id"),
                aTenantId,
                aCalendarEntryId);
    }

    public Collection<CalendarEntryData> calendarEntryDataOfCalendarId(
            String aTenantId,
            String aCalendarId) {

        return this.queryObjects(
                CalendarEntryData.class,
                "select "
                +  "entry.calendar_entry_id, entry.alarm_alarm_units, entry.alarm_alarm_units_type, "
                +  "entry.calendar_id, entry.description, entry.location, "
                +  "entry.owner_email_address, entry.owner_identity, entry.owner_name, "
                +  "entry.repetition_ends, entry.repetition_type, entry.tenant_id, "
                +  "entry.time_span_begins, entry.time_span_ends, "
                +  "invitee.calendar_entry_id as o_invitees_calendar_entry_id, "
                +  "invitee.participant_email_address as o_invitees_participant_email_address, "
                +  "invitee.participant_identity as o_invitees_participant_identity, "
                +  "invitee.participant_name as o_invitees_participant_name, "
                +  "invitee.tenant_id as o_invitees_tenant_id "
                + "from tbl_vw_calendar_entry as entry left outer join tbl_vw_calendar_entry_invitee as invitee "
                + " on entry.calendar_entry_id = invitee.calendar_entry_id "
                + "where entry.tenant_id = ? and entry.calendar_id = ?",
                new JoinOn("calendar_entry_id", "o_invitees_calendar_entry_id"),
                aTenantId,
                aCalendarId);
    }

    public Collection<CalendarEntryData> timeSpanningCalendarEntries(
            String aTenantId,
            String aCalendarId,
            Date aTimeSpanBegins,
            Date aTimeSpanEnds) {

        return this.queryObjects(
                CalendarEntryData.class,
                "select "
                +  "entry.calendar_entry_id, entry.alarm_alarm_units, entry.alarm_alarm_units_type, "
                +  "entry.calendar_id, entry.description, entry.location, "
                +  "entry.owner_email_address, entry.owner_identity, entry.owner_name, "
                +  "entry.repetition_ends, entry.repetition_type, entry.tenant_id, "
                +  "entry.time_span_begins, entry.time_span_ends, "
                +  "invitee.calendar_entry_id as o_invitees_calendar_entry_id, "
                +  "invitee.participant_email_address as o_invitees_participant_email_address, "
                +  "invitee.participant_identity as o_invitees_participant_identity, "
                +  "invitee.participant_name as o_invitees_participant_name, "
                +  "invitee.tenant_id as o_invitees_tenant_id "
                + "from tbl_vw_calendar_entry as entry left outer join tbl_vw_calendar_entry_invitee as invitee "
                + " on entry.calendar_entry_id = invitee.calendar_entry_id "
                + "where entry.tenant_id = ? and entry.calendar_id = ? and "
                +  "((entry.time_span_begins between ? and ?) or "
                +  " (entry.repetition_ends between ? and ?))",
                new JoinOn("calendar_entry_id", "o_invitees_calendar_entry_id"),
                aTenantId,
                aCalendarId,
                aTimeSpanBegins,
                aTimeSpanEnds,
                aTimeSpanBegins,
                aTimeSpanEnds);
    }
}
