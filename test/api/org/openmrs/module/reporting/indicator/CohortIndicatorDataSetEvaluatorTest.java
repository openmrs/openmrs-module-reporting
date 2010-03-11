package org.openmrs.module.reporting.indicator;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.column.DataSetColumn;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.test.BaseModuleContextSensitiveTest;


public class CohortIndicatorDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Test
	public void shouldEvaluteIndicatorWithNoParameters() throws Exception {
		GenderCohortDefinition female = new GenderCohortDefinition();
		female.setFemaleIncluded(true);
		GenderCohortDefinition male = new GenderCohortDefinition();
		male.setMaleIncluded(true);
		
		CohortDefinitionDimension gender = new CohortDefinitionDimension();
		gender.addCohortDefinition("female", female, null);
		gender.addCohortDefinition("male", male, null);
		
		InProgramCohortDefinition inProgram = new InProgramCohortDefinition();
		inProgram.setPrograms(Collections.singletonList(Context.getProgramWorkflowService().getProgram(1)));

		CohortIndicator ind = CohortIndicator.newCountIndicator("In HIV Program", new Mapped<InProgramCohortDefinition>(inProgram, null), null);
		
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
				IndicatorResult result = (IndicatorResult) col.getValue();
				System.out.println(col.getKey().getDisplayName() + " -> " + result.getValue());
			}
		}
	}
	
}
