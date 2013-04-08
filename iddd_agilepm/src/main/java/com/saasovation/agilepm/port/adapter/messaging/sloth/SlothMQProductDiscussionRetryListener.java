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

package com.saasovation.agilepm.port.adapter.messaging.sloth;

import com.saasovation.agilepm.application.product.ProductApplicationService;
import com.saasovation.agilepm.application.product.RetryProductDiscussionRequestCommand;
import com.saasovation.agilepm.application.product.TimeOutProductDiscussionRequestCommand;
import com.saasovation.agilepm.domain.model.product.ProductDiscussionRequestTimedOut;
import com.saasovation.common.notification.Notification;
import com.saasovation.common.notification.NotificationSerializer;
import com.saasovation.common.port.adapter.messaging.Exchanges;
import com.saasovation.common.port.adapter.messaging.slothmq.ExchangeListener;

public class SlothMQProductDiscussionRetryListener extends ExchangeListener {

    private ProductApplicationService productApplicationService;

    public SlothMQProductDiscussionRetryListener(
            ProductApplicationService aProductApplicationService) {

        super();

        this.productApplicationService = aProductApplicationService;
    }

    @Override
    protected String exchangeName() {
        return Exchanges.AGILEPM_EXCHANGE_NAME;
    }

    @Override
    protected void filteredDispatch(String aType, String aTextMessage) {
        Notification notification =
            NotificationSerializer
                .instance()
                .deserialize(aTextMessage, Notification.class);

        ProductDiscussionRequestTimedOut event = notification.event();

        if (event.hasFullyTimedOut()) {
            this.productApplicationService().timeOutProductDiscussionRequest(
                    new TimeOutProductDiscussionRequestCommand(
                            event.tenantId(),
                            event.processId().id(),
                            event.occurredOn()));
        } else {
            this.productApplicationService().retryProductDiscussionRequest(
                    new RetryProductDiscussionRequestCommand(
                            event.tenantId(),
                            event.processId().id()));
        }
    }

    @Override
    protected String[] listensTo() {
        return new String[] {
                "com.saasovation.agilepm.domain.model.process.ProductDiscussionRequestTimedOut"
                };
    }

    @Override
    protected String name() {
        return this.getClass().getSimpleName();
    }

    private ProductApplicationService productApplicationService() {
        return this.productApplicationService;
    }
}
