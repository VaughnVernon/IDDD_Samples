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

import org.iq80.leveldb.DB;


public abstract class AbstractLevelDBRepository {

    private DB database;
    private String databasePath;

    protected AbstractLevelDBRepository(String aDirectoryPath) {
        super();

        this.openDatabase(aDirectoryPath);
    }

    protected DB database() {
        return this.database;
    }

    protected String databasePath() {
        return this.databasePath;
    }

    private void setDatabase(DB aDatabase) {
        this.database = aDatabase;
    }

    private void setDatabasePath(String aDatabasePath) {
        this.databasePath = aDatabasePath;
    }

    private void openDatabase(String aDirectoryPath) {
        LevelDBProvider levelDBProvider = LevelDBProvider.instance();

        DB db = levelDBProvider.databaseFrom(aDirectoryPath);

        this.setDatabase(db);
        this.setDatabasePath(aDirectoryPath);
    }
}
