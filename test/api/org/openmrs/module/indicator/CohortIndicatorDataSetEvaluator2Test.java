package org.openmrs.module.indicator;

import java.util.Map;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.ProgramStateCohortDefinition;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition2;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.test.BaseModuleContextSensitiveTest;


public class CohortIndicatorDataSetEvaluator2Test extends BaseModuleContextSensitiveTest {

	@Test
	public void shouldEvaluteIndicatorWithNoParameters() throws Exception {
		CohortDefinition female = new GenderCohortDefinition("F");
		CohortDefinition male = new GenderCohortDefinition("M");
		
		CohortDefinitionDimension gender = new CohortDefinitionDimension();
		gender.addCohortDefinition("female", female, "");
		gender.addCohortDefinition("male", male, "");
		
		ProgramStateCohortDefinition inProgram = new ProgramStateCohortDefinition();
		inProgram.setProgram(Context.getProgramWorkflowService().getProgram(1));

		CohortIndicator ind = new CohortIndicator("In HIV Program", null, new Mapped<ProgramStateCohortDefinition>(inProgram, ""), null, null);
		
		CohortIndicatorDataSetDefinition2 dsd = new CohortIndicatorDataSetDefinition2();
		dsd.addDimension("gender", new Mapped<CohortDefinitionDimension>(gender, ""));
		dsd.addColumn("1", "Total in program", new Mapped<CohortIndicator>(ind, ""), "");
		dsd.addColumn("1.a", "Males in program", new Mapped<CohortIndicator>(ind, ""), "gender=male");
		dsd.addColumn("1.b", "Females in program", new Mapped<CohortIndicator>(ind, ""), "gender=female");
		
		DataSet<?> ds = Context.getService(DataSetDefinitionService.class).evaluate(dsd, null);
		
		int i = 0;
		for (DataSetRow<?> row : ds) {
			System.out.println("Row " + (++i));
			for (Map.Entry<DataSetColumn, ?> col : row.getColumnValues().entrySet()) {
				System.out.println(col.getKey().getDisplayName() + " -> " + col.getValue());
			}
		}
	}
	
}
