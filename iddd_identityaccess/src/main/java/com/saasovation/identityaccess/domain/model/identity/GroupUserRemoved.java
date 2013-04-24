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

public class GroupUserRemoved extends DomainEvent {

    private String groupName;
    private TenantId tenantId;
    private String username;

    public GroupUserRemoved(TenantId aTenantId, String aGroupName, String aUsername) {
	super();

	this.groupName = aGroupName;
	this.tenantId = aTenantId;
	this.username = aUsername;
    }

    public String groupName() {
	return this.groupName;
    }

    public TenantId tenantId() {
	return this.tenantId;
    }

    public String username() {
	return this.username;
    }
}
