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

import javax.sql.DataSource;

import com.saasovation.collaboration.application.calendar.data.CalendarData;
import com.saasovation.common.port.adapter.persistence.AbstractQueryService;
import com.saasovation.common.port.adapter.persistence.JoinOn;

public class CalendarQueryService extends AbstractQueryService {

    public CalendarQueryService(DataSource aDataSource) {
        super(aDataSource);
    }

    public Collection<CalendarData> allCalendarsDataOfTenant(String aTenantId) {

        return this.queryObjects(
                CalendarData.class,
                "select "
                +  "cal.calendar_id, cal.description, cal.name, cal.owner_email_address, "
                +  "cal.owner_identity, cal.owner_name, cal.tenant_id, "
                +  "sharer.calendar_id as o_sharers_calendar_id, "
                +  "sharer.participant_email_address as o_sharers_participant_email_address, "
                +  "sharer.participant_identity as o_sharers_participant_identity, "
                +  "sharer.participant_name as o_sharers_participant_name, "
                +  "sharer.tenant_id as o_sharers_tenant_id "
                + "from tbl_vw_calendar as cal left outer join tbl_vw_calendar_sharer as sharer "
                + " on cal.calendar_id = sharer.calendar_id "
                + "where (cal.tenant_id = ?)",
                new JoinOn("calendar_id", "o_sharers_calendar_id"),
                aTenantId);
    }

    public CalendarData calendarDataOfId(String aTenantId, String aCalendarId) {
        return this.queryObject(
                CalendarData.class,
                "select "
                +  "cal.calendar_id, cal.description, cal.name, cal.owner_email_address, "
                +  "cal.owner_identity, cal.owner_name, cal.tenant_id, "
                +  "sharer.calendar_id as o_sharers_calendar_id, "
                +  "sharer.participant_email_address as o_sharers_participant_email_address, "
                +  "sharer.participant_identity as o_sharers_participant_identity, "
                +  "sharer.participant_name as o_sharers_participant_name, "
                +  "sharer.tenant_id as o_sharers_tenant_id "
                + "from tbl_vw_calendar as cal left outer join tbl_vw_calendar_sharer as sharer "
                + " on cal.calendar_id = sharer.calendar_id "
                + "where (cal.tenant_id = ? and cal.calendar_id = ?)",
                new JoinOn("calendar_id", "o_sharers_calendar_id"),
                aTenantId,
                aCalendarId);
    }
}
