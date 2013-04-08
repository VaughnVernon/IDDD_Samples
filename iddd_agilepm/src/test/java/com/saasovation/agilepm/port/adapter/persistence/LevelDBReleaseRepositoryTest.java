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

import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.product.release.Release;
import com.saasovation.agilepm.domain.model.product.release.ReleaseId;
import com.saasovation.agilepm.domain.model.product.release.ReleaseRepository;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBProvider;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public class LevelDBReleaseRepositoryTest extends TestCase {

    private DB database;
    private ReleaseRepository releaseRepository = new LevelDBReleaseRepository();

    public LevelDBReleaseRepositoryTest() {
        super();
    }

    public void testSave() throws Exception {
        Release release = new Release(
                new TenantId("12345"), new ProductId("p00000"), new ReleaseId("r11111"),
                "release1", "My release 1.", new Date(), new Date());

        LevelDBUnitOfWork.start(this.database);
        releaseRepository.save(release);
        LevelDBUnitOfWork.current().commit();

        Release savedRelease = releaseRepository.releaseOfId(release.tenantId(), release.releaseId());

        assertNotNull(savedRelease);
        assertEquals(release.tenantId(), savedRelease.tenantId());
        assertEquals(release.name(), savedRelease.name());

        Collection<Release> savedReleases =
                this.releaseRepository.allProductReleases(release.tenantId(), release.productId());

        assertFalse(savedReleases.isEmpty());
        assertEquals(1, savedReleases.size());
    }

    public void testRemove() {
        Release release1 = new Release(
                new TenantId("12345"), new ProductId("p00000"), new ReleaseId("r11111"),
                "release1", "My release 1.", new Date(), new Date());

        Release release2 = new Release(
                new TenantId("12345"), new ProductId("p00000"), new ReleaseId("r11112"),
                "release2", "My release 2.", new Date(), new Date());

        LevelDBUnitOfWork.start(this.database);
        releaseRepository.save(release1);
        releaseRepository.save(release2);
        LevelDBUnitOfWork.current().commit();

        LevelDBUnitOfWork.start(this.database);
        releaseRepository.remove(release1);
        LevelDBUnitOfWork.current().commit();

        TenantId tenantId = release2.tenantId();
        ProductId productId = release2.productId();

        Collection<Release> savedReleases = releaseRepository.allProductReleases(tenantId, productId);
        assertFalse(savedReleases.isEmpty());
        assertEquals(1, savedReleases.size());
        assertEquals(release2.name(), savedReleases.iterator().next().name());

        LevelDBUnitOfWork.start(this.database);
        releaseRepository.remove(release2);
        LevelDBUnitOfWork.current().commit();

        savedReleases = releaseRepository.allProductReleases(tenantId, productId);
        assertTrue(savedReleases.isEmpty());
    }

    public void testSaveAllRemoveAll() throws Exception {
        Release release1 = new Release(
                new TenantId("12345"), new ProductId("p00000"), new ReleaseId("r11111"),
                "release1", "My release 1.", new Date(), new Date());

        Release release2 = new Release(
                new TenantId("12345"), new ProductId("p00000"), new ReleaseId("r11112"),
                "release2", "My release 2.", new Date(), new Date());

        Release release3 = new Release(
                new TenantId("12345"), new ProductId("p00000"), new ReleaseId("r11113"),
                "release3", "My release 3.", new Date(), new Date());

        LevelDBUnitOfWork.start(this.database);
        releaseRepository.saveAll(Arrays.asList(new Release[] { release1, release2, release3 }));
        LevelDBUnitOfWork.current().commit();

        TenantId tenantId = release1.tenantId();
        ProductId productId = release1.productId();

        Collection<Release> savedReleases = releaseRepository.allProductReleases(tenantId, productId);
        assertFalse(savedReleases.isEmpty());
        assertEquals(3, savedReleases.size());

        LevelDBUnitOfWork.start(this.database);
        releaseRepository.removeAll(Arrays.asList(new Release[] { release1, release3 }));
        LevelDBUnitOfWork.current().commit();

        savedReleases = releaseRepository.allProductReleases(tenantId, productId);
        assertFalse(savedReleases.isEmpty());
        assertEquals(1, savedReleases.size());
        assertEquals(release2.name(), savedReleases.iterator().next().name());

        LevelDBUnitOfWork.start(this.database);
        releaseRepository.removeAll(Arrays.asList(new Release[] { release2 }));
        LevelDBUnitOfWork.current().commit();

        savedReleases = releaseRepository.allProductReleases(tenantId, productId);
        assertTrue(savedReleases.isEmpty());
    }

    public void testConcurrentTransactions() throws Exception {
        final List<Integer> orderOfCommits = new ArrayList<Integer>();

        Release release1 = new Release(
                new TenantId("12345"), new ProductId("p00000"), new ReleaseId("r11111"),
                "release1", "My release 1.", new Date(), new Date());

        LevelDBUnitOfWork.start(database);
        releaseRepository.save(release1);

        new Thread() {
           @Override
           public void run() {
               Release release2 = new Release(
                       new TenantId("12345"), new ProductId("p00000"), new ReleaseId("r11112"),
                       "release2", "My release 2.", new Date(), new Date());

               LevelDBUnitOfWork.start(database);
               releaseRepository.save(release2);
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

        Collection<Release> savedReleases = releaseRepository.allProductReleases(release1.tenantId(), release1.productId());

        assertFalse(savedReleases.isEmpty());
        assertEquals(2, savedReleases.size());
    }

    @Override
    protected void setUp() throws Exception {
        DomainEventPublisher.instance().reset();

        this.database = LevelDBProvider.instance().databaseFrom(LevelDBDatabasePath.agilePMPath());

        LevelDBProvider.instance().purge(this.database);

        super.setUp();
    }
}
