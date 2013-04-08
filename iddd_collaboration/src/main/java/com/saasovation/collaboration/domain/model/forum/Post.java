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

import java.util.Date;
import java.util.List;

import com.saasovation.collaboration.domain.model.collaborator.Author;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.EventSourcedRootEntity;

public class Post extends EventSourcedRootEntity {

    private Author author;
    private String bodyText;
    private Date changedOn;
    private Date createdOn;
    private DiscussionId discussionId;
    private ForumId forumId;
    private PostId postId;
    private PostId replyToPostId;
    private String subject;
    private Tenant tenant;

    public Post(List<DomainEvent> anEventStream, int aStreamVersion) {
        super(anEventStream, aStreamVersion);
    }

    public Author author() {
        return this.author;
    }

    public String bodyText() {
        return this.bodyText;
    }

    public Date changedOn() {
        return this.changedOn;
    }

    public Date createdOn() {
        return this.createdOn;
    }

    public DiscussionId discussionId() {
        return this.discussionId;
    }

    public ForumId forumId() {
        return this.forumId;
    }

    public PostId postId() {
        return this.postId;
    }

    public PostId replyToPostId() {
        return this.replyToPostId;
    }

    public String subject() {
        return this.subject;
    }

    public Tenant tenant() {
        return this.tenant;
    }

    protected Post(
            Tenant aTenant,
            ForumId aForumId,
            DiscussionId aDiscussionId,
            PostId aPostId,
            Author anAuthor,
            String aSubject,
            String aBodyText) {

        this(aTenant, aForumId, aDiscussionId, null, aPostId, anAuthor, aSubject, aBodyText);
    }

    protected Post(
            Tenant aTenant,
            ForumId aForumId,
            DiscussionId aDiscussionId,
            PostId aReplyToPost,
            PostId aPostId,
            Author anAuthor,
            String aSubject,
            String aBodyText) {

        this.assertArgumentNotNull(anAuthor, "The author must be provided.");
        this.assertArgumentNotEmpty(aBodyText, "The body text must be provided.");
        this.assertArgumentNotNull(aDiscussionId, "The discussion id must be provided.");
        this.assertArgumentNotNull(aForumId, "The forum id must be provided.");
        this.assertArgumentNotNull(aPostId, "The post id must be provided.");
        this.assertArgumentNotEmpty(aSubject, "The subject must be provided.");
        this.assertArgumentNotNull(aTenant, "The tenant must be provided.");

        this.apply(new PostedToDiscussion(aTenant, aForumId, aDiscussionId,
                aReplyToPost, aPostId, anAuthor, aSubject, aBodyText));
    }

    protected Post() {
        super();
    }

    protected void alterPostContent(String aSubject, String aBodyText) {

        this.assertArgumentNotEmpty(aSubject, "The subject must be provided.");
        this.assertArgumentNotEmpty(aBodyText, "The body text must be provided.");

        this.apply(new PostContentAltered(this.tenant(), this.forumId(), this.discussionId(),
                this.postId(), aSubject, aBodyText));
    }

    protected void when(PostContentAltered anEvent) {
        this.setBodyText(anEvent.bodyText());
        this.setChangedOn(anEvent.occurredOn());
        this.setSubject(anEvent.subject());
    }

    protected void when(PostedToDiscussion anEvent) {
        this.setAuthor(anEvent.author());
        this.setBodyText(anEvent.bodyText());
        this.setChangedOn(anEvent.occurredOn());
        this.setCreatedOn(anEvent.occurredOn());
        this.setDiscussionId(anEvent.discussionId());
        this.setForumId(anEvent.forumId());
        this.setPostId(anEvent.postId());
        this.setReplyToPostId(anEvent.replyToPost());
        this.setSubject(anEvent.subject());
        this.setTenant(anEvent.tenant());
    }

    private void setAuthor(Author anAuthor) {
        this.author = anAuthor;
    }

    private void setBodyText(String aBodyText) {
        this.bodyText = aBodyText;
    }

    private void setChangedOn(Date aChangedOnDate) {
        this.changedOn = aChangedOnDate;
    }

    private void setCreatedOn(Date aCreatedOnDate) {
        this.createdOn = aCreatedOnDate;
    }

    private void setDiscussionId(DiscussionId aDiscussionId) {
        this.discussionId = aDiscussionId;
    }

    private void setForumId(ForumId aForumId) {
        this.forumId = aForumId;
    }

    private void setPostId(PostId aPostId) {
        this.postId = aPostId;
    }

    private void setReplyToPostId(PostId aReplyToPostId) {
        this.replyToPostId = aReplyToPostId;
    }

    private void setSubject(String aSubject) {
        this.subject = aSubject;
    }

    private void setTenant(Tenant aTenant) {
        this.tenant = aTenant;
    }
}
