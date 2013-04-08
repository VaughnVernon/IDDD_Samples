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

package com.saasovation.collaboration.domain.model.calendar;

import com.saasovation.collaboration.domain.model.collaborator.Participant;
import com.saasovation.common.AssertionConcern;

public final class CalendarSharer
        extends AssertionConcern
        implements Comparable<CalendarSharer> {

    private Participant participant;

    public CalendarSharer(Participant aParticipant) {
        this();

        this.setParticipant(aParticipant);
    }

    public Participant participant() {
        return this.participant;
    }

    @Override
    public int compareTo(CalendarSharer aCalendarSharer) {
        return this.participant().compareTo(aCalendarSharer.participant());
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            CalendarSharer typedObject = (CalendarSharer) anObject;
            equalObjects = this.participant().equals(typedObject.participant());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        return (72715 * 71) + this.participant().hashCode();
    }

    @Override
    public String toString() {
        return "CalendarSharer [participant=" + participant + "]";
    }

    protected CalendarSharer() {
        super();
    }

    private void setParticipant(Participant aParticipant) {
        this.assertArgumentNotNull(aParticipant, "Participant must be provided.");

        this.participant = aParticipant;
    }
}
