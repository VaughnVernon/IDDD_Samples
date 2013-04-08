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

package com.saasovation.common.port.adapter.persistence.eventsourcing.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.event.EventSerializer;
import com.saasovation.common.event.sourcing.DispatchableDomainEvent;
import com.saasovation.common.event.sourcing.EventNotifiable;
import com.saasovation.common.event.sourcing.EventStore;
import com.saasovation.common.event.sourcing.EventStoreAppendException;
import com.saasovation.common.event.sourcing.EventStoreException;
import com.saasovation.common.event.sourcing.EventStream;
import com.saasovation.common.event.sourcing.EventStreamId;
import com.saasovation.common.port.adapter.persistence.eventsourcing.DefaultEventStream;

public class MySQLJDBCEventStore implements EventStore, ApplicationContextAware {

    private static MySQLJDBCEventStore instance;

    private DataSource collaborationDataSource;
    private EventNotifiable eventNotifiable;
    private EventSerializer serializer;

    public synchronized static MySQLJDBCEventStore instance() {
        return instance;
    }

    public MySQLJDBCEventStore(DataSource aDataSource) {
        super();

        this.setCollaborationDataSource(aDataSource);
        this.setSerializer(EventSerializer.instance());
    }

    @Override
    public void appendWith(EventStreamId aStartingIdentity, List<DomainEvent> anEvents) {

        // tbl_es_event_store must have a composite primary key
        // consisting of {stream_name}:{streamVersion} so that
        // appending a stale version will fail the pk constraint

        Connection connection = this.connection();

        try {
            int index = 0;

            for (DomainEvent event : anEvents) {
                this.appendEventStore(connection, aStartingIdentity, index++, event);
            }

            connection.commit();

            this.notifyDispatchableEvents();

        } catch (Throwable t1) {
            try {
                this.connection().rollback();
            } catch (Throwable t2) {
                // ignore
            }

            throw new EventStoreAppendException(
                    "Could not append to event store because: "
                            + t1.getMessage(),
                    t1);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public List<DispatchableDomainEvent> eventsSince(long aLastReceivedEvent) {

        Connection connection = this.connection();

        ResultSet result = null;

        try {
            PreparedStatement statement =
                    connection
                        .prepareStatement(
                                "SELECT event_id, event_body, event_type FROM tbl_es_event_store "
                                + "WHERE event_id > ? "
                                + "ORDER BY event_id");

            statement.setLong(1, aLastReceivedEvent);

            result = statement.executeQuery();

            List<DispatchableDomainEvent> sequence = this.buildEventSequence(result);

            connection.commit();

            return sequence;

        } catch (Throwable t) {
            throw new EventStoreException(
                    "Cannot query event for sequence since: "
                        + aLastReceivedEvent
                        + " because: "
                        + t.getMessage(),
                    t);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    @Override
    public EventStream eventStreamSince(EventStreamId anIdentity) {

        Connection connection = this.connection();

        ResultSet result = null;

        try {
            PreparedStatement statement =
                    connection
                        .prepareStatement(
                                "SELECT stream_version, event_type, event_body FROM tbl_es_event_store "
                                + "WHERE stream_name = ? AND stream_version >= ? "
                                + "ORDER BY stream_version");

            statement.setString(1, anIdentity.streamName());
            statement.setInt(2, anIdentity.streamVersion());

            result = statement.executeQuery();

            EventStream eventStream = this.buildEventStream(result);

            if (eventStream.version() == 0) {
                throw new EventStoreException(
                        "There is no such event stream: "
                        + anIdentity.streamName()
                        + " : "
                        + anIdentity.streamVersion());
            }

            connection.commit();

            return eventStream;

        } catch (Throwable t) {
            throw new EventStoreException(
                    "Cannot query event stream for: "
                        + anIdentity.streamName()
                        + " since version: "
                        + anIdentity.streamVersion()
                        + " because: "
                        + t.getMessage(),
                    t);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    @Override
    public EventStream fullEventStreamFor(EventStreamId anIdentity) {

        Connection connection = this.connection();

        ResultSet result = null;

        try {
            PreparedStatement statement =
                    connection
                        .prepareStatement(
                                "SELECT stream_version, event_type, event_body FROM tbl_es_event_store "
                                + "WHERE stream_name = ? "
                                + "ORDER BY stream_version");

            statement.setString(1, anIdentity.streamName());

            result = statement.executeQuery();

            connection.commit();

            return this.buildEventStream(result);

        } catch (Throwable t) {
            throw new EventStoreException(
                    "Cannot query full event stream for: "
                        + anIdentity.streamName()
                        + " because: "
                        + t.getMessage(),
                    t);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    @Override
    public void purge() {
        Connection connection = this.connection();

        try {
            connection.createStatement().execute("delete from tbl_es_event_store");

            connection.commit();

        } catch (Throwable t) {
            throw new EventStoreException(
                    "Problem purging event store because: "
                        + t.getMessage(),
                    t);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    @Override
    public void registerEventNotifiable(EventNotifiable anEventNotifiable) {
        this.eventNotifiable = anEventNotifiable;
    }

    private void appendEventStore(
            Connection aConnection,
            EventStreamId anIdentity,
            int anIndex,
            DomainEvent aDomainEvent)
    throws Exception {

        PreparedStatement statement =
                aConnection
                    .prepareStatement(
                            "INSERT INTO tbl_es_event_store VALUES(?, ?, ?, ?, ?)");

        statement.setLong(1, 0);
        statement.setString(2, this.serializer().serialize(aDomainEvent));
        statement.setString(3, aDomainEvent.getClass().getName());
        statement.setString(4, anIdentity.streamName());
        statement.setInt(5, anIdentity.streamVersion() + anIndex);

        statement.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    private List<DispatchableDomainEvent> buildEventSequence(ResultSet aResultSet) throws Exception {

        List<DispatchableDomainEvent> events = new ArrayList<DispatchableDomainEvent>();

        while (aResultSet.next()) {
            long eventId = aResultSet.getLong("event_id");

            String eventClassName = aResultSet.getString("event_type");

            String eventBody = aResultSet.getString("event_body");

            Class<DomainEvent> eventClass = (Class<DomainEvent>) Class.forName(eventClassName);

            DomainEvent domainEvent = this.serializer().deserialize(eventBody, eventClass);

            events.add(new DispatchableDomainEvent(eventId, domainEvent));
        }

        return events;
    }

    @SuppressWarnings("unchecked")
    private EventStream buildEventStream(ResultSet aResultSet) throws Exception {

        List<DomainEvent> events = new ArrayList<DomainEvent>();

        int version = 0;

        while (aResultSet.next()) {
            version = aResultSet.getInt("stream_version");

            String eventClassName = aResultSet.getString("event_type");

            String eventBody = aResultSet.getString("event_body");

            Class<DomainEvent> eventClass = (Class<DomainEvent>) Class.forName(eventClassName);

            DomainEvent domainEvent = this.serializer().deserialize(eventBody, eventClass);

            events.add(domainEvent);
        }

        return new DefaultEventStream(events, version);
    }

    private DataSource collaborationDataSource() {
        return this.collaborationDataSource;
    }

    private void setCollaborationDataSource(DataSource aDataSource) {
        this.collaborationDataSource = aDataSource;
    }

    private Connection connection() {
        Connection connection = null;

        try {
            connection = this.collaborationDataSource().getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot acquire database connection.");
        }

        return connection;
    }

    private EventNotifiable eventNotifiable() {
        return this.eventNotifiable;
    }

    private void notifyDispatchableEvents() {
        EventNotifiable eventNotifiable = this.eventNotifiable();

        if (eventNotifiable != null) {
            this.eventNotifiable().notifyDispatchableEvents();
        }
    }

    private EventSerializer serializer() {
        return this.serializer;
    }

    private void setSerializer(EventSerializer aSerializer) {
        this.serializer = aSerializer;
    }

    @Override
    public synchronized void setApplicationContext(
            ApplicationContext anApplicationContext)
    throws BeansException {
        instance = (MySQLJDBCEventStore)
                anApplicationContext.getBean("mysqlJdbcEventStore");
    }
}
