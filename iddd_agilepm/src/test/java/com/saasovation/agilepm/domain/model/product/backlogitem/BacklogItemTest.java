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

package com.saasovation.agilepm.domain.model.product.backlogitem;

import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability;
import com.saasovation.agilepm.domain.model.discussion.DiscussionDescriptor;
import com.saasovation.agilepm.domain.model.product.Product;
import com.saasovation.agilepm.domain.model.product.ProductBacklogItemPlanned;
import com.saasovation.agilepm.domain.model.product.ProductCommonTest;
import com.saasovation.agilepm.domain.model.product.ProductCreated;
import com.saasovation.agilepm.domain.model.product.ProductReleaseScheduled;
import com.saasovation.agilepm.domain.model.product.ProductSprintScheduled;
import com.saasovation.agilepm.domain.model.product.release.Release;
import com.saasovation.agilepm.domain.model.product.sprint.Sprint;
import com.saasovation.common.domain.model.process.ProcessId;

public class BacklogItemTest extends ProductCommonTest {

    public BacklogItemTest() {
        super();
    }

    public void testAnyTaskHoursRemaining() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        assertFalse(backlogItem.anyTaskHoursRemaining());

        Task task1 = this.taskForTest(backlogItem, 1);
        int taskHours1 = task1.hoursRemaining();

        Task task2 = this.taskForTest(backlogItem, 2);
        int taskHours2 = task2.hoursRemaining();

        Task task3 = this.taskForTest(backlogItem, 3);
        int taskHours3 = task3.hoursRemaining();

        assertTrue(backlogItem.anyTaskHoursRemaining());
        assertEquals(taskHours1 + taskHours2 + taskHours3, backlogItem.totalTaskHoursRemaining());

        backlogItem.removeTask(task1.taskId());
        assertTrue(backlogItem.anyTaskHoursRemaining());
        assertEquals(taskHours2 + taskHours3, backlogItem.totalTaskHoursRemaining());

        backlogItem.removeTask(task2.taskId());
        assertTrue(backlogItem.anyTaskHoursRemaining());
        assertEquals(taskHours3, backlogItem.totalTaskHoursRemaining());

        backlogItem.removeTask(task3.taskId());
        assertFalse(backlogItem.anyTaskHoursRemaining());
        assertEquals(0, backlogItem.totalTaskHoursRemaining());
    }

    public void testAssignBusinessPriority() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        BusinessPriority businessPriority =
                new BusinessPriority(new BusinessPriorityRatings(2, 4, 1, 1));

        backlogItem.assignBusinessPriority(businessPriority);

        expectedEvents(3);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(BusinessPriorityAssigned.class);
    }

    public void testAssignStoryPoints() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        backlogItem.assignStoryPoints(StoryPoints.TWENTY);

        assertEquals(StoryPoints.TWENTY, backlogItem.storyPoints());

        expectedEvents(3);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(BacklogItemStoryPointsAssigned.class);
    }

    public void testAssignTaskVolunteer() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        Task task = this.taskForTest(backlogItem, 1);

        backlogItem.assignTaskVolunteer(task.taskId(), this.teamMemberForTest());

        expectedEvents(4);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(TaskDefined.class);
        expectedEvent(TaskVolunteerAssigned.class);
    }

    public void testChangeCategory() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        assertFalse("User Interface".equals(backlogItem.category()));

        backlogItem.changeCategory("User Interface");

        assertEquals("User Interface", backlogItem.category());

        expectedEvents(3);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(BacklogItemCategoryChanged.class);
    }

    public void testChangeTaskStatus() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        Task task = this.taskForTest(backlogItem, 1);

        assertEquals(TaskStatus.NOT_STARTED, task.status());

        backlogItem.changeTaskStatus(task.taskId(), TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, task.status());

        expectedEvents(4);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(TaskDefined.class);
        expectedEvent(TaskStatusChanged.class);
    }

    public void testChangeType() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        assertTrue(backlogItem.type().equals(BacklogItemType.FEATURE));
        assertFalse(backlogItem.type().equals(BacklogItemType.ENHANCEMENT));

        backlogItem.changeType(BacklogItemType.ENHANCEMENT);

        assertFalse(backlogItem.type().equals(BacklogItemType.FEATURE));
        assertTrue(backlogItem.type().equals(BacklogItemType.ENHANCEMENT));

        expectedEvents(3);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(BacklogItemTypeChanged.class);
    }

    public void testCommitTo() {
        Product product = this.productForTest();

        BacklogItem backlogItem = this.backlogItemForTest(product);

        Sprint sprint = this.sprintForTest(product);

        try {
            backlogItem.commitTo(sprint);

            fail("Must be scheduled for release before committing to sprint.");

        } catch (IllegalStateException e) {
            // good
        }

        Release release = this.releaseForTest(product);

        backlogItem.scheduleFor(release);

        // later...
        backlogItem.commitTo(sprint);

        expectedEvents(6);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(ProductSprintScheduled.class);
        expectedEvent(ProductReleaseScheduled.class);
        expectedEvent(BacklogItemScheduled.class);
        expectedEvent(BacklogItemCommitted.class);
    }

    public void testDefineTask() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        backlogItem.defineTask(
                this.teamMemberForTest(),
                "Model the Discussion",
                "Collaboration discussions must manage transitioning state.",
                1);

        assertFalse(backlogItem.allTasks().isEmpty());
        assertEquals(1, backlogItem.allTasks().size());

        expectedEvents(3);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(TaskDefined.class);
    }

    public void testDescribeTask() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        Task task = this.taskForTest(backlogItem, 1);

        assertFalse("New and improved description.".equals(task.description()));

        backlogItem.describeTask(task.taskId(), "New and improved description.");

        assertEquals("New and improved description.", task.description());

        expectedEvents(4);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(TaskDefined.class);
        expectedEvent(TaskDescribed.class);
    }

    public void testRequestAndInitiateDiscussion() throws Exception {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        backlogItem.requestDiscussion(DiscussionAvailability.REQUESTED);

        assertTrue(backlogItem.discussion().descriptor().isUndefined());
        assertEquals(DiscussionAvailability.REQUESTED, backlogItem.discussion().availability());

        expectedEvents(3);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(BacklogItemDiscussionRequested.class);

        // eventually...
        ProcessId processId = ProcessId.newProcessId();
        backlogItem.startDiscussionInitiation(processId.id());

        // eventually...
        backlogItem.initiateDiscussion(new DiscussionDescriptor("CollabDiscussion45678"));

        expectedEvents(4);
        expectedEvent(BacklogItemDiscussionInitiated.class);

        assertEquals(processId.id(), backlogItem.discussionInitiationId());
        assertFalse(backlogItem.discussion().descriptor().isUndefined());
        assertEquals(DiscussionAvailability.READY, backlogItem.discussion().availability());
    }

    public void testRequestAndFailedDiscussion() throws Exception {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        backlogItem.requestDiscussion(DiscussionAvailability.REQUESTED);

        assertTrue(backlogItem.discussion().descriptor().isUndefined());
        assertEquals(DiscussionAvailability.REQUESTED, backlogItem.discussion().availability());

        expectedEvents(3);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(BacklogItemDiscussionRequested.class);

        // eventually...
        ProcessId processId = ProcessId.newProcessId();
        backlogItem.startDiscussionInitiation(processId.id());

        // eventually...
        backlogItem.failDiscussionInitiation();

        expectedEvents(3);

        assertNull(backlogItem.discussionInitiationId());
        assertTrue(backlogItem.discussion().descriptor().isUndefined());
        assertEquals(DiscussionAvailability.FAILED, backlogItem.discussion().availability());
    }

    public void testEstimateTaskHoursRemaining() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        Task task = this.taskForTest(backlogItem, 1);

        assertTrue(backlogItem.isPlanned());

        assertEquals(TaskStatus.NOT_STARTED, task.status());

        backlogItem.estimateTaskHoursRemaining(task.taskId(), task.hoursRemaining() / 2);

        assertEquals(TaskStatus.IN_PROGRESS, task.status());

        assertEquals(1, task.allEstimationLogEntries().size());
        assertEquals(task.hoursRemaining(), task.allEstimationLogEntries().get(0).hoursRemaining());

        expectedEvents(5);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(TaskDefined.class);
        expectedEvent(TaskHoursRemainingEstimated.class);
        expectedEvent(TaskStatusChanged.class);

        // later...
        backlogItem.estimateTaskHoursRemaining(task.taskId(), 0);

        assertEquals(TaskStatus.DONE, task.status());

        // same day re-estimation means that existing log is changed
        assertEquals(1, task.allEstimationLogEntries().size());
        assertEquals(0, task.allEstimationLogEntries().get(0).hoursRemaining());

        expectedEvents(8);
        expectedEvent(TaskHoursRemainingEstimated.class, 2);
        expectedEvent(TaskStatusChanged.class, 2);
        expectedEvent(BacklogItemStatusChanged.class);

        assertTrue(backlogItem.isDone());
    }

    public void testMarkAsRemoved() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        assertTrue(backlogItem.isPlanned());
        assertFalse(backlogItem.isRemoved());

        backlogItem.markAsRemoved();

        assertFalse(backlogItem.isPlanned());
        assertTrue(backlogItem.isRemoved());
    }

    public void testRemoveTask() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        assertFalse(backlogItem.anyTaskHoursRemaining());

        Task task1 = this.taskForTest(backlogItem, 1);
        Task task2 = this.taskForTest(backlogItem, 2);
        Task task3 = this.taskForTest(backlogItem, 3);

        assertEquals(3, backlogItem.allTasks().size());

        backlogItem.removeTask(task1.taskId());
        assertTrue(backlogItem.anyTaskHoursRemaining());

        backlogItem.removeTask(task2.taskId());
        assertTrue(backlogItem.anyTaskHoursRemaining());

        backlogItem.removeTask(task3.taskId());
        assertFalse(backlogItem.anyTaskHoursRemaining());

        assertEquals(0, backlogItem.allTasks().size());

        expectedEvent(TaskRemoved.class, 3);
    }

    public void testRenameTask() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        Task task = this.taskForTest(backlogItem, 1);

        assertFalse(task.name().equals("New and Improved Task Name"));

        backlogItem.renameTask(task.taskId(), "New and Improved Task Name");

        assertEquals("New and Improved Task Name", task.name());

        expectedEvent(TaskRenamed.class);
    }

    public void testScheduleFor() {
        Product product = this.productForTest();

        BacklogItem backlogItem = this.backlogItemForTest(product);

        Release release = this.releaseForTest(product);

        backlogItem.scheduleFor(release);

        expectedEvents(4);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(ProductReleaseScheduled.class);
        expectedEvent(BacklogItemScheduled.class);
    }

    public void testSummarize() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        assertFalse(backlogItem.summary().equals("New and Improved Summary"));

        backlogItem.summarize("New and Improved Summary");

        assertEquals("New and Improved Summary", backlogItem.summary());

        expectedEvent(BacklogItemSummarized.class);
    }

    public void testTellStory() {
        BacklogItem backlogItem = this.backlogItemForTest(this.productForTest());

        assertFalse(backlogItem.summary().equals("New and Improved Story"));

        backlogItem.tellStory("New and Improved Story");

        assertEquals("New and Improved Story", backlogItem.story());

        expectedEvent(BacklogItemStoryTold.class);
    }

    public void testUncommitFromSprint() {
        Product product = this.productForTest();

        BacklogItem backlogItem = this.backlogItemForTest(product);

        Release release = this.releaseForTest(product);
        backlogItem.scheduleFor(release);

        Sprint sprint = this.sprintForTest(product);
        backlogItem.commitTo(sprint);

        // later...
        backlogItem.uncommitFromSprint();

        expectedEvents(7);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(ProductSprintScheduled.class);
        expectedEvent(ProductReleaseScheduled.class);
        expectedEvent(BacklogItemScheduled.class);
        expectedEvent(BacklogItemCommitted.class);
        expectedEvent(BacklogItemUncommitted.class);
    }

    public void testUnscheduleFromRelease() {
        Product product = this.productForTest();

        BacklogItem backlogItem = this.backlogItemForTest(product);

        Release release = this.releaseForTest(product);
        backlogItem.scheduleFor(release);

        // later...
        backlogItem.unscheduleFromRelease();

        expectedEvents(5);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(ProductReleaseScheduled.class);
        expectedEvent(BacklogItemScheduled.class);
        expectedEvent(BacklogItemUnscheduled.class);
    }

    public void testFailUnscheduleFromRelease() {
        Product product = this.productForTest();

        BacklogItem backlogItem = this.backlogItemForTest(product);

        Release release = this.releaseForTest(product);
        backlogItem.scheduleFor(release);

        Sprint sprint = this.sprintForTest(product);
        backlogItem.commitTo(sprint);

        try {
            // later...
            backlogItem.unscheduleFromRelease();

            fail("The backlog item must first be uncommitted from the sprint.");

        } catch (Exception e) {
            // good
        }

        expectedEvents(6);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductBacklogItemPlanned.class);
        expectedEvent(ProductReleaseScheduled.class);
        expectedEvent(ProductSprintScheduled.class);
        expectedEvent(BacklogItemScheduled.class);
        expectedEvent(BacklogItemCommitted.class);
    }
}
