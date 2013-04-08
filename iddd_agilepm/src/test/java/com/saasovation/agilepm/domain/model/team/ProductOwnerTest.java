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

package com.saasovation.agilepm.domain.model.team;

import java.util.Date;

import com.saasovation.agilepm.domain.model.tenant.TenantId;

public class ProductOwnerTest extends TeamCommonTest {

    public ProductOwnerTest() {
        super();
    }

    public void testCreate() throws Exception {
        ProductOwner productOwner =
                new ProductOwner(
                        new TenantId("T-12345"),
                        "zoe",
                        "Zoe",
                        "Doe",
                        "zoe@saasovation.com",
                        new Date());

        assertNotNull(productOwner);

        this.productOwnerRepository.save(productOwner);

        assertEquals("zoe", productOwner.username());
        assertEquals("Zoe", productOwner.firstName());
        assertEquals("Doe", productOwner.lastName());
        assertEquals("zoe@saasovation.com", productOwner.emailAddress());
        assertEquals(productOwner.username(), productOwner.productOwnerId().id());
    }

    public void testChangeEmailAddress() throws Exception {
        ProductOwner productOwner = this.productOwnerForTest();

        assertFalse(productOwner.emailAddress().equals("zoedoe@saasovation.com"));

        // later...
        Date notificationOccurredOn = new Date();

        productOwner.changeEmailAddress("zoedoe@saasovation.com", notificationOccurredOn);

        assertEquals("zoedoe@saasovation.com", productOwner.emailAddress());
    }

    public void testChangeName() throws Exception {
        ProductOwner productOwner = this.productOwnerForTest();

        assertFalse(productOwner.lastName().equals("Doe-Jones"));

        // later...
        Date notificationOccurredOn = new Date();

        productOwner.changeName("Zoe", "Doe-Jones", notificationOccurredOn);

        assertEquals("Zoe", productOwner.firstName());
        assertEquals("Doe-Jones", productOwner.lastName());
    }

    public void testDisable() throws Exception {
        ProductOwner productOwner = this.productOwnerForTest();

        assertTrue(productOwner.isEnabled());

        // later...
        Date notificationOccurredOn = new Date();

        productOwner.disable(notificationOccurredOn);

        assertFalse(productOwner.isEnabled());
    }

    public void testEnable() throws Exception {
        ProductOwner productOwner = this.productOwnerForTest();

        productOwner.disable(this.twoHoursEarlierThanNow());

        assertFalse(productOwner.isEnabled());

        // later...
        Date notificationOccurredOn = new Date();

        productOwner.enable(notificationOccurredOn);

        assertTrue(productOwner.isEnabled());
    }

    public void testDisallowEarlierDisabling() {
        ProductOwner productOwner = this.productOwnerForTest();

        productOwner.disable(this.twoHoursEarlierThanNow());

        assertFalse(productOwner.isEnabled());

        // later...
        Date notificationOccurredOn = new Date();

        productOwner.enable(notificationOccurredOn);

        assertTrue(productOwner.isEnabled());

        // latent notification...
        productOwner.disable(this.twoMinutesEarlierThanNow());

        assertTrue(productOwner.isEnabled());
    }

    public void testDisallowEarlierEnabling() {
        ProductOwner productOwner = this.productOwnerForTest();

        assertTrue(productOwner.isEnabled());

        // later...
        Date notificationOccurredOn = new Date();

        productOwner.disable(notificationOccurredOn);

        assertFalse(productOwner.isEnabled());

        // latent notification...
        productOwner.enable(this.twoMinutesEarlierThanNow());

        assertFalse(productOwner.isEnabled());
    }
}
