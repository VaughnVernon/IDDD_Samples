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

import com.saasovation.common.domain.model.DomainEvent;

public class UserRegistered extends DomainEvent {

    private EmailAddress emailAddress;
    private FullName name;
    private TenantId tenantId;
    private String username;

    public UserRegistered(
	    TenantId aTenantId,
	    String aUsername,
	    FullName aName,
	    EmailAddress anEmailAddress) {

	super();

	this.emailAddress = anEmailAddress;
	this.name = aName;
	this.tenantId = aTenantId;
	this.username = aUsername;
    }

    public EmailAddress emailAddress() {
	return this.emailAddress;
    }

    public FullName name() {
	return this.name;
    }

    public TenantId tenantId() {
	return this.tenantId;
    }

    public String username() {
	return this.username;
    }
}
