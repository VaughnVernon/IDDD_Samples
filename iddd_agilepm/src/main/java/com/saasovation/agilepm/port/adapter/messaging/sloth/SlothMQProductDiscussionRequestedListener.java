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

import java.util.Properties;

import com.saasovation.agilepm.application.product.ProductApplicationService;
import com.saasovation.agilepm.application.product.StartDiscussionInitiationCommand;
import com.saasovation.agilepm.port.adapter.messaging.ProductDiscussionExclusiveOwnerId;
import com.saasovation.common.notification.NotificationReader;
import com.saasovation.common.port.adapter.messaging.Exchanges;
import com.saasovation.common.port.adapter.messaging.slothmq.ExchangeListener;
import com.saasovation.common.port.adapter.messaging.slothmq.ExchangePublisher;
import com.saasovation.common.serializer.PropertiesSerializer;

public class SlothMQProductDiscussionRequestedListener
        extends ExchangeListener {

    private static final String COMMAND =
            "com.saasovation.collaboration.discussion.CreateExclusiveDiscussion";

    private ProductApplicationService productApplicationService;

    protected SlothMQProductDiscussionRequestedListener(
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
        NotificationReader reader = new NotificationReader(aTextMessage);

        if (!reader.eventBooleanValue("requestingDiscussion")) {
            return;
        }

        String tenantId = reader.eventStringValue("tenantId.id");
        String productId = reader.eventStringValue("product.id");

        this.productApplicationService().startDiscussionInitiation(
                new StartDiscussionInitiationCommand(
                        tenantId,
                        productId));

        Properties parameters = this.parametersFrom(reader);
        PropertiesSerializer serializer = PropertiesSerializer.instance();
        String serialization = serializer.serialize(parameters);

        this.exchangePublisher().publish(COMMAND, serialization);
    }

    @Override
    protected String[] listensTo() {
        return new String[] {
                "com.saasovation.agilepm.domain.model.product.ProductCreated",
                "com.saasovation.agilepm.domain.model.product.ProductDiscussionRequested"
                };
    }

    @Override
    protected String name() {
        return this.getClass().getSimpleName();
    }

    private ExchangePublisher exchangePublisher() {
        return new ExchangePublisher(Exchanges.COLLABORATION_EXCHANGE_NAME);
    }

    private Properties parametersFrom(NotificationReader aReader) {
        Properties properties = new Properties();

        properties.put("command", COMMAND);

        properties.put("tenantId",
                aReader.eventStringValue("tenantId.id"));

        ProductDiscussionExclusiveOwnerId exclusiveOwnerId =
                new ProductDiscussionExclusiveOwnerId(
                        aReader.eventStringValue("productId.id"));

        properties.put("exclusiveOwnerId",
                exclusiveOwnerId.encoded());

        properties.put("forumSubject",
                "ProjectOvation Forum: " + aReader.eventStringValue("name"));

        properties.put("forumDescription",
                "About: " + aReader.eventStringValue("description"));

        properties.put("discussionSubject",
                "Product Discussion: " + aReader.eventStringValue("name"));

        String productOwnerId =
                aReader.eventStringValue("productOwnerId.id");

        properties.put("creatorId", productOwnerId);

        properties.put("moderatorId", productOwnerId);

        return properties;
    }

    private ProductApplicationService productApplicationService() {
        return this.productApplicationService;
    }
}
