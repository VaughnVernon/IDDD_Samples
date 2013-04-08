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

import com.saasovation.collaboration.application.forum.data.PostData;
import com.saasovation.common.port.adapter.persistence.AbstractQueryService;
import com.saasovation.common.port.adapter.persistence.JoinOn;

public class PostQueryService extends AbstractQueryService {

    public PostQueryService(DataSource aDataSource) {
        super(aDataSource);
    }

    public Collection<PostData> allPostsDataOfDiscussion(String aTenantId, String aDiscussionId) {
        return this.queryObjects(
                PostData.class,
                "select * from tbl_vw_post where tenant_id = ? and discussion_id = ?",
                new JoinOn(),
                aTenantId,
                aDiscussionId);
    }

    public PostData postDataOfId(String aTenantId, String aPostId) {
        return this.queryObject(
                PostData.class,
                "select * from tbl_vw_post where tenant_id = ? and post_id = ?",
                new JoinOn(),
                aTenantId,
                aPostId);
    }
}
