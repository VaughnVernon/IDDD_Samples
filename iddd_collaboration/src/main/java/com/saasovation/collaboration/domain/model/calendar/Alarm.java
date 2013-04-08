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

import com.saasovation.common.AssertionConcern;

public final class Alarm extends AssertionConcern {

    private int alarmUnits;
    private AlarmUnitsType alarmUnitsType;

    public Alarm(AlarmUnitsType anAlarmUnitsType, int anAlarmUnits) {
        this();

        this.setAlarmUnits(anAlarmUnits);
        this.setAlarmUnitsType(anAlarmUnitsType);
    }

    public int alarmUnits() {
        return this.alarmUnits;
    }

    public AlarmUnitsType alarmUnitsType() {
        return this.alarmUnitsType;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Alarm typedObject = (Alarm) anObject;
            equalObjects =
                this.alarmUnitsType().equals(typedObject.alarmUnitsType()) &&
                this.alarmUnits() == typedObject.alarmUnits();
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (48483 * 97)
            + this.alarmUnitsType().hashCode()
            + this.alarmUnits();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Alarm [alarmUnits=" + alarmUnits + ", alarmUnitsType=" + alarmUnitsType + "]";
    }

    protected Alarm() {
        super();
    }

    protected void setAlarmUnits(int anAlarmUnits) {
        this.alarmUnits = anAlarmUnits;
    }

    protected void setAlarmUnitsType(AlarmUnitsType anAlarmUnitsType) {
        this.alarmUnitsType = anAlarmUnitsType;
    }
}
