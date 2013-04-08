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
import com.saasovation.collaboration.application.forum.data.DiscussionData;
import com.saasovation.collaboration.application.forum.data.DiscussionPostsData;
import com.saasovation.collaboration.application.forum.data.PostData;
import com.saasovation.collaboration.domain.model.DomainRegistry;
import com.saasovation.collaboration.domain.model.forum.Discussion;
import com.saasovation.collaboration.domain.model.forum.Forum;
import com.saasovation.collaboration.domain.model.forum.Post;

public class DiscussionQueryServiceTest extends ApplicationTest {

    public DiscussionQueryServiceTest() {
        super();
    }

    public void testAllDiscussionsDataOfForum() throws Exception {

        Forum forum = this.forumAggregate();
        DomainRegistry.forumRepository().save(forum);

        Discussion[] discussions = this.discussionAggregates(forum);

        for (Discussion discussion : discussions) {
            DomainRegistry.discussionRepository().save(discussion);
        }

        Collection<DiscussionData> discussionsData =
                discussionQueryService.allDiscussionsDataOfForum(
                        forum.tenant().id(),
                        forum.forumId().id());

        assertNotNull(discussionsData);
        assertFalse(discussionsData.isEmpty());
        assertEquals(discussions.length, discussionsData.size());
    }

    public void testDiscussionDataOfId() throws Exception {

        Forum forum = this.forumAggregate();
        DomainRegistry.forumRepository().save(forum);

        Discussion discussion = this.discussionAggregate(forum);
        DomainRegistry.discussionRepository().save(discussion);

        DiscussionData discussionData =
                discussionQueryService.discussionDataOfId(
                        discussion.tenant().id(), discussion.discussionId().id());

        assertNotNull(discussionData);
        assertEquals(discussion.discussionId().id(), discussionData.getDiscussionId());
        assertEquals(discussion.forumId().id(), discussionData.getForumId());
        assertEquals(discussion.tenant().id(), discussionData.getTenantId());
        assertEquals(discussion.author().emailAddress(), discussionData.getAuthorEmailAddress());
        assertEquals(discussion.author().identity(), discussionData.getAuthorIdentity());
        assertEquals(discussion.author().name(), discussionData.getAuthorName());
        assertEquals(discussion.subject(), discussionData.getSubject());
        assertEquals(discussion.exclusiveOwner(), discussionData.getExclusiveOwner());
        assertEquals(discussion.isClosed(), discussionData.isClosed());
    }

    public void testDiscussionIdOfExclusiveOwner() throws Exception {

        Forum forum = this.forumAggregate();
        DomainRegistry.forumRepository().save(forum);

        Discussion discussion = this.discussionAggregate(forum);
        DomainRegistry.discussionRepository().save(discussion);

        String discussionId =
                discussionQueryService.discussionIdOfExclusiveOwner(
                        discussion.tenant().id(), discussion.exclusiveOwner());

        assertNotNull(discussionId);
        assertEquals(discussion.discussionId().id(), discussionId);
    }

    public void testDiscussionPostsDataOfId() throws Exception {

        Forum forum = this.forumAggregate();
        DomainRegistry.forumRepository().save(forum);

        Discussion discussion = this.discussionAggregate(forum);
        DomainRegistry.discussionRepository().save(discussion);


        Post[] posts = this.postAggregates(discussion);

        for (Post post : posts) {
            DomainRegistry.postRepository().save(post);
        }

        DiscussionPostsData discussionPostsData =
                discussionQueryService.discussionPostsDataOfId(
                        discussion.tenant().id(), discussion.discussionId().id());

        assertNotNull(discussionPostsData);
        assertEquals(discussion.discussionId().id(), discussionPostsData.getDiscussionId());
        assertEquals(discussion.forumId().id(), discussionPostsData.getForumId());
        assertEquals(discussion.tenant().id(), discussionPostsData.getTenantId());
        assertEquals(discussion.author().emailAddress(), discussionPostsData.getAuthorEmailAddress());
        assertEquals(discussion.author().identity(), discussionPostsData.getAuthorIdentity());
        assertEquals(discussion.author().name(), discussionPostsData.getAuthorName());
        assertEquals(discussion.subject(), discussionPostsData.getSubject());
        assertEquals(discussion.exclusiveOwner(), discussionPostsData.getExclusiveOwner());
        assertEquals(discussion.isClosed(), discussionPostsData.isClosed());

        assertNotNull(discussionPostsData.getPosts());
        assertFalse(discussionPostsData.getPosts().isEmpty());
        assertEquals(posts.length, discussionPostsData.getPosts().size());

        for (PostData post : discussionPostsData.getPosts()) {
            assertNotNull(post.getAuthorEmailAddress());
            assertNotNull(post.getAuthorName());
            assertNotNull(post.getBodyText());
            assertNotNull(post.getSubject());
            assertTrue(post.getAuthorIdentity().equals("jdoe") ||
                       post.getAuthorIdentity().equals("zoe") ||
                       post.getAuthorIdentity().equals("joe"));
        }
    }
}
