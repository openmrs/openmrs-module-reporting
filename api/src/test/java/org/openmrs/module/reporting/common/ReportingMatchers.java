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

package org.openmrs.module.reporting.common;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.Collection;

/**
 *
 */
public class ReportingMatchers {

    public static Matcher<Definition> hasParameter(String parameterName, Class<?> ofType) {
        return hasParameter(parameterName, ofType, null);
    }

    public static Matcher<Definition> hasParameter(final String withName, final Class<?> ofType, final Class<? extends Collection> ofCollectionType) {
        return new BaseMatcher<Definition>() {
            @Override
            public boolean matches(Object o) {
                Definition actual = (Definition) o;
                Parameter parameter = actual.getParameter(withName);
                return parameter != null &&
                        parameter.getType().equals(ofType) &&
                        ( (ofCollectionType == null && parameter.getCollectionType() == null)
                                || (ofCollectionType != null && ofCollectionType.equals(parameter.getCollectionType())) );
            }

            // TODO fix this implementation or figure out which other matcher implementation to use
            @Override
            public void describeTo(Description description) {
                description.appendText("should have parameter " + withName + " of type " + ofType + " and collection type " + ofCollectionType);
            }
        };
    }

}
