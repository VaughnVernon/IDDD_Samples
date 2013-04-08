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

package com.saasovation.identityaccess.application.command;

public class RemoveGroupFromGroupCommand {

    private String tenantId;
    private String childGroupName;
    private String parentGroupName;

    public RemoveGroupFromGroupCommand(String tenantId, String parentGroupName, String childGroupName) {
        super();

        this.tenantId = tenantId;
        this.parentGroupName = parentGroupName;
        this.childGroupName = childGroupName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getChildGroupName() {
        return childGroupName;
    }

    public void setChildGroupName(String childGroupName) {
        this.childGroupName = childGroupName;
    }

    public String getParentGroupName() {
        return parentGroupName;
    }

    public void setParentGroupName(String parentGroupName) {
        this.parentGroupName = parentGroupName;
    }
}
