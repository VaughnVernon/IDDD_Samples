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

package com.saasovation.agilepm.port.adapter.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.saasovation.agilepm.domain.model.team.ProductOwner;
import com.saasovation.agilepm.domain.model.team.ProductOwnerRepository;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.port.adapter.persistence.leveldb.AbstractLevelDBRepository;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBKey;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public class LevelDBProductOwnerRepository
        extends AbstractLevelDBRepository
        implements ProductOwnerRepository {

    private static final String PRIMARY = "PRODUCTOWNER#PK";
    private static final String PRODUCT_OWNER_OF_TENANT = "PRODUCTOWNER#T";

    public LevelDBProductOwnerRepository() {
        super(LevelDBDatabasePath.agilePMPath());
    }

    @Override
    public Collection<ProductOwner> allProductOwnersOfTenant(TenantId aTenantId) {
        List<ProductOwner> productOwners = new ArrayList<ProductOwner>();

        LevelDBKey productOwnersOfTenant = new LevelDBKey(PRODUCT_OWNER_OF_TENANT, aTenantId.id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        List<Object> keys = uow.readKeys(productOwnersOfTenant);

        for (Object productOwnerId : keys) {
            ProductOwner productOwner = uow.readObject(productOwnerId.toString().getBytes(), ProductOwner.class);

            if (productOwner != null) {
                productOwners.add(productOwner);
            }
        }

        return productOwners;
    }

    @Override
    public ProductOwner productOwnerOfIdentity(TenantId aTenantId, String aUsername) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aTenantId.id(), aUsername);

        ProductOwner productOwner =
                LevelDBUnitOfWork.readOnly(this.database())
                    .readObject(primaryKey.key().getBytes(), ProductOwner.class);

        return productOwner;
    }

    @Override
    public void remove(ProductOwner aProductOwner) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY, aProductOwner.tenantId().id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        uow.lock(lockKey.key());

        this.remove(aProductOwner, uow);
    }

    @Override
    public void removeAll(Collection<ProductOwner> aProductOwnerCollection) {
        boolean locked = false;

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        for (ProductOwner productOwner : aProductOwnerCollection) {
            if (!locked) {
                LevelDBKey lockKey = new LevelDBKey(PRIMARY, productOwner.tenantId().id());

                uow.lock(lockKey.key());

                locked = true;
            }

            this.remove(productOwner, uow);
        }
    }

    @Override
    public void save(ProductOwner aProductOwner) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY, aProductOwner.tenantId().id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        uow.lock(lockKey.key());

        this.save(aProductOwner, uow);
    }

    @Override
    public void saveAll(Collection<ProductOwner> aProductOwnerCollection) {
        boolean locked = false;

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        for (ProductOwner productOwner : aProductOwnerCollection) {
            if (!locked) {
                LevelDBKey lockKey = new LevelDBKey(PRIMARY, productOwner.tenantId().id());

                uow.lock(lockKey.key());

                locked = true;
            }

            this.save(productOwner, uow);
        }
    }

    private void remove(ProductOwner aProductOwner, LevelDBUnitOfWork aUoW) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aProductOwner.tenantId().id(), aProductOwner.username());
        aUoW.remove(primaryKey);

        LevelDBKey teamMemberOfTenant = new LevelDBKey(primaryKey, PRODUCT_OWNER_OF_TENANT, aProductOwner.tenantId().id());
        aUoW.removeKeyReference(teamMemberOfTenant);
    }

    private void save(ProductOwner aProductOwner, LevelDBUnitOfWork aUoW) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aProductOwner.tenantId().id(), aProductOwner.username());
        aUoW.write(primaryKey, aProductOwner);

        LevelDBKey productOwnersOfTenant = new LevelDBKey(primaryKey, PRODUCT_OWNER_OF_TENANT, aProductOwner.tenantId().id());
        aUoW.updateKeyReference(productOwnersOfTenant);
    }
}
