package com.saasovation.issuetrack.domain.model.product;

import static java.util.UUID.randomUUID;

import java.util.UUID;

import com.saasovation.common.domain.model.Id;

public class ProductId extends Id {

    public static ProductId newProductId() {
	return productId(randomUUID());
    }

    public static ProductId productId(UUID value) {
	return new ProductId(value);
    }

    private ProductId(UUID value) {
	super(value);
    }

}
