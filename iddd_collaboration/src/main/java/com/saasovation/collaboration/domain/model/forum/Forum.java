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
import com.saasovation.collaboration.domain.model.collaborator.Creator;
import com.saasovation.collaboration.domain.model.collaborator.Moderator;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.EventSourcedRootEntity;

public class Forum extends EventSourcedRootEntity {

    private boolean closed;
    private Creator creator;
    private String description;
    private String exclusiveOwner;
    private ForumId forumId;
    private Moderator moderator;
    private String subject;
    private Tenant tenant;

    public Forum(
            Tenant aTenant,
            ForumId aForumId,
            Creator aCreator,
            Moderator aModerator,
            String aSubject,
            String aDescription,
            String anExclusiveOwner) {

        this();

        this.assertArgumentNotNull(aCreator, "The creator must be provided.");
        this.assertArgumentNotEmpty(aDescription, "The description must be provided.");
        this.assertArgumentNotNull(aForumId, "The forum id must be provided.");
        this.assertArgumentNotNull(aModerator, "The moderator must be provided.");
        this.assertArgumentNotEmpty(aSubject, "The subject must be provided.");
        this.assertArgumentNotNull(aTenant, "The creator must be provided.");

        this.apply(new ForumStarted(aTenant, aForumId, aCreator,
                aModerator, aSubject, aDescription, anExclusiveOwner));
    }

    public Forum(List<DomainEvent> anEventStream, int aStreamVersion) {
        super(anEventStream, aStreamVersion);
    }

    public void assignModerator(Moderator aModerator) {
        this.assertStateFalse(this.isClosed(), "Forum is closed.");
        this.assertArgumentNotNull(aModerator, "The moderator must be provided.");

        this.apply(new ForumModeratorChanged(this.tenant(), this.forumId(),
                aModerator, this.exclusiveOwner()));
    }

    public void changeDescription(String aDescription) {
        this.assertStateFalse(this.isClosed(), "Forum is closed.");
        this.assertArgumentNotEmpty(aDescription, "The description must be provided.");

        this.apply(new ForumDescriptionChanged(this.tenant(), this.forumId(),
                aDescription, this.exclusiveOwner()));
    }

    public void changeSubject(String aSubject) {
        this.assertStateFalse(this.isClosed(), "Forum is closed.");
        this.assertArgumentNotEmpty(aSubject, "The subject must be provided.");

        this.apply(new ForumSubjectChanged(this.tenant(), this.forumId(),
                aSubject, this.exclusiveOwner()));
    }

    public void close() {
        this.assertStateFalse(this.isClosed(), "Forum is already closed.");

        this.apply(new ForumClosed(this.tenant(), this.forumId(), this.exclusiveOwner()));
    }

    public boolean isClosed() {
        return this.closed;
    }

    public Creator creator() {
        return this.creator;
    }

    public String description() {
        return this.description;
    }

    public String exclusiveOwner() {
        return this.exclusiveOwner;
    }

    public boolean hasExclusiveOwner() {
        return this.exclusiveOwner() != null;
    }

    public ForumId forumId() {
        return this.forumId;
    }

    public boolean isModeratedBy(Moderator aModerator) {
        return this.moderator().equals(aModerator);
    }

    public void moderatePost(
            Post aPost,
            Moderator aModerator,
            String aSubject,
            String aBodyText) {

        this.assertStateFalse(this.isClosed(), "Forum is closed.");
        this.assertArgumentNotNull(aPost, "Post may not be null.");
        this.assertArgumentEquals(aPost.forumId(), this.forumId(), "Not a post of this forum.");
        this.assertArgumentTrue(this.isModeratedBy(aModerator), "Not the moderator of this forum.");

        aPost.alterPostContent(aSubject, aBodyText);
    }

    public Moderator moderator() {
        return this.moderator;
    }

    public void reopen() {
        this.assertStateTrue(this.isClosed(), "Forum is not closed.");

        this.apply(new ForumReopened(this.tenant(), this.forumId(), this.exclusiveOwner()));
    }

    public Discussion startDiscussion(
            ForumIdentityService aForumIdentityService,
            Author anAuthor,
            String aSubject) {

        return this.startDiscussionFor(aForumIdentityService, anAuthor, aSubject, null);
    }

    public Discussion startDiscussionFor(
            ForumIdentityService aForumIdentityService,
            Author anAuthor,
            String aSubject,
            String anExclusiveOwner) {

        if (this.isClosed()) {
            throw new IllegalStateException("Forum is closed.");
        }

        Discussion discussion =
                new Discussion(
                    this.tenant(),
                    this.forumId(),
                    aForumIdentityService.nextDiscussionId(),
                    anAuthor,
                    aSubject,
                    anExclusiveOwner);

        return discussion;
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
            Forum typedObject = (Forum) anObject;
            equalObjects =
                this.tenant().equals(typedObject.tenant()) &&
                this.forumId().equals(typedObject.forumId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (75219 * 41)
            + this.tenant().hashCode()
            + this.forumId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Forum [closed=" + closed + ", creator=" + creator
                + ", description=" + description + ", exclusiveOwner="+ exclusiveOwner
                + ", forumId=" + forumId + ", moderator=" + moderator
                + ", subject=" + subject + ", tenantId=" + tenant + "]";
    }

    protected Forum() {
        super();
    }

    protected void when(ForumClosed anEvent) {
        this.setClosed(true);
    }

    protected void when(ForumDescriptionChanged anEvent) {
        this.setDescription(anEvent.description());
    }

    protected void when(ForumModeratorChanged anEvent) {
        this.setModerator(anEvent.moderator());
    }

    protected void when(ForumReopened anEvent) {
        this.setClosed(false);
    }

    protected void when(ForumStarted anEvent) {
        this.setCreator(anEvent.creator());
        this.setDescription(anEvent.description());
        this.setExclusiveOwner(anEvent.exclusiveOwner());
        this.setForumId(anEvent.forumId());
        this.setModerator(anEvent.moderator());
        this.setSubject(anEvent.subject());
        this.setTenant(anEvent.tenant());
    }

    protected void when(ForumSubjectChanged anEvent) {
        this.setSubject(anEvent.subject());
    }

    private void setClosed(boolean isClosed) {
        this.closed = isClosed;
    }

    private void setCreator(Creator aCreator) {
        this.creator = aCreator;
    }

    private void setDescription(String aDescription) {
        this.description = aDescription;
    }

    private void setExclusiveOwner(String anExclusiveOwner) {
        this.exclusiveOwner = anExclusiveOwner;
    }

    private void setForumId(ForumId aForumId) {
        this.forumId = aForumId;
    }

    private void setModerator(Moderator aModerator) {
        this.moderator = aModerator;
    }

    private void setSubject(String aSubject) {
        this.subject = aSubject;
    }

    private void setTenant(Tenant aTenant) {
        this.tenant = aTenant;
    }
}
