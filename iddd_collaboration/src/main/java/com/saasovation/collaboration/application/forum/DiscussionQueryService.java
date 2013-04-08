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

import com.saasovation.collaboration.application.forum.data.DiscussionData;
import com.saasovation.collaboration.application.forum.data.DiscussionPostsData;
import com.saasovation.common.port.adapter.persistence.AbstractQueryService;
import com.saasovation.common.port.adapter.persistence.JoinOn;

public class DiscussionQueryService extends AbstractQueryService {

    public DiscussionQueryService(DataSource aDataSource) {
        super(aDataSource);
    }

    public Collection<DiscussionData> allDiscussionsDataOfForum(String aTenantId, String aForumId) {
        return this.queryObjects(
                DiscussionData.class,
                "select * from tbl_vw_discussion where tenant_id = ? and forum_id = ?",
                new JoinOn(),
                aTenantId,
                aForumId);
    }

    public DiscussionData discussionDataOfId(String aTenantId, String aDiscussionId) {
        return this.queryObject(
                DiscussionData.class,
                "select * from tbl_vw_discussion where tenant_id = ? and discussion_id = ?",
                new JoinOn(),
                aTenantId,
                aDiscussionId);
    }

    public String discussionIdOfExclusiveOwner(String aTenantId, String anExclusiveOwner) {
        return this.queryString(
                "select discussion_id from tbl_vw_discussion where tenant_id = ? and exclusive_owner = ?",
                aTenantId,
                anExclusiveOwner);
    }

    public DiscussionPostsData discussionPostsDataOfId(String aTenantId, String aDiscussionId) {
        return this.queryObject(
                DiscussionPostsData.class,
                "select "
                +  "disc.author_email_address, disc.author_identity, disc.author_name, "
                +  "disc.closed, disc.discussion_id, disc.exclusive_owner, "
                +  "disc.forum_id, disc.subject, disc.tenant_id, "
                +  "post.author_email_address as o_posts_author_email_address, "
                +  "post.author_identity as o_posts_author_identity, "
                +  "post.author_name as o_posts_author_name, "
                +  "post.body_text as o_posts_body_text, post.changed_on as o_posts_changed_on, "
                +  "post.created_on as o_posts_created_on, "
                +  "post.discussion_id as o_posts_discussion_id, "
                +  "post.forum_id as o_posts_forum_id, post.post_id as o_posts_post_id, "
                +  "post.reply_to_post_id as o_posts_reply_to_post_id, post.subject as o_posts_subject, "
                +  "post.tenant_id as o_posts_tenant_id "
                + "from tbl_vw_discussion as disc left outer join tbl_vw_post as post "
                + " on disc.discussion_id = post.discussion_id "
                + "where (disc.tenant_id = ? and disc.discussion_id = ?)",
                new JoinOn("discussion_id", "o_posts_discussion_id"),
                aTenantId,
                aDiscussionId);
    }
}
