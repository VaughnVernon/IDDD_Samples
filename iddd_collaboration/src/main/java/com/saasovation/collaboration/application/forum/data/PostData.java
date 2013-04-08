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

package com.saasovation.collaboration.application.forum.data;

import java.util.Date;

public class PostData {

    private String authorEmailAddress;
    private String authorIdentity;
    private String authorName;
    private String bodyText;
    private Date changedOn;
    private Date createdOn;
    private String discussionId;
    private String forumId;
    private String postId;
    private String replyToPostId;
    private String subject;
    private String tenantId;

    public PostData() {
        super();
    }

    public String getAuthorEmailAddress() {
        return this.authorEmailAddress;
    }

    public void setAuthorEmailAddress(String authorEmailAddress) {
        this.authorEmailAddress = authorEmailAddress;
    }

    public String getAuthorIdentity() {
        return this.authorIdentity;
    }

    public void setAuthorIdentity(String authorIdentity) {
        this.authorIdentity = authorIdentity;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBodyText() {
        return this.bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public Date getChangedOn() {
        return this.changedOn;
    }

    public void setChangedOn(Date changedOn) {
        this.changedOn = changedOn;
    }

    public Date getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getDiscussionId() {
        return this.discussionId;
    }

    public void setDiscussionId(String discussionId) {
        this.discussionId = discussionId;
    }

    public String getForumId() {
        return this.forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public String getPostId() {
        return this.postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getReplyToPostId() {
        return this.replyToPostId;
    }

    public void setReplyToPostId(String replyToPostId) {
        this.replyToPostId = replyToPostId;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
