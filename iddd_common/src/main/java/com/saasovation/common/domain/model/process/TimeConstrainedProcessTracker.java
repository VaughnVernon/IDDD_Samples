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

import java.lang.reflect.Constructor;
import java.util.Date;

import com.saasovation.common.AssertionConcern;
import com.saasovation.common.domain.model.DomainEventPublisher;

public class TimeConstrainedProcessTracker extends AssertionConcern {

    private long allowableDuration;
    private boolean completed;
    private int concurrencyVersion;
    private String description;
    private ProcessId processId;
    private boolean processInformedOfTimeout;
    private String processTimedOutEventType;
    private int retryCount;
    private String tenantId;
    private long timeConstrainedProcessTrackerId;
    private long timeoutOccursOn;
    private int totalRetriesPermitted;

    public TimeConstrainedProcessTracker(
            String aTenantId,
            ProcessId aProcessId,
            String aDescription,
            Date anOriginalStartTime,
            long anAllowableDuration,
            int aTotalRetriesPermitted,
            String aProcessTimedOutEventType) {

        super();

        this.setAllowableDuration(anAllowableDuration);
        this.setDescription(aDescription);
        this.setProcessId(aProcessId);
        this.setProcessTimedOutEventType(aProcessTimedOutEventType);
        this.setTenantId(aTenantId);
        this.setTimeConstrainedProcessTrackerId(-1L);
        this.setTimeoutOccursOn(anOriginalStartTime.getTime() + anAllowableDuration);
        this.setTotalRetriesPermitted(aTotalRetriesPermitted);
    }

    public long allowableDuration() {
        return this.allowableDuration;
    }

    public void completed() {
        this.completed = true;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public String description() {
        return this.description;
    }

    public void failWhenConcurrencyViolation(int aVersion) {
        this.assertStateTrue(
                aVersion == this.concurrencyVersion(),
                "Concurrency Violation: Stale data detected. Entity was already modified.");
    }

    public ProcessId processId() {
        return this.processId;
    }

    public boolean isProcessInformedOfTimeout() {
        return this.processInformedOfTimeout;
    }

    public String processTimedOutEventType() {
        return this.processTimedOutEventType;
    }

    public boolean hasTimedOut() {
        Date timeout = new Date(this.timeoutOccursOn());
        Date now = new Date();

        return (timeout.equals(now) || timeout.before(now));
    }

    public void informProcessTimedOut() {
        if (!this.isProcessInformedOfTimeout() && this.hasTimedOut()) {

            ProcessTimedOut processTimedOut = null;

            if (this.totalRetriesPermitted() == 0) {
                processTimedOut = this.processTimedOutEvent();

                this.setProcessInformedOfTimeout(true);
            } else {
                this.incrementRetryCount();

                processTimedOut = this.processTimedOutEventWithRetries();

                if (this.totalRetriesReached()) {
                    this.setProcessInformedOfTimeout(true);
                } else {
                    this.setTimeoutOccursOn(
                            this.timeoutOccursOn()
                            + this.allowableDuration());
                }
            }

            DomainEventPublisher.instance().publish(processTimedOut);
        }
    }

    public int retryCount() {
        return this.retryCount;
    }

    public String tenantId() {
        return this.tenantId;
    }

    public long timeConstrainedProcessTrackerId() {
        return this.timeConstrainedProcessTrackerId;
    }

    public long timeoutOccursOn() {
        return this.timeoutOccursOn;
    }

    public int totalRetriesPermitted() {
        return this.totalRetriesPermitted;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            TimeConstrainedProcessTracker typedObject = (TimeConstrainedProcessTracker) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.processId().equals(typedObject.processId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (79157 * 107)
            + this.tenantId().hashCode()
            + this.processId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "TimeConstrainedProcessTracker [allowableDuration=" + allowableDuration + ", completed=" + completed
                + ", description=" + description + ", processId=" + processId + ", processInformedOfTimeout="
                + processInformedOfTimeout + ", processTimedOutEventType=" + processTimedOutEventType + ", retryCount="
                + retryCount + ", tenantId=" + tenantId + ", timeConstrainedProcessTrackerId=" + timeConstrainedProcessTrackerId
                + ", timeoutOccursOn=" + timeoutOccursOn + ", totalRetriesPermitted=" + totalRetriesPermitted + "]";
    }

    protected TimeConstrainedProcessTracker() {
        super();
    }

    protected int concurrencyVersion() {
        return this.concurrencyVersion;
    }

    protected void setConcurrencyVersion(int aConcurrencyVersion) {
        this.concurrencyVersion = aConcurrencyVersion;
    }

    private void incrementRetryCount() {
        this.retryCount++;
    }

    private void setAllowableDuration(long anAllowableDuration) {
        this.assertArgumentTrue(anAllowableDuration > 0, "The allowable duration must be greater than zero.");

        this.allowableDuration = anAllowableDuration;
    }

    private void setDescription(String aDescription) {
        this.assertArgumentNotEmpty(aDescription, "Description is required.");
        this.assertArgumentLength(aDescription, 1, 100, "Description must be 1 to 100 characters in length.");

        this.description = aDescription;
    }

    private void setProcessInformedOfTimeout(boolean isProcessInformedOfTimeout) {
        this.processInformedOfTimeout = isProcessInformedOfTimeout;
    }

    private ProcessTimedOut processTimedOutEvent() {
        ProcessTimedOut processTimedOut = null;

        try {
            Class<?> processTimedOutClass =
                    (Class<?>) Class.forName(this.processTimedOutEventType());

            Constructor<?> ctor = processTimedOutClass.getConstructor(ProcessId.class);

            processTimedOut = (ProcessTimedOut) ctor.newInstance(this.processId());

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error creating new ProcessTimedOut instance because: "
                    + e.getMessage());
        }

        return processTimedOut;
    }

    private ProcessTimedOut processTimedOutEventWithRetries() {
        ProcessTimedOut processTimedOut = null;

        try {
            Class<?> processTimedOutClass =
                    (Class<?>) Class.forName(this.processTimedOutEventType());

            Constructor<?> ctor =
                    processTimedOutClass
                        .getConstructor(
                                ProcessId.class,
                                int.class,
                                int.class);

            processTimedOut = (ProcessTimedOut)
                    ctor.newInstance(
                            this.processId(),
                            this.totalRetriesPermitted(),
                            this.retryCount());

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error creating new ProcessTimedOut instance because: "
                    + e.getMessage());
        }

        return processTimedOut;
    }

    private void setProcessId(ProcessId aProcessId) {
        this.assertArgumentNotNull(aProcessId, "ProcessId is required.");

        this.processId = aProcessId;
    }

    private void setProcessTimedOutEventType(String aProcessTimedOutEventType) {
        this.assertArgumentNotEmpty(aProcessTimedOutEventType, "ProcessTimedOutEventType is required.");

        this.processTimedOutEventType = aProcessTimedOutEventType;
    }

    private void setTenantId(String aTenantId) {
        this.assertArgumentNotEmpty(aTenantId, "TenantId is required.");

        this.tenantId = aTenantId;
    }

    private void setTimeConstrainedProcessTrackerId(long aTimeConstrainedProcessTrackerId) {
        this.timeConstrainedProcessTrackerId = aTimeConstrainedProcessTrackerId;
    }

    private void setTimeoutOccursOn(long aTimeoutOccursOn) {
        this.assertArgumentTrue(aTimeoutOccursOn > 0, "Timeout must be greater than zero.");

        this.timeoutOccursOn = aTimeoutOccursOn;
    }

    private void setTotalRetriesPermitted(int aTotalRetriesPermitted) {
        this.assertArgumentTrue(
                aTotalRetriesPermitted >= 0,
                "Total retries must be greater than or equal to zero.");

        this.totalRetriesPermitted = aTotalRetriesPermitted;
    }

    private boolean totalRetriesReached() {
        return this.retryCount() >= this.totalRetriesPermitted();
    }
}
