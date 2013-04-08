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

package com.saasovation.collaboration.port.adapter.persistence.view;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.saasovation.collaboration.domain.model.forum.DiscussionClosed;
import com.saasovation.collaboration.domain.model.forum.DiscussionReopened;
import com.saasovation.collaboration.domain.model.forum.DiscussionStarted;
import com.saasovation.common.event.sourcing.DispatchableDomainEvent;
import com.saasovation.common.event.sourcing.EventDispatcher;
import com.saasovation.common.port.adapter.persistence.AbstractProjection;
import com.saasovation.common.port.adapter.persistence.ConnectionProvider;

public class MySQLDiscussionProjection
        extends AbstractProjection
        implements EventDispatcher {

    private static final Class<?> understoodEventTypes[] = {
        DiscussionClosed.class,
        DiscussionReopened.class,
        DiscussionStarted.class
    };

    public MySQLDiscussionProjection(EventDispatcher aParentEventDispatcher) {
        super();

        aParentEventDispatcher.registerEventDispatcher(this);
    }

    @Override
    public void dispatch(DispatchableDomainEvent aDispatchableDomainEvent) {
        this.projectWhen(aDispatchableDomainEvent);
    }

    @Override
    public void registerEventDispatcher(EventDispatcher anEventDispatcher) {
        throw new UnsupportedOperationException("Cannot register additional dispatchers.");
    }

    @Override
    public boolean understands(DispatchableDomainEvent aDispatchableDomainEvent) {
        return this.understandsAnyOf(
                aDispatchableDomainEvent.domainEvent().getClass(),
                understoodEventTypes);
    }

    protected void when(DiscussionClosed anEvent) throws Exception {
        Connection connection = ConnectionProvider.connection();

        PreparedStatement statement =
                connection.prepareStatement(
                        "update tbl_vw_discussion "
                        + "set closed=1 "
                        + "where tenant_id = ? and discussion_id = ?");

        statement.setString(1, anEvent.tenant().id());
        statement.setString(2, anEvent.discussionId().id());

        this.execute(statement);
    }

    protected void when(DiscussionReopened anEvent) throws Exception {
        Connection connection = ConnectionProvider.connection();

        PreparedStatement statement =
                connection.prepareStatement(
                        "update tbl_vw_discussion "
                        + "set closed=0 "
                        + "where tenant_id = ? and discussion_id = ?");

        statement.setString(1, anEvent.tenant().id());
        statement.setString(2, anEvent.discussionId().id());

        this.execute(statement);
    }

    protected void when(DiscussionStarted anEvent) throws Exception {
        Connection connection = ConnectionProvider.connection();

        // idempotent operation
        if (this.exists(
                "select discussion_id from tbl_vw_discussion "
                    + "where tenant_id = ? and discussion_id = ?",
                anEvent.tenant().id(),
                anEvent.discussionId().id())) {
            return;
        }

        PreparedStatement statement =
                connection.prepareStatement(
                        "insert into tbl_vw_discussion( "
                        + "discussion_id, author_email_address, author_identity, author_name, "
                        + "closed, exclusive_owner, forum_id, "
                        + "subject, tenant_id"
                        + ") values(?,?,?,?,?,?,?,?,?)");

        statement.setString(1, anEvent.discussionId().id());
        statement.setString(2, anEvent.author().emailAddress());
        statement.setString(3, anEvent.author().identity());
        statement.setString(4, anEvent.author().name());
        statement.setInt(5, 0);
        statement.setString(6, anEvent.exclusiveOwner());
        statement.setString(7, anEvent.forumId().id());
        statement.setString(8, anEvent.subject());
        statement.setString(9, anEvent.tenant().id());

        this.execute(statement);
    }
}
