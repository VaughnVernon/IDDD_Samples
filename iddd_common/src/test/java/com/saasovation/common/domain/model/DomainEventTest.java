package com.saasovation.common.domain.model;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.saasovation.common.AbstractTest;

public class DomainEventTest extends AbstractTest {

    @Test
    public void eventVersion_whenUnversioned() throws Exception {
	assertThat(new UnversionedEvent().eventVersion()).isEqualTo(1);
    }

    @Test
    public void eventVersion_whenVersioned() throws Exception {
	assertThat(new VersionedEvent().eventVersion()).isEqualTo(3);
    }

    private static final class UnversionedEvent extends DomainEvent {}

    @Version(3)
    private static final class VersionedEvent extends DomainEvent {}

}
