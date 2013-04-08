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

package com.saasovation.identityaccess.application.representation;

import com.saasovation.identityaccess.domain.model.identity.User;
import com.saasovation.identityaccess.domain.model.identity.UserDescriptor;

public class UserInRoleRepresentation {

    private String emailAddress;
    private String firstName;
    private String lastName;
    private String role;
    private String tenantId;
    private String username;

    public UserInRoleRepresentation(User aUser, String aRole) {
        this();

        this.initializeFrom(aUser, aRole);
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getRole() {
        return this.role;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public String getUsername() {
        return this.username;
    }

    protected UserInRoleRepresentation() {
        super();
    }

    private void initializeFrom(User aUser, String aRole) {
        UserDescriptor desc = aUser.userDescriptor();
        this.setEmailAddress(desc.emailAddress());
        this.setFirstName(aUser.person().name().firstName());
        this.setLastName(aUser.person().name().lastName());
        this.setRole(aRole);
        this.setTenantId(desc.tenantId().id());
        this.setUsername(desc.username());
    }

    private void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    private void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    private void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private void setRole(String aRole) {
        this.role = aRole;
    }

    private void setTenantId(String aTenantId) {
        this.tenantId = aTenantId;
    }

    private void setUsername(String aUsername) {
        this.username = aUsername;
    }
}
