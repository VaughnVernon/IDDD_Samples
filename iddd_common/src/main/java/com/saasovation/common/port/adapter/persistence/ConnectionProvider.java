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

package com.saasovation.common.port.adapter.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class ConnectionProvider {

    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();

    public static void closeConnection() {

        try {
            Connection connection = connection();

            if (connection != null) {
                connection.close();

                // System.out.println("---CONNECTION CLOSED");
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Cannot close connection because: "
                            + e.getMessage(),
                    e);
        } finally {
            connectionHolder.set(null);
        }
    }

    public static Connection connection() {
        Connection connection = connectionHolder.get();

        return connection;
    }

    public static Connection connection(DataSource aDataSource) {

        Connection connection = connection();

        try {
            if (connection == null) {
                connection = aDataSource.getConnection();

                connectionHolder.set(connection);

                // System.out.println("CONNECTION OPENED");
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Connection not provided because: "
                            + e.getMessage(),
                    e);
        }

        return connection;
    }
}
