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

package com.saasovation.common.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.saasovation.common.spring.ApplicationContextProvider;

public class PersistenceManagerProvider {

    private Session hibernateSession;

    public static PersistenceManagerProvider forHibernateSessionNamed(
            String aHibernateSessionFactoryName) {

        SessionFactory sessionFactory = (SessionFactory)
                ApplicationContextProvider
                    .instance()
                    .applicationContext()
                    .getBean(aHibernateSessionFactoryName);

        return new PersistenceManagerProvider(sessionFactory.openSession());
    }

    public PersistenceManagerProvider(Session aHibernateSession) {
        super();

        this.setHibernateSession(aHibernateSession);
    }

    public Session hibernateSession() {
        return this.hibernateSession;
    }

    public boolean hasHibernateSession() {
        return this.hibernateSession() != null;
    }

    protected PersistenceManagerProvider() {
        super();
    }

    private void setHibernateSession(Session aHibernateSession) {
        this.hibernateSession = aHibernateSession;
    }
}
