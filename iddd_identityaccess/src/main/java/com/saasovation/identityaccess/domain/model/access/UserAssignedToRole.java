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

package com.saasovation.identityaccess.domain.model.access;

import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.identityaccess.domain.model.identity.TenantId;

public class UserAssignedToRole extends DomainEvent {

    private String emailAddress;
    private String firstName;
    private String lastName;
    private String roleName;
    private TenantId tenantId;
    private String username;

    public UserAssignedToRole(
	    TenantId aTenantId,
	    String aRoleName,
	    String aUsername,
	    String aFirstName,
	    String aLastName,
	    String anEmailAddress) {

	super();

	this.emailAddress = anEmailAddress;
	this.firstName = aFirstName;
	this.lastName = aLastName;
	this.roleName = aRoleName;
	this.tenantId = aTenantId;
	this.username = aUsername;
    }

    public String emailAddress() {
	return this.emailAddress;
    }

    public String firstName() {
	return this.firstName;
    }

    public String lastName() {
	return this.lastName;
    }

    public String roleName() {
	return this.roleName;
    }

    public TenantId tenantId() {
	return this.tenantId;
    }

    public String username() {
	return this.username;
    }
}
