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

package com.saasovation.collaboration.domain.model.forum;

import com.saasovation.collaboration.domain.model.DomainRegistry;
import com.saasovation.collaboration.domain.model.DomainTest;
import com.saasovation.collaboration.domain.model.collaborator.Creator;
import com.saasovation.collaboration.domain.model.collaborator.Moderator;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEventPublisher;

public abstract class AbstractForumTest extends DomainTest {

    public AbstractForumTest() {
        super();
    }

    protected Forum forumAggregate() {

        Tenant tenant = new Tenant("01234567");

        Forum forum =
            new Forum(
                    tenant,
                    DomainRegistry.forumRepository().nextIdentity(),
                    new Creator("jdoe", "John Doe", "jdoe@saasovation.com"),
                    new Moderator("jdoe", "John Doe", "jdoe@saasovation.com"),
                    "John Doe Does DDD",
                    "A set of discussions about DDD for anonymous developers.",
                    null);

        return forum;
    }

    @Override
    protected void setUp() throws Exception {
        DomainEventPublisher.instance().reset();

        super.setUp();
    }
}
