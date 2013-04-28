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

package com.saasovation.agilepm.port.adapter.messaging.rabbitmq;

import com.saasovation.agilepm.application.sprint.*;
import com.saasovation.common.notification.NotificationReader;
import com.saasovation.common.port.adapter.messaging.Exchanges;
import com.saasovation.common.port.adapter.messaging.rabbitmq.ExchangeListener;

public class RabbitMQBacklogItemCommittedListener extends ExchangeListener {

    private SprintApplicationService sprintApplicationService;

    public RabbitMQBacklogItemCommittedListener(
            SprintApplicationService aSprintApplicationService) {

        super();

        this.sprintApplicationService = aSprintApplicationService;
    }

    @Override
    protected String exchangeName() {
        return Exchanges.AGILEPM_EXCHANGE_NAME;
    }

    @Override
    protected void filteredDispatch(String aType, String aTextMessage) {
        NotificationReader reader = new NotificationReader(aTextMessage);

        String tenantId = reader.eventStringValue("tenant.id");
        String backlogItemId = reader.eventStringValue("backlogItemId.id");
        String committedToSprintId = reader.eventStringValue("committedToSprintId.id");

        this.sprintApplicationService().commitBacklogItemToSprint(
                new CommitBacklogItemToSprintCommand(
                    tenantId,
                    committedToSprintId,
                    backlogItemId));
    }

    @Override
    protected String[] listensTo() {
        return new String[] {
                "com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemCommitted"
                };
    }

    private SprintApplicationService sprintApplicationService() {
        return this.sprintApplicationService;
    }
}
