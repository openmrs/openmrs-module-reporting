package org.openmrs.module.reporting.report.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.LogicCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.StaticCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.TextObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class CohortDefinitionServiceTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when logic cohort definition has no logic value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenLogicCohortDefinitionHasNoLogicValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		LogicCohortDefinition logicCohortDefinition = new LogicCohortDefinition();
		logicCohortDefinition.setName("Some Logic Cohort Definition");
		service.saveDefinition(logicCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when logic cohort definition has logic value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenLogicCohortDefinitionHasLogicValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		LogicCohortDefinition logicCohortDefinition = new LogicCohortDefinition();
		logicCohortDefinition.setName("Some Logic Cohort Definition");
		logicCohortDefinition.setLogic("Some Logic Value");
		service.saveDefinition(logicCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when sql cohort definition has no query value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenSqlCohortDefinitionHasNoQueryValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
		sqlCohortDefinition.setName("Some SQL Cohort Definition");
		service.saveDefinition(sqlCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when sql cohort definition has query value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenSqlCohortDefinitionHasQueryValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
		sqlCohortDefinition.setName("Some Sql Cohort Definition");
		sqlCohortDefinition.setQuery("Some Query Value");
		service.saveDefinition(sqlCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when static cohort definition has no cohort value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenStaticCohortDefinitionHasNoCohortValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		StaticCohortDefinition staticCohortDefinition = new StaticCohortDefinition();
		staticCohortDefinition.setName("Some Static Cohort Definition");
		staticCohortDefinition.setCohort(null);
		service.saveDefinition(staticCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when static cohort definition has cohort value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenStaticCohortDefinitionHasCohortValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		StaticCohortDefinition staticCohortDefinition = new StaticCohortDefinition();
		staticCohortDefinition.setName("Some Static Cohort Definition");
		Cohort cohort = new Cohort(1);
		cohort.setName("Cohort");
		staticCohortDefinition.setCohort(cohort);
		service.saveDefinition(staticCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when program enrollment cohort definition has no programs value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenProgramEnrollmentCohortDefinitionHasNoProgramsValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		ProgramEnrollmentCohortDefinition programEnrollmentCohortDefinition = new ProgramEnrollmentCohortDefinition();
		programEnrollmentCohortDefinition.setName("Some Program Enrollment Cohort Definition");
		service.saveDefinition(programEnrollmentCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when program enrollment cohort definition has empty programs value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenProgramEnrollmentCohortDefinitionHasEmptyProgramsValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		ProgramEnrollmentCohortDefinition programEnrollmentCohortDefinition = new ProgramEnrollmentCohortDefinition();
		programEnrollmentCohortDefinition.setName("Some Program Enrollment Cohort Definition");
		programEnrollmentCohortDefinition.setPrograms(new ArrayList<Program>());
		service.saveDefinition(programEnrollmentCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when program enrollment cohort definition has programs value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenProgramEnrollmentCohortDefinitionHasProgramsValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		ProgramEnrollmentCohortDefinition programEnrollmentCohortDefinition = new ProgramEnrollmentCohortDefinition();
		programEnrollmentCohortDefinition.setName("Some Program Enrollment Cohort Definition");
		List<Program> programs = new ArrayList<Program>();
		programs.add(new Program());
		programEnrollmentCohortDefinition.setPrograms(programs);
		service.saveDefinition(programEnrollmentCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when patient state cohort definition has no states value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenPatientStateCohortDefinitionHasNoStatesValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		PatientStateCohortDefinition patientStateCohortDefinition = new PatientStateCohortDefinition();
		patientStateCohortDefinition.setName("Some Patient State Cohort Definition");
		service.saveDefinition(patientStateCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when patient state cohort definition has empty states value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenPatientStateCohortDefinitionHasEmptyStatesValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		PatientStateCohortDefinition patientStateCohortDefinition = new PatientStateCohortDefinition();
		patientStateCohortDefinition.setName("Some Patient State Cohort Definition");
		patientStateCohortDefinition.setStates(new ArrayList<ProgramWorkflowState>());
		service.saveDefinition(patientStateCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when patient state cohort definition has programs value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenPatientStateCohortDefinitionHasStatesValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		PatientStateCohortDefinition patientStateCohortDefinition = new PatientStateCohortDefinition();
		patientStateCohortDefinition.setName("Some Patient State Cohort Definition");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states.add(new ProgramWorkflowState());
		patientStateCohortDefinition.setStates(states);
		service.saveDefinition(patientStateCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when in state cohort definition has no states value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenInStateCohortDefinitionHasNoStatesValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		InStateCohortDefinition inStateCohortDefinition = new InStateCohortDefinition();
		inStateCohortDefinition.setName("Some In State Cohort Definition");
		service.saveDefinition(inStateCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when in state cohort definition has empty states value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenInStateCohortDefinitionHasEmptyStatesValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		InStateCohortDefinition inStateCohortDefinition = new InStateCohortDefinition();
		inStateCohortDefinition.setName("Some In State Cohort Definition");
		inStateCohortDefinition.setStates(new ArrayList<ProgramWorkflowState>());
		service.saveDefinition(inStateCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when in state cohort definition has programs value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenInStateCohortDefinitionHasStatesValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		InStateCohortDefinition inStateCohortDefinition = new InStateCohortDefinition();
		inStateCohortDefinition.setName("Some In State Cohort Definition");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states.add(new ProgramWorkflowState());
		inStateCohortDefinition.setStates(states);
		service.saveDefinition(inStateCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when inverse cohort definition has no baseDefinition value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenInverseCohortDefinitionHasNoBaseDefinitionValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		InverseCohortDefinition inverseCohortDefinition = new InverseCohortDefinition();
		inverseCohortDefinition.setName("Some Inverse Cohort Definition");
		service.saveDefinition(inverseCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when inverse cohort definition has baseDefinition value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenInverseCohortDefinitionHasBaseDefinitionValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		InverseCohortDefinition inverseCohortDefinition = new InverseCohortDefinition();
		inverseCohortDefinition.setName("Some Inverse Cohort Definition");
		inverseCohortDefinition.setBaseDefinition(new SqlCohortDefinition());
		service.saveDefinition(inverseCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when composition cohort definition has no searches value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenCompositionCohortDefinitionHasNoSearchesValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.setName("Some Composition Cohort Definition");
		compositionCohortDefinition.setCompositionString("Some Composition String");
		service.saveDefinition(compositionCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when composition cohort definition has no compostion string value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenCompositionCohortDefinitionHasNoCompostitionStringValue() throws Exception {
		HashMap<String, Mapped<CohortDefinition>> searches = new HashMap<String, Mapped<CohortDefinition>>();
		searches.put("Some Key", new Mapped());
		
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.setSearches(searches);
		compositionCohortDefinition.setCompositionString(null);
		
		service.saveDefinition(compositionCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when composition cohort definition has empty searches value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenCompositionCohortDefinitionHasEmptyStatesValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.setName("Some Composition Cohort Definition");
		compositionCohortDefinition.setSearches(new HashMap<String, Mapped<CohortDefinition>>());
		compositionCohortDefinition.setCompositionString("Some Composition String");
		service.saveDefinition(compositionCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when composition cohort definition has empty composition string value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenCompositionCohortDefinitionHasEmptyCompositionStringValue() throws Exception {
		HashMap<String, Mapped<CohortDefinition>> searches = new HashMap<String, Mapped<CohortDefinition>>();
		searches.put("Some Key", new Mapped());
		
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.setName("Some Composition Cohort Definition");
		compositionCohortDefinition.setSearches(searches);
		compositionCohortDefinition.setCompositionString(" ");
		service.saveDefinition(compositionCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when composition cohort definition has states and composition string values", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenCompositionCohortDefinitionHasStatesValue() throws Exception {
		HashMap<String, Mapped<CohortDefinition>> searches = new HashMap<String, Mapped<CohortDefinition>>();
		searches.put("Some Key", new Mapped());
		
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.setName("Some Composition Cohort Definition");
		compositionCohortDefinition.setSearches(searches);
		compositionCohortDefinition.setCompositionString("Some Composition String");
		service.saveDefinition(compositionCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when in program cohort definition has no programs value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenInProgramCohortDefinitionHasNoProgramsValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		InProgramCohortDefinition inProgramCohortDefinition = new InProgramCohortDefinition();
		inProgramCohortDefinition.setName("Some In Program Cohort Definition");
		service.saveDefinition(inProgramCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when in program cohort definition has empty programs value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenInProgramCohortDefinitionHasEmptyProgramsValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		InProgramCohortDefinition inProgramCohortDefinition = new InProgramCohortDefinition();
		inProgramCohortDefinition.setName("Some In Program Cohort Definition");
		inProgramCohortDefinition.setPrograms(new ArrayList<Program>());
		service.saveDefinition(inProgramCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when in program cohort definition has programs value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenInProgramCohortDefinitionHasProgramsValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		InProgramCohortDefinition inProgramCohortDefinition = new InProgramCohortDefinition();
		inProgramCohortDefinition.setName("Some In Program Cohort Definition");
		List<Program> programs = new ArrayList<Program>();
		programs.add(new Program());
		inProgramCohortDefinition.setPrograms(programs);
		service.saveDefinition(inProgramCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when coded obs cohort definition has no time modifier value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenCodedObsCohortDefinitionHasNoTimeModifierValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		CodedObsCohortDefinition codedObsCohortDefinition = new CodedObsCohortDefinition();
		codedObsCohortDefinition.setTimeModifier(null);
		codedObsCohortDefinition.setQuestion(new Concept(10));
		service.saveDefinition(codedObsCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when coded obs cohort definition has no question value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenCodedObsCohortDefinitionHasNoQuestionValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		CodedObsCohortDefinition codedObsCohortDefinition = new CodedObsCohortDefinition();
		codedObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		codedObsCohortDefinition.setQuestion(null);
		service.saveDefinition(codedObsCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when coded obs cohort definition has time modifier and question values", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenCodedObsCohortDefinitionHasTimeModifierAndQuestionValues() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		CodedObsCohortDefinition codedObsCohortDefinition = new CodedObsCohortDefinition();
		codedObsCohortDefinition.setName("Coded Obs Cohort Definition");
		codedObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		codedObsCohortDefinition.setQuestion(Context.getConceptService().getConcept(10));
		service.saveDefinition(codedObsCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when date obs cohort definition has no time modifier value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenDateObsCohortDefinitionHasNoTimeModifierValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		DateObsCohortDefinition dateObsCohortDefinition = new DateObsCohortDefinition();
		dateObsCohortDefinition.setTimeModifier(null);
		dateObsCohortDefinition.setQuestion(new Concept(10));
		service.saveDefinition(dateObsCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when date obs cohort definition has no question value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenDateObsCohortDefinitionHasNoQuestionValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		DateObsCohortDefinition dateObsCohortDefinition = new DateObsCohortDefinition();
		dateObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		dateObsCohortDefinition.setQuestion(null);
		service.saveDefinition(dateObsCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when date obs cohort definition has time modifier and question values", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenDateObsCohortDefinitionHasTimeModifierAndQuestionValues() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		DateObsCohortDefinition dateObsCohortDefinition = new DateObsCohortDefinition();
		dateObsCohortDefinition.setName("Date Obs Cohort Definition");
		dateObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		dateObsCohortDefinition.setQuestion(Context.getConceptService().getConcept(10));
		service.saveDefinition(dateObsCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when numeric obs cohort definition has no time modifier value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenNumericObsCohortDefinitionHasNoTimeModifierValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		NumericObsCohortDefinition numericObsCohortDefinition = new NumericObsCohortDefinition();
		numericObsCohortDefinition.setTimeModifier(null);
		numericObsCohortDefinition.setQuestion(new Concept(10));
		service.saveDefinition(numericObsCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when numeric obs cohort definition has no question value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenNumericObsCohortDefinitionHasNoQuestionValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		NumericObsCohortDefinition numericObsCohortDefinition = new NumericObsCohortDefinition();
		numericObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		numericObsCohortDefinition.setQuestion(null);
		service.saveDefinition(numericObsCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when numeric obs cohort definition has time modifier and question values", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenNumericObsCohortDefinitionHasTimeModifierAndQuestionValues() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		NumericObsCohortDefinition numericObsCohortDefinition = new NumericObsCohortDefinition();
		numericObsCohortDefinition.setName("Numeric Obs Cohort Definition");
		numericObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		numericObsCohortDefinition.setQuestion(Context.getConceptService().getConcept(10));
		service.saveDefinition(numericObsCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when text obs cohort definition has no time modifier value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenTextObsCohortDefinitionHasNoTimeModifierValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		TextObsCohortDefinition textObsCohortDefinition = new TextObsCohortDefinition();
		textObsCohortDefinition.setTimeModifier(null);
		textObsCohortDefinition.setQuestion(new Concept(10));
		service.saveDefinition(textObsCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test(expected = APIException.class)
	@Verifies(value = "should fail when text obs cohort definition has no question value", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldFailWhenTextObsCohortDefinitionHasNoQuestionValue() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		TextObsCohortDefinition textObsCohortDefinition = new TextObsCohortDefinition();
		textObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		textObsCohortDefinition.setQuestion(null);
		service.saveDefinition(textObsCohortDefinition);
	}
	
	/**
	 * @see {@link CohortDefinitionService#saveDefinition(CohortDefinition)}
	 */
	@Test
	@Verifies(value = "should pass when text obs cohort definition has time modifier and question values", method = "saveDefinition(CohortDefinition)")
	public void saveDefinition_shouldPassWhenTextObsCohortDefinitionHasTimeModifierAndQuestionValues() throws Exception {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		TextObsCohortDefinition textObsCohortDefinition = new TextObsCohortDefinition();
		textObsCohortDefinition.setName("Text Obs Cohort Definition");
		textObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		textObsCohortDefinition.setQuestion(Context.getConceptService().getConcept(10));
		service.saveDefinition(textObsCohortDefinition);
	}
}
