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

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.exception.ConstraintViolationException;

import com.saasovation.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import com.saasovation.identityaccess.domain.model.access.Role;
import com.saasovation.identityaccess.domain.model.access.RoleRepository;
import com.saasovation.identityaccess.domain.model.identity.TenantId;

public class HibernateRoleRepository
        extends AbstractHibernateSession
        implements RoleRepository {

    public HibernateRoleRepository() {
        super();
    }

    @Override
    public void add(Role aRole) {
        try {
            this.session().saveOrUpdate(aRole);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("Role is not unique.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Role> allRoles(TenantId aTenantId) {
        Query query = this.session().createQuery(
                "from com.saasovation.identityaccess.domain.model.access.Role as _obj_ "
                + "where _obj_.tenantId = ?");

        query.setParameter(0, aTenantId);

        return (Collection<Role>) query.list();
    }

    @Override
    public void remove(Role aRole) {
        this.session().delete(aRole);
    }

    @Override
    public Role roleNamed(TenantId aTenantId, String aRoleName) {
        Query query = this.session().createQuery(
                "from com.saasovation.identityaccess.domain.model.access.Role as _obj_ "
                + "where _obj_.tenantId = ? "
                  + "and _obj_.name = ?");

        query.setParameter(0, aTenantId);
        query.setParameter(1, aRoleName, Hibernate.STRING);

        return (Role) query.uniqueResult();
    }
}
