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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;
import com.saasovation.common.port.adapter.messaging.MessageException;

/**
 * I am a message consumer, which facilitates receiving messages
 * from a Queue. A MessageListener or a client may close me,
 * terminating message consumption.
 *
 * @author Vaughn Vernon
 */
public class MessageConsumer {

    /** My autoAcknowledged property. */
    private boolean autoAcknowledged;

    /** My closed property, which indicates I have been closed. */
    private boolean closed;

    /** My messageTypes, which indicates the messages of types I accept. */
    private Set<String> messageTypes;

    /** My queue, which is where my messages come from. */
    private Queue queue;

    /** My tag, which is produced by the broker. */
    private String tag;

    /**
     * Answers a new auto-acknowledged MessageConsumer, which means all
     * messages received are automatically considered acknowledged as
     * received from the broker.
     * @param aQueue the Queue from which messages are received
     * @return MessageConsumer
     */
    public static MessageConsumer autoAcknowledgedInstance(Queue aQueue) {
        return MessageConsumer.instance(aQueue, true);
    }

    /**
     * Answers a new MessageConsumer with manual acknowledgment.
     * @param aQueue the Queue from which messages are received
     * @return MessageConsumer
     */
    public static MessageConsumer instance(Queue aQueue) {
        return new MessageConsumer(aQueue, false);
    }

    /**
     * Answers a new MessageConsumer with acknowledgment managed per
     * isAutoAcknowledged.
     * @param aQueue the Queue from which messages are received
     * @param isAutoAcknowledged the boolean indicating whether or not auto-acknowledgment is used
     * @return MessageConsumer
     */
    public static MessageConsumer instance(
            Queue aQueue,
            boolean isAutoAcknowledged) {
        return new MessageConsumer(aQueue, isAutoAcknowledged);
    }

    /**
     * Closes me, which closes my queue.
     */
    public void close() {
        this.setClosed(true);

        this.queue().close();
    }

    /**
     * Answers whether or not I have been closed.
     * @return boolean
     */
    public boolean isClosed() {
        return this.closed;
    }

    /**
     * Ensure an equalization of message distribution
     * across all consumers of this queue.
     */
    public void equalizeMessageDistribution() {
        try {
            this.queue().channel().basicQos(1);
        } catch (IOException e) {
            throw new MessageException("Cannot equalize distribution.", e);
        }
    }

    /**
     * Receives all messages on a separate thread and dispatches
     * them to aMessageListener until I am closed or until the
     * broker is shut down.
     * @param aMessageListener the MessageListener that handles messages
     */
    public void receiveAll(final MessageListener aMessageListener) {
        this.receiveFor(aMessageListener);
    }

    /**
     * Receives only messages of types included in aMessageTypes
     * on a separate thread and dispatches them to aMessageListener
     * until I am closed or until the broker is shut down. The type
     * must be included in the message's basic properties. If the
     * message's type is null, the message is filtered out.
     * @param aMessageTypes the String[] indicating filtered message types
     * @param aMessageListener the MessageListener that handles messages
     */
    public void receiveOnly(
            final String[] aMessageTypes,
            final MessageListener aMessageListener) {
        String[] filterOutAllBut = aMessageTypes;

        if (filterOutAllBut == null) {
            filterOutAllBut = new String[0];
        }
        this.setMessageTypes(new HashSet<String>(Arrays.asList(filterOutAllBut)));

        this.receiveFor(aMessageListener);
    }

    /**
     * Answers my tag, which was produced by the broker.
     * @return String
     */
    public String tag() {
        return this.tag;
    }

    /**
     * Constructs my default state.
     * @param aQueue the Queue from which I receive messages
     * @param isAutoAcknowledged the boolean indicating whether or not auto-acknowledgment is used
     */
    protected MessageConsumer(
            Queue aQueue,
            boolean isAutoAcknowledged) {

        super();

        this.setMessageTypes(new HashSet<String>(Arrays.asList(new String[0])));

        this.setQueue(aQueue);

        this.setAutoAcknowledged(isAutoAcknowledged);
    }

    /**
     * Answers my autoAcknowledged.
     * @return boolean
     */
    private boolean isAutoAcknowledged() {
        return this.autoAcknowledged;
    }

    /**
     * Sets my autoAcknowledged.
     * @param isAutoAcknowledged the boolean to set as my autoAcknowledged
     */
    private void setAutoAcknowledged(boolean isAutoAcknowledged) {
        this.autoAcknowledged = isAutoAcknowledged;
    }

    /**
     * Sets my closed.
     * @param aClosed the boolean to set as my closed
     */
    private void setClosed(boolean aClosed) {
        this.closed = aClosed;
    }

    /**
     * Answers my queue.
     * @return Queue
     */
    protected Queue queue() {
        return this.queue;
    }

    /**
     * Answers my messageTypes.
     * @return Set<String>
     */
    private Set<String> messageTypes() {
        return this.messageTypes;
    }

    /**
     * Registers aMessageListener with the channel indirectly using
     * a DispatchingConsumer.
     * @param aMessageListener the MessageListener
     */
    private void receiveFor(MessageListener aMessageListener) {
        Queue queue = this.queue();
        Channel channel = queue.channel();

        try {
            String tag =
                channel.basicConsume(
                        queue.name(),
                        this.isAutoAcknowledged(),
                        new DispatchingConsumer(channel, aMessageListener));

            this.setTag(tag);

        } catch (IOException e) {
            throw new MessageException("Failed to initiate consumer.", e);
        }
    }

    /**
     * Sets my messageTypes.
     * @param aMessageTypes the Set<String> to set as my messageTypes
     */
    private void setMessageTypes(Set<String> aMessageTypes) {
        this.messageTypes = aMessageTypes;
    }

    /**
     * Sets my queue.
     * @param aQueue the Queue to set as my queue
     */
    private void setQueue(Queue aQueue) {
        this.queue = aQueue;
    }

    /**
     * Sets my tag.
     * @param aTag the String to set as my tag
     */
    private void setTag(String aTag) {
        this.tag = aTag;
    }

    private class DispatchingConsumer extends DefaultConsumer {

        private MessageListener messageListener;

        public DispatchingConsumer(Channel aChannel, MessageListener aMessageListener) {
            super(aChannel);

            this.setMessageListener(aMessageListener);
        }

        @Override
        public void handleDelivery(
                String aConsumerTag,
                Envelope anEnvelope,
                BasicProperties aProperties,
                byte[] aBody) throws IOException {

            if (!isClosed()) {
                handle(this.messageListener(), new Delivery(anEnvelope, aProperties, aBody));
            }

            if (isClosed()) {
                queue().close();
            }
        }

        @Override
        public void handleShutdownSignal(
                String aConsumerTag,
                ShutdownSignalException aSignal) {

            close();
        }

        private void handle(
                MessageListener aMessageListener,
                Delivery aDelivery) {
            try {
                if (this.filteredMessageType(aDelivery)) {
                    ;
                } else if (aMessageListener.type().isBinaryListener()) {
                    aMessageListener
                        .handleMessage(
                                aDelivery.getProperties().getType(),
                                aDelivery.getProperties().getMessageId(),
                                aDelivery.getProperties().getTimestamp(),
                                aDelivery.getBody(),
                                aDelivery.getEnvelope().getDeliveryTag(),
                                aDelivery.getEnvelope().isRedeliver());
                } else if (aMessageListener.type().isTextListener()) {
                    aMessageListener
                        .handleMessage(
                                aDelivery.getProperties().getType(),
                                aDelivery.getProperties().getMessageId(),
                                aDelivery.getProperties().getTimestamp(),
                                new String(aDelivery.getBody()),
                                aDelivery.getEnvelope().getDeliveryTag(),
                                aDelivery.getEnvelope().isRedeliver());
                }

                this.ack(aDelivery);

            } catch (MessageException e) {
                // System.out.println("MESSAGE EXCEPTION (MessageConsumer): " + e.getMessage());
                this.nack(aDelivery, e.isRetry());
            } catch (Throwable t) {
                // System.out.println("EXCEPTION (MessageConsumer): " + t.getMessage());
                this.nack(aDelivery, false);
            }
        }

        private void ack(Delivery aDelivery) {
            try {
                if (!isAutoAcknowledged()) {
                    this.getChannel().basicAck(
                            aDelivery.getEnvelope().getDeliveryTag(),
                            false);
                }
            } catch (IOException ioe) {
                // fall through
            }
        }

        private void nack(Delivery aDelivery, boolean isRetry) {
            try {
                if (!isAutoAcknowledged()) {
                    this.getChannel().basicNack(
                            aDelivery.getEnvelope().getDeliveryTag(),
                            false,
                            isRetry);
                }
            } catch (IOException ioe) {
                // fall through
            }
        }

        private boolean filteredMessageType(Delivery aDelivery) {
            boolean filtered = false;

            Set<String> filteredMessageTypes = messageTypes();

            if (!filteredMessageTypes.isEmpty()) {
                String messageType = aDelivery.getProperties().getType();

                if (messageType == null || !filteredMessageTypes.contains(messageType)) {
                    filtered = true;
                }
            }

            return filtered;
        }

        /**
         * Answers my messageListener.
         * @return MessageListener
         */
        private MessageListener messageListener() {
            return messageListener;
        }

        /**
         * Sets my messageListener.
         * @param messageListener the MessageListener to set as my messageListener
         */
        private void setMessageListener(MessageListener messageListener) {
            this.messageListener = messageListener;
        }
    }
}
