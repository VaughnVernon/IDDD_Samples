package com.saasovation.common.domain.model;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode;

import java.io.Serializable;
import java.util.UUID;

public abstract class Id implements Serializable {

    private UUID value;

    protected Id(UUID value) {
	this.value = value;
    }

    public UUID getValue() {
	return value;
    }

    @Override
    public boolean equals(Object obj) {
	return reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
	return reflectionHashCode(this);
    }

}
