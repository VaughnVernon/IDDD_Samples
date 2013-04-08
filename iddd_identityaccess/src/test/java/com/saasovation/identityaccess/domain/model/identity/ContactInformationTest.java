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

package com.saasovation.identityaccess.domain.model.identity;

import com.saasovation.identityaccess.domain.model.IdentityAccessTest;

public class ContactInformationTest extends IdentityAccessTest {

    public ContactInformationTest() {
        super();
    }

    public void testContactInformation() throws Exception {
        ContactInformation contactInformation = this.contactInformation();

        assertEquals(FIXTURE_USER_EMAIL_ADDRESS, contactInformation.emailAddress().address());
        assertEquals("Boulder", contactInformation.postalAddress().city());
        assertEquals("CO", contactInformation.postalAddress().stateProvince());
    }

    public void testChangeEmailAddress() throws Exception {
        ContactInformation contactInformation = this.contactInformation();
        ContactInformation contactInformationCopy = new ContactInformation(contactInformation);

        ContactInformation contactInformation2 =
                contactInformation
                    .changeEmailAddress(
                            new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS2));

        assertEquals(contactInformationCopy, contactInformation);
        assertFalse(contactInformation.equals(contactInformation2));
        assertFalse(contactInformationCopy.equals(contactInformation2));

        assertEquals(FIXTURE_USER_EMAIL_ADDRESS, contactInformation.emailAddress().address());
        assertEquals(FIXTURE_USER_EMAIL_ADDRESS2, contactInformation2.emailAddress().address());
        assertEquals("Boulder", contactInformation.postalAddress().city());
        assertEquals("CO", contactInformation.postalAddress().stateProvince());
    }

    public void testChangePostalAddress() throws Exception {
        ContactInformation contactInformation = this.contactInformation();
        ContactInformation contactInformationCopy = new ContactInformation(contactInformation);

        ContactInformation contactInformation2 =
                contactInformation
                    .changePostalAddress(
                            new PostalAddress("321 Mockingbird Lane", "Denver", "CO", "81121", "US"));

        assertEquals(contactInformationCopy, contactInformation);
        assertFalse(contactInformation.equals(contactInformation2));
        assertFalse(contactInformationCopy.equals(contactInformation2));

        assertEquals("321 Mockingbird Lane", contactInformation2.postalAddress().streetAddress());
        assertEquals("Denver", contactInformation2.postalAddress().city());
        assertEquals("CO", contactInformation2.postalAddress().stateProvince());
    }

    public void testChangePrimaryTelephone() throws Exception {
        ContactInformation contactInformation = this.contactInformation();
        ContactInformation contactInformationCopy = new ContactInformation(contactInformation);

        ContactInformation contactInformation2 =
                contactInformation
                    .changePrimaryTelephone(
                            new Telephone("720-555-1212"));

        assertEquals(contactInformationCopy, contactInformation);
        assertFalse(contactInformation.equals(contactInformation2));
        assertFalse(contactInformationCopy.equals(contactInformation2));

        assertEquals("720-555-1212", contactInformation2.primaryTelephone().number());
        assertEquals("Boulder", contactInformation2.postalAddress().city());
        assertEquals("CO", contactInformation2.postalAddress().stateProvince());
    }

    public void testChangeSecondaryTelephone() throws Exception {
        ContactInformation contactInformation = this.contactInformation();
        ContactInformation contactInformationCopy = new ContactInformation(contactInformation);

        ContactInformation contactInformation2 =
                contactInformation
                    .changeSecondaryTelephone(
                            new Telephone("720-555-1212"));

        assertEquals(contactInformationCopy, contactInformation);
        assertFalse(contactInformation.equals(contactInformation2));
        assertFalse(contactInformationCopy.equals(contactInformation2));

        assertEquals("720-555-1212", contactInformation2.secondaryTelephone().number());
        assertEquals("Boulder", contactInformation2.postalAddress().city());
        assertEquals("CO", contactInformation2.postalAddress().stateProvince());
    }
}
