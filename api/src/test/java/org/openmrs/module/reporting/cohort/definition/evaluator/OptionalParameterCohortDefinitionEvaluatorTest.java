package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.OptionalParameterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tests the OptionalParameterCohortDefinition
 */
public class OptionalParameterCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected final Log log = LogFactory.getLog(getClass());
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	@Autowired
	BuiltInCohortDefinitionLibrary builtInCohortDefinitionLibrary;
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	/**
	 * @see {@link OptionalParameterCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)}
	 */
	@Test
	public void evaluate_shouldSupportIntegerParameter() throws Exception {

		Cohort allPatients = cohortDefinitionService.evaluate(new AllPatientsCohortDefinition(), new EvaluationContext());
		Cohort males = cohortDefinitionService.evaluate(builtInCohortDefinitionLibrary.getMales(), new EvaluationContext());

		GenderCohortDefinition gender = new GenderCohortDefinition();
		gender.addParameter(new Parameter("maleIncluded", "Males", Boolean.class));
		gender.addParameter(new Parameter("femaleIncluded", "Females", Boolean.class));

		OptionalParameterCohortDefinition cd = new OptionalParameterCohortDefinition(gender, "maleIncluded", "femaleIncluded");
		
		EvaluationContext context = new EvaluationContext();

		context.addParameterValue("maleIncluded", Boolean.TRUE);
		Cohort test1 = cohortDefinitionService.evaluate(cd, context);
		Assert.assertEquals(allPatients.getSize(), test1.getSize());

		context.addParameterValue("femaleIncluded", Boolean.FALSE);
		Cohort test2 = cohortDefinitionService.evaluate(cd, context);
		Assert.assertEquals(males.getSize(), test2.getSize());
	}
}
