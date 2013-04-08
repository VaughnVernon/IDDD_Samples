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

import java.util.Date;

import com.saasovation.agilepm.application.team.EnableProductOwnerCommand;
import com.saasovation.agilepm.application.team.EnableTeamMemberCommand;
import com.saasovation.agilepm.application.team.TeamApplicationService;
import com.saasovation.common.notification.NotificationReader;
import com.saasovation.common.port.adapter.messaging.Exchanges;
import com.saasovation.common.port.adapter.messaging.rabbitmq.ExchangeListener;

public class RabbitMQTeamMemberEnablerListener extends ExchangeListener {

    private TeamApplicationService teamApplicationService;

    public RabbitMQTeamMemberEnablerListener(
            TeamApplicationService aTeamApplicationService) {

        super();

        this.teamApplicationService = aTeamApplicationService;
    }

    @Override
    protected String exchangeName() {
        return Exchanges.IDENTITY_ACCESS_EXCHANGE_NAME;
    }

    @Override
    protected void filteredDispatch(String aType, String aTextMessage) {
        NotificationReader reader = new NotificationReader(aTextMessage);

        String roleName = reader.eventStringValue("roleName");

        if (!roleName.equals("ScrumProductOwner") &&
            !roleName.equals("ScrumTeamMember")) {
            return;
        }

        String emailAddress = reader.eventStringValue("emailAddress");
        String firstName = reader.eventStringValue("firstName");
        String lastName = reader.eventStringValue("lastName");
        String tenantId = reader.eventStringValue("tenantId.id");
        String username = reader.eventStringValue("username");
        Date occurredOn = reader.occurredOn();

        if (roleName.equals("ScrumProductOwner")) {
            this.teamApplicationService().enableProductOwner(
                    new EnableProductOwnerCommand(
                        tenantId,
                        username,
                        firstName,
                        lastName,
                        emailAddress,
                        occurredOn));
        } else {
            this.teamApplicationService().enableTeamMember(
                    new EnableTeamMemberCommand(
                        tenantId,
                        username,
                        firstName,
                        lastName,
                        emailAddress,
                        occurredOn));
        }
    }

    @Override
    protected String[] listensTo() {
        return new String[] {
                "com.saasovation.identityaccess.domain.model.access.UserAssignedToRole"
                };
    }

    private TeamApplicationService teamApplicationService() {
        return this.teamApplicationService;
    }
}
