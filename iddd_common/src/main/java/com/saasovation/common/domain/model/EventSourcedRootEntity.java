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

package com.saasovation.common.domain.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saasovation.common.AssertionConcern;

public abstract class EventSourcedRootEntity extends AssertionConcern {

    private static final String MUTATOR_METHOD_NAME = "when";

    private static Map<String, Method> mutatorMethods =
            new HashMap<String, Method>();

    private List<DomainEvent> mutatingEvents;
    private int unmutatedVersion;

    public int mutatedVersion() {
        return this.unmutatedVersion() + 1;
    }

    public List<DomainEvent> mutatingEvents() {
        return this.mutatingEvents;
    }

    public int unmutatedVersion() {
        return this.unmutatedVersion;
    }

    protected EventSourcedRootEntity(
            List<DomainEvent> anEventStream,
            int aStreamVersion) {

        this();

        for (DomainEvent event : anEventStream) {
            this.mutateWhen(event);
        }

        this.setUnmutatedVersion(aStreamVersion);
    }

    protected EventSourcedRootEntity() {
        super();

        this.setMutatingEvents(new ArrayList<DomainEvent>(2));
    }

    protected void apply(DomainEvent aDomainEvent) {

        this.mutatingEvents().add(aDomainEvent);

        this.mutateWhen(aDomainEvent);
    }

    protected void mutateWhen(DomainEvent aDomainEvent) {

        Class<? extends EventSourcedRootEntity> rootType = this.getClass();

        Class<? extends DomainEvent> eventType = aDomainEvent.getClass();

        String key = rootType.getName() + ":" + eventType.getName();

        Method mutatorMethod = mutatorMethods.get(key);

        if (mutatorMethod == null) {
            mutatorMethod = this.cacheMutatorMethodFor(key, rootType, eventType);
        }

        try {
            mutatorMethod.invoke(this, aDomainEvent);

        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw new RuntimeException(
                        "Method "
                                + MUTATOR_METHOD_NAME
                                + "("
                                + eventType.getSimpleName()
                                + ") failed. See cause: "
                                + e.getMessage(),
                        e.getCause());
            }

            throw new RuntimeException(
                    "Method "
                            + MUTATOR_METHOD_NAME
                            + "("
                            + eventType.getSimpleName()
                            + ") failed. See cause: "
                            + e.getMessage(),
                    e);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "Method "
                            + MUTATOR_METHOD_NAME
                            + "("
                            + eventType.getSimpleName()
                            + ") failed because of illegal access. See cause: "
                            + e.getMessage(),
                    e);
        }
    }

    private Method cacheMutatorMethodFor(
            String aKey,
            Class<? extends EventSourcedRootEntity> aRootType,
            Class<? extends DomainEvent> anEventType) {

        synchronized (mutatorMethods) {
            try {
                Method method = this.hiddenOrPublicMethod(aRootType, anEventType);

                method.setAccessible(true);

                mutatorMethods.put(aKey, method);

                return method;

            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "I do not understand "
                                + MUTATOR_METHOD_NAME
                                + "("
                                + anEventType.getSimpleName()
                                + ") because: "
                                + e.getClass().getSimpleName() + ">>>" + e.getMessage(),
                        e);
            }
        }
    }

    private Method hiddenOrPublicMethod(
            Class<? extends EventSourcedRootEntity> aRootType,
            Class<? extends DomainEvent> anEventType)
    throws Exception {

        Method method = null;

        try {

            // assume protected or private...

            method = aRootType.getDeclaredMethod(
                    MUTATOR_METHOD_NAME,
                    anEventType);

        } catch (Exception e) {

            // then public...

            method = aRootType.getMethod(
                    MUTATOR_METHOD_NAME,
                    anEventType);
        }

        return method;
    }

    private void setMutatingEvents(List<DomainEvent> aMutatingEventsList) {
        this.mutatingEvents = aMutatingEventsList;
    }

    private void setUnmutatedVersion(int aStreamVersion) {
        this.unmutatedVersion = aStreamVersion;
    }
}
