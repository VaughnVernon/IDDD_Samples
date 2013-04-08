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

public interface Process {

    public enum ProcessCompletionType {
        NotCompleted,
        CompletedNormally,
        TimedOut
    }

    public long allowableDuration();

    public boolean canTimeout();

    public long currentDuration();

    public String description();

    public boolean didProcessingComplete();

    public void informTimeout(Date aTimedOutDate);

    public boolean isCompleted();

    public boolean isTimedOut();

    public boolean notCompleted();

    public ProcessCompletionType processCompletionType();

    public ProcessId processId();

    public Date startTime();

    public TimeConstrainedProcessTracker timeConstrainedProcessTracker();

    public Date timedOutDate();

    public long totalAllowableDuration();

    public int totalRetriesPermitted();
}
