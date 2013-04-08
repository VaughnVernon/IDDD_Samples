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

import com.saasovation.common.serializer.AbstractSerializer;

public class NotificationSerializer extends AbstractSerializer {

    private static NotificationSerializer notificationSerializer;

    public static synchronized NotificationSerializer instance() {
        if (NotificationSerializer.notificationSerializer == null) {
            NotificationSerializer.notificationSerializer = new NotificationSerializer();
        }

        return NotificationSerializer.notificationSerializer;
    }

    public NotificationSerializer(boolean isCompact) {
        this(false, isCompact);
    }

    public NotificationSerializer(boolean isPretty, boolean isCompact) {
        super(isPretty, isCompact);
    }

    public String serialize(Notification aNotification) {
        String serialization = this.gson().toJson(aNotification);

        return serialization;
    }

    public <T extends Notification> T deserialize(String aSerialization, final Class<T> aType) {
        T notification = this.gson().fromJson(aSerialization, aType);

        return notification;
    }

    private NotificationSerializer() {
        this(false, false);
    }
}
