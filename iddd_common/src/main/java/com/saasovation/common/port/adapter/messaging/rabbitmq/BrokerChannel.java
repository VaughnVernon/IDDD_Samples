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

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.saasovation.common.port.adapter.messaging.MessageException;

/**
 * I am an abstract base class for all channels to
 * the RabbitMQ message broker.
 *
 * @author Vaughn Vernon
 */
public abstract class BrokerChannel {

    /** My channel. */
    private Channel channel;

    /** My connection, which is the connection to my host broker. */
    private Connection connection;

    /** My durable property, which indicates whether or not messages are durable. */
    private boolean durable;

    /** My host, which is the host of the broker. There may be a :port appended. */
    private String host;

    /** My name. */
    private String name;

    /**
     * Answers my host.
     * @return String
     */
    public String host() {
        return this.host;
    }

    /**
     * Answers my name.
     * @return String
     */
    public String name() {
        return this.name;
    }

    /**
     * Constructs my default state.
     * @param aConnectionSettings the ConnectionSettings
     */
    protected BrokerChannel(ConnectionSettings aConnectionSettings) {
        this(aConnectionSettings, null);
    }

    /**
     * Constructs my default state.
     * @param aConnectionSettings the ConnectionSettings
     * @param aName the String name of my implementor
     */
    protected BrokerChannel(
            ConnectionSettings aConnectionSettings,
            String aName) {

        super();

        ConnectionFactory factory =
            this.configureConnectionFactoryUsing(aConnectionSettings);

        this.setName(aName);

        try {

            this.setConnection(factory.newConnection());

            this.setChannel(this.connection().createChannel());

        } catch (IOException e) {
            throw new MessageException("Failed to create/open the queue.", e);
        }
    }

    /**
     * Constructs my default state.
     * @param aBrokerChannel the BrokerChannel to initialize with
     */
    protected BrokerChannel(BrokerChannel aBrokerChannel) {
        this(aBrokerChannel, null);
    }

    /**
     * Constructs my default state.
     * @param aBrokerChannel the BrokerChannel to initialize with
     * @param aName the String name of my implementor
     */
    protected BrokerChannel(BrokerChannel aBrokerChannel, String aName) {

        super();

        this.setHost(aBrokerChannel.host());
        this.setName(aName);
        this.setConnection(aBrokerChannel.connection());
        this.setChannel(aBrokerChannel.channel());
    }

    /**
     * Answers my channel.
     * @return Channel
     */
    protected Channel channel() {
        return this.channel;
    }

    /**
     * Closes me.
     */
    protected void close() {

        // RabbitMQ doesn't guarantee that if isOpen()
        // answers true that close() will work because
        // another client may be racing to close the
        // same process and/or components. so here just
        // attempt to close, catch and ignore, and move
        // on to next steps is the recommended approach.
        //
        // for the purpose here, the isOpen() checks prevent
        // closing a shared channel and connection that is
        // shared by a subscriber exchange and queue.

        try {
            if (this.channel() != null && this.channel().isOpen()) {
                this.channel().close();
            }
        } catch (Throwable e) {
            // fall through
        }

        try {
            if (this.connection() != null && this.connection().isOpen()) {
                this.connection().close();
            }
        } catch (Throwable e) {
            // fall through
        }

        this.setChannel(null);
        this.setConnection(null);
    }

    /**
     * Answers a new ConnectionFactory configured with aConnectionSettings.
     * @param aConnectionSettings the ConnectionFactory
     * @return ConnectionFactory
     */
    protected ConnectionFactory configureConnectionFactoryUsing(
            ConnectionSettings aConnectionSettings) {

        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(aConnectionSettings.hostName());

        if (aConnectionSettings.hasPort()) {
            factory.setPort(aConnectionSettings.port());
        }

        factory.setVirtualHost(aConnectionSettings.virtualHost());

        if (aConnectionSettings.hasUserCredentials()) {
            factory.setUsername(aConnectionSettings.username());
            factory.setPassword(aConnectionSettings.password());
        }

        return factory;
    }

    /**
     * Answers whether or not I am durable.
     * @return boolean
     */
    protected boolean isDurable() {
        return this.durable;
    }

    /**
     * Sets my durable.
     * @param aDurable the boolean to set as my durable
     */
    protected void setDurable(boolean aDurable) {
        this.durable = aDurable;
    }

    /**
     * Answers whether or not I am an exchange channel.
     * @return boolean
     */
    protected boolean isExchange() {
        return false;
    }

    /**
     * Answers my name as the exchange name if I am
     * an Exchange; otherwise the empty String.
     * @return String
     */
    protected String exchangeName() {
        return this.isExchange() ? this.name() : "";
    }

    /**
     * Answers whether or not I am a queue channel.
     * @return boolean
     */
    protected boolean isQueue() {
        return false;
    }

    /**
     * Answers my name as the queue name if I am
     * a Queue; otherwise the empty String.
     * @return String
     */
    protected String queueName() {
        return this.isQueue() ? this.name() : "";
    }

    /**
     * Sets my name.
     * @param aName the String to set as my name
     */
    protected void setName(String aName) {
        this.name = aName;
    }

    /**
     * Sets my channel.
     * @param aChannel the Channel to set as my channel
     */
    private void setChannel(Channel aChannel) {
        this.channel = aChannel;
    }

    /**
     * Answers my connection.
     * @return Connection
     */
    private Connection connection() {
        return this.connection;
    }

    /**
     * Sets my connection.
     * @param aConnection the Connection to set as my connection
     */
    private void setConnection(Connection aConnection) {
        this.connection = aConnection;
    }

    /**
     * Sets my host.
     * @param aHost the String to set as my host
     */
    private void setHost(String aHost) {
        this.host = aHost;
    }
}
