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

package com.saasovation.agilepm.domain.model.product;

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemId;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

public class ProductBacklogItem extends Entity {

    private BacklogItemId backlogItemId;
    private int ordering;
    private ProductId productId;
    private TenantId tenantId;

    public BacklogItemId backlogItemId() {
        return this.backlogItemId;
    }

    public int ordering() {
        return this.ordering;
    }

    public ProductId productId() {
        return this.productId;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            ProductBacklogItem typedObject = (ProductBacklogItem) anObject;
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
            + (15389 * 97)
            + this.tenantId().hashCode()
            + this.productId().hashCode()
            + this.backlogItemId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "ProductBacklogItem [tenantId=" + tenantId
                + ", productId=" + productId
                + ", backlogItemId=" + backlogItemId
                + ", ordering=" + ordering + "]";
    }

    protected ProductBacklogItem(
            TenantId aTenantId,
            ProductId aProductId,
            BacklogItemId aBacklogItemId,
            int anOrdering) {

        this();

        this.setBacklogItemId(aBacklogItemId);
        this.setOrdering(anOrdering);
        this.setProductId(aProductId);
        this.setTenantId(aTenantId);
    }

    protected ProductBacklogItem() {
        super();
    }

    protected void reorderFrom(BacklogItemId anId, int anOrdering) {
        if (this.backlogItemId().equals(anId)) {
            this.setOrdering(anOrdering);
        } else if (this.ordering() >= anOrdering) {
            this.setOrdering(this.ordering() + 1);
        }
    }

    protected void setBacklogItemId(BacklogItemId aBacklogItemId) {
        this.assertArgumentNotNull(aBacklogItemId, "The backlog item id must be provided.");

        this.backlogItemId = aBacklogItemId;
    }

    protected void setOrdering(int anOrdering) {
        this.ordering = anOrdering;
    }

    protected void setProductId(ProductId aProductId) {
        this.assertArgumentNotNull(aProductId, "The product id must be provided.");

        this.productId = aProductId;
    }

    protected void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "The tenant id must be provided.");

        this.tenantId = aTenantId;
    }
}
