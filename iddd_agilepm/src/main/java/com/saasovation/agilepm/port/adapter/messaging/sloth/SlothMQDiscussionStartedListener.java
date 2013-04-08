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

import com.saasovation.agilepm.application.product.InitiateDiscussionCommand;
import com.saasovation.agilepm.application.product.ProductApplicationService;
import com.saasovation.agilepm.port.adapter.messaging.ProductDiscussionExclusiveOwnerId;
import com.saasovation.common.notification.NotificationReader;
import com.saasovation.common.port.adapter.messaging.Exchanges;
import com.saasovation.common.port.adapter.messaging.slothmq.ExchangeListener;

public class SlothMQDiscussionStartedListener extends ExchangeListener {

    private ProductApplicationService productApplicationService;

    public SlothMQDiscussionStartedListener(
            ProductApplicationService aProductApplicationService) {

        super();

        this.productApplicationService = aProductApplicationService;
    }

    @Override
    protected String exchangeName() {
        return Exchanges.COLLABORATION_EXCHANGE_NAME;
    }

    @Override
    protected void filteredDispatch(String aType, String aTextMessage) {
        NotificationReader reader = new NotificationReader(aTextMessage);

        String ownerId = reader.eventStringValue("exclusiveOwner");

        if (!ProductDiscussionExclusiveOwnerId.isValid(ownerId)) {
            return;
        }

        String tenantId = reader.eventStringValue("tenant.id");
        String productId =
                ProductDiscussionExclusiveOwnerId
                    .fromEncodedId(ownerId)
                    .id();
        String discussionId = reader.eventStringValue("discussionId.id");

        this.productApplicationService().initiateDiscussion(
                new InitiateDiscussionCommand(
                    tenantId,
                    productId,
                    discussionId));
    }

    @Override
    protected String[] listensTo() {
        return new String[] {
                "com.saasovation.collaboration.domain.model.forum.DiscussionStarted"
                };
    }

    private ProductApplicationService productApplicationService() {
        return this.productApplicationService;
    }

    @Override
    protected String name() {
        return this.getClass().getName();
    }
}
