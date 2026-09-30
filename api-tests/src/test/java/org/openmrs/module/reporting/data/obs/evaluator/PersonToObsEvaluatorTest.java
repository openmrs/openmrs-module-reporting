/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.obs.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.ObsData;
import org.openmrs.module.reporting.data.obs.definition.PersonToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.service.ObsDataService;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PersonToObsEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    PersonService personService;

    @Autowired @Qualifier("reportingObsDataService")
    ObsDataService obsDataService;

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
    public void evaluate_shouldReturnPersonDataByForEachObsInContext() throws Exception {
        PersonToObsDataDefinition d = new PersonToObsDataDefinition(new BirthdateDataDefinition());

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet(20, 27));
        EvaluatedObsData ed = Context.getService(ObsDataService.class).evaluate(d, context);

        Assert.assertEquals(2, ed.getData().size());
        BirthdateConverter c = new BirthdateConverter("yyyy-MM-dd");
        Assert.assertEquals("1959-06-08", c.convert(ed.getData().get(20)));
        Assert.assertEquals("1997-07-08", c.convert(ed.getData().get(27)));

    }

    @Test
    public void evaluate_shouldEmptySetIfObsSetEmtpy() throws Exception {
        PersonToObsDataDefinition d = new PersonToObsDataDefinition(new BirthdateDataDefinition());

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet());
        EvaluatedObsData ed = Context.getService(ObsDataService.class).evaluate(d, context);

        Assert.assertEquals(0, ed.getData().size());
    }

    @Test
    public void evaluate_shouldProperlyPassParametersThroughToNestedDefinition() throws Exception {

        PersonToObsDataDefinition dataDef = new PersonToObsDataDefinition();

        PersonAttributeDataDefinition personAttributeDef = new PersonAttributeDataDefinition();
        personAttributeDef.addParameter(new Parameter("personAttributeType", "Attribute", String.class));
        dataDef.setJoinedDefinition(personAttributeDef);

        ObsEvaluationContext context = new ObsEvaluationContext();
        PersonAttributeType birthplaceType = personService.getPersonAttributeTypeByName("Birthplace");
        context.addParameterValue("personAttributeType", birthplaceType);

        context.setBaseObs(new ObsIdSet(6));

        ObsData data = obsDataService.evaluate(dataDef, context);

        PersonAttribute att1 = (PersonAttribute) data.getData().get(6);
        Assert.assertEquals("Paris, France", att1.getValue());
    }
}
