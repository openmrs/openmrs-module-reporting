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

package org.openmrs.module.reporting.data;

import org.junit.Test;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JoinDataDefinitionTest {

    @Test
    public void getName_shouldGetNameIfExplicitlySet() throws Exception {
        GenderDataDefinition joined = new GenderDataDefinition();
        joined.setName("Joined Name");

        PersonToPatientDataDefinition actual = new PersonToPatientDataDefinition(joined);
        actual.setName("Actual Name");

        assertThat(actual.getName(), is("Actual Name"));
    }

    @Test
    public void getName_shouldGetNameFromJoinedDefinitionIfNotSet() throws Exception {
        GenderDataDefinition joined = new GenderDataDefinition();
        joined.setName("Joined Name");

        PersonToPatientDataDefinition actual = new PersonToPatientDataDefinition(joined);

        assertThat(actual.getName(), is("Joined Name"));
    }

}
