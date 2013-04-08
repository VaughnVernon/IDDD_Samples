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

package com.saasovation.agilepm.application.product;

import java.util.Date;

public class TimeOutProductDiscussionRequestCommand {

    private String tenantId;
    private String processId;
    private Date timedOutDate;

    public TimeOutProductDiscussionRequestCommand(String tenantId, String processId, Date timedOutDate) {
        super();
        this.tenantId = tenantId;
        this.processId = processId;
        this.timedOutDate = timedOutDate;
    }

    public TimeOutProductDiscussionRequestCommand() {
        super();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Date getTimedOutDate() {
        return timedOutDate;
    }

    public void setTimedOutDate(Date timedOutDate) {
        this.timedOutDate = timedOutDate;
    }
}
