/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.visit.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.VisitData;
import org.openmrs.module.reporting.data.visit.definition.PersonToVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.service.VisitDataService;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PersonToVisitDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    PersonService personService;

    @Autowired @Qualifier("reportingVisitDataService")
    VisitDataService visitDataService;

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
    public void evaluate_shouldReturnPatientDataForEachVisitInThePassedContext() throws Exception {

        PersonToVisitDataDefinition d = new PersonToVisitDataDefinition(new BirthdateDataDefinition());

        VisitEvaluationContext context = new VisitEvaluationContext();
        context.setBaseVisits(new VisitIdSet(3, 4));
        EvaluatedVisitData ed = visitDataService.evaluate(d, context);

        Assert.assertEquals(2, ed.getData().size());
        BirthdateConverter c = new BirthdateConverter("yyyy-MM-dd");
        Assert.assertEquals("1975-04-08", c.convert(ed.getData().get(3)));
        Assert.assertEquals("2007-05-27", c.convert(ed.getData().get(4)));
    }

    @Test
    public void evaluate_shouldReturnEmptySetIfInputSetEmpty() throws Exception {

        PersonToVisitDataDefinition d = new PersonToVisitDataDefinition(new BirthdateDataDefinition());

        VisitEvaluationContext context = new VisitEvaluationContext();
        context.setBaseVisits(new VisitIdSet());
        EvaluatedVisitData ed = visitDataService.evaluate(d, context);

        Assert.assertEquals(0, ed.getData().size());
    }

    @Test
    public void evaluate_shouldProperlyPassParametersThroughToNestedDefinition() throws Exception {

        PersonToVisitDataDefinition visitDef = new PersonToVisitDataDefinition();

        PersonAttributeDataDefinition personAttributeDef = new PersonAttributeDataDefinition();
        personAttributeDef.addParameter(new Parameter("personAttributeType", "Attribute", String.class));
        visitDef.setJoinedDefinition(personAttributeDef);

        VisitEvaluationContext context = new VisitEvaluationContext();
        PersonAttributeType birthplaceType = personService.getPersonAttributeTypeByName("Birthplace");
        context.addParameterValue("personAttributeType", birthplaceType);

        context.setBaseVisits(new VisitIdSet(1, 4));

        VisitData data = visitDataService.evaluate(visitDef, context);

        PersonAttribute att1 = (PersonAttribute) data.getData().get(1);
        PersonAttribute att2 = (PersonAttribute) data.getData().get(4);

        Assert.assertEquals("Mooresville, NC", att1.getValue());
        Assert.assertEquals("Jamaica", att2.getValue());
    }

}
