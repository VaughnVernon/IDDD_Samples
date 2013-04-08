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

package com.saasovation.common.port.adapter.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.event.sourcing.DispatchableDomainEvent;
import com.saasovation.common.event.sourcing.EventDispatcher;

public abstract class AbstractProjection implements EventDispatcher {

    private static final String PROJECTION_METHOD_NAME = "when";

    private static Map<String, Method> projectionMethods =
            new HashMap<String, Method>();

    protected AbstractProjection() {
        super();
    }

    protected void execute(PreparedStatement aStatement) throws Exception {
        try {
            aStatement.executeUpdate();
        } finally {
            aStatement.close();
        }
    }

    protected boolean exists(String aQuery, String... anArguments) throws Exception {
        boolean exists = false;

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement =
                ConnectionProvider
                    .connection()
                    .prepareStatement(aQuery);

            for (int idx = 0; idx < anArguments.length; ++idx) {
                statement.setString(idx+1, anArguments[idx]);
            }

            result = statement.executeQuery();

            if (result.next()) {
                exists = true;
            }

        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (Exception e) {
                    // ignore
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        return exists;
    }

    protected void projectWhen(DispatchableDomainEvent aDispatchableDomainEvent) {

        if (!this.understands(aDispatchableDomainEvent)) {
            return;
        }

//        System.out.println("Dispatching: " + aDispatchableDomainEvent.domainEvent().getClass().getSimpleName());

        DomainEvent domainEvent = aDispatchableDomainEvent.domainEvent();

        Class<? extends AbstractProjection> rootType = this.getClass();

        Class<? extends DomainEvent> eventType = domainEvent.getClass();

        String key = rootType.getName() + ":" + eventType.getName();

        Method mutatorMethod = projectionMethods.get(key);

        if (mutatorMethod == null) {
            mutatorMethod = this.cacheProjectionMethodFor(key, rootType, eventType);
        }

        try {
            mutatorMethod.invoke(this, domainEvent);

        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw new RuntimeException(
                        "Method "
                                + PROJECTION_METHOD_NAME
                                + "("
                                + eventType.getSimpleName()
                                + ") failed. See cause: "
                                + e.getMessage(),
                        e.getCause());
            }

            throw new RuntimeException(
                    "Method "
                            + PROJECTION_METHOD_NAME
                            + "("
                            + eventType.getSimpleName()
                            + ") failed. See cause: "
                            + e.getMessage(),
                    e);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "Method "
                            + PROJECTION_METHOD_NAME
                            + "("
                            + eventType.getSimpleName()
                            + ") failed because of illegal access. See cause: "
                            + e.getMessage(),
                    e);
        }
    }

    protected boolean understandsAnyOf(
            Class<?> aDispatchedType,
            Class<?>[] anUnderstoodEventTypes) {

        for (Class<?> eventType : anUnderstoodEventTypes) {
            if (aDispatchedType == eventType) {
                return true;
            }
        }

//        System.out.println(this.getClass().getSimpleName() + " doesn't understand: " + aDispatchedType.getSimpleName());

        return false;
    }

    private Method cacheProjectionMethodFor(
            String aKey,
            Class<? extends AbstractProjection> aRootType,
            Class<? extends DomainEvent> anEventType) {

        synchronized (projectionMethods) {
            try {
                Method method = this.hiddenOrPublicMethod(aRootType, anEventType);

                method.setAccessible(true);

                projectionMethods.put(aKey, method);

                return method;

            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "I do not understand "
                                + PROJECTION_METHOD_NAME
                                + "("
                                + anEventType.getSimpleName()
                                + ") because: "
                                + e.getClass().getSimpleName() + ">>>" + e.getMessage(),
                        e);
            }
        }
    }

    private Method hiddenOrPublicMethod(
            Class<? extends AbstractProjection> aRootType,
            Class<? extends DomainEvent> anEventType)
    throws Exception {

        Method method = null;

        try {

            // assume protected or private...

            method = aRootType.getDeclaredMethod(
                    PROJECTION_METHOD_NAME,
                    anEventType);

        } catch (Exception e) {

            // then public...

            method = aRootType.getMethod(
                    PROJECTION_METHOD_NAME,
                    anEventType);
        }

        return method;
    }
}
