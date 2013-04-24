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

public class GroupUnassignedFromRole extends DomainEvent {

    private String groupName;
    private String roleName;
    private TenantId tenantId;

    public GroupUnassignedFromRole(TenantId aTenantId, String aRoleName, String aGroupName) {
	super();

	this.groupName = aGroupName;
	this.roleName = aRoleName;
	this.tenantId = aTenantId;
    }

    public String groupName() {
	return this.groupName;
    }

    public String roleName() {
	return this.roleName;
    }

    public TenantId tenantId() {
	return this.tenantId;
    }
}
