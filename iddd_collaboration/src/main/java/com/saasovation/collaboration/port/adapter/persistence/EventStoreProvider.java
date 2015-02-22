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

package com.saasovation.collaboration.port.adapter.persistence;

import com.saasovation.common.event.sourcing.EventStore;
import com.saasovation.common.port.adapter.persistence.eventsourcing.leveldb.LevelDBEventStore;
import com.saasovation.common.port.adapter.persistence.eventsourcing.mysql.MySQLJDBCEventStore;

public class EventStoreProvider {

    private static final boolean FOR_LEVELDB = true;
    private static final boolean FOR_MYSQL = false;

    private EventStore eventStore;

    public static EventStoreProvider instance() {
        return new EventStoreProvider();
    }

    public EventStore eventStore() {
        return this.eventStore;
    }

    protected EventStoreProvider() {
        super();

        this.initializeLevelDB();

        this.initializeMySQL();
    }

    private void initializeLevelDB() {
        if (FOR_LEVELDB) {
            this.eventStore =
                    LevelDBEventStore
                            .instance(this.getClass().getResource("/").getPath() + "/data/leveldb/iddd_collaboration_es");
        }
    }

    private void initializeMySQL() {
        if (FOR_MYSQL) {
            this.eventStore = MySQLJDBCEventStore.instance();
        }
    }
}
