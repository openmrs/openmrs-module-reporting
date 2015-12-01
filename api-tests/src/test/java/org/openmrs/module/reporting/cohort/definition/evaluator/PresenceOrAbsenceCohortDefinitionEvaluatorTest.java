package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.PresenceOrAbsenceCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests the expected behavior of the CompositionCohortDefinitionEvaluator
 */
public class PresenceOrAbsenceCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected final Log log = LogFactory.getLog(getClass());
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	public PresenceOrAbsenceCohortDefinition getBaseDefinition() {
		PresenceOrAbsenceCohortDefinition ccd = new PresenceOrAbsenceCohortDefinition();

		return ccd;
	}

	public void testComposition(Integer min, Integer max, Integer...expectedIds) throws Exception {
		PresenceOrAbsenceCohortDefinition ccd = getBaseDefinition();
		ccd.addCohortToCheck(Mapped.noMappings(new SqlCohortDefinition("select patient_id from patient where patient_id in (2,6,7,8)")));
		ccd.addCohortToCheck(Mapped.noMappings(new SqlCohortDefinition("select patient_id from patient where patient_id in (21,22,23,24)")));
		ccd.addCohortToCheck(Mapped.noMappings(new SqlCohortDefinition("select patient_id from patient where patient_id in (7,8,21,22)")));
		ccd.setPresentInAtLeast(min);
		ccd.setPresentInAtMost(max);
		EvaluatedCohort cohort = Context.getService(CohortDefinitionService.class).evaluate(ccd, new EvaluationContext());
		if (expectedIds == null) {
			Assert.assertEquals(0, cohort.size());
		}
		else {
			Assert.assertEquals(expectedIds.length, cohort.size());
			for (Integer expectedId : expectedIds) {
				Assert.assertTrue(cohort.contains(expectedId));
			}
		}
	}

	@Test
	public void evaluate_shouldHandleAtLeast() throws Exception {
		testComposition(1, null, 2,6,7,8,21,22,23,24);
		testComposition(2, null, 7,8,21,22);
		testComposition(3, null);
	}

	@Test
	public void evaluate_shouldHandleAtMost() throws Exception {
		testComposition(1, 2, 2,6,7,8,21,22,23,24);
		testComposition(1, 1, 2,6,23,24);
	}

	@Test
	public void evaluate_shouldHandleZero() throws Exception {
		testComposition(null, 1, 2,6,20,23,24);
		testComposition(0, 1, 2,6,20,23,24);
	}
}
