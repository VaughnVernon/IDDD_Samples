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

import junit.framework.TestCase;

import org.iq80.leveldb.DB;

import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.domain.model.DomainEventSubscriber;
import com.saasovation.common.domain.model.process.ProcessId;
import com.saasovation.common.domain.model.process.TestableTimeConstrainedProcess;
import com.saasovation.common.domain.model.process.TestableTimeConstrainedProcessTimedOut;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTracker;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTrackerRepository;
import com.saasovation.common.domain.model.process.Process.ProcessCompletionType;

public class LevelDBTimeConstrainedProcessTrackerRepositoryTest extends TestCase {

    private static final String TEST_DATABASE = LevelDBTest.TEST_DATABASE;
    private static final String TENANT_ID = "1234567890";

    private DB database;
    private TestableTimeConstrainedProcess process;
    private boolean received;
    private TimeConstrainedProcessTrackerRepository trackerRepository;

    public LevelDBTimeConstrainedProcessTrackerRepositoryTest() {
        super();
    }

    public void testCompletedProcess() throws Exception {
        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<TestableTimeConstrainedProcessTimedOut>() {

            @Override
            public void handleEvent(TestableTimeConstrainedProcessTimedOut aDomainEvent) {
                received = true;
                process.informTimeout(aDomainEvent.occurredOn());
            }

            @Override
            public Class<TestableTimeConstrainedProcessTimedOut> subscribedToEventType() {
                return TestableTimeConstrainedProcessTimedOut.class;
            }
        });

        process = new TestableTimeConstrainedProcess(
                TENANT_ID,
                ProcessId.newProcessId(),
                "Testable Time Constrained Process",
                5000L);

        TimeConstrainedProcessTracker tracker =
                process.timeConstrainedProcessTracker();

        LevelDBUnitOfWork.start(this.database);
        trackerRepository.save(tracker);
        LevelDBUnitOfWork.current().commit();

        process.confirm1();

        assertFalse(received);
        assertFalse(process.isCompleted());
        assertFalse(process.didProcessingComplete());
        assertEquals(ProcessCompletionType.NotCompleted, process.processCompletionType());

        process.confirm2();

        assertFalse(received);
        assertTrue(process.isCompleted());
        assertTrue(process.didProcessingComplete());
        assertEquals(ProcessCompletionType.CompletedNormally, process.processCompletionType());
        assertNull(process.timedOutDate());

        tracker.informProcessTimedOut();

        assertFalse(received);
        assertFalse(process.isTimedOut());

        assertFalse(trackerRepository.allTrackers(process.tenantId()).isEmpty());
        assertTrue(trackerRepository.allTimedOutOf(process.tenantId()).isEmpty());
    }

    public void testTimedOutProcess() throws Exception {
        TestableTimeConstrainedProcess process1 = new TestableTimeConstrainedProcess(
                TENANT_ID,
                ProcessId.newProcessId(),
                "Testable Time Constrained Process 1",
                1L); // forced timeout

        TimeConstrainedProcessTracker tracker1 =
                process1.timeConstrainedProcessTracker();

        TestableTimeConstrainedProcess process2 =
                new TestableTimeConstrainedProcess(
                        TENANT_ID,
                        ProcessId.newProcessId(),
                        "Testable Time Constrained Process 2",
                        5000L);

        TimeConstrainedProcessTracker tracker2 =
                process2.timeConstrainedProcessTracker();

        LevelDBUnitOfWork.start(this.database);
        trackerRepository.save(tracker1);
        trackerRepository.save(tracker2);
        LevelDBUnitOfWork.current().commit();

        Thread.sleep(500L); // forced timeout of process1

        assertFalse(trackerRepository.allTrackers(process1.tenantId()).isEmpty());
        assertEquals(2, trackerRepository.allTrackers(process1.tenantId()).size());
        assertFalse(trackerRepository.allTimedOut().isEmpty());
        assertEquals(1, trackerRepository.allTimedOut().size());
    }

    @Override
    protected void setUp() throws Exception {
        this.database = LevelDBProvider.instance().databaseFrom(TEST_DATABASE);

        LevelDBProvider.instance().purge(this.database);

        this.trackerRepository =
                new LevelDBTimeConstrainedProcessTrackerRepository(TEST_DATABASE);

        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        LevelDBProvider.instance().purge(this.database);

        super.tearDown();
    }
}
