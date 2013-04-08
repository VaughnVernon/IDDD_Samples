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

import java.util.UUID;

import com.saasovation.collaboration.application.ApplicationTest;
import com.saasovation.collaboration.application.forum.data.ForumCommandResult;
import com.saasovation.collaboration.domain.model.DomainRegistry;
import com.saasovation.collaboration.domain.model.forum.Discussion;
import com.saasovation.collaboration.domain.model.forum.DiscussionId;
import com.saasovation.collaboration.domain.model.forum.Forum;
import com.saasovation.collaboration.domain.model.forum.ForumId;

public class ForumApplicationServiceTest extends ApplicationTest {

    private String discussionId;
    private String forumId;

    public ForumApplicationServiceTest() {
        super();
    }

    public void testAssignModeratorToForum() throws Exception {

        Forum forum = this.forumAggregate();

        DomainRegistry.forumRepository().save(forum);

        forumApplicationService
            .assignModeratorToForum(
                    forum.tenant().id(),
                    forum.forumId().id(),
                    "newModerator");

        forum = DomainRegistry
                    .forumRepository()
                    .forumOfId(
                            forum.tenant(),
                            forum.forumId());

        assertNotNull(forum);
        assertEquals("newModerator", forum.moderator().identity());
    }

    public void testChangeForumDescription() throws Exception {

        Forum forum = this.forumAggregate();

        assertFalse("A changed description.".equals(forum.description()));

        DomainRegistry.forumRepository().save(forum);

        forumApplicationService
            .changeForumDescription(
                    forum.tenant().id(),
                    forum.forumId().id(),
                    "A changed description.");

        forum = DomainRegistry
                    .forumRepository()
                    .forumOfId(
                            forum.tenant(),
                            forum.forumId());

        assertNotNull(forum);
        assertEquals("A changed description.", forum.description());
    }

    public void testChangeForumSubject() throws Exception {

        Forum forum = this.forumAggregate();

        assertFalse("A changed subject.".equals(forum.subject()));

        DomainRegistry.forumRepository().save(forum);

        forumApplicationService
            .changeForumSubject(
                    forum.tenant().id(),
                    forum.forumId().id(),
                    "A changed subject.");

        forum = DomainRegistry
                    .forumRepository()
                    .forumOfId(
                            forum.tenant(),
                            forum.forumId());

        assertNotNull(forum);
        assertEquals("A changed subject.", forum.subject());
    }

    public void testCloseForum() throws Exception {

        Forum forum = this.forumAggregate();

        assertFalse(forum.isClosed());

        DomainRegistry.forumRepository().save(forum);

        forumApplicationService.closeForum(forum.tenant().id(), forum.forumId().id());

        forum = DomainRegistry
                    .forumRepository()
                    .forumOfId(
                            forum.tenant(),
                            forum.forumId());

        assertNotNull(forum);
        assertTrue(forum.isClosed());
    }

    public void testReopenForum() throws Exception {

        Forum forum = this.forumAggregate();

        forum.close();

        assertTrue(forum.isClosed());

        DomainRegistry.forumRepository().save(forum);

        forumApplicationService.reopenForum(forum.tenant().id(), forum.forumId().id());

        forum = DomainRegistry
                    .forumRepository()
                    .forumOfId(
                            forum.tenant(),
                            forum.forumId());

        assertNotNull(forum);
        assertFalse(forum.isClosed());
    }

    public void testStartForum() throws Exception {

        Forum forum = this.forumAggregate();

        ForumCommandResult result = new ForumCommandResult() {
            @Override
            public void resultingForumId(String aForumId) {
                forumId = aForumId;
            }
            @Override
            public void resultingDiscussionId(String aDiscussionId) {
                throw new UnsupportedOperationException("Should not be reached.");
            }
        };

        forumApplicationService.startForum(
                forum.tenant().id(),
                forum.creator().identity(),
                forum.moderator().identity(),
                forum.subject(),
                forum.description(),
                result);

        assertNotNull(this.forumId);

        Forum newlyStartedForum =
                DomainRegistry
                    .forumRepository()
                    .forumOfId(forum.tenant(), new ForumId(this.forumId));

        assertNotNull(newlyStartedForum);
        assertEquals(forum.tenant(), newlyStartedForum.tenant());
        assertEquals(this.forumId, newlyStartedForum.forumId().id());
        assertEquals(forum.creator().identity(), newlyStartedForum.creator().identity());
        assertEquals(forum.moderator().identity(), newlyStartedForum.moderator().identity());
        assertEquals(forum.subject(), newlyStartedForum.subject());
        assertEquals(forum.description(), newlyStartedForum.description());
    }

    public void testStartExclusiveForum() throws Exception {

        Forum forum = this.forumAggregate();

        ForumCommandResult result = new ForumCommandResult() {
            @Override
            public void resultingForumId(String aForumId) {
                forumId = aForumId;
            }
            @Override
            public void resultingDiscussionId(String aDiscussionId) {
                throw new UnsupportedOperationException("Should not be reached.");
            }
        };

        String exclusiveOwner = UUID.randomUUID().toString().toUpperCase();

        forumApplicationService.startExclusiveForum(
                forum.tenant().id(),
                exclusiveOwner,
                forum.creator().identity(),
                forum.moderator().identity(),
                forum.subject(),
                forum.description(),
                result);

        assertNotNull(this.forumId);

        Forum newlyStartedForum =
                DomainRegistry
                    .forumRepository()
                    .forumOfId(forum.tenant(), new ForumId(this.forumId));

        assertNotNull(newlyStartedForum);
        assertEquals(forum.tenant(), newlyStartedForum.tenant());
        assertEquals(this.forumId, newlyStartedForum.forumId().id());
        assertEquals(forum.creator().identity(), newlyStartedForum.creator().identity());
        assertEquals(forum.moderator().identity(), newlyStartedForum.moderator().identity());
        assertEquals(forum.subject(), newlyStartedForum.subject());
        assertEquals(forum.description(), newlyStartedForum.description());
        assertEquals(exclusiveOwner, newlyStartedForum.exclusiveOwner());
    }

    public void testStartExclusiveForumWithDiscussion() throws Exception {

        Forum forum = this.forumAggregate();

        ForumCommandResult result = new ForumCommandResult() {
            @Override
            public void resultingForumId(String aForumId) {
                forumId = aForumId;
            }
            @Override
            public void resultingDiscussionId(String aDiscussionId) {
                discussionId = aDiscussionId;
            }
        };

        String exclusiveOwner = UUID.randomUUID().toString().toUpperCase();

        forumApplicationService.startExclusiveForumWithDiscussion(
                forum.tenant().id(),
                exclusiveOwner,
                forum.creator().identity(),
                forum.moderator().identity(),
                "authorId1",
                forum.subject(),
                forum.description(),
                "Discussion Subject",
                result);

        assertNotNull(this.forumId);
        assertNotNull(this.discussionId);

        Forum newlyStartedForum =
                DomainRegistry
                    .forumRepository()
                    .forumOfId(forum.tenant(), new ForumId(this.forumId));

        assertNotNull(newlyStartedForum);
        assertEquals(forum.tenant(), newlyStartedForum.tenant());
        assertEquals(this.forumId, newlyStartedForum.forumId().id());
        assertEquals(forum.creator().identity(), newlyStartedForum.creator().identity());
        assertEquals(forum.moderator().identity(), newlyStartedForum.moderator().identity());
        assertEquals(forum.subject(), newlyStartedForum.subject());
        assertEquals(forum.description(), newlyStartedForum.description());
        assertEquals(exclusiveOwner, newlyStartedForum.exclusiveOwner());

        Discussion newlyStartedDiscussion =
                DomainRegistry
                    .discussionRepository()
                    .discussionOfId(forum.tenant(), new DiscussionId(this.discussionId));

        assertNotNull(newlyStartedDiscussion);
        assertEquals("authorId1", newlyStartedDiscussion.author().identity());
        assertEquals("Discussion Subject", newlyStartedDiscussion.subject());
    }
}
