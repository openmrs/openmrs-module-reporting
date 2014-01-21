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
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.util.OpenmrsUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

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

    public static Matcher<IdSet<?>> hasExactlyIds(final Integer... expectedMemberIds) {
        return new BaseMatcher<IdSet<?>>() {
            @Override
            public boolean matches(Object o) {
                Set<Integer> actual = ((IdSet<?>) o).getMemberIds();
                return (actual.size() == expectedMemberIds.length) && containsInAnyOrder(expectedMemberIds).matches(actual);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue("IdSet with " + expectedMemberIds.length + " members: " + OpenmrsUtil.join(Arrays.asList(expectedMemberIds), ", "));
            }
        };
    }

    public static Matcher<Cohort> isCohortWithExactlyIds(final Integer... expectedMemberIds) {
        return new BaseMatcher<Cohort>() {
            @Override
            public boolean matches(Object o) {
                Set<Integer> actual = ((Cohort) o).getMemberIds();
                return (actual.size() == expectedMemberIds.length) && containsInAnyOrder(expectedMemberIds).matches(actual);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue("Cohort with " + expectedMemberIds.length + " members: " + OpenmrsUtil.join(Arrays.asList(expectedMemberIds), ", "));
            }
        };
    }

    public static Matcher<Cohort> isCohortWithExactlyMembers(Person... expectedMembers) {
        final Integer[] expectedMemberIds = new Integer[expectedMembers.length];
        for (int i = 0; i < expectedMembers.length; ++i) {
            expectedMemberIds[i] = expectedMembers[i].getId();
        }

        return new BaseMatcher<Cohort>() {
            @Override
            public boolean matches(Object o) {
                Set<Integer> actual = ((Cohort) o).getMemberIds();
                return (actual.size() == expectedMemberIds.length) && containsInAnyOrder(expectedMemberIds).matches(actual);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue("Cohort with " + expectedMemberIds.length + " members: " + OpenmrsUtil.join(Arrays.asList(expectedMemberIds), ", "));
            }
        };
    }

    public static Matcher<Parameter> parameterNamed(final String expectedName) {
        return new BaseMatcher<Parameter>() {
            @Override
            public boolean matches(Object actual) {
                Parameter parameter = (Parameter) actual;
                return ((Parameter) actual).getName().equals(expectedName);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue("parameter named " + expectedName);
            }
        };
    }

}
