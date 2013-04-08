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
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.saasovation.common.domain.model.process.ProcessId;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTracker;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTrackerRepository;

public class LevelDBTimeConstrainedProcessTrackerRepository
        extends AbstractLevelDBRepository
        implements TimeConstrainedProcessTrackerRepository {

    private static final String PRIMARY = "TCPROC_TRACKER#PK";
    private static final String ALL_TRACKERS = "TCPROC_TRACKER#ALL";
    private static final String ALL_TENANT_TRACKERS = "TCPROC_TRACKER#TENANT";

    public LevelDBTimeConstrainedProcessTrackerRepository(String aDirectioryPath) {
        super(aDirectioryPath);
    }

    @Override
    public void add(TimeConstrainedProcessTracker aTimeConstrainedProcessTracker) {
        this.save(aTimeConstrainedProcessTracker);
    }

    @Override
    public Collection<TimeConstrainedProcessTracker> allTimedOut() {
        List<TimeConstrainedProcessTracker> trackers = this.listAllTrackers();

        this.filterTimedOut(trackers);

        return trackers;
    }

    @Override
    public Collection<TimeConstrainedProcessTracker> allTimedOutOf(String aTenantId) {
        List<TimeConstrainedProcessTracker> trackers = this.listAllTrackers(aTenantId);

        this.filterTimedOut(trackers);

        return trackers;
    }

    @Override
    public Collection<TimeConstrainedProcessTracker> allTrackers(String aTenantId) {
        return this.listAllTrackers(aTenantId);
    }

    @Override
    public void save(TimeConstrainedProcessTracker aTimeConstrainedProcessTracker) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY);

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.start(this.database());

        uow.lock(lockKey.key());

        this.save(aTimeConstrainedProcessTracker, uow);
    }

    @Override
    public TimeConstrainedProcessTracker trackerOfProcessId(String aTenantId, ProcessId aProcessId) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aTenantId, aProcessId.id());

        TimeConstrainedProcessTracker tracker =
                LevelDBUnitOfWork.readOnly(this.database())
                    .readObject(
                            primaryKey.key().getBytes(),
                            TimeConstrainedProcessTracker.class);

        return tracker;
    }

    private void filterTimedOut(List<TimeConstrainedProcessTracker> trackers) {
        Date now = new Date();

        Iterator<TimeConstrainedProcessTracker> iterator = trackers.iterator();

        while (iterator.hasNext()) {
            TimeConstrainedProcessTracker tracker = iterator.next();

            if (tracker != null) {
                Date timeout = new Date(tracker.timeoutOccursOn());

                if (timeout.after(now)) {
                    iterator.remove();
                }
            }
        }
    }

    private List<TimeConstrainedProcessTracker> listAllTrackers() {
        List<TimeConstrainedProcessTracker> allTrackers =
                new ArrayList<TimeConstrainedProcessTracker>();

        LevelDBKey allTrackerKey = new LevelDBKey(ALL_TRACKERS);

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        List<Object> keys = uow.readKeys(allTrackerKey);

        for (Object trackerId : keys) {
            TimeConstrainedProcessTracker tracker =
                    uow.readObject(
                            trackerId.toString().getBytes(),
                            TimeConstrainedProcessTracker.class);

            if (tracker != null) {
                allTrackers.add(tracker);
            }
        }

        return allTrackers;
    }

    private List<TimeConstrainedProcessTracker> listAllTrackers(String aTenantId) {
        List<TimeConstrainedProcessTracker> allTrackers =
                new ArrayList<TimeConstrainedProcessTracker>();

        LevelDBKey allTrackerKey = new LevelDBKey(ALL_TENANT_TRACKERS, aTenantId);

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        List<Object> keys = uow.readKeys(allTrackerKey);

        for (Object trackerId : keys) {
            TimeConstrainedProcessTracker tracker =
                    uow.readObject(
                            trackerId.toString().getBytes(),
                            TimeConstrainedProcessTracker.class);

            if (tracker != null) {
                allTrackers.add(tracker);
            }
        }

        return allTrackers;
    }

    private void save(
            TimeConstrainedProcessTracker aTimeConstrainedProcessTracker,
            LevelDBUnitOfWork aUoW) {

        LevelDBKey primaryKey =
                new LevelDBKey(
                        PRIMARY,
                        aTimeConstrainedProcessTracker.tenantId(),
                        aTimeConstrainedProcessTracker.processId().id());

        aUoW.write(primaryKey, aTimeConstrainedProcessTracker);

        LevelDBKey allTrackers =
                new LevelDBKey(
                        primaryKey,
                        ALL_TRACKERS);

        aUoW.updateKeyReference(allTrackers);

        LevelDBKey allTenantTrackers =
                new LevelDBKey(
                        primaryKey,
                        ALL_TENANT_TRACKERS,
                        aTimeConstrainedProcessTracker.tenantId());

        aUoW.updateKeyReference(allTenantTrackers);
    }
}
