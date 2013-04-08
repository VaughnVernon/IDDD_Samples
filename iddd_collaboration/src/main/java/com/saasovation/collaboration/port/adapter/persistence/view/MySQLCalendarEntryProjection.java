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

package com.saasovation.collaboration.port.adapter.persistence.view;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.saasovation.collaboration.domain.model.calendar.CalendarEntryDescriptionChanged;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntryId;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntryParticipantInvited;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntryParticipantUninvited;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntryRelocated;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntryRescheduled;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntryScheduled;
import com.saasovation.collaboration.domain.model.collaborator.Participant;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.event.sourcing.DispatchableDomainEvent;
import com.saasovation.common.event.sourcing.EventDispatcher;
import com.saasovation.common.port.adapter.persistence.AbstractProjection;
import com.saasovation.common.port.adapter.persistence.ConnectionProvider;

public class MySQLCalendarEntryProjection
        extends AbstractProjection
        implements EventDispatcher {

    private static final Class<?> understoodEventTypes[] = {
        CalendarEntryDescriptionChanged.class,
        CalendarEntryParticipantInvited.class,
        CalendarEntryParticipantUninvited.class,
        CalendarEntryRelocated.class,
        CalendarEntryRescheduled.class,
        CalendarEntryScheduled.class
    };

    public MySQLCalendarEntryProjection(EventDispatcher aParentEventDispatcher) {
        super();

        aParentEventDispatcher.registerEventDispatcher(this);
    }

    @Override
    public void dispatch(DispatchableDomainEvent aDispatchableDomainEvent) {
        this.projectWhen(aDispatchableDomainEvent);
    }

    @Override
    public void registerEventDispatcher(EventDispatcher anEventDispatcher) {
        throw new UnsupportedOperationException("Cannot register additional dispatchers.");
    }

    @Override
    public boolean understands(DispatchableDomainEvent aDispatchableDomainEvent) {
        return this.understandsAnyOf(
                aDispatchableDomainEvent.domainEvent().getClass(),
                understoodEventTypes);
    }

    protected void when(CalendarEntryDescriptionChanged anEvent) throws Exception {
        Connection connection = ConnectionProvider.connection();

        PreparedStatement statement =
                connection.prepareStatement(
                        "update tbl_vw_calendar_entry set description=? "
                        + " where calendar_entry_id = ?");

        statement.setString(1, anEvent.description());
        statement.setString(2, anEvent.calendarEntryId().id());

        this.execute(statement);
    }

    protected void when(CalendarEntryParticipantInvited anEvent) throws Exception {
        this.insertInvitee(anEvent.tenant(), anEvent.calendarEntryId(), anEvent.participant());
    }

    protected void when(CalendarEntryParticipantUninvited anEvent) throws Exception {
        Connection connection = ConnectionProvider.connection();

        PreparedStatement statement =
                connection.prepareStatement(
                        "delete from tbl_vw_calendar_entry_invitee "
                        + "where tenant_id = ? and calendar_entry_id = ? and participant_identity = ?");

        statement.setString(1, anEvent.tenant().id());
        statement.setString(2, anEvent.calendarEntryId().id());
        statement.setString(3, anEvent.participant().identity());

        this.execute(statement);
    }

    protected void when(CalendarEntryRelocated anEvent) throws Exception {
        Connection connection = ConnectionProvider.connection();

        PreparedStatement statement =
                connection.prepareStatement(
                        "update tbl_vw_calendar_entry set location=? "
                        + " where calendar_entry_id = ?");

        statement.setString(1, anEvent.location());
        statement.setString(2, anEvent.calendarEntryId().id());

        this.execute(statement);
    }

    protected void when(CalendarEntryRescheduled anEvent) throws Exception {
        Connection connection = ConnectionProvider.connection();

        PreparedStatement statement =
                connection.prepareStatement(
                        "update tbl_vw_calendar_entry "
                        + "set alarm_alarm_units = ?, alarm_alarm_units_type = ?, "
                        + "repetition_ends = ?, repetition_type = ?, "
                        + "time_span_begins = ?, time_span_ends = ? "
                        + " where tenant_id = ? and calendar_entry_id = ?");

        statement.setInt(1, anEvent.alarm().alarmUnits());
        statement.setString(2, anEvent.alarm().alarmUnitsType().name());
        statement.setDate(3, new java.sql.Date(anEvent.repetition().ends().getTime()));
        statement.setString(4, anEvent.repetition().repeats().name());
        statement.setDate(5, new java.sql.Date(anEvent.timeSpan().begins().getTime()));
        statement.setDate(6, new java.sql.Date(anEvent.timeSpan().ends().getTime()));
        statement.setString(7, anEvent.tenant().id());
        statement.setString(8, anEvent.calendarEntryId().id());

        this.execute(statement);
    }

    protected void when(CalendarEntryScheduled anEvent) throws Exception {
        Connection connection = ConnectionProvider.connection();

        // idempotent operation
        if (this.exists(
                "select calendar_entry_id from tbl_vw_calendar_entry "
                    + "where tenant_id = ? and calendar_entry_id = ?",
                anEvent.tenant().id(),
                anEvent.calendarEntryId().id())) {
            return;
        }

        PreparedStatement statement =
                connection.prepareStatement(
                        "insert into tbl_vw_calendar_entry( "
                        + "calendar_entry_id, alarm_alarm_units, alarm_alarm_units_type, "
                        + "calendar_id, description, location, "
                        + "owner_email_address, owner_identity, owner_name, "
                        + "repetition_ends, repetition_type, "
                        + "tenant_id, time_span_begins, time_span_ends"
                        + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        statement.setString(1, anEvent.calendarEntryId().id());
        statement.setInt(2, anEvent.alarm().alarmUnits());
        statement.setString(3, anEvent.alarm().alarmUnitsType().name());
        statement.setString(4, anEvent.calendarId().id());
        statement.setString(5, anEvent.description());
        statement.setString(6, anEvent.location());
        statement.setString(7, anEvent.owner().emailAddress());
        statement.setString(8, anEvent.owner().identity());
        statement.setString(9, anEvent.owner().name());
        statement.setDate(10, new java.sql.Date(anEvent.repetition().ends().getTime()));
        statement.setString(11, anEvent.repetition().repeats().name());
        statement.setString(12, anEvent.tenant().id());
        statement.setDate(13, new java.sql.Date(anEvent.timeSpan().begins().getTime()));
        statement.setDate(14, new java.sql.Date(anEvent.timeSpan().ends().getTime()));

        this.execute(statement);

        for (Participant participant : anEvent.invitees()) {
            this.insertInvitee(anEvent.tenant(), anEvent.calendarEntryId(), participant);
        }
    }

    private void insertInvitee(
            Tenant aTenant,
            CalendarEntryId aCalendarEntryId,
            Participant aParticipant)
    throws Exception {

        Connection connection = ConnectionProvider.connection();

        // idempotent operation
        if (this.exists(
                "select id from tbl_vw_calendar_entry_invitee "
                    + "where tenant_id = ? and calendar_entry_id = ? and participant_identity = ?",
                aTenant.id(),
                aCalendarEntryId.id(),
                aParticipant.identity())) {
            return;
        }

        PreparedStatement statement =
                connection.prepareStatement(
                        "insert into tbl_vw_calendar_entry_invitee( "
                        + "id, calendar_entry_id, "
                        + "participant_email_address, participant_identity, participant_name, "
                        + "tenant_id"
                        + ") values(?,?,?,?,?,?)");

        statement.setLong(1, 0);
        statement.setString(2, aCalendarEntryId.id());
        statement.setString(3, aParticipant.emailAddress());
        statement.setString(4, aParticipant.identity());
        statement.setString(5, aParticipant.name());
        statement.setString(6, aTenant.id());

        this.execute(statement);
    }
}
