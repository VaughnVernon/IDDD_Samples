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

package com.saasovation.common.port.adapter.persistence.hibernate;

import org.hibernate.Session;

import com.saasovation.common.spring.SpringHibernateSessionProvider;

public abstract class AbstractHibernateSession {

    private Session session;
    private SpringHibernateSessionProvider sessionProvider;

    protected AbstractHibernateSession() {
        super();
    }

    protected AbstractHibernateSession(Session aSession) {
        this();

        this.setSession(aSession);
    }

    protected Session session() {
        Session actualSession = this.session;

        if (actualSession == null) {
            if (this.sessionProvider == null) {
                throw new IllegalStateException("Requires either a Session or SpringHibernateSessionProvider.");
            }

            actualSession = this.sessionProvider.session();

            // This is not a lazy creation and should not be set on
            // this.session. Setting the session instance assumes that
            // you have used the single argument constructor for a single
            // use. If actualSession is set by the sessionProvider then
            // this instance is for use only by the current thread and
            // must not be retained for subsequent requests.
        }

        return actualSession;
    }

    protected void setSession(Session aSession) {
        this.session = aSession;
    }

    public void setSessionProvider(SpringHibernateSessionProvider aSessionProvider) {
        this.sessionProvider = aSessionProvider;
    }
}
