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

import javax.sql.DataSource;

import com.saasovation.collaboration.application.forum.data.ForumData;
import com.saasovation.collaboration.application.forum.data.ForumDiscussionsData;
import com.saasovation.common.port.adapter.persistence.AbstractQueryService;
import com.saasovation.common.port.adapter.persistence.JoinOn;

public class ForumQueryService extends AbstractQueryService {

    public ForumQueryService(DataSource aDataSource) {
        super(aDataSource);
    }

    public Collection<ForumData> allForumsDataOfTenant(String aTenantId) {
        return this.queryObjects(
                ForumData.class,
                "select * from tbl_vw_forum where tenant_id = ?",
                new JoinOn(),
                aTenantId);
    }

    public ForumData forumDataOfId(String aTenantId, String aForumId) {
        return this.queryObject(
                ForumData.class,
                "select * from tbl_vw_forum where tenant_id = ? and forum_id = ?",
                new JoinOn(),
                aTenantId,
                aForumId);
    }

    public ForumDiscussionsData forumDiscussionsDataOfId(String aTenantId, String aForumId) {
        return this.queryObject(
                ForumDiscussionsData.class,
                "select "
                +  "forum.closed, forum.creator_email_address, forum.creator_identity, "
                +  "forum.creator_name, forum.description, forum.exclusive_owner, forum.forum_id, "
                +  "forum.moderator_email_address, forum.moderator_identity, forum.moderator_name, "
                +  "forum.subject, forum.tenant_id, "
                +  "disc.author_email_address as o_discussions_author_email_address, "
                +  "disc.author_identity as o_discussions_author_identity, "
                +  "disc.author_name as o_discussions_author_name, "
                +  "disc.closed as o_discussions_closed, "
                +  "disc.discussion_id as o_discussions_discussion_id, "
                +  "disc.exclusive_owner as o_discussions_exclusive_owner, "
                +  "disc.forum_id as o_discussions_forum_id, "
                +  "disc.subject as o_discussions_subject, "
                +  "disc.tenant_id as o_discussions_tenant_id "
                + "from tbl_vw_forum as forum left outer join tbl_vw_discussion as disc "
                + " on forum.forum_id = disc.forum_id "
                + "where (forum.tenant_id = ? and forum.forum_id = ?)",
                new JoinOn("forum_id", "o_discussions_forum_id"),
                aTenantId,
                aForumId);
    }

    public String forumIdOfExclusiveOwner(String aTenantId, String anExclusiveOwner) {
        return this.queryString(
                "select forum_id from tbl_vw_forum where tenant_id = ? and exclusive_owner = ?",
                aTenantId,
                anExclusiveOwner);
    }
}
