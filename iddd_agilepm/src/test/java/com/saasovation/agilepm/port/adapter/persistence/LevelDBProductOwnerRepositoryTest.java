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
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.iq80.leveldb.DB;

import com.saasovation.agilepm.domain.model.team.ProductOwner;
import com.saasovation.agilepm.domain.model.team.ProductOwnerRepository;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBProvider;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public class LevelDBProductOwnerRepositoryTest extends TestCase {

    private DB database;
    private ProductOwnerRepository productOwnerRepository = new LevelDBProductOwnerRepository();

    public LevelDBProductOwnerRepositoryTest() {
        super();
    }

    public void testSave() throws Exception {
        ProductOwner productOwner =
                new ProductOwner(
                        new TenantId("12345"),
                        "jdoe",
                        "John",
                        "Doe",
                        "jdoe@saasovation.com",
                        new Date());

        LevelDBUnitOfWork.start(this.database);
        productOwnerRepository.save(productOwner);
        LevelDBUnitOfWork.current().commit();

        ProductOwner savedProductOwner =
                productOwnerRepository.productOwnerOfIdentity(
                        productOwner.tenantId(),
                        productOwner.username());

        assertNotNull(savedProductOwner);
        assertEquals(productOwner.tenantId(), savedProductOwner.tenantId());
        assertEquals(productOwner.username(), savedProductOwner.username());
        assertEquals(productOwner.firstName(), savedProductOwner.firstName());
        assertEquals(productOwner.lastName(), savedProductOwner.lastName());
        assertEquals(productOwner.emailAddress(), savedProductOwner.emailAddress());

        Collection<ProductOwner> savedProductOwners =
                this.productOwnerRepository.allProductOwnersOfTenant(productOwner.tenantId());

        assertFalse(savedProductOwners.isEmpty());
        assertEquals(1, savedProductOwners.size());
    }

    public void testRemove() {
        ProductOwner productOwner1 =
                new ProductOwner(
                        new TenantId("12345"),
                        "jdoe",
                        "John",
                        "Doe",
                        "jdoe@saasovation.com",
                        new Date());

        ProductOwner productOwner2 =
                new ProductOwner(
                        new TenantId("12345"),
                        "zdoe",
                        "Zoe",
                        "Doe",
                        "zoe@saasovation.com",
                        new Date());

        LevelDBUnitOfWork.start(this.database);
        productOwnerRepository.save(productOwner1);
        productOwnerRepository.save(productOwner2);
        LevelDBUnitOfWork.current().commit();

        LevelDBUnitOfWork.start(this.database);
        productOwnerRepository.remove(productOwner1);
        LevelDBUnitOfWork.current().commit();

        TenantId tenantId = productOwner2.tenantId();

        Collection<ProductOwner> savedProductOwners = productOwnerRepository.allProductOwnersOfTenant(tenantId);
        assertFalse(savedProductOwners.isEmpty());
        assertEquals(1, savedProductOwners.size());
        assertEquals(productOwner2.username(), savedProductOwners.iterator().next().username());

        LevelDBUnitOfWork.start(this.database);
        productOwnerRepository.remove(productOwner2);
        LevelDBUnitOfWork.current().commit();

        savedProductOwners = productOwnerRepository.allProductOwnersOfTenant(tenantId);
        assertTrue(savedProductOwners.isEmpty());
    }

    public void testSaveAllRemoveAll() throws Exception {
        ProductOwner productOwner1 =
                new ProductOwner(
                        new TenantId("12345"),
                        "jdoe",
                        "John",
                        "Doe",
                        "jdoe@saasovation.com",
                        new Date());

        ProductOwner productOwner2 =
                new ProductOwner(
                        new TenantId("12345"),
                        "zdoe",
                        "Zoe",
                        "Doe",
                        "zoe@saasovation.com",
                        new Date());

        ProductOwner productOwner3 =
                new ProductOwner(
                        new TenantId("12345"),
                        "jsmith",
                        "John",
                        "Smith",
                        "jsmith@saasovation.com",
                        new Date());

        LevelDBUnitOfWork.start(this.database);
        productOwnerRepository.saveAll(Arrays.asList(new ProductOwner[] { productOwner1, productOwner2, productOwner3 }));
        LevelDBUnitOfWork.current().commit();

        TenantId tenantId = productOwner1.tenantId();

        Collection<ProductOwner> savedProductOwners = productOwnerRepository.allProductOwnersOfTenant(tenantId);
        assertFalse(savedProductOwners.isEmpty());
        assertEquals(3, savedProductOwners.size());

        LevelDBUnitOfWork.start(this.database);
        productOwnerRepository.removeAll(Arrays.asList(new ProductOwner[] { productOwner1, productOwner3 }));
        LevelDBUnitOfWork.current().commit();

        savedProductOwners = productOwnerRepository.allProductOwnersOfTenant(tenantId);
        assertFalse(savedProductOwners.isEmpty());
        assertEquals(1, savedProductOwners.size());
        assertEquals(productOwner2.username(), savedProductOwners.iterator().next().username());

        LevelDBUnitOfWork.start(this.database);
        productOwnerRepository.removeAll(Arrays.asList(new ProductOwner[] { productOwner2 }));
        LevelDBUnitOfWork.current().commit();

        savedProductOwners = productOwnerRepository.allProductOwnersOfTenant(tenantId);
        assertTrue(savedProductOwners.isEmpty());
    }

    public void testConcurrentTransactions() throws Exception {
        final List<Integer> orderOfCommits = new ArrayList<Integer>();

        ProductOwner productOwner1 =
                new ProductOwner(
                        new TenantId("12345"),
                        "jdoe",
                        "John",
                        "Doe",
                        "jdoe@saasovation.com",
                        new Date());

        LevelDBUnitOfWork.start(database);
        productOwnerRepository.save(productOwner1);

        new Thread() {
           @Override
           public void run() {
               ProductOwner productOwner2 =
                       new ProductOwner(
                               new TenantId("12345"),
                               "zdoe",
                               "Zoe",
                               "Doe",
                               "zoe@saasovation.com",
                               new Date());

               LevelDBUnitOfWork.start(database);
               productOwnerRepository.save(productOwner2);
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

        Collection<ProductOwner> savedProductOwners =
                productOwnerRepository.allProductOwnersOfTenant(productOwner1.tenantId());

        assertFalse(savedProductOwners.isEmpty());
        assertEquals(2, savedProductOwners.size());
    }

    @Override
    protected void setUp() throws Exception {
        DomainEventPublisher.instance().reset();

        this.database = LevelDBProvider.instance().databaseFrom(LevelDBDatabasePath.agilePMPath());

        LevelDBProvider.instance().purge(this.database);

        super.setUp();
    }
}
