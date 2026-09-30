/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.converter;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ObsFromObsGroupConverterTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ConceptService conceptService;

    @Test
    public void shouldReturnObsFromObsGroup() {

        Obs groupObs = buildObsGroup();
        Concept cd4 = conceptService.getConcept(5497);
        Concept weight = conceptService.getConcept(5089);

        DataConverter converter = new ObsFromObsGroupConverter(cd4);
        assertThat(((Obs) converter.convert(groupObs)).getId(), is(20));

        converter = new ObsFromObsGroupConverter(weight);
        assertThat(((Obs) converter.convert(groupObs)).getId(), is(10));
    }

    @Test
    public void shouldReturnNullIfNoMatchingObs() {

        Obs groupObs = buildObsGroup();
        Concept treatmentStatus = conceptService.getConcept(12);

        DataConverter converter = new ObsFromObsGroupConverter(treatmentStatus);
        assertNull(converter.convert(groupObs));
    }

    private Obs buildObsGroup() {

        Obs groupObs = new Obs();
        Obs weight = new Obs(10);
        Obs cd4 = new Obs(20);

        weight.setConcept(conceptService.getConcept(5089));
        cd4.setConcept(conceptService.getConcept(5497));

        groupObs.addGroupMember(weight);
        groupObs.addGroupMember(cd4);

        return groupObs;
    }

}
