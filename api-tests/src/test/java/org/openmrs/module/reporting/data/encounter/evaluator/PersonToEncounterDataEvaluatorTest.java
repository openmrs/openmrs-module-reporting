/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.encounter.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.encounter.EncounterData;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.PersonToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PersonToEncounterDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    PersonService personService;

    @Autowired @Qualifier("reportingEncounterDataService")
    EncounterDataService encounterDataService;

    /**
     * Run this before each unit test in this class. The "@Before" method in
     * {@link org.openmrs.test.BaseContextSensitiveTest} is run right before this method.
     *
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void evaluate_shouldReturnPersonDataByForEachEncounterInContext() throws Exception {

        PersonToEncounterDataDefinition d = new PersonToEncounterDataDefinition(new BirthdateDataDefinition());

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(3,6));
        EvaluatedEncounterData ed = encounterDataService.evaluate(d, context);

        Assert.assertEquals(2, ed.getData().size());
        BirthdateConverter c = new BirthdateConverter("yyyy-MM-dd");
        Assert.assertEquals("1976-08-25", c.convert(ed.getData().get(3)));
        Assert.assertEquals("1925-02-08", c.convert(ed.getData().get(6)));

    }

    @Test
    public void evaluate_shouldEmptySetIfInputSetEmpty() throws Exception {

        PersonToEncounterDataDefinition d = new PersonToEncounterDataDefinition(new BirthdateDataDefinition());

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet());
        EvaluatedEncounterData ed = encounterDataService.evaluate(d, context);

        Assert.assertEquals(0, ed.getData().size());
    }

    @Test
    public void evaluate_shouldProperlyPassParametersThroughToNestedDefinition() throws Exception {

        PersonToEncounterDataDefinition encounterDef = new PersonToEncounterDataDefinition();

        PersonAttributeDataDefinition personAttributeDef = new PersonAttributeDataDefinition();
        personAttributeDef.addParameter(new Parameter("personAttributeType", "Attribute", String.class));
        encounterDef.setJoinedDefinition(personAttributeDef);

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        PersonAttributeType birthplaceType = personService.getPersonAttributeTypeByName("Birthplace");
        context.addParameterValue("personAttributeType", birthplaceType);

        context.setBaseEncounters(new EncounterIdSet(3));

        EncounterData data = encounterDataService.evaluate(encounterDef, context);

        PersonAttribute att1 = (PersonAttribute) data.getData().get(3);

        Assert.assertEquals("Paris, France", att1.getValue());
    }
}
