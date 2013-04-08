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

package com.saasovation.identityaccess.application.representation;

import java.util.Collection;

import com.saasovation.common.media.Link;
import com.saasovation.common.notification.Notification;
import com.saasovation.common.notification.NotificationLog;

public class NotificationLogRepresentation {

    private boolean archived;
    private String id;
    private Collection<Notification> notifications;
    private Link linkNext;
    private Link linkPrevious;
    private Link linkSelf;

    public NotificationLogRepresentation(NotificationLog aLog) {
        this();

        this.initializeFrom(aLog);
    }

    public boolean getArchived() {
        return this.archived;
    }

    public String getId() {
        return this.id;
    }

    public void addNotification(Notification aNotification) {
        this.getNotifications().add(aNotification);
    }

    public Collection<Notification> getNotifications() {
        return this.notifications;
    }

    public int getNotificationsCount() {
        return this.getNotifications().size();
    }

    public boolean hasNotifications() {
        return this.getNotificationsCount() > 0;
    }

    public Link getLinkNext() {
        return this.linkNext;
    }

    public void setLinkNext(Link aNext) {
        this.linkNext = aNext;
    }

    public Link getLinkPrevious() {
        return this.linkPrevious;
    }

    public void setLinkPrevious(Link aPrevious) {
        this.linkPrevious = aPrevious;
    }

    public Link getLinkSelf() {
        return this.linkSelf;
    }

    public void setLinkSelf(Link aSelf) {
        this.linkSelf = aSelf;
    }

    protected NotificationLogRepresentation() {
        super();
    }

    private void initializeFrom(NotificationLog aLog) {
        this.setArchived(aLog.isArchived());
        this.setId(aLog.notificationLogId());
        this.setNotifications(aLog.notifications());
    }

    private void setArchived(boolean isArchived) {
        this.archived = isArchived;
    }

    private void setId(String anId) {
        this.id = anId;
    }

    private void setNotifications(
            Collection<Notification> aNotifications) {
        this.notifications = aNotifications;
    }
}
