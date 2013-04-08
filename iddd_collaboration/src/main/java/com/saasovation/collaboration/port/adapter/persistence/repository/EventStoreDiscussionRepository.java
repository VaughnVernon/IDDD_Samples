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

import com.saasovation.collaboration.domain.model.forum.Discussion;
import com.saasovation.collaboration.domain.model.forum.DiscussionId;
import com.saasovation.collaboration.domain.model.forum.DiscussionRepository;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.collaboration.port.adapter.persistence.EventStoreProvider;
import com.saasovation.common.event.sourcing.EventStream;
import com.saasovation.common.event.sourcing.EventStreamId;

public class EventStoreDiscussionRepository
        extends EventStoreProvider
        implements DiscussionRepository {

    public EventStoreDiscussionRepository() {
        super();
    }

    @Override
    public Discussion discussionOfId(Tenant aTenant, DiscussionId aDiscussionId) {
        // snapshots not currently supported; always use version 1

        EventStreamId eventId = new EventStreamId(aTenant.id(), aDiscussionId.id());

        EventStream eventStream = this.eventStore().eventStreamSince(eventId);

        Discussion Discussion = new Discussion(eventStream.events(), eventStream.version());

        return Discussion;
    }

    @Override
    public DiscussionId nextIdentity() {
        return new DiscussionId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public void save(Discussion aDiscussion) {
        EventStreamId eventId =
                new EventStreamId(
                        aDiscussion.tenant().id(),
                        aDiscussion.discussionId().id(),
                        aDiscussion.mutatedVersion());

        this.eventStore().appendWith(eventId, aDiscussion.mutatingEvents());
    }
}
