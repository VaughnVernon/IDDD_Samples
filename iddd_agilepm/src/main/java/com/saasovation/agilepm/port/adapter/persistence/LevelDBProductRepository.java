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
import java.util.UUID;

import com.saasovation.agilepm.domain.model.product.Product;
import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.product.ProductRepository;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.port.adapter.persistence.leveldb.AbstractLevelDBRepository;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBKey;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public class LevelDBProductRepository
        extends AbstractLevelDBRepository
        implements ProductRepository {

    private static final String PRIMARY = "PRODUCT#PK";
    private static final String PRODUCTS_OF_TENANT = "PRODUCT#T";
    private static final String PRODUCT_OF_DISCUSSION = "PRODUCT#D";

    public LevelDBProductRepository() {
        super(LevelDBDatabasePath.agilePMPath());
    }

    @Override
    public Collection<Product> allProductsOfTenant(TenantId aTenantId) {
        List<Product> products = new ArrayList<Product>();

        LevelDBKey productsOfTenant = new LevelDBKey(PRODUCTS_OF_TENANT, aTenantId.id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        List<Object> keys = uow.readKeys(productsOfTenant);

        for (Object productId : keys) {
            Product product = uow.readObject(productId.toString().getBytes(), Product.class);

            if (product != null) {
                products.add(product);
            }
        }

        return products;
    }

    @Override
    public ProductId nextIdentity() {
        return new ProductId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public Product productOfDiscussionInitiationId(
            TenantId aTenantId,
            String aDiscussionInitiationId) {

        Product product = null;

        LevelDBKey productsOfDiscussion =
                new LevelDBKey(
                        PRODUCT_OF_DISCUSSION,
                        aTenantId.id(),
                        aDiscussionInitiationId);

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        Object productId = uow.readKey(productsOfDiscussion);

        if (productId != null) {
            product = uow.readObject(productId.toString().getBytes(), Product.class);
        }

        return product;
    }

    @Override
    public Product productOfId(TenantId aTenantId, ProductId aProductId) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aTenantId.id(), aProductId.id());

        Product product =
                LevelDBUnitOfWork.readOnly(this.database())
                    .readObject(primaryKey.key().getBytes(), Product.class);

        return product;
    }

    @Override
    public void remove(Product aProduct) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY, aProduct.tenantId().id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        uow.lock(lockKey.key());

        this.remove(aProduct, uow);
    }

    @Override
    public void removeAll(Collection<Product> aProductCollection) {
        boolean locked = false;

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        for (Product product : aProductCollection) {
            if (!locked) {
                LevelDBKey lockKey = new LevelDBKey(PRIMARY, product.tenantId().id());

                uow.lock(lockKey.key());

                locked = true;
            }

            this.remove(product, uow);
        }
    }

    @Override
    public void save(Product aProduct) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY, aProduct.tenantId().id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        uow.lock(lockKey.key());

        this.save(aProduct, uow);
    }

    @Override
    public void saveAll(Collection<Product> aProductCollection) {
        boolean locked = false;

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        for (Product product : aProductCollection) {
            if (!locked) {
                LevelDBKey lockKey = new LevelDBKey(PRIMARY, product.tenantId().id());

                uow.lock(lockKey.key());

                locked = true;
            }

            this.save(product, uow);
        }
    }

    private void remove(Product aProduct, LevelDBUnitOfWork aUoW) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aProduct.tenantId().id(), aProduct.productId().id());
        aUoW.remove(primaryKey);

        LevelDBKey productsOfTenant = new LevelDBKey(primaryKey, PRODUCTS_OF_TENANT, aProduct.tenantId().id());
        aUoW.removeKeyReference(productsOfTenant);

        if (aProduct.discussionInitiationId() != null) {
            LevelDBKey productsOfDiscussion = new LevelDBKey(primaryKey, PRODUCT_OF_DISCUSSION, aProduct.tenantId().id(), aProduct.discussionInitiationId());
            aUoW.removeKeyReference(productsOfDiscussion);
        }
    }

    private void save(Product aProduct, LevelDBUnitOfWork aUoW) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aProduct.tenantId().id(), aProduct.productId().id());
        aUoW.write(primaryKey, aProduct);

        LevelDBKey productsOfTenant = new LevelDBKey(primaryKey, PRODUCTS_OF_TENANT, aProduct.tenantId().id());
        aUoW.updateKeyReference(productsOfTenant);

        if (aProduct.discussionInitiationId() != null) {
            LevelDBKey productsOfDiscussion = new LevelDBKey(primaryKey, PRODUCT_OF_DISCUSSION, aProduct.tenantId().id(), aProduct.discussionInitiationId());
            aUoW.updateKeyReference(productsOfDiscussion);
        }
    }
}
