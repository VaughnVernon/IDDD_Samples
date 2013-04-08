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

public class DiscussionData {

    private String authorEmailAddress;
    private String authorIdentity;
    private String authorName;
    private boolean closed;
    private String discussionId;
    private String exclusiveOwner;
    private String forumId;
    private String subject;
    private String tenantId;

    public DiscussionData() {
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

    public boolean isClosed() {
        return this.closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getDiscussionId() {
        return this.discussionId;
    }

    public void setDiscussionId(String discussionId) {
        this.discussionId = discussionId;
    }

    public String getExclusiveOwner() {
        return this.exclusiveOwner;
    }

    public void setExclusiveOwner(String exclusiveOwner) {
        this.exclusiveOwner = exclusiveOwner;
    }

    public String getForumId() {
        return this.forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
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
