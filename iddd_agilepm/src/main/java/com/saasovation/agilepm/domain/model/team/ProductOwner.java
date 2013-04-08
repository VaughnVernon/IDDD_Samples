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

import java.util.Date;

import com.saasovation.agilepm.domain.model.tenant.TenantId;

public class ProductOwner extends Member {

    public ProductOwner(
            TenantId aTenantId,
            String aUsername,
            String aFirstName,
            String aLastName,
            String anEmailAddress,
            Date anInitializedOn) {

        super(aTenantId, aUsername, aFirstName, aLastName, anEmailAddress, anInitializedOn);
    }

    public ProductOwnerId productOwnerId() {
        return new ProductOwnerId(this.tenantId(), this.username());
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            ProductOwner typedObject = (ProductOwner) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.username().equals(typedObject.username());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (71121 * 79)
            + this.tenantId().hashCode()
            + this.username().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "ProductOwner [productOwnerId()=" + productOwnerId() + ", emailAddress()=" + emailAddress() + ", isEnabled()="
                + isEnabled() + ", firstName()=" + firstName() + ", lastName()=" + lastName() + ", tenantId()=" + tenantId()
                + ", username()=" + username() + "]";
    }

    protected ProductOwner() {
        super();
    }
}
