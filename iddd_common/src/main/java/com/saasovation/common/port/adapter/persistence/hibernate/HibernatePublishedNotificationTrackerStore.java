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

package com.saasovation.common.port.adapter.persistence.hibernate;

import java.util.List;

import org.hibernate.Query;

import com.saasovation.common.notification.Notification;
import com.saasovation.common.notification.PublishedNotificationTracker;
import com.saasovation.common.notification.PublishedNotificationTrackerStore;
import com.saasovation.common.persistence.PersistenceManagerProvider;

public class HibernatePublishedNotificationTrackerStore
    extends AbstractHibernateSession
    implements PublishedNotificationTrackerStore {

    private String typeName;

    public HibernatePublishedNotificationTrackerStore(
            PersistenceManagerProvider aPersistenceManagerProvider,
            String aPublishedNotificationTrackerType) {
        this();

        if (!aPersistenceManagerProvider.hasHibernateSession()) {
            throw new IllegalArgumentException("The PersistenceManagerProvider must have a Hibernate Session.");
        }

        this.setSession(aPersistenceManagerProvider.hibernateSession());
        this.setTypeName(aPublishedNotificationTrackerType);
    }

    public HibernatePublishedNotificationTrackerStore() {
        super();
    }

    @Override
    public PublishedNotificationTracker publishedNotificationTracker() {
        return this.publishedNotificationTracker(this.typeName());
    }

    @Override
    public PublishedNotificationTracker publishedNotificationTracker(String aTypeName) {
        Query query =
                this.session().createQuery(
                        "from PublishedNotificationTracker as pnt "
                        + "where pnt.typeName = ?");

        query.setParameter(0, aTypeName);

        PublishedNotificationTracker publishedNotificationTracker = null;

        try {
            publishedNotificationTracker =
                    (PublishedNotificationTracker) query.uniqueResult();
        } catch (Exception e) {
            // fall through
        }

        if (publishedNotificationTracker == null) {
            publishedNotificationTracker =
                    new PublishedNotificationTracker(this.typeName());
        }

        return publishedNotificationTracker;
    }

    @Override
    public void trackMostRecentPublishedNotification(
            PublishedNotificationTracker aPublishedNotificationTracker,
            List<Notification> aNotifications) {
        int lastIndex = aNotifications.size() - 1;

        if (lastIndex >= 0) {
            long mostRecentId = aNotifications.get(lastIndex).notificationId();

            aPublishedNotificationTracker.setMostRecentPublishedNotificationId(mostRecentId);

            this.session().save(aPublishedNotificationTracker);
        }
    }

    @Override
    public String typeName() {
        return typeName;
    }

    private void setTypeName(String aTypeName) {
        this.typeName = aTypeName;
    }
}
