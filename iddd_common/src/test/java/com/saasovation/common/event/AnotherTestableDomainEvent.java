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

package com.saasovation.common.event;

import java.util.Date;

import com.saasovation.common.domain.model.DomainEvent;

public class AnotherTestableDomainEvent implements DomainEvent {

    private int eventVersion;
    private Date occurredOn;
    private double value;

    public AnotherTestableDomainEvent(double aValue) {
        super();

        this.setEventVersion(1);
        this.setOccurredOn(new Date());
        this.setValue(aValue);
    }

    public int eventVersion() {
        return eventVersion;
    }

    @Override
    public Date occurredOn() {
        return this.occurredOn;
    }

    public double value() {
        return this.value;
    }

    private void setEventVersion(int eventVersion) {
        this.eventVersion = eventVersion;
    }

    private void setOccurredOn(Date occurredOn) {
        this.occurredOn = occurredOn;
    }

    private void setValue(double value) {
        this.value = value;
    }
}
