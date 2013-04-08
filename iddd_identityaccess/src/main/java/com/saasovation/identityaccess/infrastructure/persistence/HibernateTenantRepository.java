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

import java.util.UUID;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.exception.ConstraintViolationException;

import com.saasovation.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import com.saasovation.identityaccess.domain.model.identity.Tenant;
import com.saasovation.identityaccess.domain.model.identity.TenantId;
import com.saasovation.identityaccess.domain.model.identity.TenantRepository;

public class HibernateTenantRepository
        extends AbstractHibernateSession
        implements TenantRepository {

    public HibernateTenantRepository() {
        super();
    }

    @Override
    public void add(Tenant aTenant) {
        try {
            this.session().saveOrUpdate(aTenant);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("Tenant is not unique.", e);
        }
    }

    @Override
    public TenantId nextIdentity() {
        return new TenantId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public void remove(Tenant aTenant) {
        this.session().delete(aTenant);
    }

    @Override
    public Tenant tenantNamed(String aName) {
        Query query = this.session().createQuery(
                "from com.saasovation.identityaccess.domain.model.identity.Tenant as _obj_ "
                + "where _obj_.name = ?");

        query.setParameter(0, aName, Hibernate.STRING);

        return (Tenant) query.uniqueResult();
    }

    @Override
    public Tenant tenantOfId(TenantId aTenantId) {
        Query query = this.session().createQuery(
                "from com.saasovation.identityaccess.domain.model.identity.Tenant as _obj_ "
                + "where _obj_.tenantId = ?");

        query.setParameter(0, aTenantId);

        return (Tenant) query.uniqueResult();
    }
}
