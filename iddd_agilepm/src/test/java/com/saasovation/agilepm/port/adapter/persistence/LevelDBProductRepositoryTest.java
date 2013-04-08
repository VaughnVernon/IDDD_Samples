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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.iq80.leveldb.DB;

import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability;
import com.saasovation.agilepm.domain.model.product.Product;
import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.product.ProductRepository;
import com.saasovation.agilepm.domain.model.team.ProductOwnerId;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBProvider;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public class LevelDBProductRepositoryTest extends TestCase {

    private DB database;
    private ProductRepository productRepository = new LevelDBProductRepository();

    public LevelDBProductRepositoryTest() {
        super();
    }

    public void testSave() throws Exception {
        TenantId tenantId = new TenantId("T12345");

        Product product =
                new Product(
                        tenantId,
                        new ProductId("679890"),
                        new ProductOwnerId(tenantId, "thepm"),
                        "My Product",
                        "My product, which is my product.",
                        DiscussionAvailability.NOT_REQUESTED);

        LevelDBUnitOfWork.start(this.database);
        productRepository.save(product);
        LevelDBUnitOfWork.current().commit();

        Product savedProduct =
                productRepository
                    .productOfId(
                            product.tenantId(),
                            product.productId());

        assertNotNull(savedProduct);
        assertEquals(product.tenantId(), savedProduct.tenantId());
        assertEquals(product.productId(), savedProduct.productId());
        assertEquals(product.productOwnerId(), savedProduct.productOwnerId());
        assertEquals("My Product", savedProduct.name());
        assertEquals("My product, which is my product.", savedProduct.description());
        assertEquals(DiscussionAvailability.NOT_REQUESTED, savedProduct.discussion().availability());

        Collection<Product> savedProducts =
                productRepository
                    .allProductsOfTenant(product.tenantId());

        assertFalse(savedProducts.isEmpty());
        assertEquals(1, savedProducts.size());
    }

    public void testStartDiscussionInitiationSave() throws Exception {
        TenantId tenantId = new TenantId("T12345");

        Product product =
                new Product(
                        tenantId,
                        new ProductId("679890"),
                        new ProductOwnerId(tenantId, "thepm"),
                        "My Product",
                        "My product, which is my product.",
                        DiscussionAvailability.NOT_REQUESTED);

        product.startDiscussionInitiation("ABCDEFGHIJ");

        LevelDBUnitOfWork.start(this.database);

        productRepository.save(product);

        LevelDBUnitOfWork.current().commit();

        Product savedProduct =
                productRepository
                    .productOfDiscussionInitiationId(
                            product.tenantId(),
                            "ABCDEFGHIJ");

        assertNotNull(savedProduct);
        assertEquals(product.tenantId(), savedProduct.tenantId());
        assertEquals(product.productId(), savedProduct.productId());
        assertEquals(product.productOwnerId(), savedProduct.productOwnerId());
        assertEquals("My Product", savedProduct.name());
        assertEquals("My product, which is my product.", savedProduct.description());
        assertEquals(DiscussionAvailability.NOT_REQUESTED, savedProduct.discussion().availability());
    }

    public void testRemove() throws Exception {
        TenantId tenantId = new TenantId("T12345");

        Product product1 =
                new Product(
                        tenantId,
                        new ProductId("679890"),
                        new ProductOwnerId(tenantId, "thepm"),
                        "My Product 1",
                        "My product 1, which is my product.",
                        DiscussionAvailability.NOT_REQUESTED);

        Product product2 =
                new Product(
                        tenantId,
                        new ProductId("09876"),
                        new ProductOwnerId(tenantId, "thepm"),
                        "My Product 2",
                        "My product 2, which is my product.",
                        DiscussionAvailability.NOT_REQUESTED);

        LevelDBUnitOfWork.start(this.database);
        productRepository.save(product1);
        productRepository.save(product2);
        LevelDBUnitOfWork.current().commit();

        LevelDBUnitOfWork.start(this.database);
        productRepository.remove(product1);
        LevelDBUnitOfWork.current().commit();

        Collection<Product> savedProducts = productRepository.allProductsOfTenant(tenantId);
        assertFalse(savedProducts.isEmpty());
        assertEquals(1, savedProducts.size());
        assertEquals(product2.productId(), savedProducts.iterator().next().productId());

        LevelDBUnitOfWork.start(this.database);
        productRepository.remove(product2);
        LevelDBUnitOfWork.current().commit();

        savedProducts = productRepository.allProductsOfTenant(tenantId);
        assertTrue(savedProducts.isEmpty());
    }

    public void testSaveAllRemoveAll() throws Exception {
        TenantId tenantId = new TenantId("T12345");

        Product product1 =
                new Product(
                        tenantId,
                        new ProductId("679890"),
                        new ProductOwnerId(tenantId, "thepm"),
                        "My Product 1",
                        "My product 1, which is my product.",
                        DiscussionAvailability.NOT_REQUESTED);

        Product product2 =
                new Product(
                        tenantId,
                        new ProductId("09876"),
                        new ProductOwnerId(tenantId, "thepm"),
                        "My Product 2",
                        "My product 2, which is my product.",
                        DiscussionAvailability.NOT_REQUESTED);

        Product product3 =
                new Product(
                        tenantId,
                        new ProductId("100200300"),
                        new ProductOwnerId(tenantId, "thepm"),
                        "My Product 3",
                        "My product 3, which is my product.",
                        DiscussionAvailability.NOT_REQUESTED);

        LevelDBUnitOfWork.start(this.database);
        productRepository.saveAll(Arrays.asList(new Product[] { product1, product2, product3 }));
        LevelDBUnitOfWork.current().commit();

        Collection<Product> savedProducts = productRepository.allProductsOfTenant(tenantId);
        assertFalse(savedProducts.isEmpty());
        assertEquals(3, savedProducts.size());

        LevelDBUnitOfWork.start(this.database);
        productRepository.removeAll(Arrays.asList(new Product[] { product1, product3 }));
        LevelDBUnitOfWork.current().commit();

        savedProducts = productRepository.allProductsOfTenant(tenantId);
        assertFalse(savedProducts.isEmpty());
        assertEquals(1, savedProducts.size());
        assertEquals(product2.productId(), savedProducts.iterator().next().productId());

        LevelDBUnitOfWork.start(this.database);
        productRepository.removeAll(Arrays.asList(new Product[] { product2 }));
        LevelDBUnitOfWork.current().commit();

        savedProducts = productRepository.allProductsOfTenant(tenantId);
        assertTrue(savedProducts.isEmpty());
    }

    public void testConcurrentTransactions() throws Exception {
        final List<Integer> orderOfCommits = new ArrayList<Integer>();

        final TenantId tenantId = new TenantId("T12345");

        Product product1 =
                new Product(
                        tenantId,
                        new ProductId("679890"),
                        new ProductOwnerId(tenantId, "thepm"),
                        "My Product 1",
                        "My product 1, which is my product.",
                        DiscussionAvailability.NOT_REQUESTED);

        LevelDBUnitOfWork.start(database);
        productRepository.save(product1);

        new Thread() {
           @Override
           public void run() {
               Product product2 =
                       new Product(
                               tenantId,
                               new ProductId("09876"),
                               new ProductOwnerId(tenantId, "thepm"),
                               "My Product 2",
                               "My product 2, which is my product.",
                               DiscussionAvailability.NOT_REQUESTED);

               LevelDBUnitOfWork.start(database);
               productRepository.save(product2);
               LevelDBUnitOfWork.current().commit();
               orderOfCommits.add(2);
           }
        }.start();

        Thread.sleep(250L);

        LevelDBUnitOfWork.current().commit();
        orderOfCommits.add(1);

        for (int idx = 0; idx < orderOfCommits.size(); ++idx) {
            assertEquals(idx + 1, orderOfCommits.get(idx).intValue());
        }

        Thread.sleep(250L);

        Collection<Product> savedProducts =
                productRepository.allProductsOfTenant(product1.tenantId());

        assertFalse(savedProducts.isEmpty());
        assertEquals(2, savedProducts.size());
    }

    @Override
    protected void setUp() throws Exception {
        DomainEventPublisher.instance().reset();

        this.database = LevelDBProvider.instance().databaseFrom(LevelDBDatabasePath.agilePMPath());

        LevelDBProvider.instance().purge(this.database);

        super.setUp();
    }
}
