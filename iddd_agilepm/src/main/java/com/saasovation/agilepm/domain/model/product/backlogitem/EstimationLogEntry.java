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

import java.util.Date;

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

public class EstimationLogEntry extends Entity {

    private Date date;
    private int hoursRemaining;
    private TaskId taskId;
    private TenantId tenantId;

    public static Date currentLogDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        java.util.Date today = calendar.getTime();

        return today;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            EstimationLogEntry typedObject = (EstimationLogEntry) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.taskId().equals(typedObject.taskId()) &&
                this.date().equals(typedObject.date()) &&
                this.hoursRemaining() == typedObject.hoursRemaining();
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (54531 * 29)
            + this.tenantId().hashCode()
            + (this.taskId() == null ? 0:this.taskId().hashCode())
            + (this.date() == null ? 0:this.date().hashCode())
            + this.hoursRemaining();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "EstimationLogEntry [tenantId=" + tenantId + ", taskId=" + taskId + ", date=" + date + ", hoursRemaining="
                + hoursRemaining + "]";
    }

    protected EstimationLogEntry(
            TenantId aTenantId,
            TaskId aTaskId,
            Date aDate,
            int aHoursRemaining) {

        this();

        this.setDate(aDate);
        this.setHoursRemaining(aHoursRemaining);
        this.setTaskId(aTaskId);
        this.setTenantId(aTenantId);
    }

    private EstimationLogEntry() {
        super();
    }

    protected Date date() {
        return this.date;
    }

    protected void setDate(Date aDate) {
        this.assertArgumentNotNull(aDate, "The date must be provided.");

        this.date = aDate;
    }

    protected int hoursRemaining() {
        return this.hoursRemaining;
    }

    protected void setHoursRemaining(int aHoursRemaining) {
        this.hoursRemaining = aHoursRemaining;
    }

    protected boolean isMatching(Date aDate) {
        return this.date().equals(aDate);
    }

    protected TaskId taskId() {
        return this.taskId;
    }

    protected void setTaskId(TaskId aTaskId) {
        this.assertArgumentNotNull(aTaskId, "The task id must be provided.");

        this.taskId = aTaskId;
    }

    protected TenantId tenantId() {
        return this.tenantId;
    }

    protected void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "The tenant id must be provided.");

        this.tenantId = aTenantId;
    }

    protected boolean updateHoursRemainingWhenDateMatches(int anHoursRemaining, Date aDate) {
        if (this.isMatching(aDate)) {
            this.setHoursRemaining(anHoursRemaining);

            return true;
        }

        return false;
    }
}
