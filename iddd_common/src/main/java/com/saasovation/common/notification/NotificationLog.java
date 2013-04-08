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

package com.saasovation.common.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationLog {

    private boolean archived;
    private List<Notification> notifications;
    private String notificationLogId;
    private String nextNotificationLogId;
    private String previousNotificationLogId;

    public NotificationLog(
            String aNotificationLogId,
            String aNextNotificationLogId,
            String aPreviousNotificationLogId,
            List<Notification> aNotifications,
            boolean anArchivedIndicator) {

        super();

        this.setArchived(anArchivedIndicator);
        this.setNextNotificationLogId(aNextNotificationLogId);
        this.setNotificationLogId(aNotificationLogId);
        this.setNotifications(aNotifications);
        this.setPreviousNotificationLogId(aPreviousNotificationLogId);
    }

    public boolean isArchived() {
        return this.archived;
    }

    public List<Notification> notifications() {
        return Collections.unmodifiableList(this.notifications);
    }

    public NotificationLogId decodedNotificationLogId() {
        return new NotificationLogId(this.notificationLogId());
    }

    public String notificationLogId() {
        return this.notificationLogId;
    }

    public NotificationLogId decodedNextNotificationLogId() {
        return new NotificationLogId(this.nextNotificationLogId());
    }

    public String nextNotificationLogId() {
        return this.nextNotificationLogId;
    }

    public boolean hasNextNotificationLog() {
        return this.nextNotificationLogId() != null;
    }

    public NotificationLogId decodedPreviousNotificationLogId() {
        return new NotificationLogId(this.previousNotificationLogId());
    }

    public String previousNotificationLogId() {
        return this.previousNotificationLogId;
    }

    public boolean hasPreviousNotificationLog() {
        return this.previousNotificationLogId() != null;
    }

    public int totalNotifications() {
        return this.notifications.size();
    }

    protected NotificationLog() {
        super();

        this.setNotifications(new ArrayList<Notification>());
    }

    private void setNotifications(List<Notification> aNotifications) {
        this.notifications = aNotifications;
    }

    private void setNotificationLogId(String aNotificationLogId) {
        this.notificationLogId = aNotificationLogId;
    }

    private void setNextNotificationLogId(String aNextNotificationLogId) {
        this.nextNotificationLogId = aNextNotificationLogId;
    }

    private void setPreviousNotificationLogId(String aPreviousNotificationLogId) {
        this.previousNotificationLogId = aPreviousNotificationLogId;
    }

    private void setArchived(boolean aArchived) {
        this.archived = aArchived;
    }
}
