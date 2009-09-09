package org.openmrs.module.dataset.definition.evaluator;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition2;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.MultiPeriodIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.MultiPeriodIndicatorDataSetDefinition.Iteration;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicatorResult;
import org.openmrs.module.indicator.PeriodCohortIndicator;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class MultiPeriodIndicatorDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link MultiPeriodIndicatorDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 * 
	 */
	@Test
	@Verifies(value = "should evaluate a data set definition", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateADataSetDefinition() throws Exception {
		// patient 6's birthdate is 2007-05-27 in the standard test dataset
		DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		Assert.assertEquals(ymd.parse("2007-05-27"), Context.getPatientService().getPatient(6).getBirthdate());
		
		AgeCohortDefinition lessThanOne = new AgeCohortDefinition();
		lessThanOne.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		lessThanOne.setMaxAge(1);
		
		PeriodCohortIndicator lessThanOneAtStart = new PeriodCohortIndicator();
		lessThanOneAtStart.setUuid(UUID.randomUUID().toString());
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("effectiveDate", "${startDate}");
		lessThanOneAtStart.setCohortDefinition(lessThanOne, mappings);
		
		Map<String, Object> periodMappings = new HashMap<String, Object>();
		periodMappings.put("startDate", "${startDate}");
		periodMappings.put("endDate", "${endDate}");
		periodMappings.put("location", "${location}");
		
		CohortIndicatorDataSetDefinition2 def = new CohortIndicatorDataSetDefinition2();
		def.addColumn("1", "Indicator", new Mapped<PeriodCohortIndicator>(lessThanOneAtStart, periodMappings), "");
		
		MultiPeriodIndicatorDataSetDefinition multi = new MultiPeriodIndicatorDataSetDefinition(def);
		// for every month in 2009, which is the year that patient 6 turns 1 year old. (Actually this
		// seems wrong but I think the underlying cohort definition is broken.)
		Assert.assertEquals(0, Calendar.JANUARY);
		Location loc = new Location(1);
		for (int i = 0; i < 12; ++i) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2009);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date startDate = cal.getTime();
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = cal.getTime();
			multi.addIteration(new Iteration(startDate, endDate, loc));
		}
		
		// make sure the number changes from 1 to 0 in June
		DataSet<?> result = Context.getService(DataSetDefinitionService.class).evaluate(multi, null);
		Date june1 = ymd.parse("2009-06-01");
		for (DataSetRow<?> row : result) {
			System.out.println("Row: " + row);
			if (((Date) row.getColumnValue("startDate")).compareTo(june1) < 0) {
				Assert.assertEquals("Should be 1 before June", 1d, ((CohortIndicatorResult) row.getColumnValue("1")).getValue().doubleValue());
			} else {
				Assert.assertEquals("Should be 0 after June", 0d, ((CohortIndicatorResult) row.getColumnValue("1")).getValue().doubleValue());
			}
		}
	}
}