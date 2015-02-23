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

package com.saasovation.common.port.adapter.persistence.leveldb;

import junit.framework.TestCase;

import org.iq80.leveldb.DB;

import com.saasovation.common.domain.model.DomainEventPublisher;

public abstract class LevelDBTest extends TestCase {

    protected static final String TEST_DATABASE = LevelDBTest.class.getResource("/").getPath() + "/data/leveldb/iddd_common_test";

    private DB database;

    public LevelDBTest() {
        super();
    }

    protected DB database() {
        return this.database;
    }

    @Override
    protected void setUp() throws Exception {
        this.database = LevelDBProvider.instance().databaseFrom(TEST_DATABASE);

        DomainEventPublisher.instance().reset();

        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        LevelDBProvider.instance().purge(this.database);

        super.tearDown();
    }

}
