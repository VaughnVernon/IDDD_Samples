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

public class TestableDomainEvent implements DomainEvent {

    private int eventVersion;
    private long id;
    private String name;
    private Date occurredOn;

    public TestableDomainEvent(long anId, String aName) {
        super();

        this.setEventVersion(1);
        this.setId(anId);
        this.setName(aName);
        this.setOccurredOn(new Date());
    }

    public int eventVersion() {
        return eventVersion;
    }

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public Date occurredOn() {
        return this.occurredOn;
    }

    private void setEventVersion(int anEventVersion) {
        this.eventVersion = anEventVersion;
    }

    private void setId(long id) {
        this.id = id;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setOccurredOn(Date occurredOn) {
        this.occurredOn = occurredOn;
    }
}
