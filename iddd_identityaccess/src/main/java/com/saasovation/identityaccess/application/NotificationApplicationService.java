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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.saasovation.common.event.EventStore;
import com.saasovation.common.notification.NotificationLog;
import com.saasovation.common.notification.NotificationLogFactory;
import com.saasovation.common.notification.NotificationLogId;
import com.saasovation.common.notification.NotificationPublisher;

public class NotificationApplicationService {

    @Autowired
    private EventStore eventStore;

    @Autowired
    private NotificationPublisher notificationPublisher;

    public NotificationApplicationService() {
        super();
    }

    @Transactional(readOnly=true)
    public NotificationLog currentNotificationLog() {
        NotificationLogFactory factory = new NotificationLogFactory(this.eventStore());

        return factory.createCurrentNotificationLog();
    }

    @Transactional(readOnly=true)
    public NotificationLog notificationLog(String aNotificationLogId) {
        NotificationLogFactory factory = new NotificationLogFactory(this.eventStore());

        return factory.createNotificationLog(new NotificationLogId(aNotificationLogId));
    }

    @Transactional
    public void publishNotifications() {
        this.notificationPublisher().publishNotifications();
    }

    protected EventStore eventStore() {
        return this.eventStore;
    }

    protected NotificationPublisher notificationPublisher() {
        return this.notificationPublisher;
    }
}
