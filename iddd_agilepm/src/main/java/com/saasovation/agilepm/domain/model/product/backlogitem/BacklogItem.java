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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability;
import com.saasovation.agilepm.domain.model.discussion.DiscussionDescriptor;
import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.product.release.Release;
import com.saasovation.agilepm.domain.model.product.release.ReleaseId;
import com.saasovation.agilepm.domain.model.product.sprint.Sprint;
import com.saasovation.agilepm.domain.model.product.sprint.SprintId;
import com.saasovation.agilepm.domain.model.team.TeamMember;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEventPublisher;

public class BacklogItem extends Entity {

    private String associatedIssueId;
    private BacklogItemId backlogItemId;
    private BusinessPriority businessPriority;
    private String category;
    private BacklogItemDiscussion discussion;
    private String discussionInitiationId;
    private ProductId productId;
    private ReleaseId releaseId;
    private SprintId sprintId;
    private BacklogItemStatus status;
    private String story;
    private StoryPoints storyPoints;
    private String summary;
    private Set<Task> tasks;
    private TenantId tenantId;
    private BacklogItemType type;

    public BacklogItem(
            TenantId aTenantId,
            ProductId aProductId,
            BacklogItemId aBacklogItemId,
            String aSummary,
            String aCategory,
            BacklogItemType aType,
            BacklogItemStatus aStatus,
            StoryPoints aStoryPoints) {

        this();

        this.setBacklogItemId(aBacklogItemId);
        this.setCategory(aCategory);
        this.setDiscussion(
                BacklogItemDiscussion
                    .fromAvailability(DiscussionAvailability.NOT_REQUESTED));
        this.setProductId(aProductId);
        this.setStatus(aStatus);
        this.setStoryPoints(aStoryPoints);
        this.setSummary(aSummary);
        this.setTenantId(aTenantId);
        this.setType(aType);
    }

    public Set<Task> allTasks() {
        return Collections.unmodifiableSet(this.tasks());
    }

    public boolean anyTaskHoursRemaining() {
        return this.totalTaskHoursRemaining() > 0;
    }

    public String associatedIssueId() {
        return this.associatedIssueId;
    }

    public void associateWithIssue(String anIssueId) {
        if (this.associatedIssueId == null) {
            this.associatedIssueId = anIssueId;
        }
    }

    public void assignBusinessPriority(BusinessPriority aBusinessPriority) {
        this.setBusinessPriority(aBusinessPriority);

        DomainEventPublisher
            .instance()
            .publish(new BusinessPriorityAssigned(
                    this.tenantId(),
                    this.backlogItemId(),
                    this.businessPriority()));
    }

    public void assignStoryPoints(StoryPoints aStoryPoints) {
        this.setStoryPoints(aStoryPoints);

        DomainEventPublisher
            .instance()
            .publish(new BacklogItemStoryPointsAssigned(
                    this.tenantId(),
                    this.backlogItemId(),
                    this.storyPoints()));
    }

    public void assignTaskVolunteer(TaskId aTaskId, TeamMember aVolunteer) {
        Task task = this.task(aTaskId);

        if (task == null) {
            throw new IllegalStateException("Task does not exist.");
        }

        task.assignVolunteer(aVolunteer);
    }

    public BacklogItemId backlogItemId() {
        return this.backlogItemId;
    }

    public BusinessPriority businessPriority() {
        return this.businessPriority;
    }

    public String category() {
        return this.category;
    }

    public void changeCategory(String aCategory) {
        this.setCategory(aCategory);

        DomainEventPublisher
            .instance()
            .publish(new BacklogItemCategoryChanged(
                    this.tenantId(),
                    this.backlogItemId(),
                    this.category()));
    }

    public void changeTaskStatus(TaskId aTaskId, TaskStatus aStatus) {
        Task task = this.task(aTaskId);

        if (task == null) {
            throw new IllegalStateException("Task does not exist.");
        }

        task.changeStatus(aStatus);
    }

    public void changeType(BacklogItemType aType) {
        this.setType(aType);

        DomainEventPublisher
            .instance()
            .publish(new BacklogItemTypeChanged(
                    this.tenantId(),
                    this.backlogItemId(),
                    this.type()));
    }

    public void commitTo(Sprint aSprint) {
        this.assertArgumentNotNull(aSprint, "Sprint must not be null.");
        this.assertArgumentEquals(aSprint.tenantId(), this.tenantId(), "Sprint must be of same tenant.");
        this.assertArgumentEquals(aSprint.productId(), this.productId(), "Sprint must be of same product.");

        if (!this.isScheduledForRelease()) {
            throw new IllegalStateException("Must be scheduled for release to commit to sprint.");
        }

        if (this.isCommittedToSprint()) {
            if (!aSprint.sprintId().equals(this.sprintId())) {
                this.uncommitFromSprint();
            }
        }

        this.elevateStatusWith(BacklogItemStatus.COMMITTED);

        this.setSprintId(aSprint.sprintId());

        DomainEventPublisher
            .instance()
            .publish(new BacklogItemCommitted(
                    this.tenantId(),
                    this.backlogItemId(),
                    this.sprintId()));
    }

    public void defineTask(TeamMember aVolunteer, String aName, String aDescription, int anHoursRemaining) {
        Task task = new Task(
                this.tenantId(),
                this.backlogItemId(),
                new TaskId(),
                aVolunteer,
                aName,
                aDescription,
                anHoursRemaining,
                TaskStatus.NOT_STARTED);

        this.tasks().add(task);

        DomainEventPublisher
            .instance()
            .publish(new TaskDefined(
                    this.tenantId(),
                    this.backlogItemId(),
                    task.taskId(),
                    aVolunteer.username(),
                    aName,
                    aDescription,
                    anHoursRemaining));
    }

    public void describeTask(TaskId aTaskId, String aDescription) {
        Task task = this.task(aTaskId);

        if (task == null) {
            throw new IllegalStateException("Task does not exist.");
        }

        task.describeAs(aDescription);
    }

    public BacklogItemDiscussion discussion() {
        return this.discussion;
    }

    public String discussionInitiationId() {
        return this.discussionInitiationId;
    }

    public void failDiscussionInitiation() {
        if (!this.discussion().availability().isReady()) {
            this.setDiscussionInitiationId(null);
            this.setDiscussion(
                    BacklogItemDiscussion
                        .fromAvailability(DiscussionAvailability.FAILED));
        }
    }

    public void initiateDiscussion(DiscussionDescriptor aDescriptor) {
        if (aDescriptor == null) {
            throw new IllegalArgumentException("The descriptor must not be null.");
        }

        if (this.discussion().availability().isRequested()) {
            this.setDiscussion(this.discussion().nowReady(aDescriptor));

            DomainEventPublisher
                .instance()
                .publish(new BacklogItemDiscussionInitiated(
                        this.tenantId(),
                        this.backlogItemId(),
                        this.discussion()));
        }
    }

    public void estimateTaskHoursRemaining(TaskId aTaskId, int anHoursRemaining) {
        Task task = this.task(aTaskId);

        if (task == null) {
            throw new IllegalStateException("Task does not exist.");
        }

        task.estimateHoursRemaining(anHoursRemaining);

        BacklogItemStatus changedStatus = null;

        if (anHoursRemaining == 0) {
            if (!this.anyTaskHoursRemaining()) {
                changedStatus = BacklogItemStatus.DONE;
            }
        } else if (this.isDone()) {
            // regress to the logical previous state
            // because "done" is no longer appropriate
            if (this.isCommittedToSprint()) {
                changedStatus = BacklogItemStatus.COMMITTED;
            } else if (this.isScheduledForRelease()) {
                changedStatus = BacklogItemStatus.SCHEDULED;
            } else {
                changedStatus = BacklogItemStatus.PLANNED;
            }
        }

        if (changedStatus != null) {
            this.setStatus(changedStatus);

            DomainEventPublisher
                .instance()
                .publish(new BacklogItemStatusChanged(
                        this.tenantId(),
                        this.backlogItemId(),
                        changedStatus));
        }
    }

    public boolean hasBusinessPriority() {
        return this.businessPriority() != null;
    }

    public void initiateDiscussion(BacklogItemDiscussion aDiscussion) {
        this.setDiscussion(aDiscussion);

        DomainEventPublisher
            .instance()
            .publish(new BacklogItemDiscussionInitiated(
                    this.tenantId(),
                    this.backlogItemId(),
                    this.discussion()));
    }

    public boolean isCommittedToSprint() {
        return this.sprintId() != null;
    }

    public boolean isDone() {
        return this.status().isDone();
    }

    public boolean isPlanned() {
        return this.status().isPlanned();
    }

    public boolean isRemoved() {
        return this.status().isRemoved();
    }

    public boolean isScheduledForRelease() {
        return this.releaseId() != null;
    }

    public void markAsRemoved() {
        if (this.isRemoved()) {
            throw new IllegalStateException("Already removed, not outstanding.");
        }
        if (this.isDone()) {
            throw new IllegalStateException("Already done, not outstanding.");
        }
        if (this.isCommittedToSprint()) {
            this.uncommitFromSprint();
        }
        if (this.isScheduledForRelease()) {
            this.unscheduleFromRelease();
        }

        this.setStatus(BacklogItemStatus.REMOVED);

        DomainEventPublisher
            .instance()
            .publish(new BacklogItemMarkedAsRemoved(
                    this.tenantId(),
                    this.backlogItemId()));
    }

    public ProductId productId() {
        return this.productId;
    }

    public ReleaseId releaseId() {
        return this.releaseId;
    }

    public void removeTask(TaskId aTaskId) {
        Task task = this.task(aTaskId);

        if (task == null) {
            throw new IllegalStateException("Task does not exist.");
        }

        if (!this.tasks().remove(task)) {
            throw new IllegalStateException("Task was not removed.");
        }

        DomainEventPublisher
            .instance()
            .publish(new TaskRemoved(
                    this.tenantId(),
                    this.backlogItemId(),
                    aTaskId));
    }

    public void renameTask(TaskId aTaskId, String aName) {
        Task task = this.task(aTaskId);

        if (task == null) {
            throw new IllegalStateException("Task does not exist.");
        }

        task.rename(aName);
    }

    public void requestDiscussion(DiscussionAvailability aDiscussionAvailability) {
        if (!this.discussion().availability().isReady()) {
            this.setDiscussion(
                    BacklogItemDiscussion.fromAvailability(
                            aDiscussionAvailability));

            DomainEventPublisher
                .instance()
                .publish(new BacklogItemDiscussionRequested(
                        this.tenantId(),
                        this.productId(),
                        this.backlogItemId(),
                        this.discussion().availability().isRequested()));
        }
    }

    public void scheduleFor(Release aRelease) {
        this.assertArgumentNotNull(aRelease, "Release must not be null.");
        this.assertArgumentEquals(aRelease.tenantId(), this.tenantId(), "Release must be of same tenant.");
        this.assertArgumentEquals(aRelease.productId(), this.productId(), "Release must be of same product.");

        if (this.isScheduledForRelease()) {
            if (!aRelease.releaseId().equals(this.releaseId())) {
                this.unscheduleFromRelease();
            }
        }

        if (this.status().isPlanned()) {
            this.setStatus(BacklogItemStatus.SCHEDULED);
        }

        this.setReleaseId(aRelease.releaseId());

        DomainEventPublisher
            .instance()
            .publish(new BacklogItemScheduled(
                    this.tenantId(),
                    this.backlogItemId(),
                    this.releaseId()));
    }

    public SprintId sprintId() {
        return this.sprintId;
    }

    public void startDiscussionInitiation(String aDiscussionInitiationId) {
        if (!this.discussion().availability().isReady()) {
            this.setDiscussionInitiationId(aDiscussionInitiationId);
        }
    }

    public String story() {
        return this.story;
    }

    public StoryPoints storyPoints() {
        return this.storyPoints;
    }

    public String summary() {
        return this.summary;
    }

    public void summarize(String aSummary) {
        this.setSummary(aSummary);

        DomainEventPublisher
            .instance()
            .publish(new BacklogItemSummarized(
                    this.tenantId(),
                    this.backlogItemId(),
                    this.summary()));
    }

    public Task task(TaskId aTaskId) {
        for (Task task : this.tasks()) {
            if (task.taskId().equals(aTaskId)) {
                return task;
            }
        }

        return null;
    }

    public void tellStory(String aStory) {
        this.setStory(aStory);

        DomainEventPublisher
            .instance()
            .publish(new BacklogItemStoryTold(
                    this.tenantId(),
                    this.backlogItemId(),
                    this.story()));
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public int totalTaskHoursRemaining() {
        int totalHoursRemaining = 0;

        for (Task task : this.tasks()) {
            totalHoursRemaining += task.hoursRemaining();
        }

        return totalHoursRemaining;
    }

    public BacklogItemType type() {
        return this.type;
    }

    public void uncommitFromSprint() {
        if (!this.isCommittedToSprint()) {
            throw new IllegalStateException("Not currently committed.");
        }

        this.setStatus(BacklogItemStatus.SCHEDULED);
        SprintId uncommittedSprintId = this.sprintId();
        this.setSprintId(null);

        DomainEventPublisher
            .instance()
            .publish(new BacklogItemUncommitted(
                    this.tenantId(),
                    this.backlogItemId(),
                    uncommittedSprintId));
    }

    public void unscheduleFromRelease() {
        if (this.isCommittedToSprint()) {
            throw new IllegalStateException("Must first uncommit.");
        }
        if (!this.isScheduledForRelease()) {
            throw new IllegalStateException("Not scheduled for release.");
        }

        this.setStatus(BacklogItemStatus.PLANNED);
        ReleaseId unscheduledReleaseId = this.releaseId();
        this.setReleaseId(null);

        DomainEventPublisher
            .instance()
            .publish(new BacklogItemUnscheduled(
                    this.tenantId(),
                    this.backlogItemId(),
                    unscheduledReleaseId));
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            BacklogItem typedObject = (BacklogItem) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.productId().equals(typedObject.productId()) &&
                this.backlogItemId().equals(typedObject.backlogItemId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (34685 * 7)
            + this.tenantId().hashCode()
            + this.productId().hashCode()
            + this.backlogItemId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "BacklogItem [tenantId=" + tenantId + ", productId=" + productId
                + ", backlogItemId=" + backlogItemId
                + ", businessPriority=" + businessPriority
                + ", category=" + category + ", discussion=" + discussion
                + ", releaseId=" + releaseId + ", sprintId=" + sprintId
                + ", status=" + status + ", story=" + story
                + ", storyPoints=" + storyPoints + ", summary=" + summary
                + ", tasks=" + tasks + ", type=" + type + "]";
    }

    private BacklogItem() {
        super();

        this.setTasks(new HashSet<Task>(0));
    }

    private void setBacklogItemId(BacklogItemId aBacklogItemId) {
        this.assertArgumentNotNull(aBacklogItemId, "The backlogItemId must be provided.");

        this.backlogItemId = aBacklogItemId;
    }

    private void setBusinessPriority(BusinessPriority aBusinessPriority) {
        this.businessPriority = aBusinessPriority;
    }

    private void setCategory(String aCategory) {
        this.assertArgumentNotEmpty(aCategory, "The category must be provided.");
        this.assertArgumentLength(aCategory, 25, "The category must be 25 characters or less.");

        this.category = aCategory;
    }

    private void setDiscussion(BacklogItemDiscussion aDiscussion) {
        this.discussion = aDiscussion;
    }

    private void setDiscussionInitiationId(String aDiscussionInitiationId) {
        if (aDiscussionInitiationId != null) {
            this.assertArgumentLength(
                    aDiscussionInitiationId,
                    100,
                    "Discussion initiation identity must be 100 characters or less.");
        }

        this.discussionInitiationId = aDiscussionInitiationId;
    }

    private void setProductId(ProductId aProductId) {
        this.assertArgumentNotNull(aProductId, "The product id must be provided.");

        this.productId = aProductId;
    }

    private void setReleaseId(ReleaseId aReleaseId) {
        this.releaseId = aReleaseId;
    }

    private void setSprintId(SprintId aSprintId) {
        this.sprintId = aSprintId;
    }

    private BacklogItemStatus status() {
        return this.status;
    }

    private void elevateStatusWith(BacklogItemStatus aStatus) {
        if (this.status().isScheduled()) {
            this.setStatus(BacklogItemStatus.COMMITTED);
        }
    }

    private void setStatus(BacklogItemStatus aStatus) {
        this.status = aStatus;
    }

    private void setStory(String aStory) {
        if (aStory != null) {
            this.assertArgumentLength(aStory, 65000, "The story must be 65000 characters or less.");
        }

        this.story = aStory;
    }

    private void setStoryPoints(StoryPoints aStoryPoints) {
        this.storyPoints = aStoryPoints;
    }

    private void setSummary(String aSummary) {
        this.assertArgumentNotEmpty(aSummary, "The summary must be provided.");
        this.assertArgumentLength(aSummary, 100, "The summary must be 100 characters or less.");

        this.summary = aSummary;
    }

    private Set<Task> tasks() {
        return this.tasks;
    }

    private void setTasks(Set<Task> aTasks) {
        this.tasks = aTasks;
    }

    private void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "The tenant id must be provided.");

        this.tenantId = aTenantId;
    }

    private void setType(BacklogItemType aType) {
        this.assertArgumentNotNull(aType, "The backlog item type must be provided.");

        this.type = aType;
    }
}
