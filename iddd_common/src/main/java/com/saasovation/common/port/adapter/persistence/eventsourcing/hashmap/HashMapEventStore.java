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

package com.saasovation.common.port.adapter.persistence.eventsourcing.hashmap;

import java.util.*;

import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.event.EventSerializer;
import com.saasovation.common.event.sourcing.*;
import com.saasovation.common.port.adapter.persistence.eventsourcing.DefaultEventStream;

/**
 * I am an EventStore backed by a HashMap.
 *
 * @author Vaughn Vernon
 */
public class HashMapEventStore implements EventStore {

    private static HashMapEventStore instance;

    private EventNotifiable eventNotifiable;
    private HashMapJournal journal;
    private EventSerializer serializer;

    public static synchronized HashMapEventStore instance() {
        if (instance == null) {
            instance = new HashMapEventStore();
        } else {
            // normally unnecessary, but tests close the journal
            instance.setJournal(HashMapJournal.initializeInstance());
        }

        return instance;
    }

    @Override
    public void appendWith(EventStreamId aStartingIdentity, List<DomainEvent> anEvents) {

        LoggableJournalEntry[] entries =
                new LoggableJournalEntry[anEvents.size()];

        JournalKeyProvider keyProvider =
                new StreamKeyProvider(
                        aStartingIdentity.streamName(),
                        aStartingIdentity.streamVersion());

        int entryIndex = 0;

        for (DomainEvent event : anEvents) {

            String streamKey = keyProvider.nextReferenceKey();

            String eventValue =
                    this.journal()
                        .valueWithMetadata(
                                this.serializer().serialize(event),
                                event.getClass().getName());

            entries[entryIndex++] =
                    new LoggableJournalEntry(
                            eventValue,
                            streamKey,
                            keyProvider.primaryResourceName());
        }

        this.journal().logEntries(entries);

        this.notifyDispatchableEvents();
    }

    @Override
    public void close() {
        this.journal().close();
    }

    @Override
    public List<DispatchableDomainEvent> eventsSince(long aLastReceivedEvent) {

        List<DispatchableDomainEvent> events = null;

        try {
            List<LoggedJournalEntry> entries =
                    this.journal()
                        .loggedJournalEntriesSince(aLastReceivedEvent);

            events = this.toDispatchableDomainEvents(entries);

        } catch (Throwable t) {
            throw new EventStoreException(
                    "Cannot query event store for events since: "
                        + aLastReceivedEvent
                        + " because: "
                        + t.getMessage(),
                    t);
        }

        return events;
    }

    @Override
    public EventStream eventStreamSince(EventStreamId anIdentity) {
        List<DomainEvent> events = null;

        int version = 0;

        try {
            JournalKeyProvider keyProvider =
                new StreamKeyProvider(
                        anIdentity.streamName(),
                        anIdentity.streamVersion());

            List<LoggedJournalEntry> entries =
                    this.journal()
                        .referencedLoggedJournalEntries(
                                keyProvider);

            events = this.toDomainEvents(entries);

            LoggedJournalEntry entry = entries.get(entries.size() - 1);

            String streamVersion = keyProvider.lastKeyPart(entry.referenceKey());

            version = Integer.parseInt(streamVersion);

        } catch (Throwable t) {
            throw new EventStoreException(
                    "Cannot query event stream for: "
                        + anIdentity.streamName()
                        + " since version: "
                        + anIdentity.streamVersion()
                        + " because: "
                        + t.getMessage(),
                    t);
        }

        if (events.isEmpty()) {
            throw new EventStoreException(
                    "There is no such event stream: "
                    + anIdentity.streamName()
                    + " : "
                    + anIdentity.streamVersion());
        }

        return new DefaultEventStream(events, version);
    }

    @Override
    public EventStream fullEventStreamFor(EventStreamId anIdentity) {
        return this.eventStreamSince(anIdentity.withStreamVersion(1));
    }

    @Override
    public void purge() {
        this.journal().purge();
    }

    @Override
    public void registerEventNotifiable(EventNotifiable anEventNotifiable) {
        this.eventNotifiable = anEventNotifiable;
    }

    private HashMapEventStore() {
        super();

        this.setJournal(HashMapJournal.initializeInstance());
        this.setSerializer(EventSerializer.instance());
    }

    private EventNotifiable eventNotifiable() {
        return this.eventNotifiable;
    }

    private HashMapJournal journal() {
        return this.journal;
    }

    private void setJournal(HashMapJournal aJournal) {
        this.journal = aJournal;
    }

    private void notifyDispatchableEvents() {
        EventNotifiable eventNotifiable = this.eventNotifiable();

        if (eventNotifiable != null) {
            eventNotifiable.notifyDispatchableEvents();
        }
    }

    private EventSerializer serializer() {
        return this.serializer;
    }

    private void setSerializer(EventSerializer aSerializer) {
        this.serializer = aSerializer;
    }

    @SuppressWarnings("unchecked")
    private List<DomainEvent> toDomainEvents(
            List<LoggedJournalEntry> anEntries)
    throws Exception {

        List<DomainEvent> events = new ArrayList<DomainEvent>();

        for (LoggedJournalEntry entry : anEntries) {

            String eventClassName = entry.nextMetadataValue();

            String eventBody = entry.value();

            Class<DomainEvent> eventClass =
                    (Class<DomainEvent>) Class.forName(eventClassName);

            DomainEvent domainEvent =
                    this.serializer().deserialize(eventBody, eventClass);

            events.add(domainEvent);
        }

        return events;
    }

    @SuppressWarnings("unchecked")
    private List<DispatchableDomainEvent> toDispatchableDomainEvents(
            List<LoggedJournalEntry> anEntries)
    throws Exception {

        List<DispatchableDomainEvent> events = new ArrayList<DispatchableDomainEvent>();

        for (LoggedJournalEntry entry : anEntries) {

            String eventClassName = entry.nextMetadataValue();

            String eventBody = entry.value();

            Class<DomainEvent> eventClass =
                    (Class<DomainEvent>) Class.forName(eventClassName);

            DomainEvent domainEvent =
                    this.serializer().deserialize(eventBody, eventClass);

            events.add(new DispatchableDomainEvent(entry.journalSequence(), domainEvent));
        }

        return events;
    }

    private class StreamKeyProvider
            extends JournalKeyProvider {

        private String streamName;
        private int streamVersion;

        public StreamKeyProvider(String aStreamName, int aStartingStreamVersion) {
            super();

            this.streamName = aStreamName;
            this.streamVersion = aStartingStreamVersion;
        }

        @Override
        public String nextReferenceKey() {
            String key =
                    this.compositeReferenceKeyFrom(
                                streamName,
                                ""+streamVersion);

            ++streamVersion;

            return key;
        }

        @Override
        public String primaryResourceName() {
            return this.streamName;
        }
    }
}
