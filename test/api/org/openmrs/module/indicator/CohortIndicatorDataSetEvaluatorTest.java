package org.openmrs.module.indicator;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.MapDataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.test.BaseModuleContextSensitiveTest;


public class CohortIndicatorDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Test
	public void shouldEvaluteIndicatorWithNoParameters() throws Exception {
		CohortDefinition female = new GenderCohortDefinition("F");
		CohortDefinition male = new GenderCohortDefinition("M");
		
		CohortDefinitionDimension gender = new CohortDefinitionDimension();
		gender.addCohortDefinition("female", female, null);
		gender.addCohortDefinition("male", male, null);
		
		InProgramCohortDefinition inProgram = new InProgramCohortDefinition();
		inProgram.setPrograms(Collections.singletonList(Context.getProgramWorkflowService().getProgram(1)));

		CohortIndicator ind = new CohortIndicator("In HIV Program", null, new Mapped<InProgramCohortDefinition>(inProgram, null), null, null);
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.addDimension("gender", new Mapped<CohortDefinitionDimension>(gender, null));
		dsd.addColumn("1", "Total in program", new Mapped<CohortIndicator>(ind, null), "");
		dsd.addColumn("1.a", "Males in program", new Mapped<CohortIndicator>(ind, null), "gender=male");
		dsd.addColumn("1.b", "Females in program", new Mapped<CohortIndicator>(ind, null), "gender=female");
		
		MapDataSet ds = (MapDataSet) Context.getService(DataSetDefinitionService.class).evaluate(dsd, null);
		
		int i = 0;
		for (DataSetRow row : ds) {
			System.out.println("Row " + (++i));
			for (Map.Entry<DataSetColumn, Object> col : row.getColumnValues().entrySet()) {
				IndicatorResult<CohortIndicator> result = (IndicatorResult<CohortIndicator>) col.getValue();
				System.out.println(col.getKey().getDisplayName() + " -> " + result.getValue());
			}
		}
	}
	
}
