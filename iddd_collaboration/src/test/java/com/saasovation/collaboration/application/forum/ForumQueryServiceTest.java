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

package com.saasovation.collaboration.application.forum;

import java.util.Collection;

import com.saasovation.collaboration.application.ApplicationTest;
import com.saasovation.collaboration.application.forum.data.ForumData;
import com.saasovation.collaboration.application.forum.data.ForumDiscussionsData;
import com.saasovation.collaboration.domain.model.DomainRegistry;
import com.saasovation.collaboration.domain.model.forum.Discussion;
import com.saasovation.collaboration.domain.model.forum.Forum;

public class ForumQueryServiceTest extends ApplicationTest {

    public ForumQueryServiceTest() {
        super();
    }

    public void testAllForumsDataOfTenant() throws Exception {

        Forum[] forums = this.forumAggregates();

        for (Forum forum : forums) {
            DomainRegistry.forumRepository().save(forum);
        }

        Collection<ForumData> forumsData =
                forumQueryService.allForumsDataOfTenant(forums[0].tenant().id());

        assertNotNull(forumsData);
        assertFalse(forumsData.isEmpty());
        assertEquals(forums.length, forumsData.size());
    }

    public void testForumDataOfId() throws Exception {

        Forum forum = this.forumAggregate();

        DomainRegistry.forumRepository().save(forum);

        ForumData forumData =
                forumQueryService.forumDataOfId(
                        forum.tenant().id(), forum.forumId().id());

        assertNotNull(forumData);
        assertEquals(forum.forumId().id(), forumData.getForumId());
        assertEquals(forum.tenant().id(), forumData.getTenantId());
        assertEquals(forum.creator().emailAddress(), forumData.getCreatorEmailAddress());
        assertEquals(forum.creator().identity(), forumData.getCreatorIdentity());
        assertEquals(forum.creator().name(), forumData.getCreatorName());
        assertEquals(forum.description(), forumData.getDescription());
        assertEquals(forum.exclusiveOwner(), forumData.getExclusiveOwner());
        assertEquals(forum.isClosed(), forumData.isClosed());
        assertEquals(forum.subject(), forumData.getSubject());
        assertEquals(forum.moderator().emailAddress(), forumData.getModeratorEmailAddress());
        assertEquals(forum.moderator().identity(), forumData.getModeratorIdentity());
        assertEquals(forum.moderator().name(), forumData.getModeratorName());
    }

    public void testForumDiscussionsDataOfId() throws Exception {

        Forum forum = this.forumAggregate();

        DomainRegistry.forumRepository().save(forum);

        Discussion[] discussions = this.discussionAggregates(forum);

        for (Discussion discussion : discussions) {
            DomainRegistry.discussionRepository().save(discussion);
        }

        ForumDiscussionsData forumDiscussionsData =
                forumQueryService.forumDiscussionsDataOfId(
                        forum.tenant().id(), forum.forumId().id());

        assertNotNull(forumDiscussionsData);
        assertEquals(forum.forumId().id(), forumDiscussionsData.getForumId());
        assertEquals(forum.tenant().id(), forumDiscussionsData.getTenantId());
        assertEquals(forum.creator().emailAddress(), forumDiscussionsData.getCreatorEmailAddress());
        assertEquals(forum.creator().identity(), forumDiscussionsData.getCreatorIdentity());
        assertEquals(forum.creator().name(), forumDiscussionsData.getCreatorName());
        assertEquals(forum.description(), forumDiscussionsData.getDescription());
        assertEquals(forum.exclusiveOwner(), forumDiscussionsData.getExclusiveOwner());
        assertEquals(forum.isClosed(), forumDiscussionsData.isClosed());
        assertEquals(forum.subject(), forumDiscussionsData.getSubject());
        assertEquals(forum.moderator().emailAddress(), forumDiscussionsData.getModeratorEmailAddress());
        assertEquals(forum.moderator().identity(), forumDiscussionsData.getModeratorIdentity());
        assertEquals(forum.moderator().name(), forumDiscussionsData.getModeratorName());

        assertNotNull(forumDiscussionsData.getDiscussions());
        assertFalse(forumDiscussionsData.getDiscussions().isEmpty());
        assertEquals(3, forumDiscussionsData.getDiscussions().size());
    }

    public void testForumIdOfExclusiveOwner() throws Exception {

        Forum forum = this.forumAggregate();

        DomainRegistry.forumRepository().save(forum);

        String forumId =
                forumQueryService.forumIdOfExclusiveOwner(
                        forum.tenant().id(), forum.exclusiveOwner());

        assertNotNull(forumId);
        assertEquals(forum.forumId().id(), forumId);
    }
}
