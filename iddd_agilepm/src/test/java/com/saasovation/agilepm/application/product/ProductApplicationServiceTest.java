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

import java.util.UUID;

import com.saasovation.agilepm.application.ProductApplicationCommonTest;
import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability;
import com.saasovation.agilepm.domain.model.product.Product;
import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.team.ProductOwner;

public class ProductApplicationServiceTest extends ProductApplicationCommonTest {

    public ProductApplicationServiceTest() {
        super();
    }

    public void testDiscussionProcess() throws Exception {
        Product product = this.persistedProductForTest();

        this.productApplicationService.requestProductDiscussion(
                new RequestProductDiscussionCommand(
                        product.tenantId().id(),
                        product.productId().id()));

        this.productApplicationService.startDiscussionInitiation(
                new StartDiscussionInitiationCommand(
                        product.tenantId().id(),
                        product.productId().id()));

        Product productWithStartedDiscussionInitiation =
                this.productRepository
                    .productOfId(
                            product.tenantId(),
                            product.productId());

        assertNotNull(productWithStartedDiscussionInitiation.discussionInitiationId());

        String discussionId = UUID.randomUUID().toString().toUpperCase();

        InitiateDiscussionCommand command =
                new InitiateDiscussionCommand(
                        product.tenantId().id(),
                        product.productId().id(),
                        discussionId);

        this.productApplicationService.initiateDiscussion(command);

        Product productWithInitiatedDiscussion =
                this.productRepository
                    .productOfId(
                            product.tenantId(),
                            product.productId());

        assertEquals(discussionId, productWithInitiatedDiscussion.discussion().descriptor().id());
    }

    public void testNewProduct() throws Exception {
        ProductOwner productOwner = this.persistedProductOwnerForTest();

        String newProductId =
            this.productApplicationService.newProduct(
                    new NewProductCommand(
                            "T-12345",
                            productOwner.productOwnerId().id(),
                            "My Product",
                            "The description of My Product."));

        Product newProduct =
                this.productRepository
                    .productOfId(
                            productOwner.tenantId(),
                            new ProductId(newProductId));

        assertNotNull(newProduct);
        assertEquals("My Product", newProduct.name());
        assertEquals("The description of My Product.", newProduct.description());
    }

    public void testNewProductWithDiscussion() throws Exception {
        ProductOwner productOwner = this.persistedProductOwnerForTest();

        String newProductId =
            this.productApplicationService.newProductWithDiscussion(
                    new NewProductCommand(
                            "T-12345",
                            productOwner.productOwnerId().id(),
                            "My Product",
                            "The description of My Product."));

        Product newProduct =
                this.productRepository
                    .productOfId(
                            productOwner.tenantId(),
                            new ProductId(newProductId));

        assertNotNull(newProduct);
        assertEquals("My Product", newProduct.name());
        assertEquals("The description of My Product.", newProduct.description());
        assertEquals(DiscussionAvailability.REQUESTED, newProduct.discussion().availability());
    }

    public void testRequestProductDiscussion() throws Exception {
        Product product = this.persistedProductForTest();

        this.productApplicationService.requestProductDiscussion(
                new RequestProductDiscussionCommand(
                        product.tenantId().id(),
                        product.productId().id()));

        Product productWithRequestedDiscussion =
                this.productRepository
                    .productOfId(
                            product.tenantId(),
                            product.productId());

        assertEquals(DiscussionAvailability.REQUESTED, productWithRequestedDiscussion.discussion().availability());
    }

    public void testRetryProductDiscussionRequest() throws Exception {
        Product product = this.persistedProductForTest();

        this.productApplicationService.requestProductDiscussion(
                new RequestProductDiscussionCommand(
                        product.tenantId().id(),
                        product.productId().id()));

        Product productWithRequestedDiscussion =
                this.productRepository
                    .productOfId(
                            product.tenantId(),
                            product.productId());

        assertEquals(DiscussionAvailability.REQUESTED, productWithRequestedDiscussion.discussion().availability());

        this.productApplicationService.startDiscussionInitiation(
                new StartDiscussionInitiationCommand(
                        product.tenantId().id(),
                        product.productId().id()));

        Product productWithDiscussionInitiation =
                this.productRepository
                    .productOfId(
                            product.tenantId(),
                            product.productId());

        assertNotNull(productWithDiscussionInitiation.discussionInitiationId());

        this.productApplicationService.retryProductDiscussionRequest(
                new RetryProductDiscussionRequestCommand(
                        product.tenantId().id(),
                        productWithDiscussionInitiation.discussionInitiationId()));

        Product productWithRetriedRequestedDiscussion =
                this.productRepository
                    .productOfId(
                            product.tenantId(),
                            product.productId());

        assertEquals(DiscussionAvailability.REQUESTED, productWithRetriedRequestedDiscussion.discussion().availability());
    }

    public void testStartDiscussionInitiation() throws Exception {
        Product product = this.persistedProductForTest();

        this.productApplicationService.requestProductDiscussion(
                new RequestProductDiscussionCommand(
                        product.tenantId().id(),
                        product.productId().id()));

        Product productWithRequestedDiscussion =
                this.productRepository
                    .productOfId(
                            product.tenantId(),
                            product.productId());

        assertEquals(DiscussionAvailability.REQUESTED, productWithRequestedDiscussion.discussion().availability());

        assertNull(productWithRequestedDiscussion.discussionInitiationId());

        this.productApplicationService.startDiscussionInitiation(
                new StartDiscussionInitiationCommand(
                        product.tenantId().id(),
                        product.productId().id()));

        Product productWithDiscussionInitiation =
                this.productRepository
                    .productOfId(
                            product.tenantId(),
                            product.productId());

        assertNotNull(productWithDiscussionInitiation.discussionInitiationId());
    }

    public void testTimeOutProductDiscussionRequest() throws Exception {
        // TODO: student assignment
    }
}
