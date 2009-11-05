package org.openmrs.module.cohort.query.db.hibernate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.PatientSetService.Modifier;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.cohort.query.db.CohortQueryDAO;

public class HibernateCohortQueryDAO implements CohortQueryDAO {

	protected static final Log log = LogFactory
			.getLog(HibernateCohortQueryDAO.class);

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * 
	 */
	public Cohort getPatientsHavingStartedPrograms(List<Program> programs,
			Date enrolledOnOrAfter, Date enrolledOnOrBefore) {
		return getPatientsHavingStartedOrCompletedPrograms(programs,
				"date_enrolled", enrolledOnOrAfter, enrolledOnOrBefore);
	}

	public Cohort getPatientsHavingCompletedPrograms(List<Program> programs,
			Date completedOnOrAfter, Date completedOnOrBefore) {
		return getPatientsHavingStartedOrCompletedPrograms(programs,
				"date_completed", completedOnOrAfter, completedOnOrBefore);
	}

	public Cohort getPatientsHavingStartedStates(
			List<ProgramWorkflowState> states, Date startedOnOrAfter,
			Date startedOnOrBefore) {
		return getPatientsHavingStartedOrCompletedStates(states, "start_date",
				startedOnOrAfter, startedOnOrBefore);
	}

	public Cohort getPatientsHavingCompletedStates(
			List<ProgramWorkflowState> states,

			Date completedOnOrAfter, Date completedOnOrBefore) {
		return getPatientsHavingStartedOrCompletedStates(states, "end_date",
				completedOnOrAfter, completedOnOrBefore);
	}


	/** 
	 * 
	 */
	public Cohort getPatientsHavingStartedDrugOrders(List<Drug> drugs,
			Date startedOnOrAfter, Date startedOnOrBefore) {
		return getPatientsHavingStartedOrCompletedDrugOrders(drugs,
				"start_date", startedOnOrAfter, startedOnOrBefore);
	}

	/**
	 * Gets a cohort of patients who have completed any drug orders
	 * with the given list of drugs between the given dates.
	 * 
	 * @param	drugs
	 * @param	completedOnOrAfter
	 * @param	completedOnOrBefore	
	 * @return	a cohort of patients
	 */
	public Cohort getPatientsHavingCompletedDrugOrders(List<Drug> drugs,
			Date completedOnOrAfter, Date completedOnOrBefore) {
		Cohort cohort =  getPatientsHavingStartedOrCompletedDrugOrders(drugs,
				"discontinued_date", completedOnOrAfter, completedOnOrBefore);
		return cohort;
	}


	/**
	 * 
	 * @param programs
	 * @param whichColumn
	 * @param changedOnOrAfter
	 * @param changedOnOrBefore
	 * @return
	 */
	private Cohort getPatientsHavingStartedOrCompletedPrograms(
			List<Program> programs, String whichColumn, Date changedOnOrAfter,
			Date changedOnOrBefore) {

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
			sql.append(" and pp.program_id in (:programIds) ");
		if (changedOnOrAfter != null)
			sql.append(" and pp." + whichColumn + " >= :changedOnOrAfter ");
		if (changedOnOrBefore != null)
			sql.append(" and pp." + whichColumn + " <= :changedOnOrBefore ");

		sql.append(" group by pp.patient_id ");
		log.warn("query: " + sql);

		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				sql.toString());

		log
				.debug("Patients having started or stopped programs between dates:\n "
						+ query.getQueryString());

		if (programIds != null && !programIds.isEmpty())
			query.setParameterList("programIds", programIds);
		if (changedOnOrAfter != null)
			query.setDate("changedOnOrAfter", changedOnOrAfter);
		if (changedOnOrBefore != null)
			query.setDate("changedOnOrBefore", changedOnOrBefore);

		return new Cohort(query.list());
	}

	/**
	 * select orders.patient_id from drug, drug_order, orders where
	 * drug_order.order_id = orders.order_id and drug.drug_id =
	 * drug_order.drug_inventory_id and drug.drug_id IN (:drugIds) and
	 * (discontinued_date >= '2006-01-01' and discontinued_date <= '2006-01-31')
	 * 
	 * @param drugs
	 *            the list of drugs to match against patient
	 * @param whichColumn
	 *            the start_date or discontinued_date
	 * @param changedOnOrAfter
	 *            the patient started or stopped the given drug(s) after this
	 *            date
	 * @param changedOnOrBefore
	 *            the patient started or stopped the given drug(s) before this
	 *            date
	 * @return a cohort of patients that started or stopped the given list of
	 *         drugs between the given dates
	 */
	private Cohort getPatientsHavingStartedOrCompletedDrugOrders(
			List<Drug> drugs, String whichColumn, Date changedOnOrAfter,
			Date changedOnOrBefore) {

		List<Integer> drugIds = new ArrayList<Integer>();
		for (Drug drug : drugs)
			drugIds.add(drug.getDrugId());

		// Create SQL query
		StringBuilder sql = new StringBuilder();
		sql.append("select orders.patient_id ");
		sql.append("from drug, drug_order, orders ");
		sql.append("where orders.order_id = drug_order.order_id ");
		sql.append("and drug.drug_id = drug_order.drug_inventory_id ");
		if (drugIds != null && !drugIds.isEmpty()) {
			sql.append("and drug_order.drug_inventory_id in (:drugIds) ");
		}
		sql.append("and orders." + whichColumn + " is not null ");
		if (changedOnOrAfter != null) {
			sql.append("and orders." + whichColumn + " >= :changedOnOrAfter ");
		}
		if (changedOnOrBefore != null) {
			sql.append("and orders." + whichColumn + " <= :changedOnOrBefore ");
		}
		sql.append("and drug.retired = false ");
		sql.append("and orders.voided = false ");
		sql.append("group by orders.patient_id ");
		log.warn("query: " + sql);

		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				sql.toString());

		log
				.debug("Patients having started or stopped drug orders between dates:\n "
						+ query.getQueryString());

		if (drugIds != null && !drugIds.isEmpty())
			query.setParameterList("drugIds", drugIds);
		if (changedOnOrAfter != null)
			query.setDate("changedOnOrAfter", changedOnOrAfter);
		if (changedOnOrBefore != null)
			query.setDate("changedOnOrBefore", changedOnOrBefore);
		return new Cohort(query.list());
	}

	/**
	 * 
	 * @param drugs
	 *            the list of drugs to match against patient
	 * @param asOfDate
	 *            the date up to which the patient should be on the given drugs
	 * @return a cohort of patients that started or stopped the given list of
	 *         drugs between the given dates
	 */
	public Cohort getPatientsHavingActiveDrugOrders(List<Drug> drugs,
			Date asOfDate) {
		List<Integer> drugIds = new ArrayList<Integer>();
		for (Drug drug : drugs)
			drugIds.add(drug.getDrugId());

		// Create SQL query
		StringBuilder sql = new StringBuilder();
		sql.append("select orders.patient_id ");
		sql.append("from drug, drug_order, orders ");
		sql.append("where orders.order_id = drug_order.order_id ");
		sql.append("and drug.drug_id = drug_order.drug_inventory_id ");
		if (drugIds != null && !drugIds.isEmpty()) {
			sql.append("and drug_order.drug_inventory_id in (:drugIds) ");
		}
		sql.append("and orders.start_date is not null ");
		if (asOfDate != null) {
			sql.append("and orders.start_date <= :asOfDate ");
			sql
					.append("and (orders.auto_expire_date is null or orders.auto_expire_date > :asOfDate) ");
			sql
					.append("and (orders.discontinued_date is null or orders.discontinued_date > :asOfDate) ");
		}
		sql.append("and drug.retired = false ");
		sql.append("and orders.voided = false ");
		sql.append("group by orders.patient_id ");
		log.warn("query: " + sql);

		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				sql.toString());

		log.debug("Patients having active drug orders between dates:\n "
				+ query.getQueryString());

		if (drugIds != null && !drugIds.isEmpty())
			query.setParameterList("drugIds", drugIds);
		if (asOfDate != null)
			query.setDate("asOfDate", asOfDate);
		return new Cohort(query.list());
	}

	/**
	 * 
	 * @param states
	 * @param whichColumn 
	 * @param changedOnOrAfter 
	 * @param changedOnOrBefore 
	 * @return
	 */
	public Cohort getPatientsHavingStartedOrCompletedStates(
			List<ProgramWorkflowState> states, String whichColumn,
			Date changedOnOrAfter, Date changedOnOrBefore) {

		List<Integer> stateIds = new ArrayList<Integer>();
		for (ProgramWorkflowState state : states) {
			stateIds.add(state.getProgramWorkflowStateId());
		}

		// Create SQL query
		StringBuilder sql = new StringBuilder();
		sql.append("select patient_program.patient_id ");
		sql.append("from patient_program, patient_state, patient, person ");

		// Join conditions
		sql.append("where patient_program.patient_id = patient.patient_id ");
		sql
				.append("and patient_state.patient_program_id = patient_program.patient_program_id ");
		sql.append("and person.person_id = patient.patient_id ");

		// Create a list of clauses
		if (stateIds != null && !stateIds.isEmpty())
			sql.append("and patient_state.state in (:stateIds) ");
		if (changedOnOrAfter != null)
			sql.append("and patient_state." + whichColumn
					+ " >= :changedOnOrAfter ");
		if (changedOnOrBefore != null)
			sql.append("and patient_state." + whichColumn
					+ " <= :changedOnOrBefore ");

		// Check voided
		sql.append("and patient_state.voided = false ");
		sql.append("and patient_program.voided = false ");
		sql.append("and patient.voided = false ");
		sql.append("and person.voided = false ");
		sql.append(" group by patient_program.patient_id");

		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				sql.toString());

		log
				.debug("Patients having started or completed states between dates: \n"
						+ query.getQueryString());

		if (stateIds != null && !stateIds.isEmpty())
			query.setParameterList("stateIds", stateIds);
		if (changedOnOrAfter != null)
			query.setDate("changedOnOrAfter", changedOnOrAfter);
		if (changedOnOrBefore != null)
			query.setDate("changedOnOrBefore", changedOnOrBefore);
		return new Cohort(query.list());
	}

	/**
	 * TODO: Fails to leave out patients who are voided.  
	 * 
	 * Returns the set of patients that were in a given program, 
	 * workflow, and state, within a given date range
	 * 
	 * @param program
	 *            The program the patient must have been in
	 * @param stateList
	 *            List of states the patient must have been in (implies a
	 *            workflow) (can be null)
	 * @param fromDate
	 *            If not null, then only patients in the given
	 *            program/workflow/state on or after this date
	 * @param toDate
	 *            If not null, then only patients in the given
	 *            program/workflow/state on or before this date
	 * @return Cohort of Patients matching criteria
	 */
	public Cohort getPatientsByProgramAndState(Program program,
			List<ProgramWorkflowState> stateList, Date fromDate, Date toDate) {
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
			clauses
					.add("(pp.date_completed is null or pp.date_completed >= :fromDate)");
			if (stateIds != null)
				clauses
						.add("(ps.end_date is null or ps.end_date >= :fromDate)");
		}
		if (toDate != null) {
			clauses
					.add("(pp.date_enrolled is null or pp.date_enrolled <= :toDate)");
			if (stateIds != null)
				clauses
						.add("(ps.start_date is null or ps.start_date <= :toDate)");
		}

		StringBuilder sql = new StringBuilder();
		sql.append("select pp.patient_id ");
		sql.append("from patient_program pp ");
		if (stateIds != null)
			sql
					.append("inner join patient_state ps on pp.patient_program_id = ps.patient_program_id ");
		for (ListIterator<String> i = clauses.listIterator(); i.hasNext();) {
			sql.append(i.nextIndex() == 0 ? " where " : " and ");
			sql.append(i.next());
		}
		sql.append(" group by pp.patient_id");
		log.debug("query: " + sql);

		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				sql.toString());

		log.debug("Patients having programs and states between dates: "
				+ query.getQueryString());

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

	
	/**
	 * 
	 */
	public Cohort getPatientsHavingBirthDateBetweenDates(Date bornOnOrAfter,
			Date bornOnOrBefore) throws DAOException {

		StringBuffer queryString = new StringBuffer(
				"select patientId from Patient patient");
		List<String> clauses = new ArrayList<String>();

		clauses.add("patient.voided = false");

		if (bornOnOrAfter != null) {
			clauses.add("patient.birthdate >= :bornOnOrAfter");
		}
		if (bornOnOrBefore != null) {
			clauses.add("patient.birthdate <= :bornOnOrBefore");
		}

		boolean first = true;
		for (String clause : clauses) {
			if (first) {
				queryString.append(" where ").append(clause);
				first = false;
			} else {
				queryString.append(" and ").append(clause);
			}
		}

		Query query = sessionFactory.getCurrentSession().createQuery(
				queryString.toString());

		log.debug("Patients having birthdate between dates: "
				+ query.getQueryString());

		query.setCacheMode(CacheMode.IGNORE);
		if (bornOnOrAfter != null) {
			query.setDate("bornOnOrAfter", bornOnOrAfter);
		}
		if (bornOnOrBefore != null) {
			query.setDate("bornOnOrBefore", bornOnOrBefore);
		}
		return new Cohort(query.list());

	}

	/**
	 * 
	 */
	public Cohort getPatientsHavingDiedBetweenDates(Date diedOnOrAfter,
			Date diedOnOrBefore) throws DAOException {

		StringBuffer queryString = new StringBuffer(
				"select patientId from Patient patient");
		List<String> clauses = new ArrayList<String>();

		clauses.add("patient.voided = false");
		clauses.add("patient.dead = true");

		if (diedOnOrAfter != null) {
			clauses.add("patient.deathDate >= :diedOnOrAfter");
		}
		if (diedOnOrBefore != null) {
			clauses.add("patient.deathDate <= :diedOnOrBefore");
		}

		boolean first = true;
		for (String clause : clauses) {
			if (first) {
				queryString.append(" where ").append(clause);
				first = false;
			} else {
				queryString.append(" and ").append(clause);
			}
		}
		Query query = sessionFactory.getCurrentSession().createQuery(
				queryString.toString());
		query.setCacheMode(CacheMode.IGNORE);
		if (diedOnOrAfter != null) {
			query.setDate("diedOnOrAfter", diedOnOrAfter);
		}
		if (diedOnOrBefore != null) {
			query.setDate("diedOnOrBefore", diedOnOrBefore);
		}
		log.debug("Patients having died between dates query: "
				+ query.getQueryString());

		return new Cohort(query.list());

	}

	/**
	 * 
	 */
	public Cohort getPatientsHavingObs(Integer conceptId,
			TimeModifier timeModifier, Modifier modifier, Object value,
			Date fromDate, Date toDate, List<User> providers) {

		if (conceptId == null && value == null)
			throw new IllegalArgumentException(
					"Can't have conceptId == null and value == null");
		if (conceptId == null
				&& (timeModifier != TimeModifier.ANY && timeModifier != TimeModifier.NO))
			throw new IllegalArgumentException(
					"If conceptId == null, timeModifier must be ANY or NO");
		if (conceptId == null && modifier != Modifier.EQUAL) {
			throw new IllegalArgumentException(
					"If conceptId == null, modifier must be EQUAL");
		}
		Concept concept = null;
		if (conceptId != null)
			concept = Context.getConceptService().getConcept(conceptId);

		// TODO This should be refactored out
		Number numericValue = null;
		String stringValue = null;
		Concept codedValue = null;
		Date dateValue = null;
		Boolean booleanValue = null;
		String valueSql = null;
		if (value != null) {
			if (concept == null) {
				if (value instanceof Concept)
					codedValue = (Concept) value;
				else
					codedValue = Context.getConceptService().getConceptByName(
							value.toString());
				valueSql = "o.value_coded";
			} else if (concept.getDatatype().isNumeric()) {
				if (value instanceof Number)
					numericValue = (Number) value;
				else
					numericValue = new Double(value.toString());
				valueSql = "o.value_numeric";
			} else if (concept.getDatatype().isText()) {
				stringValue = value.toString();
				valueSql = "o.value_text";
				if (modifier == null)
					modifier = Modifier.EQUAL;
			} else if (concept.getDatatype().isCoded()) {
				if (value instanceof Concept)
					codedValue = (Concept) value;
				else
					codedValue = Context.getConceptService().getConceptByName(
							value.toString());
				valueSql = "o.value_coded";
			} else if (concept.getDatatype().isDate()) {
				if (value instanceof Date) {
					dateValue = (Date) value;
				} else {
					try {
						dateValue = Context.getDateFormat().parse(
								value.toString());
					} catch (ParseException ex) {
						throw new IllegalArgumentException("Cannot interpret "
								+ dateValue + " as a date in the format "
								+ Context.getDateFormat());
					}
				}
				valueSql = "o.value_datetime";
			} else if (concept.getDatatype().isBoolean()) {
				if (value instanceof Boolean) {
					booleanValue = (Boolean) value;
				} else if (value instanceof Number) {
					numericValue = (Number) value;
					booleanValue = (numericValue.doubleValue() != 0.0) ? Boolean.TRUE
							: Boolean.FALSE;
				} else {
					booleanValue = Boolean.valueOf(value.toString());
				}
				valueSql = "o.value_numeric";
			}
		}

		StringBuilder sb = new StringBuilder();
		boolean useValue = value != null;
		boolean doSqlAggregation = timeModifier == TimeModifier.MIN
				|| timeModifier == TimeModifier.MAX
				|| timeModifier == TimeModifier.AVG;
		boolean doInvert = false;

		String dateSql = "";
		String dateSqlForSubquery = "";
		if (fromDate != null) {
			dateSql += " and o.obs_datetime >= :fromDate ";
			dateSqlForSubquery += " and obs_datetime >= :fromDate ";
		}
		if (toDate != null) {
			dateSql += " and o.obs_datetime <= :toDate ";
			dateSqlForSubquery += " and obs_datetime <= :toDate ";
		}

		if (timeModifier == TimeModifier.ANY || timeModifier == TimeModifier.NO) {
			if (timeModifier == TimeModifier.NO)
				doInvert = true;
			sb.append("select o.person_id from obs o where o.voided = false ");
			if (conceptId != null)
				sb.append("and concept_id = :concept_id ");
			sb.append(dateSql);

		} else if (timeModifier == TimeModifier.FIRST
				|| timeModifier == TimeModifier.LAST) {
			boolean isFirst = timeModifier == PatientSetService.TimeModifier.FIRST;
			sb
					.append("select o.person_id "
							+ "from obs o inner join ("
							+ "    select person_id, "
							+ (isFirst ? "min" : "max")
							+ "(obs_datetime) as obs_datetime"
							+ "    from obs"
							+ "    where voided = false and concept_id = :concept_id "
							+ dateSqlForSubquery
							+ "    group by person_id"
							+ ") subq on o.person_id = subq.person_id and o.obs_datetime = subq.obs_datetime "
							+ "where o.voided = false and o.concept_id = :concept_id ");

		} else if (doSqlAggregation) {
			String sqlAggregator = timeModifier.toString();
			valueSql = sqlAggregator + "(" + valueSql + ")";
			sb
					.append("select o.person_id "
							+ "from obs o where o.voided = false and concept_id = :concept_id "
							+ dateSql + "group by o.person_id ");

		} else {
			throw new IllegalArgumentException("TimeModifier '" + timeModifier
					+ "' not recognized");
		}

		if (useValue) {
			sb.append(doSqlAggregation ? " having " : " and ");
			sb.append(valueSql + " ");
			sb.append(modifier.getSqlRepresentation() + " :value");
		}
		if (!doSqlAggregation)
			sb.append(" group by o.person_id ");

		log.debug("query: " + sb);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				sb.toString());
		query.setCacheMode(CacheMode.IGNORE);

		if (conceptId != null)
			query.setInteger("concept_id", conceptId);
		if (useValue) {
			if (numericValue != null)
				query.setDouble("value", numericValue.doubleValue());
			else if (codedValue != null)
				query.setInteger("value", codedValue.getConceptId());
			else if (stringValue != null)
				query.setString("value", stringValue);
			else if (dateValue != null)
				query.setDate("value", dateValue);
			else if (booleanValue != null)
				query.setDouble("value", booleanValue ? 1.0 : 0.0);
			else
				throw new IllegalArgumentException(
						"useValue is true, but numeric, coded, string, boolean, and date values are all null");
		}
		if (fromDate != null)
			query.setDate("fromDate", fromDate);
		if (toDate != null)
			query.setDate("toDate", toDate);

		log.debug("Patients having obs query: " + query.getQueryString());

		Cohort ret;
		if (doInvert) {
			ret = Context.getPatientSetService().getAllPatients();
			ret.getMemberIds().removeAll(query.list());
		} else {
			ret = new Cohort(query.list());
		}

		return ret;
	}

}
