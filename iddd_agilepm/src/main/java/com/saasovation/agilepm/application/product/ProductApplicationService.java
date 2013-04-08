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

package com.saasovation.agilepm.application.product;

import java.util.Date;

import com.saasovation.agilepm.application.ApplicationServiceLifeCycle;
import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability;
import com.saasovation.agilepm.domain.model.discussion.DiscussionDescriptor;
import com.saasovation.agilepm.domain.model.product.Product;
import com.saasovation.agilepm.domain.model.product.ProductDiscussionRequestTimedOut;
import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.product.ProductRepository;
import com.saasovation.agilepm.domain.model.team.ProductOwner;
import com.saasovation.agilepm.domain.model.team.ProductOwnerRepository;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.process.ProcessId;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTracker;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTrackerRepository;

public class ProductApplicationService {

    private TimeConstrainedProcessTrackerRepository processTrackerRepository;
    private ProductOwnerRepository productOwnerRepository;
    private ProductRepository productRepository;

    public ProductApplicationService(
            ProductRepository aProductRepository,
            ProductOwnerRepository aProductOwnerRepository,
            TimeConstrainedProcessTrackerRepository aProcessTrackerRepository) {

        super();

        this.processTrackerRepository = aProcessTrackerRepository;
        this.productOwnerRepository = aProductOwnerRepository;
        this.productRepository = aProductRepository;
    }

    // TODO: additional APIs / student assignment

    public void initiateDiscussion(InitiateDiscussionCommand aCommand) {
        ApplicationServiceLifeCycle.begin();

        try {
            Product product =
                    this.productRepository()
                        .productOfId(
                                new TenantId(aCommand.getTenantId()),
                                new ProductId(aCommand.getProductId()));

            if (product == null) {
                throw new IllegalStateException(
                        "Unknown product of tenant id: "
                        + aCommand.getTenantId()
                        + " and product id: "
                        + aCommand.getProductId());
            }

            product.initiateDiscussion(new DiscussionDescriptor(aCommand.getDiscussionId()));

            this.productRepository().save(product);

            ProcessId processId = ProcessId.existingProcessId(product.discussionInitiationId());

            TimeConstrainedProcessTracker tracker =
                    this.processTrackerRepository()
                        .trackerOfProcessId(aCommand.getTenantId(), processId);

            tracker.completed();

            this.processTrackerRepository().save(tracker);

            ApplicationServiceLifeCycle.success();

        } catch (RuntimeException e) {
            ApplicationServiceLifeCycle.fail(e);
        }
    }

    public String newProduct(NewProductCommand aCommand) {

        return this.newProductWith(
                aCommand.getTenantId(),
                aCommand.getProductOwnerId(),
                aCommand.getName(),
                aCommand.getDescription(),
                DiscussionAvailability.NOT_REQUESTED);
    }

    public String newProductWithDiscussion(NewProductCommand aCommand) {

        return this.newProductWith(
                aCommand.getTenantId(),
                aCommand.getProductOwnerId(),
                aCommand.getName(),
                aCommand.getDescription(),
                this.requestDiscussionIfAvailable());
    }

    public void requestProductDiscussion(RequestProductDiscussionCommand aCommand) {
        Product product =
                this.productRepository()
                    .productOfId(
                            new TenantId(aCommand.getTenantId()),
                            new ProductId(aCommand.getProductId()));

        if (product == null) {
            throw new IllegalStateException(
                    "Unknown product of tenant id: "
                    + aCommand.getTenantId()
                    + " and product id: "
                    + aCommand.getProductId());
        }

        this.requestProductDiscussionFor(product);
    }

    public void retryProductDiscussionRequest(RetryProductDiscussionRequestCommand aCommand) {

        ProcessId processId = ProcessId.existingProcessId(aCommand.getProcessId());

        TenantId tenantId = new TenantId(aCommand.getTenantId());

        Product product =
                this.productRepository()
                    .productOfDiscussionInitiationId(
                            tenantId,
                            processId.id());

        if (product == null) {
            throw new IllegalStateException(
                    "Unknown product of tenant id: "
                    + aCommand.getTenantId()
                    + " and discussion initiation id: "
                    + processId.id());
        }

        this.requestProductDiscussionFor(product);
    }

    public void startDiscussionInitiation(StartDiscussionInitiationCommand aCommand) {

        ApplicationServiceLifeCycle.begin();

        try {
            Product product =
                    this.productRepository()
                        .productOfId(
                                new TenantId(aCommand.getTenantId()),
                                new ProductId(aCommand.getProductId()));

            if (product == null) {
                throw new IllegalStateException(
                        "Unknown product of tenant id: "
                        + aCommand.getTenantId()
                        + " and product id: "
                        + aCommand.getProductId());
            }

            String timedOutEventName =
                    ProductDiscussionRequestTimedOut.class.getName();

            TimeConstrainedProcessTracker tracker =
                    new TimeConstrainedProcessTracker(
                            product.tenantId().id(),
                            ProcessId.newProcessId(),
                            "Create discussion for product: "
                                + product.name(),
                            new Date(),
                            5L * 60L * 1000L, // retries every 5 minutes
                            3, // 3 total retries
                            timedOutEventName);

            this.processTrackerRepository().save(tracker);

            product.startDiscussionInitiation(tracker.processId().id());

            this.productRepository().save(product);

            ApplicationServiceLifeCycle.success();

        } catch (RuntimeException e) {
            ApplicationServiceLifeCycle.fail(e);
        }
    }

    public void timeOutProductDiscussionRequest(TimeOutProductDiscussionRequestCommand aCommand) {

        ApplicationServiceLifeCycle.begin();

        try {
            ProcessId processId = ProcessId.existingProcessId(aCommand.getProcessId());

            TenantId tenantId = new TenantId(aCommand.getTenantId());

            Product product =
                    this.productRepository()
                        .productOfDiscussionInitiationId(
                                tenantId,
                                processId.id());

            this.sendEmailForTimedOutProcess(product);

            product.failDiscussionInitiation();

            this.productRepository().save(product);

            ApplicationServiceLifeCycle.success();

        } catch (RuntimeException e) {
            ApplicationServiceLifeCycle.fail(e);
        }
    }

    private void sendEmailForTimedOutProcess(Product aProduct) {

        // TODO: Implement

    }

    private String newProductWith(
            String aTenantId,
            String aProductOwnerId,
            String aName,
            String aDescription,
            DiscussionAvailability aDiscussionAvailability) {

        TenantId tenantId = new TenantId(aTenantId);
        ProductId productId = null;

        ApplicationServiceLifeCycle.begin();

        try {
            productId = this.productRepository().nextIdentity();

            ProductOwner productOwner =
                    this.productOwnerRepository()
                        .productOwnerOfIdentity(
                                tenantId,
                                aProductOwnerId);

            Product product =
                    new Product(
                            tenantId,
                            productId,
                            productOwner.productOwnerId(),
                            aName,
                            aDescription,
                            aDiscussionAvailability);

            this.productRepository().save(product);

            ApplicationServiceLifeCycle.success();

        } catch (RuntimeException e) {
            ApplicationServiceLifeCycle.fail(e);
        }

        return productId.id();
    }

    private DiscussionAvailability requestDiscussionIfAvailable() {
        DiscussionAvailability availability = DiscussionAvailability.ADD_ON_NOT_ENABLED;

        boolean enabled = true; // TODO: determine add-on enabled

        if (enabled) {
            availability = DiscussionAvailability.REQUESTED;
        }

        return availability;
    }

    private TimeConstrainedProcessTrackerRepository processTrackerRepository() {
        return this.processTrackerRepository;
    }

    private ProductOwnerRepository productOwnerRepository() {
        return this.productOwnerRepository;
    }

    private ProductRepository productRepository() {
        return this.productRepository;
    }

    private void requestProductDiscussionFor(Product aProduct) {

        ApplicationServiceLifeCycle.begin();

        try {
            aProduct.requestDiscussion(this.requestDiscussionIfAvailable());

            this.productRepository().save(aProduct);

            ApplicationServiceLifeCycle.success();

        } catch (RuntimeException e) {
            ApplicationServiceLifeCycle.fail(e);
        }
    }
}
