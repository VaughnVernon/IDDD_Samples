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

package com.saasovation.agilepm.domain.model.product.sprint;

import java.util.Date;

import com.saasovation.agilepm.domain.model.product.Product;
import com.saasovation.agilepm.domain.model.product.ProductBacklogItemPlanned;
import com.saasovation.agilepm.domain.model.product.ProductCommonTest;
import com.saasovation.agilepm.domain.model.product.ProductCreated;
import com.saasovation.agilepm.domain.model.product.ProductSprintScheduled;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItem;

public class SprintTest extends ProductCommonTest {

    public SprintTest() {
        super();
    }

    public void testScheduleSprint() throws Exception {
        Product product = this.productForTest();

        Date begins = new Date();
        Date ends = new Date(begins.getTime() + (86400000L * 30L));

        Sprint sprint =
                product.scheduleSprint(
                        new SprintId("S-12345"),
                        "Collaboration Integration Sprint",
                        "Make Scrum project collaboration possible.",
                        begins,
                        ends);

        assertNotNull(sprint);
        assertEquals("Collaboration Integration Sprint", sprint.name());
        assertEquals("Make Scrum project collaboration possible.", sprint.goals());

        expectedEvents(2);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductSprintScheduled.class);
    }

    public void testCaptureRetrospectiveMeetingResults() throws Exception {
        Sprint sprint = this.sprintForTest(this.productForTest());

        assertNull(sprint.retrospective());

        sprint.captureRetrospectiveMeetingResults("We learned these five things: ...");

        assertNotNull(sprint.retrospective());
        assertEquals("We learned these five things: ...", sprint.retrospective());
    }

    public void testDescribeAs() throws Exception {
        Sprint sprint = this.sprintForTest(this.productForTest());

        String adjustedGoals = "Make Scrum product and backlog item collaboration possible.";

        assertFalse(adjustedGoals.equals(sprint.goals()));

        sprint.adjustGoals(adjustedGoals);

        assertEquals(adjustedGoals, sprint.goals());
    }

    public void testNowBeginsOn() throws Exception {
        Sprint sprint = this.sprintForTest(this.productForTest());

        Date date = new Date(new Date().getTime() + (86400000L * 2L));

        assertFalse(date.equals(sprint.begins()));

        sprint.nowBeginsOn(date);

        assertEquals(date, sprint.begins());
    }

    public void testNowEndsOn() throws Exception {
        Sprint sprint = this.sprintForTest(this.productForTest());

        Date date = new Date(new Date().getTime() + (86400000L * 10L));

        assertFalse(date.equals(sprint.ends()));

        sprint.nowEndsOn(date);

        assertEquals(date, sprint.ends());
    }

    public void testRename() throws Exception {
        Sprint sprint = this.sprintForTest(this.productForTest());

        String changedName = "New Sprint Name";

        assertFalse(changedName.equals(sprint.name()));

        sprint.rename(changedName);

        assertEquals(changedName, sprint.name());
    }

    public void testReorderFrom() throws Exception {
        Product product = this.productForTest();
        Sprint sprint = this.sprintForTest(product);

        BacklogItem backlogItem1 = this.backlogItem1ForTest(product);
        BacklogItem backlogItem2 = this.backlogItem2ForTest(product);
        BacklogItem backlogItem3 = this.backlogItem3ForTest(product);

        expectedEvents(5);
        expectedEvent(ProductCreated.class, 1);
        expectedEvent(ProductSprintScheduled.class, 1);
        expectedEvent(ProductBacklogItemPlanned.class, 3);

        sprint.commit(backlogItem1);
        sprint.commit(backlogItem2);
        sprint.commit(backlogItem3);

        CommittedBacklogItem scheduledBacklogItem1 = null;
        CommittedBacklogItem scheduledBacklogItem2 = null;
        CommittedBacklogItem scheduledBacklogItem3 = null;

        for (CommittedBacklogItem scheduledBacklogItem : sprint.allCommittedBacklogItems()) {
            if (scheduledBacklogItem.ordering() == 1) {
                scheduledBacklogItem1 = scheduledBacklogItem;
            }
            if (scheduledBacklogItem.ordering() == 2) {
                scheduledBacklogItem2 = scheduledBacklogItem;
            }
            if (scheduledBacklogItem.ordering() == 3) {
                scheduledBacklogItem3 = scheduledBacklogItem;
            }
        }

        sprint.reorderFrom(backlogItem3.backlogItemId(), 1);

        assertEquals(1, scheduledBacklogItem3.ordering());
        assertEquals(2, scheduledBacklogItem1.ordering());
        assertEquals(3, scheduledBacklogItem2.ordering());
    }

    public void testSchedule() throws Exception {
        Product product = this.productForTest();
        Sprint sprint = this.sprintForTest(product);

        BacklogItem backlogItem1 = this.backlogItem1ForTest(product);
        BacklogItem backlogItem2 = this.backlogItem2ForTest(product);
        BacklogItem backlogItem3 = this.backlogItem3ForTest(product);

        expectedEvents(5);
        expectedEvent(ProductCreated.class, 1);
        expectedEvent(ProductSprintScheduled.class, 1);
        expectedEvent(ProductBacklogItemPlanned.class, 3);

        sprint.commit(backlogItem1);
        sprint.commit(backlogItem2);
        sprint.commit(backlogItem3);

        for (CommittedBacklogItem scheduledBacklogItem : sprint.allCommittedBacklogItems()) {
            if (scheduledBacklogItem.ordering() == 1) {
                assertTrue(scheduledBacklogItem.backlogItemId().id().endsWith("-1"));
            }
            if (scheduledBacklogItem.ordering() == 2) {
                assertTrue(scheduledBacklogItem.backlogItemId().id().endsWith("-2"));
            }
            if (scheduledBacklogItem.ordering() == 3) {
                assertTrue(scheduledBacklogItem.backlogItemId().id().endsWith("-3"));
            }
        }
    }

    public void testUnschedule() throws Exception {
        Product product = this.productForTest();
        Sprint sprint = this.sprintForTest(product);

        BacklogItem backlogItem1 = this.backlogItem1ForTest(product);
        BacklogItem backlogItem2 = this.backlogItem2ForTest(product);
        BacklogItem backlogItem3 = this.backlogItem3ForTest(product);

        sprint.commit(backlogItem1);
        sprint.commit(backlogItem2);
        sprint.commit(backlogItem3);

        assertEquals(3, sprint.allCommittedBacklogItems().size());

        sprint.uncommit(backlogItem2);

        assertEquals(2, sprint.allCommittedBacklogItems().size());

        for (CommittedBacklogItem scheduledBacklogItem : sprint.allCommittedBacklogItems()) {
            assertTrue(scheduledBacklogItem.backlogItemId().equals(backlogItem1.backlogItemId()) ||
                    scheduledBacklogItem.backlogItemId().equals(backlogItem3.backlogItemId()));
        }
    }
}
