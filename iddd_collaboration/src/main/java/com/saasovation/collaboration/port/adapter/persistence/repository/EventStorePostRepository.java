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

import com.saasovation.collaboration.domain.model.forum.Post;
import com.saasovation.collaboration.domain.model.forum.PostId;
import com.saasovation.collaboration.domain.model.forum.PostRepository;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.collaboration.port.adapter.persistence.EventStoreProvider;
import com.saasovation.common.event.sourcing.EventStream;
import com.saasovation.common.event.sourcing.EventStreamId;

public class EventStorePostRepository
        extends EventStoreProvider
        implements PostRepository {

    public EventStorePostRepository() {
        super();
    }

    @Override
    public Post postOfId(Tenant aTenantId, PostId aPostId) {
        // snapshots not currently supported; always use version 1

        EventStreamId eventId = new EventStreamId(aTenantId.id(), aPostId.id());

        EventStream eventStream = this.eventStore().eventStreamSince(eventId);

        Post Post = new Post(eventStream.events(), eventStream.version());

        return Post;
    }

    @Override
    public PostId nextIdentity() {
        return new PostId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public void save(Post aPost) {
        EventStreamId eventId =
                new EventStreamId(
                        aPost.tenant().id(),
                        aPost.postId().id(),
                        aPost.mutatedVersion());

        this.eventStore().appendWith(eventId, aPost.mutatingEvents());
    }
}
