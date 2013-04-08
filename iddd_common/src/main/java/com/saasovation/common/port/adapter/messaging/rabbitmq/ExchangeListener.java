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

import java.util.Date;

/**
 * I am an abstract base class for exchange listeners.
 * I perform the basic set up according to the answers
 * from my concrete subclass.
 *
 * @author Vaughn Vernon
 */
public abstract class ExchangeListener {

    private MessageConsumer messageConsumer;

    private Queue queue;

    /**
     * Constructs my default state.
     */
    public ExchangeListener() {
        super();

        this.attachToQueue();

        this.registerConsumer();
    }

    /**
     * Closes my queue.
     */
    public void close() {
        this.queue().close();
    }

    /**
     * Answers the String name of the exchange I listen to.
     * @return String
     */
    protected abstract String exchangeName();

    /**
     * Filters out unwanted events and dispatches ones of interest.
     * @param aType the String message type
     * @param aTextMessage the String raw text message being handled
     */
    protected abstract void filteredDispatch(String aType, String aTextMessage);

    /**
     * Answers the kinds of messages I listen to.
     * @return String[]
     */
    protected abstract String[] listensTo();

    /**
     * Answers the String name of the queue I listen to. By
     * default it is the simple name of my concrete class.
     * May be overridden to change the name.
     * @return String
     */
    protected String queueName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Attaches to the queues I listen to for messages.
     */
    private void attachToQueue() {
        Exchange exchange =
                Exchange.fanOutInstance(
                        ConnectionSettings.instance(),
                        this.exchangeName(),
                        true);

        this.queue =
                Queue.individualExchangeSubscriberInstance(
                        exchange,
                        this.exchangeName() + "." + this.queueName());
    }

    /**
     * Answers my queue.
     * @return Queue
     */
    private Queue queue() {
        return this.queue;
    }

    /**
     * Registers my listener for queue messages and dispatching.
     */
    private void registerConsumer() {
        this.messageConsumer = MessageConsumer.instance(this.queue(), false);

        this.messageConsumer.receiveOnly(
                this.listensTo(),
                new MessageListener(MessageListener.Type.TEXT) {

            @Override
            public void handleMessage(
                    String aType,
                    String aMessageId,
                    Date aTimestamp,
                    String aTextMessage,
                    long aDeliveryTag,
                    boolean isRedelivery)
            throws Exception {
                filteredDispatch(aType, aTextMessage);
            }
        });
    }
}
