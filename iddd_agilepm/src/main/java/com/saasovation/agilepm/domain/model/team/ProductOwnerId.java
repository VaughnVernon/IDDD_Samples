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

package com.saasovation.agilepm.domain.model.team;

import com.saasovation.agilepm.domain.model.ValueObject;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

public class ProductOwnerId extends ValueObject {

    private String id;
    private TenantId tenantId;

    public ProductOwnerId(TenantId aTenantId, String anId) {
        this();

        this.setId(anId);
        this.setTenantId(aTenantId);
    }

    public ProductOwnerId(ProductOwnerId aProductOwnerId) {
        this(aProductOwnerId.tenantId(), aProductOwnerId.id());
    }

    public String id() {
        return this.id;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            ProductOwnerId typedObject = (ProductOwnerId) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.id().equals(typedObject.id());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (43685 * 83)
            + this.id().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "ProductOwnerId [tenantId=" + tenantId + ", id=" + id + "]";
    }

    private ProductOwnerId() {
        super();
    }

    private void setId(String anId) {
        this.assertArgumentNotEmpty(anId, "The id must be provided.");
        this.assertArgumentLength(anId, 36, "The id must be 36 characters or less.");

        this.id = anId;
    }

    private void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "The tenantId must be provided.");

        this.tenantId = aTenantId;
    }
}
