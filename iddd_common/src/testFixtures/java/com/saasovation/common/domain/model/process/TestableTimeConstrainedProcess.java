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

public class TestableTimeConstrainedProcess extends AbstractProcess {

    private static final long serialVersionUID = 1L;

    private boolean confirm1;
    private boolean confirm2;

    public TestableTimeConstrainedProcess(
            String aTenantId,
            ProcessId aProcessId,
            String aDescription,
            long anAllowableDuration) {

        super(aTenantId, aProcessId, aDescription, anAllowableDuration);
    }

    public void confirm1() {
        this.confirm1 = true;

        this.completeProcess(ProcessCompletionType.NotCompleted);
    }

    public void confirm2() {
        this.confirm2 = true;

        this.completeProcess(ProcessCompletionType.CompletedNormally);
    }

    protected TestableTimeConstrainedProcess() {
        super();
    }

    @Override
    protected boolean completenessVerified() {
        return this.confirm1 && this.confirm2;
    }

    @Override
    protected Class<? extends ProcessTimedOut> processTimedOutEventType() {
        return TestableTimeConstrainedProcessTimedOut.class;
    }
}
