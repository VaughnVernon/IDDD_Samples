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

package com.saasovation.common.port.adapter.messaging.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.saasovation.common.port.adapter.messaging.MessageException;

/**
 * I am a queue that simplifies RabbitMQ queues.
 *
 * @author Vaughn Vernon
 */
public class Queue extends BrokerChannel {

    /**
     * Answers a new instance of a Queue with the name aName. The underlying
     * queue is non-durable, non-exclusive, and not auto-deleted.
     * @param aConnectionSettings the ConnectionSettings
     * @param aName the String name of the queue
     * @return Queue
     */
    public static Queue instance(
            ConnectionSettings aConnectionSettings,
            String aName) {
        return new Queue(aConnectionSettings, aName, false, false, false);
    }

    /**
     * Answers a new instance of a Queue with the name aName. The underlying
     * queue durability, exclusivity, and deletion properties are specified by
     * explicit parameters.
     * @param aConnectionSettings the ConnectionSettings
     * @param aName the String name of the queue
     * @param isDurable the boolean indicating whether or not I am durable
     * @param isExclusive the boolean indicating whether or not I am exclusive
     * @param isAutoDeleted the boolean indicating whether or not I should be auto-deleted
     * @return Queue
     */
    public static Queue instance(
            ConnectionSettings aConnectionSettings,
            String aName,
            boolean isDurable,
            boolean isExclusive,
            boolean isAutoDeleted) {
        return new Queue(aConnectionSettings, aName, isDurable, isExclusive, isAutoDeleted);
    }

    /**
     * Answers a new instance of a Queue with the name aName. The underlying
     * queue is durable, is non-exclusive, and not auto-deleted.
     * @param aConnectionSettings the ConnectionSettings
     * @param aName the String name of the queue
     * @return Queue
     */
    public static Queue durableInstance(
            ConnectionSettings aConnectionSettings,
            String aName) {
        return new Queue(aConnectionSettings, aName, true, false, false);
    }

    /**
     * Answers a new instance of a Queue with the name aName. The underlying
     * queue is durable, exclusive, and not auto-deleted.
     * @param aConnectionSettings the ConnectionSettings
     * @param aName the String name of the queue
     * @return Queue
     */
    public static Queue durableExclsuiveInstance(
            ConnectionSettings aConnectionSettings,
            String aName) {
        return new Queue(aConnectionSettings, aName, true, true, false);
    }

    /**
     * Answers a new instance of a Queue that is bound to anExchange, and
     * is ready to participate as an exchange subscriber (pub/sub). The
     * connection and channel of anExchange are reused. The Queue is
     * uniquely named by the server, non-durable, exclusive, and auto-deleted.
     * This Queue style best works as a temporary fan-out subscriber.
     * @param anExchange the Exchange to bind with the new Queue
     * @return Queue
     */
    public static Queue exchangeSubscriberInstance(Exchange anExchange) {

        Queue queue = new Queue(anExchange, "", false, true, true);

        try {
            queue.channel().queueBind(queue.name(), anExchange.name(), "");
        } catch (IOException e) {
            throw new MessageException("Failed to bind the queue and exchange.", e);
        }

        return queue;
    }

    /**
     * Answers a new instance of a Queue that is bound to anExchange, and
     * is ready to participate as an exchange subscriber (pub/sub). The
     * connection and channel of anExchange are reused. The Queue is
     * uniquely named by the server, non-durable, exclusive, and auto-deleted.
     * The queue is bound to all routing keys in aRoutingKeys. This Queue
     * style best works as a temporary direct or topic subscriber.
     * @param anExchange the Exchange to bind with the new Queue
     * @return Queue
     */
    public static Queue exchangeSubscriberInstance(
            Exchange anExchange,
            String[] aRoutingKeys) {

        Queue queue = new Queue(anExchange, "", false, true, true);

        try {
            for (String routingKey : aRoutingKeys) {
                queue.channel().queueBind(queue.name(), anExchange.name(), routingKey);
            }
        } catch (IOException e) {
            throw new MessageException("Failed to bind the queue and exchange.", e);
        }

        return queue;
    }

    /**
     * Answers a new instance of a Queue that is bound to anExchange, and
     * is ready to participate as an exchange subscriber (pub/sub). The
     * connection and channel of anExchange are reused. The Queue is named
     * by aName, unless it is empty, in which case the name is generated by
     * the broker. The Queue is bound to all routing keys in aRoutingKeys,
     * or to no routing key if aRoutingKeys is empty. The Queue has the
     * qualities specified by isDurable, isExclusive, isAutoDeleted. This
     * factory is provided for ultimate flexibility in case no other
     * exchange-queue binder factories fit the needs of the client.
     * @param anExchange the Exchange to bind with the new Queue
     * @param aName the String name of the queue
     * @param aRoutingKeys the routing keys to bind the queue to
     * @param isDurable the boolean indicating whether or not I am durable
     * @param isExclusive the boolean indicating whether or not I am exclusive
     * @param isAutoDeleted the boolean indicating whether or not I should be auto-deleted
     * @return Queue
     */
    public static Queue exchangeSubscriberInstance(
            Exchange anExchange,
            String aName,
            String[] aRoutingKeys,
            boolean isDurable,
            boolean isExclusive,
            boolean isAutoDeleted) {

        Queue queue = new Queue(anExchange, aName, isDurable, isExclusive, isAutoDeleted);

        try {
            if (aRoutingKeys.length == 0) {
                queue.channel().queueBind(queue.name(), anExchange.name(), "");
            } else {
                for (String routingKey : aRoutingKeys) {
                    queue.channel().queueBind(queue.name(), anExchange.name(), routingKey);
                }
            }
        } catch (IOException e) {
            throw new MessageException("Failed to bind the queue and exchange.", e);
        }

        return queue;
    }

    /**
     * Answers a new instance of a Queue that is bound to anExchange, and
     * is ready to participate as an exchange subscriber (pub/sub). The
     * connection and channel of anExchange are reused. The Queue is
     * named by aName, which must be provided and should be unique to the
     * individual subscriber. The Queue is durable, non-exclusive, and
     * is not auto-deleted. This Queue style best works as a durable
     * fan-out exchange subscriber.
     * @param anExchange the Exchange to bind with the new Queue
     * @param aName the String name of the queue, which must be unique, non-empty
     * @return Queue
     */
    public static Queue individualExchangeSubscriberInstance(
            Exchange anExchange,
            String aName) {

        if (aName == null || aName.isEmpty()) {
            throw new IllegalArgumentException("An individual subscriber must be named.");
        }

        Queue queue = new Queue(anExchange, aName, true, false, false);

        try {
            queue.channel().queueBind(queue.name(), anExchange.name(), "");
        } catch (IOException e) {
            throw new MessageException("Failed to bind the queue and exchange.", e);
        }

        return queue;
    }

    /**
     * Answers a new instance of a Queue that is bound to anExchange, and
     * is ready to participate as an exchange subscriber (pub/sub). The
     * connection and channel of anExchange are reused. The Queue is
     * by aName, which must be provided and should be unique to the
     * individual subscriber. The queue is bound to all routing keys in
     * aRoutingKeys. The Queue is durable, non-exclusive, and is not
     * auto-deleted. This Queue style best works as a durable direct or
     * topic exchange subscriber.
     * @param anExchange the Exchange to bind with the new Queue
     * @param aName the String name of the queue, which must be unique, non-empty
     * @param aRoutingKeys the routing keys to bind the queue to
     * @return Queue
     */
    public static Queue individualExchangeSubscriberInstance(
            Exchange anExchange,
            String aName,
            String[] aRoutingKeys) {

        if (aName == null || aName.isEmpty()) {
            throw new IllegalArgumentException("An individual subscriber must be named.");
        }

        Queue queue = new Queue(anExchange, aName, true, false, false);

        try {
            for (String routingKey : aRoutingKeys) {
                queue.channel().queueBind(queue.name(), anExchange.name(), routingKey);
            }
        } catch (IOException e) {
            throw new MessageException("Failed to bind the queue and exchange.", e);
        }

        return queue;
    }

    /**
     * Constructs my default state.
     * @param aConnectionSettings the ConnectionSettings
     * @param aName the String name of the exchange, or the empty string
     * @param isDurable the boolean indicating whether or not I am durable
     * @param isExclusive the boolean indicating whether or not I am exclusive
     * @param isAutoDeleted the boolean indicating whether or not I should be auto-deleted
     */
    protected Queue(
            ConnectionSettings aConnectionSettings,
            String aName,
            boolean isDurable,
            boolean isExclusive,
            boolean isAutoDeleted) {

        super(aConnectionSettings);

        this.setDurable(isDurable);

        try {
            DeclareOk result =
                this.channel().queueDeclare(
                        aName,
                        isDurable,
                        isExclusive,
                        isAutoDeleted,
                        null);

            this.setName(result.getQueue());

        } catch (IOException e) {
            throw new MessageException("Failed to create/open the queue.", e);
        }
    }

    /**
     * Constructs my default state.
     * @param aBrokerChannel the BrokerChannel to initialize with
     * @param aName the String name of the exchange, or the empty string
     * @param aType the String type of the exchange
     * @param isDurable the boolean indicating whether or not I am durable
     * @param isExclusive the boolean indicating whether or not I am exclusive
     * @param isAutoDeleted the boolean indicating whether or not I should be auto-deleted
     */
    protected Queue(
            BrokerChannel aBrokerChannel,
            String aName,
            boolean isDurable,
            boolean isExclusive,
            boolean isAutoDeleted) {

        super(aBrokerChannel);

        this.setDurable(isDurable);

        try {
            DeclareOk result =
                this.channel().queueDeclare(
                        aName,
                        isDurable,
                        isExclusive,
                        isAutoDeleted,
                        null);

            this.setName(result.getQueue());

        } catch (IOException e) {
            throw new MessageException("Failed to create/open the queue.", e);
        }
    }

    /**
     * @see com.saasovation.common.port.adapter.messaging.rabbitmq.BrokerChannel#isQueue()
     */
    @Override
    protected boolean isQueue() {
        return true;
    }
}
