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

package com.saasovation.collaboration;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import com.saasovation.collaboration.port.adapter.persistence.EventStoreProvider;
import com.saasovation.common.port.adapter.persistence.ConnectionProvider;

public class StorageCleaner extends EventStoreProvider {

    private static final String[] tablesToClean = {
        "tbl_dispatcher_last_event",
        "tbl_es_event_store",
        "tbl_vw_calendar",
        "tbl_vw_calendar_sharer",
        "tbl_vw_calendar_entry",
        "tbl_vw_calendar_entry_invitee",
        "tbl_vw_forum",
        "tbl_vw_discussion",
        "tbl_vw_post"
    };

    private DataSource dataSource;

    public StorageCleaner(DataSource aDataSource) {
        super();

        this.dataSource = aDataSource;
    }

    public void clean() {
        this.eventStore().purge();

        Connection connection = ConnectionProvider.connection(this.dataSource);
        PreparedStatement statement = null;

        try {
            for (String tableName : tablesToClean) {
                statement = connection.prepareStatement("delete from " + tableName);

                statement.executeUpdate();

                statement.close();
            }

            connection.commit();

        } catch (Exception e) {
            throw new IllegalStateException("Cannot delete tbl_dispatcher_last_event because: " + e.getMessage(), e);
        } finally {
            ConnectionProvider.closeConnection();
        }
    }
}
