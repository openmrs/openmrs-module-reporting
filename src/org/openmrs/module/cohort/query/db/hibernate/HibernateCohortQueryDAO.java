package org.openmrs.module.cohort.query.db.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Drug;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.cohort.query.db.CohortQueryDAO;

public class HibernateCohortQueryDAO implements CohortQueryDAO {

	protected static final Log log = LogFactory.getLog(HibernateCohortQueryDAO.class);

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	
	/**
	 * 
	 * select * 
	 * from 
	 * 		patient_program pp 
	 * where
	 * 		pp.program_id in (3, 4) 
	 * and 
	 * 		(pp.date_enrolled is null or pp.date_enrolled >= '2008-01-01')
	 * and 
	 * 		(pp.date_completed is null or pp.date_completed <= '2008-12-31')
	 * 
	 * 
	 * @param programs
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public Cohort getPatientsHavingStartedPrograms(List<Program> programs, Date enrolledOnOrAfter, Date enrolledOnOrBefore) { 
		List<Integer> programIds = new ArrayList<Integer>();		
		for (Program program : programs) 
			programIds.add(program.getProgramId());			

		log.warn("programs: " + programs);
		log.warn("dates: " + enrolledOnOrAfter + " " + enrolledOnOrBefore);
		
		// Create SQL query 
		StringBuilder sql = new StringBuilder();
		sql.append("select patient_program.patient_id ");
		sql.append("from patient_program ");
		sql.append("where patient_program.voided = false ");

		// Create a list of clauses 
		if (programIds != null && !programIds.isEmpty()) { 
			sql.append(" and patient_program.program_id in (:programIds)");
		}
		if (enrolledOnOrAfter != null && enrolledOnOrBefore != null) { 
			sql.append(" and patient_program.date_enrolled between :enrolledOnOrAfter and :enrolledOnOrBefore");				
		}
		else if (enrolledOnOrAfter != null) {  
			sql.append(" and patient_program.date_enrolled >= :enrolledOnOrAfter");
		}
		else if (enrolledOnOrBefore != null) {
			sql.append(" and patient_program.date_enrolled <= :enrolledOnOrBefore");
		}
		sql.append(" group by patient_program.patient_id");
		log.warn("query: " + sql);

		// Execute query 
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		if (programIds != null && !programIds.isEmpty())
			query.setParameterList("programIds", programIds);
		if (enrolledOnOrAfter != null)
			query.setDate("enrolledOnOrAfter", enrolledOnOrAfter);
		if (enrolledOnOrBefore != null)
			query.setDate("enrolledOnOrBefore", enrolledOnOrBefore);
		
		return new Cohort(query.list());		
	}
	
	



	public Cohort getPatientsHavingCompletedPrograms(List<Program> programs,
			Date completedOnOrAfter, Date completedOnOrBefore) {

		List<Integer> programIds = new ArrayList<Integer>();		
		for (Program program : programs) 
			programIds.add(program.getProgramId());			

		// Create SQL query 
		StringBuilder sql = new StringBuilder();
		sql.append("select pp.patient_id ");
		sql.append("from patient_program pp ");
		sql.append("where pp.voided = false ");

		// Create a list of clauses 
		if (programIds != null && !programIds.isEmpty())
			sql.append(" and pp.program_id in (:programIds)");
		if (completedOnOrAfter != null) 
			sql.append(" and pp.date_completed >= :completedOnOrAfter");
		if (completedOnOrBefore != null)
			sql.append(" and pp.date_completed <= :completedOnOrBefore");

		sql.append(" group by pp.patient_id");
		log.warn("query: " + sql);

		// Execute query 
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		if (programIds != null && !programIds.isEmpty())
			query.setParameterList("programIds", programIds);
		if (completedOnOrAfter != null)
			query.setDate("completedOnOrAfter", completedOnOrAfter);
		if (completedOnOrBefore != null)
			query.setDate("completedOnOrBefore", completedOnOrBefore);
		
		return new Cohort(query.list());		
	}


	
	/**
	 * select orders.patient_id 
	 * from drug, drug_order, orders 
	 * where drug_order.order_id = orders.order_id
	 * and drug.drug_id = drug_order.drug_inventory_id
	 * and drug.drug_id IN (:drugIds)
	 * and (start_date >= '2006-01-01' and start_date <= '2006-01-31')
	 * 
	 * @param drugs
	 * @param startedOnOrAfter
	 * @param startedOnOrBefore
	 * @return
	 */
	public Cohort getPatientsHavingStartedDrugs(List<Drug> drugs,
			Date startedOnOrAfter, Date startedOnOrBefore) {
		
		
		return null;
	}

	/**
	 * select orders.patient_id 
	 * from drug, drug_order, orders 
	 * where drug_order.order_id = orders.order_id
	 * and drug.drug_id = drug_order.drug_inventory_id
	 * and drug.drug_id IN (:drugIds)
	 * and (discontinued_date >= '2006-01-01' and discontinued_date <= '2006-01-31')
	 * 
	 * @param drugs
	 * @param startedOnOrAfter
	 * @param startedOnOrBefore
	 * @return
	 */
	public Cohort getPatientsHavingCompletedDrugs(List<Drug> drugs,
			Date stoppedOnOrAfter, Date stoppedOnOrBefore) {
				
		return null;
	}
	
	
	
	public Cohort getPatientsHavingStartedStates(List<ProgramWorkflowState> states,
			Date startedOnOrAfter, Date startedOnOrBefore) {
		return getPatientsHavingChangedStates(states, "started", startedOnOrAfter, startedOnOrBefore);
	}
	
	
	public Cohort getPatientsHavingCompletedStates(List<ProgramWorkflowState> states,
			Date completedOnOrAfter, Date completedOnOrBefore) {
		return getPatientsHavingChangedStates(states, "completed", completedOnOrAfter, completedOnOrBefore);
	}	
	
	

	/**
	 * 
	 * select patient_state.* 
	 * from patient_program, patient_state, patient, person
	 * where patient_program.patient_id = patient.patient_id
	 * and person.person_id = patient.patient_id
	 * and patient_program.patient_program_id = patient_state.patient_program_id
	 * and patient_state.start_date >= '2007-06-01' and patient_state.start_date <= '2007-06-30'
	 * and patient_program.voided = false
	 * and patient_state.voided = false
	 * and patient.voided = false
	 * and person.voided = false
	 * 
	 * @param states
	 * @return
	 */
	public Cohort getPatientsHavingChangedStates(List<ProgramWorkflowState> states, String changeType, Date changedOnOrAfter, Date changedOnOrBefore) { 
		
		List<Integer> stateIds = new ArrayList<Integer>();		
		for (ProgramWorkflowState state : states) {
			stateIds.add(state.getProgramWorkflowStateId());			
		}
		
		// Change types include 'started' and 'completed'
		String changedDateColumn = (changeType.equals("started")) ? "started_date" : "completed_date";
		
		// Create SQL query 
		StringBuilder sql = new StringBuilder();		
		sql.append("select patient_program.patient_id ");
		sql.append("from patient_program, patient_state, patient, person ");
		sql.append("where patient_program.patient_id = patient.patient_id ");
		sql.append("and person.person_id = patient.patient_id ");
		sql.append("and patient_program.voided = false ");
		sql.append("and patient.voided = false ");
		sql.append("and person.voided = false ");

		// Create a list of clauses 
		if (stateIds != null && !stateIds.isEmpty())
			sql.append("and patient_state.program_workflow_state_id in (:stateIds) ");
		if (changedOnOrAfter != null) 
			sql.append("and patient_state." + changedDateColumn + " >= :changedOnOrAfter ");
		if (changedOnOrBefore != null)
			sql.append("and patient_state." + changedDateColumn + " <= :changedOnOrBefore ");
		sql.append(" group by patient_program.patient_id");
		log.warn("query: " + sql);

		// Execute query 
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		if (stateIds != null && !stateIds.isEmpty()) query.setParameterList("stateIds", stateIds);
		if (changedOnOrAfter != null) query.setDate("changedOnOrAfter", changedOnOrAfter);
		if (changedOnOrBefore != null) query.setDate("changedOnOrBefore", changedOnOrBefore);		
		return new Cohort(query.list());			
	}
	
	
	
	
	/**
	 * TODO: Fails to leave out patients who are voided Returns the set of patients that were in a
	 * given program, workflow, and state, within a given date range
	 * 
	 * @param program The program the patient must have been in
	 * @param stateList List of states the patient must have been in (implies a workflow) (can be
	 *            null)
	 * @param fromDate If not null, then only patients in the given program/workflow/state on or
	 *            after this date
	 * @param toDate If not null, then only patients in the given program/workflow/state on or
	 *            before this date
	 * @return Cohort of Patients matching criteria
	 */
	public Cohort getPatientsByProgramAndState(Program program, List<ProgramWorkflowState> stateList, Date fromDate,
	                                           Date toDate) {
		Integer programId = program == null ? null : program.getProgramId();
		List<Integer> stateIds = null;
		if (stateList != null && stateList.size() > 0) {
			stateIds = new ArrayList<Integer>();
			for (ProgramWorkflowState state : stateList)
				stateIds.add(state.getProgramWorkflowStateId());
		}
		
		List<String> clauses = new ArrayList<String>();
		clauses.add("pp.voided = false");
		if (programId != null)
			clauses.add("pp.program_id = :programId");
		if (stateIds != null) {
			clauses.add("ps.state in (:stateIds)");
			clauses.add("ps.voided = false");
		}
		if (fromDate != null) {
			clauses.add("(pp.date_completed is null or pp.date_completed >= :fromDate)");
			if (stateIds != null)
				clauses.add("(ps.end_date is null or ps.end_date >= :fromDate)");
		}
		if (toDate != null) {
			clauses.add("(pp.date_enrolled is null or pp.date_enrolled <= :toDate)");
			if (stateIds != null)
				clauses.add("(ps.start_date is null or ps.start_date <= :toDate)");
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("select pp.patient_id ");
		sql.append("from patient_program pp ");
		if (stateIds != null)
			sql.append("inner join patient_state ps on pp.patient_program_id = ps.patient_program_id ");
		for (ListIterator<String> i = clauses.listIterator(); i.hasNext();) {
			sql.append(i.nextIndex() == 0 ? " where " : " and ");
			sql.append(i.next());
		}
		sql.append(" group by pp.patient_id");
		log.debug("query: " + sql);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		if (programId != null)
			query.setInteger("programId", programId);
		if (stateIds != null)
			query.setParameterList("stateIds", stateIds);
		if (fromDate != null)
			query.setDate("fromDate", fromDate);
		if (toDate != null)
			query.setDate("toDate", toDate);
		
		return new Cohort(query.list());
	}




	
	

}
