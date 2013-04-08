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

package com.saasovation.collaboration.port.adapter.persistence.view;

import java.util.ArrayList;
import java.util.List;

import com.saasovation.common.event.sourcing.DispatchableDomainEvent;
import com.saasovation.common.event.sourcing.EventDispatcher;

public class MySQLProjectionDispatcher implements EventDispatcher {

    private List<EventDispatcher> registeredProjections;

    public MySQLProjectionDispatcher(EventDispatcher aParentEventDispatcher) {
        super();

        aParentEventDispatcher.registerEventDispatcher(this);

        this.setRegisteredProjections(new ArrayList<EventDispatcher>());
    }

    @Override
    public void dispatch(DispatchableDomainEvent aDispatchableDomainEvent) {
        for (EventDispatcher projection : this.registeredProjections()) {
            projection.dispatch(aDispatchableDomainEvent);
        }
    }

    @Override
    public void registerEventDispatcher(EventDispatcher aProjection) {
        this.registeredProjections().add(aProjection);
    }

    @Override
    public boolean understands(DispatchableDomainEvent aDispatchableDomainEvent) {
        return true;
    }

    private List<EventDispatcher> registeredProjections() {
        return this.registeredProjections;
    }

    private void setRegisteredProjections(List<EventDispatcher> aDispatchers) {
        this.registeredProjections = aDispatchers;
    }
}
