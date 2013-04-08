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

package com.saasovation.identityaccess.domain.model.identity;

import java.io.Serializable;

import com.saasovation.common.AssertionConcern;

public final class UserDescriptor extends AssertionConcern implements Serializable {

    private static final long serialVersionUID = 1L;

    private String emailAddress;
    private TenantId tenantId;
    private String username;

    public static UserDescriptor nullDescriptorInstance() {
        return new UserDescriptor();
    }

    public UserDescriptor(TenantId aTenantId, String aUsername, String anEmailAddress) {
        super();

        this.setEmailAddress(anEmailAddress);
        this.setTenantId(aTenantId);
        this.setUsername(aUsername);
    }

    public String emailAddress() {
        return this.emailAddress;
    }

    public boolean isNullDescriptor() {
        return this.emailAddress() == null || this.tenantId() == null || this.username() == null;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public String username() {
        return this.username;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            UserDescriptor typedObject = (UserDescriptor) anObject;
            equalObjects =
                    this.emailAddress().equals(typedObject.emailAddress()) &&
                    this.tenantId().equals(typedObject.tenantId()) &&
                    this.username().equals(typedObject.username());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (9429 * 263)
            + this.emailAddress().hashCode()
            + this.tenantId().hashCode()
            + this.username().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "UserDescriptor [emailAddress=" + emailAddress
                + ", tenantId=" + tenantId + ", username=" + username + "]";
    }

    private UserDescriptor() {
        super();
    }

    private void setEmailAddress(String anEmailAddress) {
        this.assertArgumentNotEmpty(anEmailAddress, "Email address must be provided.");

        this.emailAddress = anEmailAddress;
    }

    private void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "TenantId must not be set as null.");

        this.tenantId = aTenantId;
    }

    private void setUsername(String aUsername) {
        this.assertArgumentNotEmpty(aUsername, "Username must not be set as null.");

        this.username = aUsername;
    }
}
