/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
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
