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

import java.util.*;

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.product.backlogitem.*;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

public class Release extends Entity {

    private boolean archived;
    private Set<ScheduledBacklogItem> backlogItems;
    private Date begins;
    private String description;
    private Date ends;
    private String name;
    private ProductId productId;
    private ReleaseId releaseId;
    private TenantId tenantId;

    public Release(
            TenantId aTenantId,
            ProductId aProductId,
            ReleaseId aReleaseId,
            String aName,
            String aDescription,
            Date aBegins,
            Date anEnds) {

        this();

        if (anEnds.before(aBegins)) {
            throw new IllegalArgumentException("Release must not end before it begins.");
        }

        this.setBegins(aBegins);
        this.setDescription(aDescription);
        this.setEnds(anEnds);
        this.setName(aName);
        this.setProductId(aProductId);
        this.setReleaseId(aReleaseId);
        this.setTenantId(aTenantId);
    }

    public Set<ScheduledBacklogItem> allScheduledBacklogItems() {
        return Collections.unmodifiableSet(this.backlogItems());
    }

    public void archived(boolean anArchived) {
        this.setArchived(anArchived);

        // TODO: publish event / student assignment
    }

    public Date begins() {
        return this.begins;
    }

    public void describeAs(String aDescription) {
        this.setDescription(aDescription);

        // TODO: publish event / student assignment
    }

    public String description() {
        return this.description;
    }

    public Date ends() {
        return this.ends;
    }

    public boolean isArchived() {
        return this.archived;
    }

    public String name() {
        return this.name;
    }

    public void nowBeginsOn(Date aBegins) {
        this.setBegins(aBegins);

        // TODO: publish event / student assignment
    }

    public void nowEndsOn(Date anEnds) {
        this.setEnds(anEnds);

        // TODO: publish event / student assignment
    }

    public ProductId productId() {
        return this.productId;
    }

    public ReleaseId releaseId() {
        return this.releaseId;
    }

    public void rename(String aName) {
        this.setName(aName);

        // TODO: publish event / student assignment
    }

    public void reorderFrom(BacklogItemId anId, int anOrderOfPriority) {
        for (ScheduledBacklogItem scheduledBacklogItem : this.backlogItems()) {
            scheduledBacklogItem.reorderFrom(anId, anOrderOfPriority);
        }
    }

    public void schedule(BacklogItem aBacklogItem) {
        this.assertArgumentEquals(this.tenantId(), aBacklogItem.tenantId(), "Must have same tenants.");
        this.assertArgumentEquals(this.productId(), aBacklogItem.productId(), "Must have same products.");

        int ordering = this.backlogItems().size() + 1;

        ScheduledBacklogItem scheduledBacklogItem =
                new ScheduledBacklogItem(
                        this.tenantId(),
                        this.releaseId(),
                        aBacklogItem.backlogItemId(),
                        ordering);

        this.backlogItems().add(scheduledBacklogItem);
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public void unschedule(BacklogItem aBacklogItem) {
        this.assertArgumentEquals(this.tenantId(), aBacklogItem.tenantId(), "Must have same tenants.");
        this.assertArgumentEquals(this.productId(), aBacklogItem.productId(), "Must have same products.");

        ScheduledBacklogItem scheduledBacklogItem =
                new ScheduledBacklogItem(
                        this.tenantId(),
                        this.releaseId(),
                        aBacklogItem.backlogItemId());

        this.backlogItems().remove(scheduledBacklogItem);
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Release typedObject = (Release) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.productId().equals(typedObject.productId()) &&
                this.releaseId().equals(typedObject.releaseId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (84519 * 41)
            + this.tenantId().hashCode()
            + this.productId().hashCode()
            + this.releaseId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Release [tenantId=" + tenantId + ", productId=" + productId
                + ", releaseId=" + releaseId + ", archived=" + archived
                + ", backlogItems=" + backlogItems + ", begins=" + begins
                + ", description=" + description + ", ends=" + ends
                + ", name=" + name + "]";
    }

    private Release() {
        super();

        this.setBacklogItems(new HashSet<ScheduledBacklogItem>(0));
    }

    private void setArchived(boolean anArchived) {
        this.archived = anArchived;
    }

    private Set<ScheduledBacklogItem> backlogItems() {
        return this.backlogItems;
    }

    private void setBacklogItems(Set<ScheduledBacklogItem> aBacklogItems) {
        this.backlogItems = aBacklogItems;
    }

    private void setBegins(Date aBegins) {
        this.assertArgumentNotNull(aBegins, "The begins must be provided.");

        this.begins = aBegins;
    }

    private void setDescription(String aDescription) {
        this.assertArgumentLength(aDescription, 500, "The description must be 500 characters or less.");

        this.description = aDescription;
    }

    private void setEnds(Date anEnds) {
        this.assertArgumentNotNull(anEnds, "The ends must be provided.");

        this.ends = anEnds;
    }

    private void setName(String aName) {
        this.assertArgumentNotEmpty(aName, "The name must be provided.");
        this.assertArgumentLength(aName, 100, "The name must be 100 characters or less.");

        this.name = aName;
    }

    private void setProductId(ProductId aProductId) {
        this.assertArgumentNotNull(aProductId, "The product id must be provided.");

        this.productId = aProductId;
    }

    private void setReleaseId(ReleaseId aReleaseId) {
        this.assertArgumentNotNull(aReleaseId, "The release id must be provided.");

        this.releaseId = aReleaseId;
    }

    private void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "The tenant id must be provided.");

        this.tenantId = aTenantId;
    }
}
