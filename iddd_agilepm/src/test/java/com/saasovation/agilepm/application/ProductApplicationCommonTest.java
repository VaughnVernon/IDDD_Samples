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

package com.saasovation.agilepm.application;

import java.util.Date;

import com.saasovation.agilepm.application.product.ProductApplicationService;
import com.saasovation.agilepm.domain.model.product.Product;
import com.saasovation.agilepm.domain.model.product.ProductCommonTest;
import com.saasovation.agilepm.domain.model.team.ProductOwner;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.agilepm.port.adapter.persistence.LevelDBDatabasePath;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTrackerRepository;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBTimeConstrainedProcessTrackerRepository;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public abstract class ProductApplicationCommonTest extends ProductCommonTest {

    protected ProductApplicationService productApplicationService;
    protected TimeConstrainedProcessTrackerRepository timeConstrainedProcessTrackerRepository;

    public ProductApplicationCommonTest() {
        super();
    }

    protected Product persistedProductForTest() {
        Product product = this.productForTest();

        LevelDBUnitOfWork.start(this.database);

        this.productRepository.save(product);

        LevelDBUnitOfWork.current().commit();

        return product;
    }

    protected ProductOwner persistedProductOwnerForTest() {
        ProductOwner productOwner =
                new ProductOwner(
                        new TenantId("T-12345"),
                        "zoe",
                        "Zoe",
                        "Doe",
                        "zoe@saasovation.com",
                        new Date(new Date().getTime() - (86400000L * 30)));

        LevelDBUnitOfWork.start(this.database);

        this.productOwnerRepository.save(productOwner);

        LevelDBUnitOfWork.current().commit();

        return productOwner;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.timeConstrainedProcessTrackerRepository =
                new LevelDBTimeConstrainedProcessTrackerRepository(
                        LevelDBDatabasePath.agilePMPath());

        this.productApplicationService =
                new ProductApplicationService(
                        this.productRepository,
                        this.productOwnerRepository,
                        this.timeConstrainedProcessTrackerRepository);
    }
}
