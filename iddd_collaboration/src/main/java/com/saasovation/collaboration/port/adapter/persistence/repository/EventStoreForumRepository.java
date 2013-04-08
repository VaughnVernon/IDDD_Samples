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

package com.saasovation.collaboration.port.adapter.persistence.repository;

import java.util.UUID;

import com.saasovation.collaboration.domain.model.forum.Forum;
import com.saasovation.collaboration.domain.model.forum.ForumId;
import com.saasovation.collaboration.domain.model.forum.ForumRepository;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.collaboration.port.adapter.persistence.EventStoreProvider;
import com.saasovation.common.event.sourcing.EventStream;
import com.saasovation.common.event.sourcing.EventStreamId;

public class EventStoreForumRepository
        extends EventStoreProvider
        implements ForumRepository {

    public EventStoreForumRepository() {
        super();
    }

    @Override
    public Forum forumOfId(Tenant aTenant, ForumId aForumId) {
        // snapshots not currently supported; always use version 1

        EventStreamId eventId = new EventStreamId(aTenant.id(), aForumId.id());

        EventStream eventStream = this.eventStore().eventStreamSince(eventId);

        Forum forum = new Forum(eventStream.events(), eventStream.version());

        return forum;
    }

    @Override
    public ForumId nextIdentity() {
        return new ForumId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public void save(Forum aForum) {
        EventStreamId eventId =
                new EventStreamId(
                        aForum.tenant().id(),
                        aForum.forumId().id(),
                        aForum.mutatedVersion());

        this.eventStore().appendWith(eventId, aForum.mutatingEvents());
    }
}
