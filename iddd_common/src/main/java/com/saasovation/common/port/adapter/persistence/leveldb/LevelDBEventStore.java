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

package com.saasovation.common.port.adapter.persistence.leveldb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.iq80.leveldb.DB;

import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.event.EventSerializer;
import com.saasovation.common.event.EventStore;
import com.saasovation.common.event.StoredEvent;

public class LevelDBEventStore
        extends AbstractLevelDBRepository
        implements EventStore {

    private static final String PRIMARY = "ES_EVT_PK:";
    private static final byte[] INTERNAL_EVENT_ID = "ES_EVT_EID".getBytes();

    private AtomicLong storedEventIdSequence = new AtomicLong();

    public LevelDBEventStore(String aDirectoryPath) {
        super(aDirectoryPath);

        this.prepareDatabase();
    }

    @Override
    public List<StoredEvent> allStoredEventsBetween(long aLowStoredEventId, long aHighStoredEventId) {
        int elements = (int) (aHighStoredEventId - aLowStoredEventId + 1);

        List<StoredEvent> storedEvents = new ArrayList<StoredEvent>(elements);

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        boolean done = false;
        for (long idSequence = aLowStoredEventId; !done && idSequence <= aHighStoredEventId; ++idSequence) {
            StoredEvent storedEvent =
                    uow.readObject(
                            (PRIMARY + idSequence).getBytes(),
                            StoredEvent.class);

            if (storedEvent != null) {
                storedEvents.add(storedEvent);
            } else {
                done = true;
            }
        }

        return storedEvents;
    }

    @Override
    public List<StoredEvent> allStoredEventsSince(long aStoredEventId) {
        return this.allStoredEventsBetween(aStoredEventId + 1, this.currentStoredEventIdSequence());
    }

    @Override
    public StoredEvent append(DomainEvent aDomainEvent) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY);

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.start(this.database());

        uow.lock(lockKey.key());

        String eventSerialization =
                EventSerializer.instance().serialize(aDomainEvent);

        StoredEvent storedEvent =
                new StoredEvent(
                        aDomainEvent.getClass().getName(),
                        aDomainEvent.occurredOn(),
                        eventSerialization,
                        this.nextStoredEventIdSequence());

        this.save(storedEvent, uow);

        return storedEvent;
    }

    @Override
    public void close() {
        this.database().put(
                INTERNAL_EVENT_ID,
                ("" + this.currentStoredEventIdSequence()).getBytes());
    }

    @Override
    public long countStoredEvents() {
        return this.currentStoredEventIdSequence();
    }

    private boolean cacheStoredEventIdSequence() {
        boolean cached = false;

        byte[] sequenceValue = this.database().get(INTERNAL_EVENT_ID);

        if (sequenceValue != null) {
            this.setStoredEventIdSequence(Long.parseLong(new String(sequenceValue)));

            // only a successful close() will save the correct
            // sequence. a missing sequence on open indicates the
            // need for a repair (unless the event store is empty).

            this.database().delete(INTERNAL_EVENT_ID);

            cached = true;

        } else {
            this.setStoredEventIdSequence(0L);
        }

        return cached;
    }

    private long currentStoredEventIdSequence() {
        long nextStoredEventIdSequence = this.storedEventIdSequence.get();

        return nextStoredEventIdSequence;
    }

    private long nextStoredEventIdSequence() {
        long nextStoredEventIdSequence = this.storedEventIdSequence.incrementAndGet();

        return nextStoredEventIdSequence;
    }

    private void prepareDatabase() {
        if (!this.cacheStoredEventIdSequence()) {
            RepairTool repairTool = new RepairTool(this.database());

            repairTool.repairEventStore();

            long lastConfirmedKey = repairTool.lastConfirmedSequence();

            if (lastConfirmedKey > 0) {
                this.setStoredEventIdSequence(lastConfirmedKey);
            }
        }
    }

    private void setStoredEventIdSequence(long aStoredEventIdSequence) {
        this.storedEventIdSequence = new AtomicLong(aStoredEventIdSequence);
    }

    private void save(StoredEvent aStoredEvent, LevelDBUnitOfWork aUoW) {
        aUoW.write((PRIMARY + aStoredEvent.eventId()).getBytes(), aStoredEvent);
    }

    public class RepairTool {

        private static final int CONTIGUOUS_MISSING_KEY_SAFE_COUNT = 100000;

        private long lastConfirmedSequence;

        public RepairTool(DB aDatabase) {
            super();
        }

        public long lastConfirmedSequence() {
            return this.lastConfirmedSequence;
        }

        public void repairEventStore() {

            if (!this.requiresRepairProbe()) {
                return;
            }

            System.out.println("REPAIRING EVENT STORE...");

            boolean cleanUpMode = false;
            int contiguousMissingKeys = 0;
            boolean done = false;
            long lastContiguousConfirmedKey = 0;

            for (long idSequence = 1; !done; ++idSequence) {
                byte[] sequenceKey = (PRIMARY + idSequence).getBytes();

                byte[] rawSequenceValue = database().get(sequenceKey);

                if (rawSequenceValue == null) {
                    cleanUpMode = true;

                    if (++contiguousMissingKeys >= CONTIGUOUS_MISSING_KEY_SAFE_COUNT) {
                        done = true;
                    }

                } else {
                    if (!cleanUpMode) {
                        lastContiguousConfirmedKey = idSequence;
                    } else {
                        contiguousMissingKeys = 0;

                        try {
                            database().delete(sequenceKey);

                            System.out.println("Repaired journal entry: " + idSequence);

                        } catch (Throwable t) {
                            System.out.println("Could not repair journal entry: " + idSequence);
                        }
                    }
                }
            }

            this.setLastConfirmedSequence(lastContiguousConfirmedKey);
        }

        private void setLastConfirmedSequence(long aLastConfirmedSequence) {

            if (aLastConfirmedSequence > 0) {
                System.out.println(
                        "REPAIRED EVENT STORE HAS VALID SEQUENCE: "
                        + aLastConfirmedSequence);
            }

            this.lastConfirmedSequence = aLastConfirmedSequence;
        }

        private boolean requiresRepairProbe() {
            boolean requiresRepairProbe = false;

            byte[] firstSequence = database().get((PRIMARY + 1).getBytes());

            if (firstSequence != null) {
                requiresRepairProbe = true;

                System.out.println("REPAIR PROBE NEEDED: KEY FOUND: " + PRIMARY + 1);

            } else {

                System.out.println("NOT REPARING: Event Store is empty.");
            }

            return requiresRepairProbe;
        }
    }
}
