package org.openmrs.module.indicator;

import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.EvaluatedCohort;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.ProgramStateCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.MapDataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.test.BaseModuleContextSensitiveTest;


public class ProgramEnrollmentIndicatorTest extends BaseModuleContextSensitiveTest {

	
	private Log log = LogFactory.getLog(this.getClass());	
	
	@Override
	public Boolean useInMemoryDatabase() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Before
	public void runBeforeTest() throws Exception { 
		authenticate();		
	}


	@Test
	public void shouldGetNumberOfPatientsEnrolledInHivProgram() throws Exception {
		
		EvaluationContext evalContext = new EvaluationContext();
		Cohort baseCohort = Context.getPatientSetService().getAllPatients();
		evalContext.setBaseCohort(baseCohort);
		
		// Change definition
		ProgramStateCohortDefinition definition = new ProgramStateCohortDefinition();
		definition.setProgram(new Program(3));
		
		//CompoundCohortDefinition compound = null;
		
		EvaluatedCohort evaluatedCohort = 
			Context.getService(CohortDefinitionService.class).evaluate(definition, evalContext);
		//Context.getService(ReportService.class);
		//Context.getService(IndicatorService.class);		
		//Context.getService(DataSetDefinitionService.class);
		log.info("cohort size: " + evaluatedCohort.size());
		
		Assert.assertEquals("value should be X", evaluatedCohort.size(), 3043);
	}

	@Test
	public void shouldGetNumberOfPatientsBlah() throws Exception {

		EvaluationContext evalContext = new EvaluationContext();
		Cohort baseCohort = Context.getPatientSetService().getAllPatients();
		evalContext.setBaseCohort(baseCohort);
		
		// Change definition
		ProgramStateCohortDefinition definition = new ProgramStateCohortDefinition();
		definition.setProgram(new Program(3));
		
		EvaluatedCohort evaluatedCohort = 
			Context.getService(CohortDefinitionService.class).evaluate(definition, evalContext);
		log.info("cohort size: " + evaluatedCohort.size());
		
		Assert.assertEquals("value should be X", evaluatedCohort.size(), 3043);
	}

	
	
	@Ignore
	public void shouldDoSomething() throws Exception {
		CohortDefinition female = new GenderCohortDefinition("F");
		CohortDefinition male = new GenderCohortDefinition("M");
		
		CohortDefinitionDimension gender = new CohortDefinitionDimension();
		gender.addCohortDefinition("female", female, null);
		gender.addCohortDefinition("male", male, null);
		
		ProgramStateCohortDefinition inProgram = new ProgramStateCohortDefinition();
		inProgram.setProgram(Context.getProgramWorkflowService().getProgram(1));

		
		Mapped<ProgramStateCohortDefinition> inProgramMapped = new Mapped<ProgramStateCohortDefinition>(inProgram, null);
		CohortIndicator ind = new CohortIndicator("In HIV Program", null, inProgramMapped, null, null);
		
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
