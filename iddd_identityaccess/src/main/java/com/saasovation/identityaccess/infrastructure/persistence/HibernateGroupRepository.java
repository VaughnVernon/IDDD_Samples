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

package com.saasovation.identityaccess.infrastructure.persistence;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.exception.ConstraintViolationException;

import com.saasovation.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import com.saasovation.identityaccess.domain.model.identity.Group;
import com.saasovation.identityaccess.domain.model.identity.GroupRepository;
import com.saasovation.identityaccess.domain.model.identity.TenantId;

public class HibernateGroupRepository
        extends AbstractHibernateSession
        implements GroupRepository {

    public HibernateGroupRepository() {
        super();
    }

    @Override
    public void add(Group aGroup) {
        try {
            this.session().saveOrUpdate(aGroup);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("Group is not unique.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Group> allGroups(TenantId aTenantId) {
        Query query = this.session().createQuery(
                "from com.saasovation.identityaccess.domain.model.identity.Group as _obj_ "
                + "where _obj_.tenantId = ? "
                  + "and _obj_.name not like '" + Group.ROLE_GROUP_PREFIX + "%'");

        query.setParameter(0, aTenantId);

        return (Collection<Group>) query.list();
    }

    @Override
    public Group groupNamed(TenantId aTenantId, String aName) {
        if (aName.startsWith(Group.ROLE_GROUP_PREFIX)) {
            throw new IllegalArgumentException("May not find internal groups.");
        }

        Query query = this.session().createQuery(
                "from com.saasovation.identityaccess.domain.model.identity.Group as _obj_ "
                + "where _obj_.tenantId = ? "
                  + "and _obj_.name = ?");

        query.setParameter(0, aTenantId);
        query.setParameter(1, aName, org.hibernate.Hibernate.STRING);

        return (Group) query.uniqueResult();
    }

    @Override
    public void remove(Group aGroup) {
        this.session().delete(aGroup);
    }
}
