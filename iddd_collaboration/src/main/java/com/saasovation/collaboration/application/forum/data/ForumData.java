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

public class ForumData {

    private boolean closed;
    private String creatorEmailAddress;
    private String creatorIdentity;
    private String creatorName;
    private String description;
    private String exclusiveOwner;
    private String forumId;
    private String moderatorEmailAddress;
    private String moderatorIdentity;
    private String moderatorName;
    private String subject;
    private String tenantId;

    public ForumData() {
        super();
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getCreatorEmailAddress() {
        return this.creatorEmailAddress;
    }

    public void setCreatorEmailAddress(String creatorEmailAddress) {
        this.creatorEmailAddress = creatorEmailAddress;
    }

    public String getCreatorIdentity() {
        return this.creatorIdentity;
    }

    public void setCreatorIdentity(String creatorIdentity) {
        this.creatorIdentity = creatorIdentity;
    }

    public String getCreatorName() {
        return this.creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getModeratorEmailAddress() {
        return this.moderatorEmailAddress;
    }

    public void setModeratorEmailAddress(String moderatorEmailAddress) {
        this.moderatorEmailAddress = moderatorEmailAddress;
    }

    public String getModeratorIdentity() {
        return this.moderatorIdentity;
    }

    public void setModeratorIdentity(String moderatorIdentity) {
        this.moderatorIdentity = moderatorIdentity;
    }

    public String getModeratorName() {
        return this.moderatorName;
    }

    public void setModeratorName(String moderatorName) {
        this.moderatorName = moderatorName;
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
