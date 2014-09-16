package org.openmrs.module.reporting.indicator;

import java.util.Collections;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.CohortDimensionResult;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;


public class CohortIndicatorDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	@Test
	public void shouldEvaluteIndicatorWithNoParameters() throws Exception {
		GenderCohortDefinition female = new GenderCohortDefinition();
		female.setFemaleIncluded(true);
		GenderCohortDefinition male = new GenderCohortDefinition();
		male.setMaleIncluded(true);
		
		CohortDefinitionDimension gender = new CohortDefinitionDimension();
		gender.addCohortDefinition("female", female, null);
		gender.addCohortDefinition("male", male, null);
		
		CohortDimensionResult results = (CohortDimensionResult)Context.getService(DimensionService.class).evaluate(gender, new EvaluationContext());
		
		InProgramCohortDefinition inProgram = new InProgramCohortDefinition();
		inProgram.setPrograms(Collections.singletonList(Context.getProgramWorkflowService().getProgram(2)));

		CohortIndicator ind = CohortIndicator.newCountIndicator("In HIV Program", new Mapped<InProgramCohortDefinition>(inProgram, null), null);
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.addDimension("gender", new Mapped<CohortDefinitionDimension>(gender, null));
		dsd.addColumn("1", "Total in program", new Mapped<CohortIndicator>(ind, null), "");
		dsd.addColumn("1.a", "Males in program", new Mapped<CohortIndicator>(ind, null), "gender=male");
		dsd.addColumn("1.b", "Females in program", new Mapped<CohortIndicator>(ind, null), "gender=female");
		
		MapDataSet ds = (MapDataSet) Context.getService(DataSetDefinitionService.class).evaluate(dsd, null);
		DataSetRow row = ds.getData();
		
		Assert.assertEquals(2, ((IndicatorResult) ds.getData().getColumnValue("1")).getValue().intValue());
		Assert.assertEquals(1, ((IndicatorResult) ds.getData().getColumnValue("1.a")).getValue().intValue());
		Assert.assertEquals(1, ((IndicatorResult) ds.getData().getColumnValue("1.b")).getValue().intValue());
	}
	
}
