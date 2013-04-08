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

package com.saasovation.agilepm.application;

import org.iq80.leveldb.DB;

import com.saasovation.agilepm.application.notification.NotificationApplicationService;
import com.saasovation.agilepm.port.adapter.persistence.LevelDBDatabasePath;
import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.domain.model.DomainEventSubscriber;
import com.saasovation.common.event.EventStore;
import com.saasovation.common.notification.NotificationPublisher;
import com.saasovation.common.notification.PublishedNotificationTrackerStore;
import com.saasovation.common.port.adapter.messaging.Exchanges;
import com.saasovation.common.port.adapter.notification.SlothMQNotificationPublisher;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBEventStore;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBProvider;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBPublishedNotificationTrackerStore;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public class ApplicationServiceLifeCycle {

    private static final DB database;
    private static final EventStore eventStore;
    private static NotificationApplicationService notificationApplicationService;
    private static NotificationPublisher notificationPublisher;
    private static NotificationPublisherTimer timer;
    private static PublishedNotificationTrackerStore publishedNotificationTrackerStore;

    static {
        database =
                LevelDBProvider
                    .instance()
                    .databaseFrom(LevelDBDatabasePath.agilePMPath());

        eventStore = new LevelDBEventStore(LevelDBDatabasePath.agilePMPath());

        timer = new NotificationPublisherTimer();

        publishedNotificationTrackerStore =
                new LevelDBPublishedNotificationTrackerStore(
                        LevelDBDatabasePath.agilePMPath(),
                        "saasovation.agilepm");

//        notificationPublisher =
//                new RabbitMQNotificationPublisher(
//                        eventStore,
//                        publishedNotificationTrackerStore,
//                        Exchanges.AGILEPM_EXCHANGE_NAME);

        notificationPublisher =
                new SlothMQNotificationPublisher(
                        eventStore,
                        publishedNotificationTrackerStore,
                        Exchanges.AGILEPM_EXCHANGE_NAME);

        notificationApplicationService = new NotificationApplicationService(notificationPublisher);

        timer.start();
    }

    public static void begin() {
        ApplicationServiceLifeCycle.begin(true);
    }

    public static void begin(boolean isListening) {
        if (isListening) {
            ApplicationServiceLifeCycle.listen();
        }

        LevelDBUnitOfWork.start(database);
    }

    public static void fail() {
        LevelDBUnitOfWork.current().rollback();
    }

    public static void fail(RuntimeException anException) {
        ApplicationServiceLifeCycle.fail();

        throw anException;
    }

    public static void fail(Throwable aThrowable) throws Throwable {
        ApplicationServiceLifeCycle.fail();

        throw aThrowable;
    }

    public static void success() {
        LevelDBUnitOfWork.current().commit();
    }

    private static void listen() {
        DomainEventPublisher.instance().reset();

        DomainEventPublisher
            .instance()
            .subscribe(new DomainEventSubscriber<DomainEvent>() {

                public void handleEvent(DomainEvent aDomainEvent) {
                    eventStore.append(aDomainEvent);
                }

                public Class<DomainEvent> subscribedToEventType() {
                    return DomainEvent.class; // all domain events
                }
            });
    }

    // TODO: need to monitor this...

    private static class NotificationPublisherTimer extends Thread {
        public NotificationPublisherTimer() {
            super();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    notificationApplicationService.publishNotifications();
                } catch (Exception e) {
                    System.out.println("Problem publishing notifications from ApplicationServiceLifeCycle.");
                }

                try {
                    Thread.sleep(100L);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }
}
