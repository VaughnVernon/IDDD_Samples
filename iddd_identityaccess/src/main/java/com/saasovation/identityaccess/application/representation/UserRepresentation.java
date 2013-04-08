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

import com.saasovation.identityaccess.domain.model.identity.TenantId;
import com.saasovation.identityaccess.domain.model.identity.User;

public class UserRepresentation {

    private String emailAddress;
    private boolean enabled;
    private String firstName;
    private String lastName;
    private TenantId tenantId;
    private String username;

    public UserRepresentation(User aUser) {
        super();

        this.initializeFrom(aUser);
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getTenantId() {
        return this.tenantId.id();
    }

    public String getUsername() {
        return this.username;
    }

    protected UserRepresentation() {
        super();
    }

    private void initializeFrom(User aUser) {
        this.emailAddress = aUser.person().emailAddress().address();
        this.enabled = aUser.isEnabled();
        this.firstName = aUser.person().name().firstName();
        this.lastName = aUser.person().name().lastName();
        this.tenantId = aUser.tenantId();
        this.username = aUser.username();
    }
}
