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

import java.util.List;

import com.saasovation.collaboration.domain.model.collaborator.Author;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.EventSourcedRootEntity;

public class Discussion extends EventSourcedRootEntity {

    private Author author;
    private boolean closed;
    private DiscussionId discussionId;
    private String exclusiveOwner;
    private ForumId forumId;
    private String subject;
    private Tenant tenant;

    public Discussion(List<DomainEvent> anEventStream, int aStreamVersion) {
        super(anEventStream, aStreamVersion);
    }

    public Author author() {
        return this.author;
    }

    public void close() {
        if (this.isClosed()) {
            throw new IllegalStateException("This discussion is already closed.");
        }

        this.apply(new DiscussionClosed(this.tenant(), this.forumId(),
                    this.discussionId(), this.exclusiveOwner()));
    }

    public boolean isClosed() {
        return this.closed;
    }

    public DiscussionId discussionId() {
        return this.discussionId;
    }

    public String exclusiveOwner() {
        return this.exclusiveOwner;
    }

    public ForumId forumId() {
        return this.forumId;
    }

    public Post post(
            ForumIdentityService aForumIdentityService,
            Author anAuthor,
            String aSubject,
            String aBodyText) {

        return this.post(aForumIdentityService, null, anAuthor, aSubject, aBodyText);
    }

    public Post post(
            ForumIdentityService aForumIdentityService,
            PostId aReplyToPost,
            Author anAuthor,
            String aSubject,
            String aBodyText) {

        Post post =
            new Post(
                    this.tenant(),
                    this.forumId(),
                    this.discussionId(),
                    aReplyToPost,
                    aForumIdentityService.nextPostId(),
                    anAuthor,
                    aSubject,
                    aBodyText);

        return post;
    }


    public void reopen() {
        if (!this.isClosed()) {
            throw new IllegalStateException("The discussion is not closed.");
        }

        this.apply(new DiscussionReopened(this.tenant(), this.forumId(),
                    this.discussionId(), this.exclusiveOwner()));
    }

    public String subject() {
        return this.subject;
    }

    public Tenant tenant() {
        return this.tenant;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Discussion typedObject = (Discussion) anObject;
            equalObjects =
                this.tenant().equals(typedObject.tenant()) &&
                this.forumId().equals(typedObject.forumId()) &&
                this.discussionId().equals(typedObject.discussionId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (87123 * 43)
            + this.tenant().hashCode()
            + this.forumId().hashCode()
            + this.discussionId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Discussion [author=" + author + ", closed=" + closed + ", discussionId=" + discussionId + ", exclusiveOwner="
                + exclusiveOwner + ", forumId=" + forumId + ", subject=" + subject + ", tenantId=" + tenant + "]";
    }

    protected Discussion(
            Tenant aTenantId,
            ForumId aForumId,
            DiscussionId aDiscussionId,
            Author anAuthor,
            String aSubject,
            String anExclusiveOwner) {

        this();

        this.assertArgumentNotNull(anAuthor, "The author must be provided.");
        this.assertArgumentNotNull(aDiscussionId, "The discussion id must be provided.");
        this.assertArgumentNotNull(aForumId, "The forum id must be provided.");
        this.assertArgumentNotEmpty(aSubject, "The subject must be provided.");
        this.assertArgumentNotNull(aTenantId, "The tenant must be provided.");

        this.apply(new DiscussionStarted(aTenantId, aForumId, aDiscussionId,
                anAuthor, aSubject, anExclusiveOwner));
    }

    protected Discussion() {
        super();
    }

    protected void when(DiscussionClosed anEvent) {
        this.setClosed(true);
    }

    protected void when(DiscussionReopened anEvent) {
        this.setClosed(false);
    }

    protected void when(DiscussionStarted anEvent) {
        this.setAuthor(anEvent.author());
        this.setDiscussionId(anEvent.discussionId());
        this.setExclusiveOwner(anEvent.exclusiveOwner());
        this.setForumId(anEvent.forumId());
        this.setSubject(anEvent.subject());
        this.setTenant(anEvent.tenant());
    }

    private void setAuthor(Author author) {
        this.author = author;
    }

    private void setClosed(boolean isClosed) {
        this.closed = isClosed;
    }

    private void setDiscussionId(DiscussionId aDiscussionId) {
        this.discussionId = aDiscussionId;
    }

    private void setExclusiveOwner(String anExclusiveOwner) {
        this.exclusiveOwner = anExclusiveOwner;
    }

    private void setForumId(ForumId aForumId) {
        this.forumId = aForumId;
    }

    private void setSubject(String aSubject) {
        this.subject = aSubject;
    }

    private void setTenant(Tenant aTenant) {
        this.tenant = aTenant;
    }
}
