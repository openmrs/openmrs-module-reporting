package org.openmrs.module.reporting.dataset.definition.evaluator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class CohortIndicatorDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see {@link CohortIndicatorDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a CohortIndicatorDataSetDefinition", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateACohortIndicatorDataSetDefinition() throws Exception {
		
		AgeCohortDefinition childrenOnDate = new AgeCohortDefinition();
		childrenOnDate.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		childrenOnDate.setMaxAge(14);
		
		AgeCohortDefinition adultsOnDate = new AgeCohortDefinition();
		adultsOnDate.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		adultsOnDate.setMinAge(15);
		
		CohortIndicator childrenAtStart = new CohortIndicator();
		childrenAtStart.addParameter(ReportingConstants.START_DATE_PARAMETER);
		childrenAtStart.addParameter(ReportingConstants.END_DATE_PARAMETER);
		childrenAtStart.setUuid(UUID.randomUUID().toString());
		childrenAtStart.setCohortDefinition(childrenOnDate, "effectiveDate=${startDate}");
		
		CohortIndicator childrenAtEnd = new CohortIndicator();
		childrenAtEnd.addParameter(ReportingConstants.START_DATE_PARAMETER);
		childrenAtEnd.addParameter(ReportingConstants.END_DATE_PARAMETER);
		childrenAtEnd.setUuid(UUID.randomUUID().toString());
		childrenAtEnd.setCohortDefinition(childrenOnDate, "effectiveDate=${endDate}");
		
		CohortIndicator adultsAtStart = new CohortIndicator();
		adultsAtStart.addParameter(ReportingConstants.START_DATE_PARAMETER);
		adultsAtStart.addParameter(ReportingConstants.END_DATE_PARAMETER);
		adultsAtStart.setUuid(UUID.randomUUID().toString());
		adultsAtStart.setCohortDefinition(adultsOnDate, "effectiveDate=${startDate}");
		
		CohortIndicator adultsAtEnd = new CohortIndicator();
		adultsAtEnd.addParameter(ReportingConstants.START_DATE_PARAMETER);
		adultsAtEnd.addParameter(ReportingConstants.END_DATE_PARAMETER);
		adultsAtEnd.setUuid(UUID.randomUUID().toString());
		adultsAtEnd.setCohortDefinition(adultsOnDate, "effectiveDate=${endDate}");
		
		Map<String, Object> periodMappings = new HashMap<String, Object>();
		periodMappings.put("startDate", "${startDate}");
		periodMappings.put("endDate", "${endDate}");
		
		CohortIndicatorDataSetDefinition d = new CohortIndicatorDataSetDefinition();
		d.addParameter(ReportingConstants.START_DATE_PARAMETER);
		d.addParameter(ReportingConstants.END_DATE_PARAMETER);
		d.addColumn("1", "Children At Start", new Mapped<CohortIndicator>(childrenAtStart, periodMappings), "");
		d.addColumn("2", "Children At End", new Mapped<CohortIndicator>(childrenAtEnd, periodMappings), "");
		d.addColumn("3", "Adults At Start", new Mapped<CohortIndicator>(adultsAtStart, periodMappings), "");
		d.addColumn("4", "Adults At End", new Mapped<CohortIndicator>(adultsAtEnd, periodMappings), "");
		
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue(ReportingConstants.START_DATE_PARAMETER.getName(), DateUtil.getDateTime(1980, 1, 1));
		context.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), DateUtil.getDateTime(2000, 1, 1));
		
		MapDataSet result = (MapDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, context);
		Assert.assertEquals(2, ((CohortIndicatorAndDimensionResult)result.getData().getColumnValue("1")).getValue());
		Assert.assertEquals(1, ((CohortIndicatorAndDimensionResult)result.getData().getColumnValue("2")).getValue());
		Assert.assertEquals(2, ((CohortIndicatorAndDimensionResult)result.getData().getColumnValue("3")).getValue());
		Assert.assertEquals(4, ((CohortIndicatorAndDimensionResult)result.getData().getColumnValue("4")).getValue());
	}
}