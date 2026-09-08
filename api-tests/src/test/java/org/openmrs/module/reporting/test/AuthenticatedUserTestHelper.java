/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.test;

import org.junit.After;
import org.junit.Before;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Sets up a mock UserContext, which lets you write unit tests that call methods that do Context.getAuthenticatedUser()
 * without needing the test to be context-sensitive, and without needing PowerMock to mock the static
 * Context.getAuthenticatedUser method.
 *
 * This is a near-copy taken on 30-Jan-2014 from a similar class in the EMR API module's test packages:
 * https://github.com/openmrs/openmrs-module-emrapi/blob/4cee13564b5a079559a9452d37c6032d73a734a0/api/src/test/java/org/openmrs/module/emrapi/test/AuthenticatedUserTestHelper.java
 */
public class AuthenticatedUserTestHelper {

    protected User authenticatedUser;
    protected UserContext mockUserContext;

    @Before
    public void setUpMockUserContext() throws Exception {
        authenticatedUser = new User();
        authenticatedUser.setPerson(new Person());

        mockUserContext = mock(UserContext.class);
        when(mockUserContext.getAuthenticatedUser()).thenReturn(authenticatedUser);

        Context.setUserContext(mockUserContext);
    }

    @After
    public void tearDownMockUserContext() throws Exception {
        Context.clearUserContext();
    }

}
