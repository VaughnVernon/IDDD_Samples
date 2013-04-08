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

import com.saasovation.collaboration.application.ApplicationTest;
import com.saasovation.collaboration.application.forum.data.DiscussionCommandResult;
import com.saasovation.collaboration.domain.model.DomainRegistry;
import com.saasovation.collaboration.domain.model.forum.Discussion;
import com.saasovation.collaboration.domain.model.forum.Forum;
import com.saasovation.collaboration.domain.model.forum.Post;
import com.saasovation.collaboration.domain.model.forum.PostId;
import com.saasovation.collaboration.domain.model.tenant.Tenant;

public class PostApplicationServiceTest extends ApplicationTest {

    private String discussionId;
    private String postId;

    public PostApplicationServiceTest() {
        super();
    }

    public void testModeratePost() throws Exception {

        Tenant tenant = new Tenant("01234567");

        Forum forum =
            new Forum(
                    tenant,
                    DomainRegistry.forumRepository().nextIdentity(),
                    collaboratorService.creatorFrom(tenant, "jdoe"),
                    collaboratorService.moderatorFrom(tenant, "jdoe"),
                    "A Forum",
                    "A forum description.",
                    null);

        DomainRegistry.forumRepository().save(forum);

        Discussion discussion = this.discussionAggregate(forum);

        DomainRegistry.discussionRepository().save(discussion);

        DiscussionCommandResult result = new DiscussionCommandResult() {
            @Override
            public void resultingDiscussionId(String aDiscussionId) {
                discussionId = aDiscussionId;
            }
            @Override
            public void resultingPostId(String aPostId) {
                postId = aPostId;
            }
            @Override
            public void resultingInReplyToPostId(String aReplyToPostId) {
                throw new UnsupportedOperationException("Should not be reached.");
            }
        };

        discussionApplicationService
            .postToDiscussion(
                    discussion.tenant().id(),
                    discussion.discussionId().id(),
                    "authorId1",
                    "Post Test",
                    "Post test text...",
                    result);

        postApplicationService
            .moderatePost(
                discussion.tenant().id(),
                forum.forumId().id(),
                postId,
                forum.moderator().identity(),
                "Post Moderated Subject Test",
                "Post moderated text test...");

        Post post =
                DomainRegistry
                    .postRepository()
                    .postOfId(
                            discussion.tenant(),
                            new PostId(postId));

        assertNotNull(discussionId);
        assertNotNull(post);
        assertEquals("Post Moderated Subject Test", post.subject());
        assertEquals("Post moderated text test...", post.bodyText());
    }
}
