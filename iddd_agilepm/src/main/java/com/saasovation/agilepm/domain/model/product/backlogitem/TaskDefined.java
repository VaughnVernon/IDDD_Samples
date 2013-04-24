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

package com.saasovation.agilepm.domain.model.product.backlogitem;

import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEvent;

public class TaskDefined extends DomainEvent {

    private BacklogItemId backlogItemId;
    private String description;
    private int hoursRemaining;
    private String name;
    private TaskId taskId;
    private TenantId tenantId;
    private String volunteerMemberId;

    public TaskDefined(
	    TenantId aTenantId,
	    BacklogItemId aBacklogItemId,
	    TaskId aTaskId,
	    String aVolunteerMemberId,
	    String aName,
	    String aDescription,
	    int aHoursRemaining) {

	super();

	this.backlogItemId = aBacklogItemId;
	this.description = aDescription;
	this.hoursRemaining = aHoursRemaining;
	this.name = aName;
	this.taskId = aTaskId;
	this.tenantId = aTenantId;
	this.volunteerMemberId = aVolunteerMemberId;
    }

    public BacklogItemId backlogItemId() {
	return this.backlogItemId;
    }

    public String description() {
	return this.description;
    }

    public int hoursRemaining() {
	return this.hoursRemaining;
    }

    public String name() {
	return this.name;
    }

    public TaskId taskId() {
	return this.taskId;
    }

    public TenantId tenantId() {
	return this.tenantId;
    }

    public String volunteerMemberId() {
	return this.volunteerMemberId;
    }
}
