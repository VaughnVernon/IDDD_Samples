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

package com.saasovation.common.domain.model.process;

import javax.persistence.PersistenceException;

import org.hibernate.Query;
import org.hibernate.Session;

import com.saasovation.common.port.adapter.persistence.hibernate.AbstractHibernateSession;

public class TestableTimeConstrainedProcessRepository
    extends AbstractHibernateSession {

    public TestableTimeConstrainedProcessRepository() {
        super();
    }

    public void add(TestableTimeConstrainedProcess aTestableTimeConstrainedProcess) {
        try {
            this.session().persist(aTestableTimeConstrainedProcess);
        } catch (PersistenceException e) {
            throw new IllegalStateException("Either TestableTimeConstrainedProcess is not unique or another constraint has been violated.", e);
        }
    }

    public TestableTimeConstrainedProcess processOfId(ProcessId aProcessId) {
        Query query =
                this.session().createQuery(
                    "from TestableTimeConstrainedProcess as ttcp where ttcp.processId = ?");

        query.setParameter(0, aProcessId);

        return (TestableTimeConstrainedProcess) query.uniqueResult();
    }

    public Session getSession() {
        return this.session();
    }
}
