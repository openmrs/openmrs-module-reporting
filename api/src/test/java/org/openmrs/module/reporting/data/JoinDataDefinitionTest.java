/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
