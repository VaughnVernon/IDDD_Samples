package com.saasovation.identityaccess.application;

import com.saasovation.common.event.EventStore;
import com.saasovation.common.event.TestableDomainEvent;
import com.saasovation.common.notification.NotificationLog;
import com.saasovation.common.notification.NotificationLogFactory;
import com.saasovation.common.notification.NotificationLogId;
import com.saasovation.common.notification.NotificationPublisher;

public class NotificationApplicationServiceTest extends ApplicationServiceTest {

    private EventStore eventStore;
    private NotificationApplicationService notificationApplicationService;
    private NotificationPublisher notificationPublisher;

    public NotificationApplicationServiceTest() {
        super();
    }

    public void testCurrentNotificationLog() throws Exception {
        NotificationLog log =
                this.notificationApplicationService.currentNotificationLog();

        assertTrue(NotificationLogFactory.notificationsPerLog() >= log.totalNotifications());
        assertTrue(eventStore.countStoredEvents() >= log.totalNotifications());
        assertFalse(log.hasNextNotificationLog());
        assertTrue(log.hasPreviousNotificationLog());
        assertFalse(log.isArchived());
    }

    public void testNotificationLog() throws Exception {
        NotificationLogId id = NotificationLogId.first(NotificationLogFactory.notificationsPerLog());

        NotificationLog log = this.notificationApplicationService.notificationLog(id.encoded());

        assertEquals(NotificationLogFactory.notificationsPerLog(), log.totalNotifications());
        assertTrue(eventStore.countStoredEvents() >= log.totalNotifications());
        assertTrue(log.hasNextNotificationLog());
        assertFalse(log.hasPreviousNotificationLog());
        assertTrue(log.isArchived());
    }

    public void testPublishNotifications() throws Exception {
        notificationApplicationService.publishNotifications();

        assertTrue(notificationPublisher.internalOnlyTestConfirmation());
    }

    protected void setUp() throws Exception {
        super.setUp();

        this.notificationApplicationService =
                ApplicationServiceRegistry
                    .notificationApplicationService();

        this.eventStore = notificationApplicationService.eventStore();

        this.notificationPublisher = notificationApplicationService.notificationPublisher();

        for (int idx = 1; idx <= 31; ++idx) {
            this.eventStore.append(new TestableDomainEvent(idx, "Event: " + idx));
        }
    }
}
