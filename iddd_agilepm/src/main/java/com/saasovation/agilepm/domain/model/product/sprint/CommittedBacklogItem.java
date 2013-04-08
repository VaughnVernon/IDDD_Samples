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

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemId;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

public class CommittedBacklogItem extends Entity {

    private BacklogItemId backlogItemId;
    private int ordering;
    private SprintId sprintId;
    private TenantId tenantId;

    public BacklogItemId backlogItemId() {
        return this.backlogItemId;
    }

    public int ordering() {
        return this.ordering;
    }

    public SprintId sprintId() {
        return this.sprintId;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            CommittedBacklogItem typedObject = (CommittedBacklogItem) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.sprintId().equals(typedObject.sprintId()) &&
                this.backlogItemId().equals(typedObject.backlogItemId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (282891 * 53)
            + this.tenantId().hashCode()
            + this.sprintId().hashCode()
            + this.backlogItemId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "CommittedBacklogItem [sprintId=" + sprintId + ", ordering=" + ordering + "]";
    }

    protected CommittedBacklogItem(
            TenantId aTenantId,
            SprintId aSprintId,
            BacklogItemId aBacklogItemId,
            int anOrdering) {

        this();

        this.setBacklogItemId(aBacklogItemId);
        this.setOrdering(anOrdering);
        this.setSprintId(aSprintId);
        this.setTenantId(aTenantId);
    }

    protected CommittedBacklogItem(
            TenantId aTenantId,
            SprintId aSprintId,
            BacklogItemId aBacklogItemId) {

        this(aTenantId, aSprintId, aBacklogItemId, 0);
    }

    private CommittedBacklogItem() {
        super();
    }

    protected void reorderFrom(BacklogItemId anId, int anOrderOfPriority) {
        if (this.backlogItemId().equals(anId)) {
            this.setOrdering(anOrderOfPriority);
        } else if (this.ordering() >= anOrderOfPriority) {
            this.setOrdering(this.ordering() + 1);
        }
    }

    protected void setOrdering(int anOrdering) {
        this.ordering = anOrdering;
    }

    private void setBacklogItemId(BacklogItemId aBacklogItemId) {
        this.assertArgumentNotNull(aBacklogItemId, "The backlog item id must be provided.");

        this.backlogItemId = aBacklogItemId;
    }

    private void setSprintId(SprintId aSprintId) {
        this.assertArgumentNotNull(aSprintId, "The sprint id must be provided.");

        this.sprintId = aSprintId;
    }

    private void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "The tenant id must be provided.");

        this.tenantId = aTenantId;
    }
}
