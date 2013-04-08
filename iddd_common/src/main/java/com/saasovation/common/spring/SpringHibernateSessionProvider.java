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

package com.saasovation.common.spring;

import java.sql.Connection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.engine.SessionFactoryImplementor;

public class SpringHibernateSessionProvider {

    private static final ThreadLocal<Session> sessionHolder = new ThreadLocal<Session>();

    private SessionFactory sessionFactory;

    public SpringHibernateSessionProvider() {
        super();
    }

    public Connection connection() {
        Connection connection = null;

        try {
            SessionFactoryImplementor sfi =
                   (SessionFactoryImplementor) this.sessionFactory;

            ConnectionProvider connectionProvider = sfi.getConnectionProvider();

            connection = connectionProvider.getConnection();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot get connection from session factory because: "
                    + e.getMessage(),
                    e);
        }

        return connection;
    }

    public Session session() {
        Session threadBoundSession = sessionHolder.get();

        if (threadBoundSession == null) {
            threadBoundSession = this.sessionFactory.openSession();
            sessionHolder.set(threadBoundSession);
        }

        return threadBoundSession;
    }

    public void setSessionFactory(SessionFactory aSessionFactory) {
        this.sessionFactory = aSessionFactory;
    }
}
