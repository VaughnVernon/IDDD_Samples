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

package com.saasovation.agilepm.domain.model.product.release;

import java.util.Date;

import com.saasovation.agilepm.domain.model.product.Product;
import com.saasovation.agilepm.domain.model.product.ProductBacklogItemPlanned;
import com.saasovation.agilepm.domain.model.product.ProductCommonTest;
import com.saasovation.agilepm.domain.model.product.ProductCreated;
import com.saasovation.agilepm.domain.model.product.ProductReleaseScheduled;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItem;

public class ReleaseTest extends ProductCommonTest {

    public ReleaseTest() {
        super();
    }

    public void testScheduleRelease() throws Exception {
        Product product = this.productForTest();

        Date begins = new Date();
        Date ends = new Date(begins.getTime() + (86400000L * 30L));

        Release release =
                product.scheduleRelease(
                        new ReleaseId("R-12345"),
                        "Release 1.3",
                        "Enterprise interactive release.",
                        begins,
                        ends);

        assertNotNull(release);
        assertEquals("Release 1.3", release.name());
        assertEquals("Enterprise interactive release.", release.description());

        expectedEvents(2);
        expectedEvent(ProductCreated.class);
        expectedEvent(ProductReleaseScheduled.class);
    }

    public void testArchived() throws Exception {
        Release release = this.releaseForTest(this.productForTest());

        assertFalse(release.isArchived());

        release.archived(true);

        assertTrue(release.isArchived());
    }

    public void testDescribeAs() throws Exception {
        Release release = this.releaseForTest(this.productForTest());

        String changedDescription = "New and improved description.";

        assertFalse(changedDescription.equals(release.description()));

        release.describeAs(changedDescription);

        assertEquals(changedDescription, release.description());
    }

    public void testNowBeginsOn() throws Exception {
        Release release = this.releaseForTest(this.productForTest());

        Date date = new Date(new Date().getTime() + (86400000L * 2L));

        assertFalse(date.equals(release.begins()));

        release.nowBeginsOn(date);

        assertEquals(date, release.begins());
    }

    public void testNowEndsOn() throws Exception {
        Release release = this.releaseForTest(this.productForTest());

        Date date = new Date(new Date().getTime() + (86400000L * 10L));

        assertFalse(date.equals(release.ends()));

        release.nowEndsOn(date);

        assertEquals(date, release.ends());
    }

    public void testRename() throws Exception {
        Release release = this.releaseForTest(this.productForTest());

        String changedName = "New Release Name";

        assertFalse(changedName.equals(release.name()));

        release.rename(changedName);

        assertEquals(changedName, release.name());
    }

    public void testReorderFrom() throws Exception {
        Product product = this.productForTest();
        Release release = this.releaseForTest(product);

        BacklogItem backlogItem1 = this.backlogItem1ForTest(product);
        BacklogItem backlogItem2 = this.backlogItem2ForTest(product);
        BacklogItem backlogItem3 = this.backlogItem3ForTest(product);

        expectedEvents(5);
        expectedEvent(ProductCreated.class, 1);
        expectedEvent(ProductReleaseScheduled.class, 1);
        expectedEvent(ProductBacklogItemPlanned.class, 3);

        release.schedule(backlogItem1);
        release.schedule(backlogItem2);
        release.schedule(backlogItem3);

        ScheduledBacklogItem scheduledBacklogItem1 = null;
        ScheduledBacklogItem scheduledBacklogItem2 = null;
        ScheduledBacklogItem scheduledBacklogItem3 = null;

        for (ScheduledBacklogItem scheduledBacklogItem : release.allScheduledBacklogItems()) {
            if (scheduledBacklogItem.ordering() == 1) {
                scheduledBacklogItem1 = scheduledBacklogItem;
            }
            if (scheduledBacklogItem.ordering() == 2) {
                scheduledBacklogItem2 = scheduledBacklogItem;
            }
            if (scheduledBacklogItem.ordering() == 3) {
                scheduledBacklogItem3 = scheduledBacklogItem;
            }
        }

        release.reorderFrom(backlogItem3.backlogItemId(), 1);

        assertEquals(1, scheduledBacklogItem3.ordering());
        assertEquals(2, scheduledBacklogItem1.ordering());
        assertEquals(3, scheduledBacklogItem2.ordering());
    }

    public void testSchedule() throws Exception {
        Product product = this.productForTest();
        Release release = this.releaseForTest(product);

        BacklogItem backlogItem1 = this.backlogItem1ForTest(product);
        BacklogItem backlogItem2 = this.backlogItem2ForTest(product);
        BacklogItem backlogItem3 = this.backlogItem3ForTest(product);

        expectedEvents(5);
        expectedEvent(ProductCreated.class, 1);
        expectedEvent(ProductReleaseScheduled.class, 1);
        expectedEvent(ProductBacklogItemPlanned.class, 3);

        release.schedule(backlogItem1);
        release.schedule(backlogItem2);
        release.schedule(backlogItem3);

        for (ScheduledBacklogItem scheduledBacklogItem : release.allScheduledBacklogItems()) {
            if (scheduledBacklogItem.ordering() == 1) {
                assertTrue(scheduledBacklogItem.backlogItemId().id().endsWith("-1"));
            }
            if (scheduledBacklogItem.ordering() == 2) {
                assertTrue(scheduledBacklogItem.backlogItemId().id().endsWith("-2"));
            }
            if (scheduledBacklogItem.ordering() == 3) {
                assertTrue(scheduledBacklogItem.backlogItemId().id().endsWith("-3"));
            }
        }
    }

    public void testUnschedule() throws Exception {
        Product product = this.productForTest();
        Release release = this.releaseForTest(product);

        BacklogItem backlogItem1 = this.backlogItem1ForTest(product);
        BacklogItem backlogItem2 = this.backlogItem2ForTest(product);
        BacklogItem backlogItem3 = this.backlogItem3ForTest(product);

        release.schedule(backlogItem1);
        release.schedule(backlogItem2);
        release.schedule(backlogItem3);

        assertEquals(3, release.allScheduledBacklogItems().size());

        release.unschedule(backlogItem2);

        assertEquals(2, release.allScheduledBacklogItems().size());

        for (ScheduledBacklogItem scheduledBacklogItem : release.allScheduledBacklogItems()) {
            assertTrue(scheduledBacklogItem.backlogItemId().equals(backlogItem1.backlogItemId()) ||
                    scheduledBacklogItem.backlogItemId().equals(backlogItem3.backlogItemId()));
        }
    }
}
