package org.openmrs.module.reporting.cohort.query.db.hibernate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.PatientSetService.Modifier;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO;
import org.openmrs.module.reporting.common.DurationUnit;

public class HibernateCohortQueryDAO implements CohortQueryDAO {

	protected static final Log log = LogFactory.getLog(HibernateCohortQueryDAO.class);

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public Cohort getPatientsWithGender(boolean includeMales, boolean includeFemales, boolean includeUnknownGender) {
		
		if (!includeMales && !includeFemales && !includeUnknownGender) {
			return new Cohort();
		}
		
		String prefixTerm = "";
		StringBuilder query = new StringBuilder("select patientId from Patient patient where patient.voided = false and ( ");
		if (includeMales) {
			query.append(" patient.gender = 'M' ");
			prefixTerm = " or";
		}
		if (includeFemales) {
			query.append(prefixTerm + " patient.gender = 'F'");
			prefixTerm = " or";
		}
		if (includeUnknownGender) {
			query.append(prefixTerm + " patient.gender is null or (patient.gender != 'M' and patient.gender != 'F')");
		}
		query.append(")");
		Query q = sessionFactory.getCurrentSession().createQuery(query.toString());
		q.setCacheMode(CacheMode.IGNORE);
		return new Cohort(q.list());
	}
	
	public Cohort getPatientsWithAgeRange(Integer minAge, DurationUnit minAgeUnit, Integer maxAge, DurationUnit maxAgeUnit, boolean unknownAgeIncluded, Date effectiveDate) {
		
		if (effectiveDate == null) {
			effectiveDate = new Date();
		}
		if (minAgeUnit == null) {
			minAgeUnit = DurationUnit.YEARS;
		}
		if (maxAgeUnit == null) {
			maxAgeUnit = DurationUnit.YEARS;
		}
		
		String sql = "select t.patient_id from patient t, person p where t.patient_id = p.person_id and t.voided = false and ";
		Map<String, Date> paramsToSet = new HashMap<String, Date>();
		
		Date maxBirthFromAge = effectiveDate;
		if (minAge != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(effectiveDate);
			cal.add(minAgeUnit.getCalendarField(), -minAgeUnit.getFieldQuantity()*minAge);
			maxBirthFromAge = cal.getTime();
		}
		
		String c = "p.birthdate <= :maxBirthFromAge";
		paramsToSet.put("maxBirthFromAge", maxBirthFromAge);
		
		Date minBirthFromAge = null;
		if (maxAge != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(effectiveDate);
			cal.add(maxAgeUnit.getCalendarField(), -(maxAgeUnit.getFieldQuantity()*maxAge + 1));
			minBirthFromAge = cal.getTime();
			c = "(" + c + " and p.birthdate >= :minBirthFromAge)";
			paramsToSet.put("minBirthFromAge", minBirthFromAge);
		}
			
		if (unknownAgeIncluded) {
			c = "(p.birthdate is null or " + c + ")";
		}
		
		sql += c;
		
		log.debug("Executing: " + sql + " with params: " + paramsToSet);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
		for (Map.Entry<String, Date> entry : paramsToSet.entrySet()) {
			query.setDate(entry.getKey(), entry.getValue());
		}
		
		return new Cohort(query.list());
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
			Date fromDate, Date toDate, List<User> providers, EncounterType encounterType) {

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
		
		String encounterSql = "";
		String encounterSqlTable = "";
		String encounterJoin = "";
		String encounterSqlForSubquery = "";
		if(encounterType != null) {
			encounterSqlTable = ", encounter e ";
			encounterJoin = " and e.encounter_id = o.encounter_id ";
			encounterSql += " and e.encounter_type = :encounterType ";
			encounterSqlForSubquery = " inner join encounter e on e.encounter_id = o.encounter_id ";
		}

		if (timeModifier == TimeModifier.ANY || timeModifier == TimeModifier.NO) {
			if (timeModifier == TimeModifier.NO)
				doInvert = true;
			sb.append("select o.person_id from obs o "+encounterSqlTable+" where o.voided = false ");
			if (conceptId != null)
				sb.append("and o.concept_id = :concept_id ");
			
			sb.append(encounterJoin);
			sb.append(encounterSql);
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
							+ "    from obs o"
							+ "    where o.voided = false and o.concept_id = :concept_id "
							+ dateSqlForSubquery
							+ "    group by person_id"
							+ ") subq on o.person_id = subq.person_id and o.obs_datetime = subq.obs_datetime "
							+ encounterSqlForSubquery
							+ "where o.voided = false and o.concept_id = :concept_id " 
							+ encounterSql);

		} else if (doSqlAggregation) {
			String sqlAggregator = timeModifier.toString();
			valueSql = sqlAggregator + "(" + valueSql + ")";
			sb
					.append("select o.person_id "
							+ "from obs o "+encounterSqlTable+" where o.voided = false and o.concept_id = :concept_id "
							+ dateSql + encounterJoin + encounterSql + "group by o.person_id ");

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
		if (encounterType != null)
			query.setInteger("encounterType", encounterType.getEncounterTypeId());

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

	/**
     * @see org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO#getPatientsHavingProgramEnrollment(java.util.List, java.util.Date, java.util.Date, java.util.Date, java.util.Date)
     */
    public Cohort getPatientsHavingProgramEnrollment(List<Program> programs, Date enrolledOnOrAfter,
                                                     Date enrolledOnOrBefore, Date completedOnOrAfter,
                                                     Date completedOnOrBefore) {
		List<Integer> programIds = new ArrayList<Integer>();
		for (Program program : programs)
			programIds.add(program.getProgramId());

		// Create SQL query
		StringBuilder sql = new StringBuilder();
		sql.append("select pp.patient_id ");
		sql.append("from patient_program pp ");
		sql.append("  inner join patient p on pp.patient_id = p.patient_id ");
		sql.append("where pp.voided = false and p.voided = false ");

		// Create a list of clauses
		if (programIds != null && !programIds.isEmpty())
			sql.append(" and pp.program_id in (:programIds) ");
		if (enrolledOnOrAfter != null)
			sql.append(" and pp.date_enrolled >= :enrolledOnOrAfter ");
		if (enrolledOnOrBefore != null)
			sql.append(" and pp.date_enrolled <= :enrolledOnOrBefore ");
		if (completedOnOrAfter != null)
			sql.append(" and pp.date_completed >= :completedOnOrAfter ");
		if (completedOnOrBefore != null)
			sql.append(" and pp.date_completed <= :completedOnOrBefore ");

		sql.append(" group by pp.patient_id ");
		log.debug("query: " + sql);

		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		if (programIds != null && !programIds.isEmpty())
			query.setParameterList("programIds", programIds);
		if (enrolledOnOrAfter != null)
			query.setDate("enrolledOnOrAfter", enrolledOnOrAfter);
		if (enrolledOnOrBefore != null)
			query.setDate("enrolledOnOrBefore", enrolledOnOrBefore);
		if (completedOnOrAfter != null)
			query.setDate("completedOnOrAfter", completedOnOrAfter);
		if (completedOnOrBefore != null)
			query.setDate("completedOnOrBefore", completedOnOrBefore);

		return new Cohort(query.list());
    }

    
	/**
     * @see org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO#getPatientsInProgram(java.util.List, java.util.Date, java.util.Date)
     */
    public Cohort getPatientsInProgram(List<Program> programs, Date onOrAfter, Date onOrBefore) {
		List<Integer> programIds = new ArrayList<Integer>();
		for (Program program : programs)
			programIds.add(program.getProgramId());

		// Create SQL query
		StringBuilder sql = new StringBuilder();
		sql.append("select pp.patient_id ");
		sql.append("from patient_program pp ");
		sql.append("  inner join patient p on pp.patient_id = p.patient_id ");
		sql.append("where pp.voided = false and p.voided = false ");

		// optional clauses
		if (programIds != null && !programIds.isEmpty())
			sql.append(" and pp.program_id in (:programIds) ");
		if (onOrAfter != null)
			sql.append(" and (pp.date_completed is null or pp.date_completed >= :onOrAfter) ");
		if (onOrBefore != null)
			sql.append(" and (pp.date_enrolled is null or pp.date_enrolled <= :onOrBefore) ");
		
		sql.append(" group by pp.patient_id ");
		log.debug("query: " + sql);

		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		if (programIds != null && !programIds.isEmpty())
			query.setParameterList("programIds", programIds);
		if (onOrAfter != null)
			query.setDate("onOrAfter", onOrAfter);
		if (onOrBefore != null)
			query.setDate("onOrBefore", onOrBefore);
		return new Cohort(query.list()); 
	}

	/**
	 * @see org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO#getPatientsHavingStates(java.util.List, java.util.Date, java.util.Date, java.util.Date, java.util.Date)
	 */
	public Cohort getPatientsHavingStates(List<ProgramWorkflowState> states,
	                                      Date startedOnOrAfter, Date startedOnOrBefore,
                                          Date endedOnOrAfter, Date endedOnOrBefore) {
		List<Integer> stateIds = new ArrayList<Integer>();
		for (ProgramWorkflowState state : states)
			stateIds.add(state.getId());

		// Create SQL query
		StringBuilder sql = new StringBuilder();
		sql.append("select pp.patient_id ");
		sql.append("from patient_state ps ");
		sql.append("  inner join patient_program pp on ps.patient_program_id = pp.patient_program_id ");
		sql.append("  inner join patient p on pp.patient_id = p.patient_id ");
		sql.append("where ps.voided = false and pp.voided = false and p.voided = false ");
		
		// Create a list of clauses
		if (stateIds != null && !stateIds.isEmpty())
			sql.append(" and ps.state in (:stateIds) ");
		if (startedOnOrAfter != null)
			sql.append(" and ps.start_date >= :startedOnOrAfter ");
		if (startedOnOrBefore != null)
			sql.append(" and ps.start_date <= :startedOnOrBefore ");
		if (endedOnOrAfter != null)
			sql.append(" and ps.end_date >= :endedOnOrAfter ");
		if (endedOnOrBefore != null)
			sql.append(" and ps.end_date <= :endedOnOrBefore ");

		sql.append(" group by pp.patient_id ");
		log.debug("query: " + sql);

		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		if (stateIds != null && !stateIds.isEmpty())
			query.setParameterList("stateIds", stateIds);
		if (startedOnOrAfter != null)
			query.setDate("startedOnOrAfter", startedOnOrAfter);
		if (startedOnOrBefore != null)
			query.setDate("startedOnOrBefore", startedOnOrBefore);
		if (endedOnOrAfter != null)
			query.setDate("endedOnOrAfter", endedOnOrAfter);
		if (endedOnOrBefore != null)
			query.setDate("endedOnOrBefore", endedOnOrBefore);

		return new Cohort(query.list());
    }

	/**
	 * @see org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO#getPatientsInStates(java.util.List, java.util.Date, java.util.Date)
	 */
	public Cohort getPatientsInStates(List<ProgramWorkflowState> states, Date onOrAfter, Date onOrBefore) {
		List<Integer> stateIds = new ArrayList<Integer>();
		for (ProgramWorkflowState state : states)
			stateIds.add(state.getId());

		// Create SQL query
		StringBuilder sql = new StringBuilder();
		sql.append("select pp.patient_id ");
		sql.append("from patient_state ps ");
		sql.append("  inner join patient_program pp on ps.patient_program_id = pp.patient_program_id ");
		sql.append("  inner join patient p on pp.patient_id = p.patient_id ");
		sql.append("where ps.voided = false and pp.voided = false and p.voided = false ");

		// optional clauses
		if (stateIds != null && !stateIds.isEmpty())
			sql.append(" and ps.state in (:stateIds) ");
		if (onOrAfter != null)
			sql.append(" and (ps.end_date is null or ps.end_date >= :onOrAfter) ");
		if (onOrBefore != null)
			sql.append(" and (ps.start_date is null or ps.start_date <= :onOrBefore) ");
		
		sql.append(" group by pp.patient_id ");
		log.debug("query: " + sql);

		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		if (stateIds != null && !stateIds.isEmpty())
			query.setParameterList("stateIds", stateIds);
		if (onOrAfter != null)
			query.setDate("onOrAfter", onOrAfter);
		if (onOrBefore != null)
			query.setDate("onOrBefore", onOrBefore);
		return new Cohort(query.list()); 
    }

	public Cohort getPatientsHavingNumericObs(TimeModifier timeModifier, Concept question, Concept groupingConcept,
                                              Date onOrAfter, Date onOrBefore,
                                              List<Location> locationList, List<EncounterType> encounterTypeList,
                                              Modifier modifier1, Double value1,
                                              Modifier modifier2, Double value2) {

		Integer questionConceptId = question == null ? null : question.getId();
		Integer groupingConceptId = groupingConcept == null ? null : groupingConcept.getId();
		if (groupingConceptId != null)
			throw new RuntimeException("grouping concept not yet implemented");

		List<Integer> locationIds = null;
		if (locationList != null && locationList.size() > 0) {
			locationIds = new ArrayList<Integer>();
			for (Location l : locationList)
				locationIds.add(l.getId());
		}
		
		List<Integer> encounterTypeIds = null;
		if (encounterTypeList != null && encounterTypeList.size() > 0) {
			encounterTypeIds = new ArrayList<Integer>();
			for (EncounterType t : encounterTypeList)
				encounterTypeIds.add(t.getId());
		}
		
		String dateAndLocationSql = "";
		String dateAndLocationSqlForSubquery = "";
		if (onOrAfter != null) {
			dateAndLocationSql += " and o.obs_datetime >= :onOrAfter ";
			dateAndLocationSqlForSubquery += " and obs_datetime >= :onOrAfter ";
		}
		if (onOrBefore != null) {
			dateAndLocationSql += " and o.obs_datetime <= :onOrBefore ";
			dateAndLocationSqlForSubquery += " and obs_datetime <= :onOrBefore ";
		}
		if (locationIds != null) {
			dateAndLocationSql += " and o.location_id in (:locationIds) ";
			dateAndLocationSqlForSubquery += " and location_id in (:locationIds) ";
		}
		if (encounterTypeIds != null)
			throw new RuntimeException("encounter types not yet handled in getPatientsHavingNumericObs"); // should probably go with date and location, not with value
		
		boolean doSqlAggregation = timeModifier == TimeModifier.MIN || timeModifier == TimeModifier.MAX || timeModifier == TimeModifier.AVG;
		boolean doInvert = timeModifier == TimeModifier.NO;

		String valueSql = " o.value_numeric ";
		if (doSqlAggregation) {
			valueSql = " " + timeModifier.toString() + "(" + valueSql + ") ";
		}
		
		List<String> valueClauses = new ArrayList<String>();
		if (value1 != null)
			valueClauses.add(valueSql + modifier1.getSqlRepresentation() + " :value1 ");
		if (value2 != null)
			valueClauses.add(valueSql + modifier2.getSqlRepresentation() + " :value2 ");
		
		StringBuilder sql = new StringBuilder();

		if (timeModifier == TimeModifier.ANY || timeModifier == TimeModifier.NO) {
			sql.append(" select distinct o.person_id from obs o where o.voided = false ");
			if (questionConceptId != null)
				sql.append(" and concept_id = :questionConceptId ");
			sql.append(dateAndLocationSql);

		} else if (timeModifier == TimeModifier.FIRST || timeModifier == TimeModifier.LAST) {
			boolean isFirst = timeModifier == PatientSetService.TimeModifier.FIRST;
			
			sql.append(" select distinct o.person_id ");
			sql.append(" from obs o ");
			sql.append(" inner join ( ");
			sql.append("    select person_id, " + (isFirst ? "MIN" : "MAX") + "(obs_datetime) as odt ");
			sql.append("    from obs where voided = false and concept_id = :questionConceptId " + dateAndLocationSqlForSubquery + " group by person_id ");
			sql.append(" ) subq on o.person_id = subq.person_id and o.obs_datetime = subq.odt ");
			sql.append(" where o.voided = false and o.concept_id = :questionConceptId ");

		} else if (doSqlAggregation) {
			sql.append(" select distinct o.person_id ");
			sql.append(" from obs o where o.voided = false and concept_id = :questionConceptId " + dateAndLocationSql );
			sql.append(" group by o.person_id ");
			
		} else {
			throw new IllegalArgumentException("TimeModifier '" + timeModifier + "' not recognized");
		}
		
		if (valueClauses.size() > 0) {
			sql.append(doSqlAggregation ? " having " : " and ");
			for (Iterator<String> i = valueClauses.iterator(); i.hasNext(); ) {
				sql.append(i.next());
				if (i.hasNext())
					sql.append(" and ");
			}
		}
		
		log.debug("sql: " + sql);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setCacheMode(CacheMode.IGNORE);
		
		if (questionConceptId != null)
			query.setInteger("questionConceptId", questionConceptId);
		if (value1 != null)
			query.setDouble("value1", value1);
		if (value2 != null)
			query.setDouble("value2", value2);
		if (onOrAfter != null)
			query.setDate("onOrAfter", onOrAfter);
		if (onOrBefore != null)
			query.setDate("onOrBefore", onOrBefore);
		if (locationIds != null)
			query.setParameterList("locationIds", locationIds);
		
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
