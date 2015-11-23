package org.openmrs.module.reporting.cohort.query.db.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.OpenmrsObject;
import org.openmrs.Person;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.PatientSetService.Modifier;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.reporting.IllegalDatabaseAccessException;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterException;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.report.util.SqlUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            ret = Cohorts.allPatients(null);
			ret.getMemberIds().removeAll(query.list());
		} else {
			ret = new Cohort(query.list());
		}

		return ret;
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
			query.setTimestamp("onOrAfter", onOrAfter);
		if (onOrBefore != null)
			query.setTimestamp("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(onOrBefore));
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
			query.setTimestamp("startedOnOrAfter", startedOnOrAfter);
		if (startedOnOrBefore != null)
			query.setTimestamp("startedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(startedOnOrBefore));
		if (endedOnOrAfter != null)
			query.setTimestamp("endedOnOrAfter", endedOnOrAfter);
		if (endedOnOrBefore != null)
			query.setTimestamp("endedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(endedOnOrBefore));

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
			query.setTimestamp("onOrAfter", onOrAfter);
		if (onOrBefore != null)
			query.setTimestamp("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(onOrBefore));
		return new Cohort(query.list()); 
    }

	/**
	 * @see org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO#getPatientsHavingRangedObs(org.openmrs.api.PatientSetService.TimeModifier, org.openmrs.Concept, org.openmrs.Concept, java.util.Date, java.util.Date, java.util.List, java.util.List, org.openmrs.module.reporting.common.RangeComparator, java.lang.Object, org.openmrs.module.reporting.common.RangeComparator, java.lang.Object)
	 */
	public Cohort getPatientsHavingRangedObs(TimeModifier timeModifier, Concept question, Concept groupingConcept,
                                              Date onOrAfter, Date onOrBefore,
                                              List<Location> locationList, List<EncounterType> encounterTypeList,
                                              RangeComparator operator1, Object value1,
                                              RangeComparator operator2, Object value2) {
		
		return getPatientsHavingObs(timeModifier, question, groupingConcept, onOrAfter, onOrBefore, locationList, encounterTypeList, operator1, value1, operator2, value2, null, null);
	}
	
	/**
	 * Encapsulates the common logic between getPatientsHavingRangedObs and getPatientsHavingDiscreteObs
	 * 
	 * The arguments passed in fall into two types:
	 * <ol>
	 *     <li>arguments that limit which obs we will look at</li>
	 *     <ul>
	 *         <li>timeModifier</li>
	 *         <li>question</li>
	 *         <li>groupingConcept</li>
	 *         <li>onOrAfter</li>
	 *         <li>onOrBefore</li>
	 *         <li>locationList</li>
	 *         <li>encounterTypeList</li>
	 *         <li></li>
	 *         <li></li>
	 *     </ul>
	 *     <li>arguments that the obs values must match after being limited by the above arguments</li>
	 *     <ul>
	 *         <li>operator1</li>
	 *         <li>value1</li>
	 *         <li>operator2</li>
	 *         <li>value2</li>
	 *         <li>setOperator</li>
	 *         <li>valueList</li>
	 *     </ul>
	 * </ol> 
	 * 
	 * @param timeModifier
	 * @param question
	 * @param groupingConcept
	 * @param onOrAfter
	 * @param onOrBefore
	 * @param locationList
	 * @param encounterTypeList
	 * @param operator1
	 * @param value1
	 * @param operator2
	 * @param value2
	 * @param setOperator
	 * @param valueList
	 * @return
	 */
	private Cohort getPatientsHavingObs(TimeModifier timeModifier, Concept question, Concept groupingConcept,
	                                    Date onOrAfter, Date onOrBefore,
	                                    List<Location> locationList, List<EncounterType> encounterTypeList,
	                                    RangeComparator operator1, Object value1,
	                                    RangeComparator operator2, Object value2,
	                                    SetComparator setOperator, List<? extends Object> valueList) {

		Integer questionConceptId = question == null ? null : question.getId();
		Integer groupingConceptId = groupingConcept == null ? null : groupingConcept.getId();
		if (groupingConceptId != null)
			throw new RuntimeException("grouping concept not yet implemented");

		List<Integer> locationIds = SqlUtils.openmrsObjectIdListHelper(locationList);
		List<Integer> encounterTypeIds = SqlUtils.openmrsObjectIdListHelper(encounterTypeList);
		
		boolean joinOnEncounter = encounterTypeIds != null;
		String dateAndLocationSql = ""; // TODO rename to include encounterType
		String dateAndLocationSqlForSubquery = "";
		if (onOrAfter != null) {
			dateAndLocationSql += " and o.obs_datetime >= :onOrAfter ";
			dateAndLocationSqlForSubquery += " and obs.obs_datetime >= :onOrAfter ";
		}
		if (onOrBefore != null) {
			dateAndLocationSql += " and o.obs_datetime <= :onOrBefore ";
			dateAndLocationSqlForSubquery += " and obs.obs_datetime <= :onOrBefore ";
		}
		if (locationIds != null) {
			dateAndLocationSql += " and o.location_id in (:locationIds) ";
			dateAndLocationSqlForSubquery += " and obs.location_id in (:locationIds) ";
		}
		if (encounterTypeIds != null) {
			dateAndLocationSql += " and e.encounter_type in (:encounterTypeIds) ";
			dateAndLocationSqlForSubquery += " and encounter.encounter_type in (:encounterTypeIds) ";
		}
		
		boolean doSqlAggregation = timeModifier == TimeModifier.MIN || timeModifier == TimeModifier.MAX || timeModifier == TimeModifier.AVG;
		boolean doInvert = timeModifier == TimeModifier.NO;

		String valueSql = null;
		List<String> valueClauses = new ArrayList<String>();
		List<Object> valueListForQuery = null;

		if (value1 != null || value2 != null) {
			valueSql = (value1 != null && value1 instanceof Number) ? " o.value_numeric " : " o.value_datetime ";
		} else if (valueList != null && valueList.size() > 0) {
			valueListForQuery = new ArrayList<Object>();
			if (valueList.get(0) instanceof String) {
				valueSql = " o.value_text ";
				for (Object o : valueList)
					valueListForQuery.add(o);
			} else { 
				valueSql = " o.value_coded ";
				for (Object o : valueList) {
					if (o instanceof Concept)
						valueListForQuery.add(((Concept) o).getConceptId());
					else if (o instanceof Number)
						valueListForQuery.add(((Number) o).intValue());
					else
						throw new IllegalArgumentException("Don't know how to handle " + o.getClass() + " in valueList");
				}
			}
		}
		
		if (doSqlAggregation) {
			valueSql = " " + timeModifier.toString() + "(" + valueSql + ") ";
		}

		if (value1 != null || value2 != null) {
			if (value1 != null) {
				valueClauses.add(valueSql + operator1.getSqlRepresentation() + " :value1 ");
			}
			if (value2 != null) {
				valueClauses.add(valueSql + operator2.getSqlRepresentation() + " :value2 ");
			}
		} else if (valueList != null && valueList.size() > 0) {
			valueClauses.add(valueSql + setOperator.getSqlRepresentation() + " (:valueList) ");
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select o.person_id from obs o ");
		sql.append(" inner join patient p on o.person_id = p.patient_id ");
		if (joinOnEncounter) {
			sql.append(" inner join encounter e on o.encounter_id = e.encounter_id ");
		}

		if (timeModifier == TimeModifier.ANY || timeModifier == TimeModifier.NO) {
			sql.append(" where o.voided = false and p.voided = false ");
			if (questionConceptId != null) {
				sql.append(" and concept_id = :questionConceptId ");
			}
			sql.append(dateAndLocationSql);
		} 
		else if (timeModifier == TimeModifier.FIRST || timeModifier == TimeModifier.LAST) {
			boolean isFirst = timeModifier == PatientSetService.TimeModifier.FIRST;
			sql.append(" inner join ( ");
			sql.append("    select person_id, " + (isFirst ? "MIN" : "MAX") + "(obs_datetime) as odt ");
			sql.append("    from obs ");
			if (joinOnEncounter) {
				sql.append(" inner join encounter on obs.encounter_id = encounter.encounter_id ");
			}
			sql.append("             where obs.voided = false and obs.concept_id = :questionConceptId " + dateAndLocationSqlForSubquery + " group by person_id ");
			sql.append(" ) subq on o.person_id = subq.person_id and o.obs_datetime = subq.odt ");
			sql.append(" where o.voided = false and p.voided = false and o.concept_id = :questionConceptId ");
			sql.append(dateAndLocationSql);
		} 
		else if (doSqlAggregation) {
			sql.append(" where o.voided = false and p.voided = false and concept_id = :questionConceptId " + dateAndLocationSql );
			sql.append(" group by o.person_id ");
		} 
		else {
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
		if (value1 != null) {
			if (value1 instanceof Number)
				query.setDouble("value1", ((Number) value1).doubleValue());
			else
				query.setDate("value1", (Date) value1);
		}
		if (value2 != null) {
			if (value2 instanceof Number)
				query.setDouble("value2", ((Number) value2).doubleValue());
			else
				query.setDate("value2", (Date) value2);
		}
		if (valueListForQuery != null) {
			query.setParameterList("valueList", valueListForQuery);
		}
		if (onOrAfter != null)
			query.setTimestamp("onOrAfter", onOrAfter);
		if (onOrBefore != null)
			query.setTimestamp("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(onOrBefore));
		if (locationIds != null)
			query.setParameterList("locationIds", locationIds);
		if (encounterTypeIds != null)
			query.setParameterList("encounterTypeIds", encounterTypeIds);
		
		Cohort ret;
		if (doInvert) {
            ret = Cohorts.allPatients(null);
			ret.getMemberIds().removeAll(query.list());
		} else {
			ret = new Cohort(query.list());
		}
		return ret;
    }

	/**
	 * @see org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO#getPatientsHavingEncounters(java.util.Date, java.util.Date, java.util.List, java.util.List, java.util.List, java.lang.Integer, java.lang.Integer, org.openmrs.User)
	 */
	public Cohort getPatientsHavingEncounters(Date onOrAfter, Date onOrBefore, TimeQualifier timeQualifier, List<Location> locationList, 
	                                          List<Person> providerList, List<EncounterType> encounterTypeList, List<Form> formList,
                                              Integer atLeastCount, Integer atMostCount, User createdBy, Date createdOnOrAfter, Date createdOnOrBefore) {
		List<Integer> encTypeIds = SqlUtils.openmrsObjectIdListHelper(encounterTypeList);
		List<Integer> locationIds = SqlUtils.openmrsObjectIdListHelper(locationList);
		List<Integer> providerIds = SqlUtils.openmrsObjectIdListHelper(providerList);
		List<Integer> formIds = SqlUtils.openmrsObjectIdListHelper(formList);
		
		// These clauses are applicable both in the overall query and in the subquery, if applicable
		// CollectionModifiers qualify the properties EncounterType, Form, and Location
		List<String> whichClauses = new ArrayList<String>();
		whichClauses.add("voided = false");
		ObjectUtil.addIfNotNull(whichClauses, "encounter_type in (:encTypeIds)", encTypeIds);
		ObjectUtil.addIfNotNull(whichClauses, "location_id in (:locationIds)", locationIds);
		
		if (!ReportUtil.isOpenmrsVersionOnePointNineAndAbove()) {
			ObjectUtil.addIfNotNull(whichClauses, "provider_id in (:providerIds)", providerIds);
		}
		
		ObjectUtil.addIfNotNull(whichClauses, "form_id in (:formIds)", formIds);
		
		// These clauses are only applicable in the overall query
		List<String> whereClauses = new ArrayList<String>(whichClauses);
		ObjectUtil.addIfNotNull(whereClauses, "encounter_datetime >= :onOrAfter", onOrAfter);
		ObjectUtil.addIfNotNull(whereClauses, "encounter_datetime <= :onOrBefore", onOrBefore);
		ObjectUtil.addIfNotNull(whereClauses, "creator = :createdBy", createdBy);
		ObjectUtil.addIfNotNull(whereClauses, "date_created >= :createdOnOrAfter", createdOnOrAfter);
		ObjectUtil.addIfNotNull(whereClauses, "date_created <= :createdOnOrBefore", createdOnOrBefore);
		
		List<String> havingClauses = new ArrayList<String>();
		ObjectUtil.addIfNotNull(havingClauses, "count(*) >= :atLeastCount", atLeastCount);
		ObjectUtil.addIfNotNull(havingClauses, "count(*) <= :atMostCount", atMostCount);
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select e.patient_id from encounter e inner join patient p on e.patient_id = p.patient_id");
		
		if (providerIds != null && ReportUtil.isOpenmrsVersionOnePointNineAndAbove()) {
			sb.append(" inner join encounter_provider ep on ep.encounter_id = e.encounter_id ");
		}
		
		if (timeQualifier == TimeQualifier.FIRST || timeQualifier == TimeQualifier.LAST) {
			boolean isFirst = timeQualifier == TimeQualifier.FIRST;
			
			sb.append(" inner join ( ");
			sb.append("    select patient_id, " + (isFirst ? "MIN" : "MAX") + "(encounter_datetime) as edt ");
			sb.append("    from encounter ");
			
			if (providerIds != null && ReportUtil.isOpenmrsVersionOnePointNineAndAbove()) {
				sb.append(" inner join encounter_provider ep on ep.encounter_id = encounter.encounter_id ");
			}
			
			for (ListIterator<String> i = whichClauses.listIterator(); i.hasNext();) {
				sb.append(i.nextIndex() == 0 ? " where " : " and ");
				sb.append("encounter." + i.next());
			}
			
			if (providerIds != null && ReportUtil.isOpenmrsVersionOnePointNineAndAbove()) {
				sb.append(whichClauses.size() == 0 ? " where " : " and ");
				sb.append("ep.provider_id in (:providerIds)");
			}
			
			sb.append(" group by encounter.patient_id ");
			sb.append(" ) subq on e.patient_id = subq.patient_id and e.encounter_datetime = subq.edt ");
	
		}
		for (ListIterator<String> i = whereClauses.listIterator(); i.hasNext();) {
			sb.append(i.nextIndex() == 0 ? " where " : " and ");
			sb.append("e." + i.next());
		}
		
		if (providerIds != null && ReportUtil.isOpenmrsVersionOnePointNineAndAbove()) {
			sb.append(whereClauses.size() == 0 ? " where " : " and ");
			sb.append("ep.provider_id in (:providerIds)");
		}
		
		sb.append(" and p.voided = false");
		sb.append(" group by e.patient_id ");
		for (ListIterator<String> i = havingClauses.listIterator(); i.hasNext();) {
			sb.append(i.nextIndex() == 0 ? " having " : " and ");
			sb.append(i.next());
		}

		log.debug("query: " + sb);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sb.toString());
		if (encTypeIds != null)
			query.setParameterList("encTypeIds", encTypeIds);
		if (locationIds != null)
			query.setParameterList("locationIds", locationIds);
		if (providerIds != null)
			query.setParameterList("providerIds", providerIds);
		if (formIds != null)
			query.setParameterList("formIds", formIds);
		if (onOrAfter != null)
			query.setTimestamp("onOrAfter", onOrAfter);
		if (onOrBefore != null)
			query.setTimestamp("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(onOrBefore));
		if (atLeastCount != null)
			query.setInteger("atLeastCount", atLeastCount);
		if (atMostCount != null)
			query.setInteger("atMostCount", atMostCount);
		if (createdBy != null)
			query.setInteger("createdBy", createdBy.getId());
		if (createdOnOrAfter != null)
			query.setTimestamp("createdOnOrAfter", createdOnOrAfter);
		if (createdOnOrBefore != null)
			query.setTimestamp("createdOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(createdOnOrBefore));
		
		return new Cohort(query.list());
    }
	

	/**
	 * @see org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO#getPatientsHavingDiscreteObs(org.openmrs.api.PatientSetService.TimeModifier, org.openmrs.Concept, org.openmrs.Concept, java.util.Date, java.util.Date, java.util.List, java.util.List, org.openmrs.module.reporting.common.SetComparator, java.util.List)
	 */
	public Cohort getPatientsHavingDiscreteObs(TimeModifier timeModifier, Concept question, Concept groupingConcept,
                                               Date onOrAfter, Date onOrBefore, List<Location> locationList,
                                               List<EncounterType> encounterTypeList, SetComparator operator,
                                               List<? extends Object> valueList) {
	    return getPatientsHavingObs(timeModifier, question, groupingConcept,
	    	onOrAfter, onOrBefore,
	    	locationList, encounterTypeList,
	    	null, null,
	    	null, null,
	    	operator, valueList);
    }

	/**
	 * @see {@link CohortQueryDAO#getPatientsHavingPersonAttributes(PersonAttributeType, List)}
	 */
	public Cohort getPatientsHavingPersonAttributes(PersonAttributeType attributeType, List<String> values) {
		
		StringBuilder sqlQuery = new StringBuilder();		
		sqlQuery.append("SELECT patient.patient_id ");
		sqlQuery.append("FROM person_attribute ");
		sqlQuery.append("INNER JOIN patient ON patient.patient_id = person_attribute.person_id ");
		sqlQuery.append("INNER JOIN person ON person.person_id = person_attribute.person_id ");
		sqlQuery.append("WHERE person_attribute.voided = false ");
		sqlQuery.append("AND person.voided = false "); 
		sqlQuery.append("AND patient.voided = false ");
		
		if (attributeType != null)
			sqlQuery.append(" AND person_attribute.person_attribute_type_id = :attributeType ");	// EQUALITY
			
		if (values != null && !values.isEmpty()) { 			
			// Use the EQUALITY operator if there's only one attribute type ID.
			if (values.size() == 1) { 
				sqlQuery.append(" AND person_attribute.value = :value");
			}
			// Otherwise, use the IN operator for a list of attribute type IDs.
			else { 
				sqlQuery.append(" AND person_attribute.value in (:values) ");
			}
		}
		
		// Only return one row per patient
		sqlQuery.append(" GROUP BY patient.patient_id ");
		
		log.debug("query: " + sqlQuery);
				
		// Create hibernate query 
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery.toString());

		// Set attribute type parameter
		if (attributeType != null) 
			query.setInteger("attributeType", attributeType.getPersonAttributeTypeId());
		
		// Set values parameter
		if (values != null && !values.isEmpty()) { 	
			if (values.size() == 1) {	// improve performance by using equality when there's only only 
				query.setString("value", values.get(0));
			} 
			else if (!values.isEmpty()) {
				query.setParameterList("values", values);
			} 
		}			
		// Execute query and return cohort 
		return new Cohort(query.list());
	}	
	

	/**
	 * Simple regular expression parser.  
	 * 
	 * As a first pass, we must support named parameters:  
	 *  	column = :paramName2
	 *  
	 * Eventually we Should support named parameters with datatype: 
	 * 		column = :paramName::java.lang.Date
	 */
	public List<Parameter> getNamedParameters(String sqlQuery) {
		List<Parameter> parameters = new ArrayList<Parameter>();

		// TODO Need to move regex code into a utility method 
		Pattern pattern = Pattern.compile("\\:\\w+\\b");
		Matcher matcher = pattern.matcher(sqlQuery);

		while (matcher.find()) {			
			// Index is 1 because we need to strip off the colon (":")
			String parameterName = matcher.group().substring(1);			
			Parameter parameter = new Parameter();			
			parameter.setName(parameterName);
			parameter.setLabel(parameterName);
			if (parameterName.toLowerCase().contains("date")) {
				parameter.setType(Date.class);
			}
			else {
				parameter.setType(String.class);
			}
			parameters.add(parameter);
		}		
		return parameters;
	}
	
	/**
	 * @see {@link CohortQueryDAO#executeSqlQuery(String, Map)}
	 */
	public Cohort executeSqlQuery(String sqlQuery, Map<String, Object> paramMap) { 
		try { 			
			validateSqlQuery(sqlQuery, paramMap);
			Query query = prepareQuery(sqlQuery, paramMap);	
			return executeQuery(query);
		} 
		catch (HibernateException e) { 
			throw new ParameterException("Error while executing SQL query [" + sqlQuery + "] with the parameters [" + paramMap + "]: " + e.getMessage() + ".  See tomcat log file for more details.", e);
		}
	}	
	
	/**
	 * Prepare a Hibernate Query object using the given sql query string 
	 * and parameter mapping.
	 * 
	 * @param sqlQuery
	 * @param paramMap
	 * @return	a Hibernate Query object
	 */
	public Query prepareQuery(String sqlQuery, Map<String, Object> paramMap) { 
		Query query = null;
		try { 			
			query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery.toString());					
			//query.setCacheMode(CacheMode.IGNORE);	// TODO figure out what this does before using it
						
			// Bind the query parameters (query is mutable
			bindQueryParameters(query, paramMap);
			
		} 
		catch (Exception e) { 
			log.error("Error while preparing sql query " + query.getQueryString() + ": " + e.getMessage());
			throw new ReportingException("Error while preparing sql query " + query.getQueryString() + ": " + e.getMessage(), e);			
		}
		return query;
	}
	
	/**
	 * This need to be a separate method so we can call it from both the 
	 * executeSqlQuery() and validateSqlQuery() methods 
	 */
	private Cohort executeQuery(Query query) { 
		// Needs to be a separate method because  
		try { 			
			// TODO Should test to make sure the returned List doesn't have more than one column			
			return new Cohort(query.list());
		} 
		catch (HibernateException e) { 
			throw new ParameterException("Error while executing SQL query [" + query.getQueryString() + "]: " + e.getMessage() + ".  See tomcat log file for more details.", e);
		}
	}		
	
	
	/**
	 * Binds the given paramMap to the query by replacing all named 
	 * parameters (e.g. :paramName) with their corresponding values 
	 * in the parameter map.
	 * 
	 * TODO Should add support for other classes.  
	 * TODO Should refactor to make more generalizable (create a new param map with correct param values)
	 * 
	 * @param query
	 * @param paramMap
	 */
	@SuppressWarnings("unchecked")
	private void bindQueryParameters(Query query, Map<String, Object> paramMap) { 

		// Iterate over parameters and bind them to the Query object
		for(String paramName : paramMap.keySet()) { 			
			
			Object paramValue = paramMap.get(paramName);				
			
			// Indicates whether we should bind this parameter in the query 
			boolean bindParameter = (query.getQueryString().indexOf(":" + paramName) > 0);
					
			if (bindParameter) { 

				// Make sure parameter value is not null
				if (paramValue == null) { 
					// TODO Should try to convert 'columnName = null' to 'columnName IS NULL'  
					throw new ParameterException("Cannot bind an empty value to parameter " + paramName + ". " + 
							"Please provide a real value or use the 'IS NULL' constraint in your query (e.g. 'table.columnName IS NULL').");					
				}
				
				// Cohort (needs to be first, otherwise it will resolve as OpenmrsObject)
				if (Cohort.class.isAssignableFrom(paramValue.getClass())) { 
					query.setParameterList(paramName, ((Cohort) paramValue).getMemberIds());				
				}
				// OpenmrsObject (e.g. Location)
				else if (OpenmrsObject.class.isAssignableFrom(paramValue.getClass())) { 					
					query.setInteger(paramName, ((OpenmrsObject) paramValue).getId());
				}
				// Collection<OpenmrsObject> (e.g. List<Location>)
				else if (Collection.class.isAssignableFrom(paramValue.getClass())) {
					Collection collection = (Collection) paramValue;
					if (collection.iterator().hasNext()) {
						if (OpenmrsObject.class.isAssignableFrom(collection.iterator().next().getClass())) {
							query.setParameterList(paramName, SqlUtils.openmrsObjectIdListHelper(
									new ArrayList<OpenmrsObject>((Collection<OpenmrsObject>) paramValue)));
						} else {
							// a List of Strings, Integers?
							query.setParameterList(paramName,
									SqlUtils.objectListHelper(new ArrayList<Object>((Collection<Object>) paramValue)));
						}
					} else {
						query.setParameter(paramName, null);
					}
				}
				// java.util.Date and subclasses
				else if (paramValue instanceof Date) {
					query.setDate(paramName, (Date) paramValue);
				}
				// String, Integer, et al (this might break since this is a catch all for all other classes)
				else { 
					query.setString(paramName, new String(paramValue.toString()));	// need to create new string for some reason
				}
			}
		}		
	}

	
	/**
	 * Validate the given sqlQuery based on the following validation rules.
	 * 
	 * @should validate that given paramMap matches parameter in given sqlQuery
	 * @should validate that given sqlQuery is not null or empty
	 * @should validate that given sqlQuery is valid sql
	 * @should validate that given sqlQuery has single column projection
	 * @should validate that given sqlQuery does not contain select star
	 * @should validate that given sqlQuery does not contain sql injection attack
	 * 
	 * @param sqlQuery
	 */
	private void validateSqlQuery(String sqlQuery, Map<String, Object> paramMap) throws ReportingException { 

		// TODO Should not allow user to provide empty sql query
		// FIXME This is going to be a really quick validation implementation  
		// TODO We need to implement a validation framework within the reporting module
		if (sqlQuery == null || sqlQuery.equals("")) 
			throw new ReportingException("SQL query string is required");
		if (!SqlUtils.isSelectQuery(sqlQuery)) {
			throw new IllegalDatabaseAccessException();
		}
    	// TODO Should have specified all parameters required to execute the query
    	List<Parameter> parameters = getNamedParameters(sqlQuery);    	
    	for (Parameter parameter : parameters) { 
    		Object parameterValue = paramMap.get(parameter.getName());
    		if (parameterValue == null) 
    			throw new ParameterException("Must specify a value for the parameter [" +  parameter.getName() + "]");    		
    	}		
		
		// TODO Should have a single column projection
		
		// TODO Should not allow use of 'select *'
		
		// TODO Should allow use of 'select distinct column'
		
		// TODO Should execute explain plan to make sure 
		// FIXME This might be a bad idea if the query does not perform well so 
		// make sure it's the last step in the validation process.
		try { 
			// Assume we are executing query on mysql, oracle, 
			// This isn't going to work like this ... 
			/* 
			Query query = 
				prepareQuery("explain plan for " + sqlQuery, paramMap);			
			executeQuery(query);
			*/
			
		} 
		catch (Exception e) { 
			log.error("Error while validating SQL query: " + e.getMessage(), e);
			throw new ReportingException("Error while validating SQL query: " + e.getMessage() + ".  See tomcat log file for more details.", e);
		}
	}	
	
	/**
	 * @see org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO#getPatientsHavingBirthAndDeath(java.util.Date, java.util.Date, java.util.Date, java.util.Date)
	 */
	public Cohort getPatientsHavingBirthAndDeath(Date bornOnOrAfter, Date bornOnOrBefore,
	                                             Date diedOnOrAfter, Date diedOnOrBefore) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select patient_id ");
		sql.append(" from patient pat ");
		sql.append(" inner join person per on pat.patient_id = per.person_id ");
		sql.append(" where pat.voided = false and per.voided = false ");
		if (bornOnOrAfter != null)
			sql.append(" and birthdate >= :bornOnOrAfter ");
		if (bornOnOrBefore != null)
			sql.append(" and birthdate <= :bornOnOrBefore ");
		if (diedOnOrAfter != null)
			sql.append(" and death_date >= :diedOnOrAfter ");
		if (diedOnOrBefore != null)
			sql.append(" and death_date <= :diedOnOrBefore ");

		Query q = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		q.setCacheMode(CacheMode.IGNORE);

		if (bornOnOrAfter != null)
			q.setTimestamp("bornOnOrAfter", bornOnOrAfter);
		if (bornOnOrBefore != null)
			q.setTimestamp("bornOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(bornOnOrBefore));
		if (diedOnOrAfter != null)
			q.setTimestamp("diedOnOrAfter", diedOnOrAfter);
		if (diedOnOrBefore != null)
			q.setTimestamp("diedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(diedOnOrBefore));
		
		return new Cohort(q.list());
    }

}
