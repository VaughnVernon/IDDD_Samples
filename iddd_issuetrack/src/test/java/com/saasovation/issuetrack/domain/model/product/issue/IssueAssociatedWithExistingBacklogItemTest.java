package com.saasovation.issuetrack.domain.model.product.issue;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.saasovation.common.AbstractTest;
import com.saasovation.common.notification.Notification;
import com.saasovation.common.notification.NotificationReader;
import com.saasovation.common.notification.NotificationSerializer;

public class IssueAssociatedWithExistingBacklogItemTest extends AbstractTest {

    private static final String ISSUE_ID = "issue_1";
    private NotificationSerializer serializer = new NotificationSerializer(true, true);

    @Test
    public void serialize() throws Exception {
	IssueAssociatedWithExistingBacklogItem event =
		new IssueAssociatedWithExistingBacklogItem(
			ISSUE_ID,
			"product_1",
			"tenant_1",
			"description",
			"summary",
			"backlog_item_1"
			);

	String serializedEvent = serializer.serialize(new Notification(1, event));
	System.out.println(serializedEvent);

	NotificationReader reader = new NotificationReader(serializedEvent);
	assertThat(reader.stringValue("event.issueId")).isEqualTo(ISSUE_ID);
    }

}
