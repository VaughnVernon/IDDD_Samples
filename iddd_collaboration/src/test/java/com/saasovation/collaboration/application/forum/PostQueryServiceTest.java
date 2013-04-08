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
import com.saasovation.collaboration.application.forum.data.PostData;
import com.saasovation.collaboration.domain.model.DomainRegistry;
import com.saasovation.collaboration.domain.model.forum.Discussion;
import com.saasovation.collaboration.domain.model.forum.Forum;
import com.saasovation.collaboration.domain.model.forum.Post;

public class PostQueryServiceTest extends ApplicationTest {

    public PostQueryServiceTest() {
        super();
    }

    public void testAllPostsDataOfDiscussion() throws Exception {

        Forum forum = this.forumAggregate();
        DomainRegistry.forumRepository().save(forum);

        Discussion discussion = this.discussionAggregate(forum);
        DomainRegistry.discussionRepository().save(discussion);

        Post[] posts = this.postAggregates(discussion);

        for (Post post : posts) {
            DomainRegistry.postRepository().save(post);
        }

        Collection<PostData> postsData =
                postQueryService.allPostsDataOfDiscussion(
                        forum.tenant().id(),
                        discussion.discussionId().id());

        assertNotNull(postsData);
        assertFalse(postsData.isEmpty());
        assertEquals(posts.length, postsData.size());
    }

    public void testPostDataOfId() throws Exception {

        Forum forum = this.forumAggregate();
        DomainRegistry.forumRepository().save(forum);

        Discussion discussion = this.discussionAggregate(forum);
        DomainRegistry.discussionRepository().save(discussion);

        Post post = this.postAggregate(discussion);
        DomainRegistry.postRepository().save(post);

        PostData postData =
                postQueryService.postDataOfId(
                        post.tenant().id(), post.postId().id());

        assertNotNull(postData);
        assertEquals(post.postId().id(), postData.getPostId());
        assertEquals(post.discussionId().id(), postData.getDiscussionId());
        assertEquals(post.forumId().id(), postData.getForumId());
        assertEquals(post.tenant().id(), postData.getTenantId());
        assertEquals(post.author().emailAddress(), postData.getAuthorEmailAddress());
        assertEquals(post.author().identity(), postData.getAuthorIdentity());
        assertEquals(post.author().name(), postData.getAuthorName());
        // assertEquals(post.changedOn(), postData.getChangedOn());
        // assertEquals(post.createdOn(), postData.getCreatedOn());
        assertEquals(post.subject(), postData.getSubject());
        assertEquals(post.bodyText(), postData.getBodyText());

        if (postData.getReplyToPostId() == null) {
            assertEquals(post.replyToPostId(), postData.getReplyToPostId());
        } else {
            assertEquals(post.replyToPostId().id(), postData.getReplyToPostId());
        }
    }
}
