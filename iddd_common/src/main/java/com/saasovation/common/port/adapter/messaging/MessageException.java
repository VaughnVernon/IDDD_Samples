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

package com.saasovation.common.port.adapter.messaging;

/**
 * I am a basic messaging RuntimeException.
 *
 * @author Vaughn Vernon
 */
public class MessageException extends RuntimeException {

    /** My serialVersionUID property. */
    private static final long serialVersionUID = 1L;

    /** My retry indicator. */
    private boolean retry;

    /**
     * Constructs my default state.
     * @param aMessage the String message
     * @param aCause the Throwable cause
     * @param isRetry the boolean indicating whether or not to retry sending
     */
    public MessageException(String aMessage, Throwable aCause, boolean isRetry) {
        super(aMessage, aCause);
        this.setRetry(isRetry);
    }

    /**
     * Constructs my default state.
     * @param aMessage the String message
     * @param aCause the Throwable cause
     */
    public MessageException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }

    /**
     * Constructs my default state.
     * @param aMessage the String message
     * @param isRetry the boolean indicating whether or not to retry sending
     */
    public MessageException(String aMessage, boolean isRetry) {
        super(aMessage);
        this.setRetry(isRetry);
    }

    /**
     * Constructs my default state.
     * @param aMessage the String message
     */
    public MessageException(String aMessage) {
        super(aMessage);
    }

    /**
     * Answers whether or not retry is set. Retry can be
     * used by a MessageListener when it wants the message
     * it has attempted to handle to be re-queued rather than
     * rejected, so that it can re-attempt handling later.
     * @return boolean
     */
    public boolean isRetry() {
        return this.retry;
    }

    /**
     * Sets my retry.
     * @param aRetry the boolean to set as my retry
     */
    private void setRetry(boolean aRetry) {
        this.retry = aRetry;
    }
}
