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
import com.saasovation.identityaccess.domain.model.identity.TenantId;
import com.saasovation.identityaccess.domain.model.identity.User;
import com.saasovation.identityaccess.domain.model.identity.UserRepository;

public class HibernateUserRepository
        extends AbstractHibernateSession
        implements UserRepository {

    public HibernateUserRepository() {
        super();
    }

    @Override
    public void add(User aUser) {
        try {
            this.session().saveOrUpdate(aUser);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("User is not unique.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<User> allSimilarlyNamedUsers(
            TenantId aTenantId,
            String aFirstNamePrefix,
            String aLastNamePrefix) {

        if (aFirstNamePrefix.endsWith("%") || aLastNamePrefix.endsWith("%")) {
            throw new IllegalArgumentException("Name prefixes must not include %.");
        }

        Query query = this.session().createQuery(
                "from com.saasovation.identityaccess.domain.model.identity.User as _obj_ "
                + "where _obj_.tenantId = ? "
                +   "and _obj_.person.name.firstName like ? "
                +   "and _obj_.person.name.lastName like ?");

        query.setParameter(0, aTenantId);
        query.setParameter(1, aFirstNamePrefix + "%", Hibernate.STRING);
        query.setParameter(2, aLastNamePrefix + "%", Hibernate.STRING);

        return query.list();
    }

    @Override
    public void remove(User aUser) {
        this.session().delete(aUser);
    }

    @Override
    public User userFromAuthenticCredentials(
            TenantId aTenantId,
            String aUsername,
            String anEncryptedPassword) {

        Query query = this.session().createQuery(
                "from com.saasovation.identityaccess.domain.model.identity.User as _obj_ "
                + "where _obj_.tenantId = ? "
                  + "and _obj_.username = ? "
                  + "and _obj_.password = ?");

        query.setParameter(0, aTenantId);
        query.setParameter(1, aUsername, Hibernate.STRING);
        query.setParameter(2, anEncryptedPassword, Hibernate.STRING);

        return (User) query.uniqueResult();
    }

    @Override
    public User userWithUsername(
            TenantId aTenantId,
            String aUsername) {

        Query query = this.session().createQuery(
                "from com.saasovation.identityaccess.domain.model.identity.User as _obj_ "
                + "where _obj_.tenantId = ? "
                  + "and _obj_.username = ?");

        query.setParameter(0, aTenantId);
        query.setParameter(1, aUsername, Hibernate.STRING);

        return (User) query.uniqueResult();
    }
}
