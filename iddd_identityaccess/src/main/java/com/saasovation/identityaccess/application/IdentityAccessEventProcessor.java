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

package com.saasovation.identityaccess.application;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.domain.model.DomainEventSubscriber;
import com.saasovation.common.event.EventStore;

@Aspect
public class IdentityAccessEventProcessor {

    @Autowired
    private EventStore eventStore;

    /**
     * Registers a IdentityAccessEventProcessor to listen
     * and forward all domain events to external subscribers.
     * This factory method is provided in the case where
     * Spring AOP wiring is not desired.
     */
    public static void register() {
        (new IdentityAccessEventProcessor()).listen();
    }

    /**
     * Constructs my default state.
     */
    public IdentityAccessEventProcessor() {
        super();
    }

    /**
     * Listens for all domain events and stores them.
     */
    @Before("execution(* com.saasovation.identityaccess.application.*.*(..))")
    public void listen() {
        DomainEventPublisher
            .instance()
            .subscribe(new DomainEventSubscriber<DomainEvent>() {

                public void handleEvent(DomainEvent aDomainEvent) {
                    store(aDomainEvent);
                }

                public Class<DomainEvent> subscribedToEventType() {
                    return DomainEvent.class; // all domain events
                }
            });
    }

    /**
     * Stores aDomainEvent to the event store.
     * @param aDomainEvent the DomainEvent to store
     */
    private void store(DomainEvent aDomainEvent) {
        this.eventStore().append(aDomainEvent);
    }

    /**
     * Answers my EventStore.
     * @return EventStore
     */
    private EventStore eventStore() {
        return this.eventStore;
    }
}
