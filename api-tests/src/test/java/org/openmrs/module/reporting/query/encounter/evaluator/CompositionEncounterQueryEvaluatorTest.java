package org.openmrs.module.reporting.query.encounter.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.CompositionEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests the expected behavior of the CompositionCohortDefinitionEvaluator
 */
public class CompositionEncounterQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected final Log log = LogFactory.getLog(getClass());
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	public CompositionEncounterQuery getBaseDefinition() {
		CompositionEncounterQuery ccd = new CompositionEncounterQuery();
		ccd.addSearch("c1", Mapped.noMappings(new SqlEncounterQuery("select encounter_id from encounter where encounter_id in (3,4,5,6)")));
		ccd.addSearch("c2", Mapped.noMappings(new SqlEncounterQuery("select encounter_id from encounter where encounter_id in (7,8,9,10)")));
		ccd.addSearch("c3", Mapped.noMappings(new SqlEncounterQuery("select encounter_id from encounter where encounter_id in (5,6,7,8)")));
		return ccd;
	}

	public void testComposition(String compositionString, Integer...expectedIds) throws Exception {
		CompositionEncounterQuery ccd = getBaseDefinition();
		ccd.setCompositionString(compositionString);
		EncounterQueryResult r = Context.getService(EncounterQueryService.class).evaluate(ccd, new EvaluationContext());
		if (expectedIds == null) {
			Assert.assertEquals(0, r.getSize());
		}
		else {
			Assert.assertEquals(expectedIds.length, r.getSize());
			for (Integer expectedId : expectedIds) {
				Assert.assertTrue(r.contains(expectedId));
			}
		}
	}

	@Test
	public void evaluate_shouldHandleAnd() throws Exception {
		testComposition("c1 and c2");
		testComposition("c2 and c3", 7,8);
		testComposition("c1 and c3", 5,6);

	}

	@Test
	public void evaluate_shouldHandleOr() throws Exception {
		testComposition("c1 or c2", 3,4,5,6,7,8,9,10);
		testComposition("c2 or c3", 5,6,7,8,9,10);
		testComposition("c1 or c3", 3,4,5,6,7,8);
	}

	@Test
	public void evaluate_shouldHandleNot() throws Exception {
		testComposition("not c1", 7,8,9,10,11,12, 13, 14);
		testComposition("c1 and not c3", 3,4);
	}

	@Test
	public void evaluate_shouldHandleParenthesis() throws Exception {
		testComposition("(c1 or c3) and not c2", 3,4,5,6);
		testComposition("(c1 or c2) and not c3", 3,4,9,10);
	}
}
