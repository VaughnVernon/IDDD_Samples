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

import java.util.Date;

import com.saasovation.common.domain.model.DomainEvent;

public class TenantAdministratorRegistered implements DomainEvent {

    private FullName administratorName;
    private EmailAddress emailAddress;
    private int eventVersion;
    private Date occurredOn;
    private String temporaryPassword;
    private TenantId tenantId;
    private String tenantName;
    private String username;

    public TenantAdministratorRegistered(
            TenantId aTenantId,
            String aTenantName,
            FullName anAdministratorName,
            EmailAddress anEmailAddress,
            String aUsername,
            String aTemporaryPassword) {

        super();

        this.administratorName = anAdministratorName;
        this.emailAddress = anEmailAddress;
        this.eventVersion = 1;
        this.occurredOn = new Date();
        this.temporaryPassword = aTemporaryPassword;
        this.tenantId = aTenantId;
        this.tenantName = aTenantName;
        this.username = aUsername;
    }

    public FullName administratorName() {
        return this.administratorName;
    }

    public EmailAddress emailAddress() {
        return this.emailAddress;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    @Override
    public Date occurredOn() {
        return this.occurredOn;
    }

    public String temporaryPassword() {
        return this.temporaryPassword;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public String getTenantName() {
        return this.tenantName;
    }

    public String username() {
        return this.username;
    }
}
