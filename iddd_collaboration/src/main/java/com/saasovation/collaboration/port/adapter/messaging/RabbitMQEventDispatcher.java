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

package com.saasovation.collaboration.port.adapter.messaging;

import com.saasovation.common.event.sourcing.DispatchableDomainEvent;
import com.saasovation.common.event.sourcing.EventDispatcher;
import com.saasovation.common.notification.Notification;
import com.saasovation.common.notification.NotificationSerializer;
import com.saasovation.common.port.adapter.messaging.Exchanges;
import com.saasovation.common.port.adapter.messaging.rabbitmq.ConnectionSettings;
import com.saasovation.common.port.adapter.messaging.rabbitmq.Exchange;
import com.saasovation.common.port.adapter.messaging.rabbitmq.MessageParameters;
import com.saasovation.common.port.adapter.messaging.rabbitmq.MessageProducer;

public class RabbitMQEventDispatcher implements EventDispatcher {

    private MessageProducer messageProducer;

    public RabbitMQEventDispatcher(EventDispatcher aParentEventDispatcher) {
        super();

        this.initializeMessageProducer();
        aParentEventDispatcher.registerEventDispatcher(this);
    }

    @Override
    public void dispatch(DispatchableDomainEvent aDispatchableDomainEvent) {
        Notification notification =
                new Notification(
                        aDispatchableDomainEvent.eventId(),
                        aDispatchableDomainEvent.domainEvent());

        MessageParameters messageParameters =
                MessageParameters.durableTextParameters(
                        notification.typeName(),
                        Long.toString(notification.notificationId()),
                        notification.occurredOn());

        String serializedNotification =
                NotificationSerializer.instance().serialize(notification);

        this.messageProducer.send(serializedNotification, messageParameters);
    }

    @Override
    public void registerEventDispatcher(EventDispatcher anEventDispatcher) {
        throw new UnsupportedOperationException("Cannot register additional dispatchers.");
    }

    @Override
    public boolean understands(DispatchableDomainEvent aDispatchableDomainEvent) {
        return true;
    }

    private void initializeMessageProducer() {
        Exchange exchange =
                Exchange.fanOutInstance(
                        ConnectionSettings.instance(),
                        Exchanges.COLLABORATION_EXCHANGE_NAME,
                        true);

        this.messageProducer = MessageProducer.instance(exchange);
    }
}
