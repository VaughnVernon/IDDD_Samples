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

import java.util.Collection;
import java.util.Date;

import org.hibernate.Query;
import org.hibernate.exception.ConstraintViolationException;

import com.saasovation.common.domain.model.process.ProcessId;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTracker;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTrackerRepository;
import com.saasovation.common.persistence.PersistenceManagerProvider;

public class HibernateTimeConstrainedProcessTrackerRepository
    extends AbstractHibernateSession
    implements TimeConstrainedProcessTrackerRepository {

    public HibernateTimeConstrainedProcessTrackerRepository() {
        super();
    }

    public HibernateTimeConstrainedProcessTrackerRepository(
            PersistenceManagerProvider aPersistenceManagerProvider) {
        this();

        if (!aPersistenceManagerProvider.hasHibernateSession()) {
            throw new IllegalArgumentException("The PersistenceManagerProvider must have a Hibernate Session.");
        }

        this.setSession(aPersistenceManagerProvider.hibernateSession());
    }

    @Override
    public void add(TimeConstrainedProcessTracker aTimeConstrainedProcessTracker) {
        this.save(aTimeConstrainedProcessTracker);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<TimeConstrainedProcessTracker> allTimedOut() {
        Query query =
                this.session().createQuery(
                    "from TimeConstrainedProcessTracker as tcpt "
                        + "where tcpt.completed = false and"
                        + " tcpt.processInformedOfTimeout = false and"
                        + " tcpt.timeoutOccursOn <= ?");

        query.setParameter(0, (new Date()).getTime());

        return (Collection<TimeConstrainedProcessTracker>) query.uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<TimeConstrainedProcessTracker> allTimedOutOf(String aTenantId) {
        Query query =
                this.session().createQuery(
                    "from TimeConstrainedProcessTracker as tcpt "
                        + "where tcpt.tenantId = ?"
                        + " tcpt.completed = false and"
                        + " tcpt.processInformedOfTimeout = false and"
                        + " tcpt.timeoutOccursOn <= ?");

        query.setParameter(0, aTenantId);
        query.setParameter(1, (new Date()).getTime());

        return (Collection<TimeConstrainedProcessTracker>) query.uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<TimeConstrainedProcessTracker> allTrackers(String aTenantId) {
        Query query =
                this.session().createQuery(
                    "from TimeConstrainedProcessTracker as tcpt where tcpt.tenantId = ?");

        query.setParameter(0, aTenantId);

        return (Collection<TimeConstrainedProcessTracker>) query.uniqueResult();
    }

    @Override
    public void save(TimeConstrainedProcessTracker aTimeConstrainedProcessTracker) {
        try {
            this.session().saveOrUpdate(aTimeConstrainedProcessTracker);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("Either TimeConstrainedProcessTracker is not unique or another constraint has been violated.", e);
        }
    }

    @Override
    public TimeConstrainedProcessTracker trackerOfProcessId(String aTenantId, ProcessId aProcessId) {
        Query query =
                this.session().createQuery(
                    "from TimeConstrainedProcessTracker as tcpt "
                    + "where tcpt.tenantId = ? and tcpt.processId = ?");

        query.setParameter(0, aTenantId);
        query.setParameter(1, aProcessId);

        return (TimeConstrainedProcessTracker) query.uniqueResult();
    }
}
