package com.saasovation.common.event;

public class AKindOfTestableDomainEvent extends TestableDomainEvent {
    private Integer value;

    public AKindOfTestableDomainEvent(long anId, String aName, Integer aValue) {
        super(anId, aName);
    }

    public Integer value() { return value; }

    private void setValue(Integer value) {
        this.value = value;
    }
}
