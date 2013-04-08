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

package com.saasovation.common.port.adapter.persistence.leveldb;

import java.util.List;

import com.saasovation.common.notification.Notification;
import com.saasovation.common.notification.PublishedNotificationTracker;
import com.saasovation.common.notification.PublishedNotificationTrackerStore;

public class LevelDBPublishedNotificationTrackerStore
    extends AbstractLevelDBRepository
    implements PublishedNotificationTrackerStore {

    private static final String PRIMARY = "PUBNOTIF_TRACKER#PK";

    private String typeName;

    public LevelDBPublishedNotificationTrackerStore(
            String aLevelDBDirectoryPath,
            String aPublishedNotificationTrackerType) {
        super(aLevelDBDirectoryPath);

        this.setTypeName(aPublishedNotificationTrackerType);
    }

    @Override
    public PublishedNotificationTracker publishedNotificationTracker() {
        return this.publishedNotificationTracker(this.typeName());
    }

    @Override
    public PublishedNotificationTracker publishedNotificationTracker(String aTypeName) {
        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, this.typeName());

        PublishedNotificationTracker publishedNotificationTracker =
                uow.readObject(primaryKey, PublishedNotificationTracker.class);

        if (publishedNotificationTracker == null) {
            publishedNotificationTracker = new PublishedNotificationTracker(this.typeName());
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

            LevelDBKey lockKey = new LevelDBKey(PRIMARY, this.typeName());

            LevelDBUnitOfWork uow = LevelDBUnitOfWork.start(this.database());

            uow.lock(lockKey.key());

            this.save(aPublishedNotificationTracker, uow);
        }
    }

    @Override
    public String typeName() {
        return this.typeName;
    }

    private void save(
            PublishedNotificationTracker aPublishedNotificationTracker,
            LevelDBUnitOfWork aUoW) {

        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, this.typeName());

        aUoW.write(primaryKey, aPublishedNotificationTracker);
    }

    private void setTypeName(String aTypeName) {
        this.typeName = aTypeName;
    }
}
