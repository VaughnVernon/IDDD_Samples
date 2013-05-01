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

import static com.saasovation.common.port.adapter.messaging.Exchanges.*;

import java.util.*;

import junit.framework.TestCase;

import com.saasovation.common.notification.NotificationReader;
import com.saasovation.common.port.adapter.messaging.Exchanges;
import com.saasovation.common.port.adapter.messaging.slothmq.SlothServer;

public abstract class EventTrackingTestCase extends TestCase {

    protected TestAgilePMRabbitMQExchangeListener agilePmRabbitMQExchangeListener;
    protected TestAgilePMSlothMQExchangeListener agilePmSlothMQExchangeListener;
    protected TestCollaborationRabbitMQExchangeListener collaborationRabbitMQExchangeListener;
    protected TestCollaborationSlothMQExchangeListener collaborationSlothMQExchangeListener;
    protected TestIdentityAccessRabbitMQExchangeListener identityAccessRabbitMQExchangeListener;
    protected TestIdentityAccessSlothMQExchangeListener identityAccessSlothMQExchangeListener;

    private List<Class<? extends DomainEvent>> handledEvents;
    private Map<String, String> handledNotifications;

    protected EventTrackingTestCase() {
        super();
    }

    protected void expectedEvent(Class<? extends DomainEvent> aDomainEventType) {
        this.expectedEvent(aDomainEventType, 1);
    }

    protected void expectedEvent(Class<? extends DomainEvent> aDomainEventType, int aTotal) {
        int count = 0;

        for (Class<? extends DomainEvent> type : this.handledEvents) {
            if (type == aDomainEventType) {
                ++count;
            }
        }

        if (count != aTotal) {
            throw new IllegalStateException("Expected " + aTotal + " " + aDomainEventType.getSimpleName() + " events, but handled "
                    + this.handledEvents.size() + " events: " + this.handledEvents);
        }
    }

    protected void expectedEvents(int anEventCount) {
        if (this.handledEvents.size() != anEventCount) {
            throw new IllegalStateException("Expected " + anEventCount + " events, but handled " + this.handledEvents.size()
                    + " events: " + this.handledEvents);
        }
    }

    protected void expectedNotification(Class<? extends DomainEvent> aNotificationType) {
        this.expectedNotification(aNotificationType, 1);
    }

    protected void expectedNotification(Class<? extends DomainEvent> aNotificationType, int aTotal) {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            // ignore
        }

        int count = 0;

        String notificationTypeName = aNotificationType.getName();

        for (String type : this.handledNotifications.values()) {
            if (type.equals(notificationTypeName)) {
                // System.out.println("MATCHED: " + type);
                // System.out.println("WITH: " + notificationTypeName);
                ++count;
            }
        }

        if (count != aTotal) {
            throw new IllegalStateException("Expected " + aTotal + " " + aNotificationType.getSimpleName()
                    + " notifications, but handled " + this.handledNotifications.size() + " notifications: "
                    + this.handledNotifications.values());
        }
    }

    protected void expectedNotifications(int anNotificationCount) {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            // ignore
        }

        if (this.handledNotifications.size() != anNotificationCount) {
            throw new IllegalStateException("Expected " + anNotificationCount + " notifications, but handled "
                    + this.handledNotifications.size() + " notifications: " + this.handledNotifications.values());
        }
    }

    @Override
    protected void setUp() throws Exception {
        Thread.sleep(100L);

        SlothServer.executeInProcessDetachedServer();

        Thread.sleep(100L);

        DomainEventPublisher.instance().reset();

        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<DomainEvent>() {
            @Override
            public void handleEvent(DomainEvent aDomainEvent) {
                handledEvents.add(aDomainEvent.getClass());
            }

            @Override
            public Class<DomainEvent> subscribedToEventType() {
                return DomainEvent.class;
            }
        });

        this.handledEvents = new ArrayList<Class<? extends DomainEvent>>();
        this.handledNotifications = new HashMap<String, String>();

        this.agilePmRabbitMQExchangeListener = new TestAgilePMRabbitMQExchangeListener();
        this.collaborationRabbitMQExchangeListener = new TestCollaborationRabbitMQExchangeListener();
        this.identityAccessRabbitMQExchangeListener = new TestIdentityAccessRabbitMQExchangeListener();

        clearExchangeListeners();

        // this.agilePmSlothMQExchangeListener = new TestAgilePMSlothMQExchangeListener();
        // this.collaborationSlothMQExchangeListener = new TestCollaborationSlothMQExchangeListener();
        // this.identityAccessSlothMQExchangeListener = new TestIdentityAccessSlothMQExchangeListener();

        Thread.sleep(200L);
    }

    private void clearExchangeListeners() throws InterruptedException {
        // At beginning of the test, give MQExchangeListeners time to receive
        // messages from queues which were published by previous tests.
        // Since RabbitMQ Java Client does not allow queue listing or cleaning
        // all queues at once, we can just consume all messages and do
        // nothing with them as a work-around.
        Thread.sleep(500L);

        this.agilePmRabbitMQExchangeListener.clear();
        this.collaborationRabbitMQExchangeListener.clear();
        this.identityAccessRabbitMQExchangeListener.clear();
    }

    @Override
    protected void tearDown() throws Exception {
        this.agilePmRabbitMQExchangeListener.close();
        this.collaborationRabbitMQExchangeListener.close();
        this.identityAccessRabbitMQExchangeListener.close();

        // this.agilePmSlothMQExchangeListener.close();
        // this.collaborationSlothMQExchangeListener.close();
        // this.identityAccessSlothMQExchangeListener.close();
        //
        // SlothClient.instance().closeAll();

        super.tearDown();
    }

    private abstract class TestExchangeListener extends com.saasovation.common.port.adapter.messaging.rabbitmq.ExchangeListener {

        public void clear() {
            handledEvents.clear();
            handledNotifications.clear();
        }

        @Override
        protected String[] listensTo() {
            return null; // receive all
        }

        @Override
        protected void filteredDispatch(String aType, String aTextMessage) {
            synchronized (handledNotifications) {
                NotificationReader notification = new NotificationReader(aTextMessage);
                handledNotifications.put(notification.notificationIdAsString(), aType);
            }
        }
    }

    protected class TestAgilePMRabbitMQExchangeListener extends TestExchangeListener {
        @Override
        protected String exchangeName() {
            return AGILEPM_EXCHANGE_NAME;
        }
    }

    protected class TestCollaborationRabbitMQExchangeListener extends TestExchangeListener {
        @Override
        protected String exchangeName() {
            return COLLABORATION_EXCHANGE_NAME;
        }
    }

    protected class TestIdentityAccessRabbitMQExchangeListener extends TestExchangeListener {
        @Override
        protected String exchangeName() {
            return IDENTITY_ACCESS_EXCHANGE_NAME;
        }
    }

    protected class TestAgilePMSlothMQExchangeListener extends
            com.saasovation.common.port.adapter.messaging.slothmq.ExchangeListener {

        TestAgilePMSlothMQExchangeListener() {
            super();
        }

        @Override
        protected String exchangeName() {
            return Exchanges.AGILEPM_EXCHANGE_NAME;
        }

        @Override
        protected void filteredDispatch(String aType, String aTextMessage) {
            synchronized (handledNotifications) {
                NotificationReader notification = new NotificationReader(aTextMessage);
                handledNotifications.put(notification.notificationIdAsString(), aType);
            }
        }

        @Override
        protected String[] listensTo() {
            return null; // receive all
        }

        @Override
        protected String name() {
            return this.getClass().getName();
        }
    }

    protected class TestCollaborationSlothMQExchangeListener extends
            com.saasovation.common.port.adapter.messaging.slothmq.ExchangeListener {

        TestCollaborationSlothMQExchangeListener() {
            super();
        }

        @Override
        protected String exchangeName() {
            return Exchanges.COLLABORATION_EXCHANGE_NAME;
        }

        @Override
        protected void filteredDispatch(String aType, String aTextMessage) {
            synchronized (handledNotifications) {
                NotificationReader notification = new NotificationReader(aTextMessage);
                handledNotifications.put(notification.notificationIdAsString(), aType);
            }
        }

        @Override
        protected String[] listensTo() {
            return new String[0]; // receive all
        }

        @Override
        protected String name() {
            return this.getClass().getName();
        }
    }

    protected class TestIdentityAccessSlothMQExchangeListener extends
            com.saasovation.common.port.adapter.messaging.slothmq.ExchangeListener {

        TestIdentityAccessSlothMQExchangeListener() {
            super();
        }

        @Override
        protected String exchangeName() {
            return Exchanges.IDENTITY_ACCESS_EXCHANGE_NAME;
        }

        @Override
        protected void filteredDispatch(String aType, String aTextMessage) {
            synchronized (handledNotifications) {
                NotificationReader notification = new NotificationReader(aTextMessage);
                handledNotifications.put(notification.notificationIdAsString(), aType);
            }
        }

        @Override
        protected String[] listensTo() {
            return null; // receive all
        }

        @Override
        protected String name() {
            return this.getClass().getName();
        }
    }
}
