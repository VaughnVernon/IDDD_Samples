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

package com.saasovation.collaboration.port.adapter.messaging;

import org.springframework.beans.factory.annotation.Autowired;

import com.saasovation.collaboration.application.forum.ForumApplicationService;
import com.saasovation.common.notification.NotificationReader;
import com.saasovation.common.port.adapter.messaging.Exchanges;
import com.saasovation.common.port.adapter.messaging.rabbitmq.ExchangeListener;

public class ExclusiveDiscussionCreationListener extends ExchangeListener {

    @Autowired
    private ForumApplicationService forumApplicationService;

    public ExclusiveDiscussionCreationListener() {
        super();
    }

    @Override
    protected String exchangeName() {
        return Exchanges.COLLABORATION_EXCHANGE_NAME;
    }

    @Override
    protected void filteredDispatch(String aType, String aTextMessage) {
        NotificationReader reader = new NotificationReader(aTextMessage);

        String tenantId = reader.eventStringValue("tenantId");
        String exclusiveOwnerId = reader.eventStringValue("exclusiveOwnerId");
        String creatorId = reader.eventStringValue("creatorId");
        String moderatorId = reader.eventStringValue("moderatorId");
        String authorId = reader.eventStringValue("authorId");
        String forumSubject = reader.eventStringValue("forumTitle");
        String forumDescription = reader.eventStringValue("forumDescription");
        String discussionSubject = reader.eventStringValue("discussionSubject");

        forumApplicationService
            .startExclusiveForumWithDiscussion(
                    tenantId,
                    exclusiveOwnerId,
                    creatorId,
                    moderatorId,
                    authorId,
                    forumSubject,
                    forumDescription,
                    discussionSubject,
                    null);
    }

    @Override
    protected String[] listensTo() {
        return new String[] {
                "com.saasovation.collaboration.discussion.CreateExclusiveDiscussion"
            };
    }
}
