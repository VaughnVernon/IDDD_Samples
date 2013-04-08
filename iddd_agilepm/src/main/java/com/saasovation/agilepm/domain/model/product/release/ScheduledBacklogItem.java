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

package com.saasovation.agilepm.domain.model.product.release;

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemId;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

public class ScheduledBacklogItem extends Entity {

    private BacklogItemId backlogItemId;
    private int ordering;
    private ReleaseId releaseId;
    private TenantId tenantId;

    public BacklogItemId backlogItemId() {
        return this.backlogItemId;
    }

    public int ordering() {
        return this.ordering;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            ScheduledBacklogItem typedObject = (ScheduledBacklogItem) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.releaseId().equals(typedObject.releaseId()) &&
                this.backlogItemId().equals(typedObject.backlogItemId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (73281 * 47)
            + this.tenantId().hashCode()
            + this.releaseId().hashCode()
            + this.backlogItemId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "ScheduledBacklogItem [tenantId=" + tenantId
                + ", releaseId=" + releaseId
                + ", backlogItemId=" + backlogItemId
                + ", ordering=" + ordering + "]";
    }

    protected ScheduledBacklogItem(
            TenantId aTenantId,
            ReleaseId aReleaseId,
            BacklogItemId aBacklogItemId,
            int anOrdering) {

        this();

        this.setBacklogItemId(aBacklogItemId);
        this.setOrdering(anOrdering);
        this.setReleaseId(aReleaseId);
        this.setTenantId(aTenantId);
    }

    protected ScheduledBacklogItem(
            TenantId aTenantId,
            ReleaseId aReleaseId,
            BacklogItemId aBacklogItemId) {

        this(aTenantId, aReleaseId, aBacklogItemId, 0);
    }

    protected ScheduledBacklogItem() {
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

    private ReleaseId releaseId() {
        return this.releaseId;
    }

    private void setReleaseId(ReleaseId aReleaseId) {
        if (aReleaseId == null) {
            throw new IllegalArgumentException("The release id is required.");
        }

        this.releaseId = aReleaseId;
    }

    private TenantId tenantId() {
        return this.tenantId;
    }

    private void setTenantId(TenantId aTenantId) {
        if (aTenantId == null) {
            throw new IllegalArgumentException("The tenant id is required.");
        }

        this.tenantId = aTenantId;
    }
}
