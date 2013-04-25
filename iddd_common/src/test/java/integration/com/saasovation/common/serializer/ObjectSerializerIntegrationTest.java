package integration.com.saasovation.common.serializer;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.unitils.inject.annotation.TestedObject;

import com.saasovation.common.AbstractTest;
import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.Version;
import com.saasovation.common.serializer.ObjectSerializer;



public class ObjectSerializerIntegrationTest extends AbstractTest {

    @TestedObject
    private ObjectSerializer serializer;

    @Before
    public void setUp() {
	serializer = ObjectSerializer.instance();
    }

    @Test
    public void serializeDomainEvent() throws Exception {
	TestDomainEvent event = new TestDomainEvent();
	String serializedEvent = serializer.serialize(event);

	NewerTestDomainEvent actual = serializer.deserialize(serializedEvent, NewerTestDomainEvent.class);

	assertThat(actual.eventVersion()).isEqualTo(1);
    }

    @Version(1)
    private static final class TestDomainEvent extends DomainEvent {}

    @Version(3)
    private static final class NewerTestDomainEvent extends DomainEvent {}

}
