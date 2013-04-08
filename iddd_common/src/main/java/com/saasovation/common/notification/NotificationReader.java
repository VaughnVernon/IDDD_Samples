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

import java.math.BigDecimal;
import java.util.Date;

import com.google.gson.JsonObject;
import com.saasovation.common.media.AbstractJSONMediaReader;

public class NotificationReader extends AbstractJSONMediaReader {

    private JsonObject event;

    public NotificationReader(String aJSONNotification) {
        super(aJSONNotification);

        this.setEvent(this.representation().get("event").getAsJsonObject());
    }

    public NotificationReader(JsonObject aRepresentationObject) {
        super(aRepresentationObject);

        this.setEvent(this.representation().get("event").getAsJsonObject());
    }

    public BigDecimal eventBigDecimalValue(String... aKeys) {
        String stringValue = this.stringValue(this.event(), aKeys);

        return stringValue == null ? null : new BigDecimal(stringValue);
    }

    public Boolean eventBooleanValue(String... aKeys) {
        String stringValue = this.stringValue(this.event(), aKeys);

        return stringValue == null ? null : Boolean.parseBoolean(stringValue);
    }

    public Date eventDateValue(String... aKeys) {
        String stringValue = this.stringValue(this.event(), aKeys);

        return stringValue == null ? null : new Date(Long.parseLong(stringValue));
    }

    public Double eventDoubleValue(String... aKeys) {
        String stringValue = this.stringValue(this.event(), aKeys);

        return stringValue == null ? null : Double.parseDouble(stringValue);
    }

    public Float eventFloatValue(String... aKeys) {
        String stringValue = this.stringValue(this.event(), aKeys);

        return stringValue == null ? null : Float.parseFloat(stringValue);
    }

    public Integer eventIntegerValue(String... aKeys) {
        String stringValue = this.stringValue(this.event(), aKeys);

        return stringValue == null ? null : Integer.parseInt(stringValue);
    }

    public Long eventLongValue(String... aKeys) {
        String stringValue = this.stringValue(this.event(), aKeys);

        return stringValue == null ? null : Long.parseLong(stringValue);
    }

    public String eventStringValue(String... aKeys) {
        String stringValue = this.stringValue(this.event(), aKeys);

        return stringValue;
    }

    public long notificationId() {
        long notificationId = this.longValue("notificationId");

        return notificationId;
    }

    public String notificationIdAsString() {
        String notificationId = this.stringValue("notificationId");

        return notificationId;
    }

    public Date occurredOn() {
        long time = this.longValue("occurredOn");

        return new Date(time);
    }

    public String typeName() {
        String typeName = this.stringValue("typeName");

        return typeName;
    }

    public int version() {
        int version = this.integerValue("version");

        return version;
    }

    private JsonObject event() {
        return this.event;
    }

    private void setEvent(JsonObject anEvent) {
        this.event = anEvent;
    }
}
