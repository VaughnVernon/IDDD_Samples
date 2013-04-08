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

package com.saasovation.common.domain.model.process;

import java.util.Date;

import com.saasovation.common.domain.model.Entity;

public abstract class AbstractProcess extends Entity implements Process {

    private static final long serialVersionUID = 1L;

    private long allowableDuration;
    private int concurrencyVersion;
    private String description;
    private ProcessId processId;
    private ProcessCompletionType processCompletionType;
    private Date startTime;
    private String tenantId;
    private Date timedOutDate;
    private int totalRetriesPermitted;

    public AbstractProcess(
            String aTenantId,
            ProcessId aProcessId,
            String aDescription) {

        super();

        this.setDescription(aDescription);
        this.setProcessCompletionType(ProcessCompletionType.NotCompleted);
        this.setProcessId(aProcessId);
        this.setStartTime(new Date());
        this.setTenantId(aTenantId);
    }

    public AbstractProcess(
            String aTenantId,
            ProcessId aProcessId,
            String aDescription,
            long anAllowableDuration) {

        this(aTenantId, aProcessId, aDescription);

        this.setAllowableDuration(anAllowableDuration);
    }

    public AbstractProcess(
            String aTenantId,
            ProcessId aProcessId,
            String aDescription,
            long anAllowableDuration,
            int aTotalRetriesPermitted) {

        this(aTenantId, aProcessId, aDescription, anAllowableDuration);

        this.setTotalRetriesPermitted(aTotalRetriesPermitted);
    }

    @Override
    public long allowableDuration() {
        return this.allowableDuration;
    }

    @Override
    public boolean canTimeout() {
        return this.allowableDuration() > 0;
    }

    @Override
    public long currentDuration() {
        return this.calculateTotalCurrentDuration(new Date());
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public boolean didProcessingComplete() {
        return this.isCompleted() && !this.isTimedOut();
    }

    public void failWhenConcurrencyViolation(int aVersion) {
        this.assertStateTrue(
                aVersion == this.concurrencyVersion(),
                "Concurrency Violation: Stale data detected. Entity was already modified.");
    }

    @Override
    public void informTimeout(Date aTimedOutDate) {
        this.assertStateTrue(
                this.hasProcessTimedOut(aTimedOutDate),
                "The date " + aTimedOutDate + " does not indicate a valid timeout.");

        this.setProcessCompletionType(ProcessCompletionType.TimedOut);
        this.setTimedOutDate(aTimedOutDate);
    }

    @Override
    public boolean isCompleted() {
        return !this.notCompleted();
    }

    @Override
    public boolean isTimedOut() {
        return this.timedOutDate() != null;
    }

    @Override
    public boolean notCompleted() {
        return this.processCompletionType().equals(ProcessCompletionType.NotCompleted);
    }

    @Override
    public ProcessCompletionType processCompletionType() {
        return this.processCompletionType;
    }

    @Override
    public ProcessId processId() {
        return this.processId;
    }

    @Override
    public Date startTime() {
        return this.startTime;
    }

    public String tenantId() {
        return this.tenantId;
    }

    @Override
    public TimeConstrainedProcessTracker timeConstrainedProcessTracker() {
        this.assertStateTrue(this.canTimeout(), "Process does not timeout.");

        TimeConstrainedProcessTracker tracker =
                new TimeConstrainedProcessTracker(
                        this.tenantId(),
                        this.processId(),
                        this.description(),
                        this.startTime(),
                        this.allowableDuration(),
                        this.totalRetriesPermitted(),
                        this.processTimedOutEventType().getName());

        return tracker;
    }

    @Override
    public Date timedOutDate() {
        return this.timedOutDate;
    }

    @Override
    public long totalAllowableDuration() {
        long totalAllowableDuration = this.allowableDuration();
        long totalRetriesPermitted = this.totalRetriesPermitted();

        if (totalRetriesPermitted > 0) {
            totalAllowableDuration *= totalRetriesPermitted;
        }

        return totalAllowableDuration;
    }

    @Override
    public int totalRetriesPermitted() {
        return this.totalRetriesPermitted;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            AbstractProcess typedObject = (AbstractProcess) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.processId().equals(typedObject.processId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (71547 * 953)
            + this.tenantId().hashCode()
            + this.processId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "AbstractProcess [id=" + id() + "allowableDuration=" + allowableDuration
                + ", description=" + description + ", processId=" + processId
                + ", processCompletionType=" + processCompletionType + ", startTime=" + startTime
                + ", tenantId=" + tenantId + ", timedOutDate=" + timedOutDate
                + ", totalRetriesPermitted=" + totalRetriesPermitted + "]";
    }

    protected AbstractProcess() {
        super();
    }

    protected void completeProcess(ProcessCompletionType aProcessCompletionType) {
        if (!this.isCompleted() && this.completenessVerified()) {
            this.setProcessCompletionType(aProcessCompletionType);
        }
    }

    protected int concurrencyVersion() {
        return this.concurrencyVersion;
    }

    protected void setConcurrencyVersion(int aConcurrencyVersion) {
        this.concurrencyVersion = aConcurrencyVersion;
    }

    protected abstract boolean completenessVerified();

    protected abstract Class<? extends ProcessTimedOut> processTimedOutEventType();

    private long calculateTotalCurrentDuration(Date aDateFollowingStartTime) {
        return aDateFollowingStartTime.getTime() - this.startTime().getTime();
    }

    private boolean hasProcessTimedOut(Date aTimedOutDate) {
        return this.calculateTotalCurrentDuration(aTimedOutDate) >=
               this.totalAllowableDuration();
    }

    private void setAllowableDuration(long anAllowableDuration) {
        this.assertArgumentTrue(
                anAllowableDuration > 0,
                "The allowable duration must be greater than zero.");

        this.allowableDuration = anAllowableDuration;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setProcessCompletionType(ProcessCompletionType aProcessCompletionType) {
        this.processCompletionType = aProcessCompletionType;
    }

    private void setProcessId(ProcessId aProcessId) {
        this.assertArgumentNotNull(aProcessId, "Process id must be provided.");

        this.processId = aProcessId;
    }

    private void setStartTime(Date aStartTime) {
        this.startTime = aStartTime;
    }

    private void setTenantId(String aTenantId) {
        this.assertArgumentNotEmpty(aTenantId, "Tenant id must be provided.");

        this.tenantId = aTenantId;
    }

    private void setTimedOutDate(Date aTimedOutDate) {
        this.timedOutDate = aTimedOutDate;
    }

    private void setTotalRetriesPermitted(int aTotalRetriesPermitted) {
        this.totalRetriesPermitted = aTotalRetriesPermitted;
    }
}
