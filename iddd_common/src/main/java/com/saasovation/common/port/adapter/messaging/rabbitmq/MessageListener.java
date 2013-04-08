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
 * I am a message listener, which is given each message received
 * by a MessageConsumer. I am also an adapter because I provide
 * defaults for both handleMessage() behaviors. A typical subclass
 * would override one or the other handleMessage() based on its
 * type and leave the remaining handleMessage() defaulted since
 * it will never be used by MessageConsumer.
 *
 * @author Vaughn Vernon
 */
public abstract class MessageListener {

    /** My type, which indicates whether I listen for BINARY or TEXT messages. */
    private Type type;

    /** I am a message listener type indicator. */
    public enum Type {
        BINARY {
            @Override
            public boolean isBinaryListener() {
                return true;
            }
        },

        TEXT {
            @Override
            public boolean isTextListener() {
                return true;
            }
        };

        /**
         * Answers whether or not I am a binary message listener.
         * @return boolean
         */
        public boolean isBinaryListener() {
            return false;
        }

        /**
         * Answers whether or not I am a text message listener.
         * @return boolean
         */
        public boolean isTextListener() {
            return false;
        }
    }

    /**
     * Constructs my default state.
     * @param aType Type of listener, either BINARY or TEXT
     */
    public MessageListener(Type aType) {
        super();
        this.setType(aType);
    }

    /**
     * Handles aBinaryMessage. If any MessageException is thrown by
     * my implementor its isRetry() is examined and, if true, the
     * message being handled will be nack'd and re-queued. Otherwise,
     * if its isRetry() is false the message will be rejected/failed
     * (not re-queued). If any other Exception is thrown the message
     * will be considered not handled and is rejected/failed.
     * @param aType the String type of the message if sent, or null
     * @param aMessageId the String id of the message if sent, or null
     * @param aTimestamp the Date timestamp of the message if sent, or null
     * @param aBinaryMessage the byte[] containing the binary message
     * @param aDeliveryTag the long tag delivered with the message
     * @param isRedelivery the boolean indicating whether or not this message is a redelivery
     * @throws Exception when any problem occurs and the message must not be acknowledged
     */
    public void handleMessage(
            String aType,
            String aMessageId,
            Date aTimestamp,
            byte[] aBinaryMessage,
            long aDeliveryTag,
            boolean isRedelivery) throws Exception {

        throw new UnsupportedOperationException("Must be implemented by my subclass.");
    }

    /**
     * Handles aTextMessage. If any MessageException is thrown by
     * my implementor its isRetry() is examined and, if true, the
     * message being handled will be nack'd and re-queued. Otherwise,
     * if its isRetry() is false the message will be rejected/failed
     * (not re-queued). If any other Exception is thrown the message
     * will be considered not handled and is rejected/failed.
     * @param aType the String type of the message if sent, or null
     * @param aMessageId the String id of the message if sent, or null
     * @param aTimestamp the Date timestamp of the message if sent, or null
     * @param aTextMessage the String containing the text message
     * @param aDeliveryTag the long tag delivered with the message
     * @param isRedelivery the boolean indicating whether or not this message is a redelivery
     * @throws Exception when any problem occurs and the message must not be acknowledged
     */
    public void handleMessage(
            String aType,
            String aMessageId,
            Date aTimestamp,
            String aTextMessage,
            long aDeliveryTag,
            boolean isRedelivery) throws Exception {

        throw new UnsupportedOperationException("Must be implemented by my subclass.");
    }

    /**
     * Answers my type.
     * @return Type
     */
    public Type type() {
        return this.type;
    }

    /**
     * Sets my type.
     * @param aType the Type to set as my type
     */
    private void setType(Type aType) {
        this.type = aType;
    }
}
