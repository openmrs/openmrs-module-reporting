package org.openmrs.module.reporting.evaluation.querybuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;
import org.openmrs.Cohort;
import org.openmrs.Voidable;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.data.person.PersonDataUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationProfiler;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for building and executing an HQL query with parameters
 */
public class HqlQueryBuilder implements QueryBuilder {

	protected static String PARENTHESIS_START = "(";
	protected static String PARENTHESIS_END = ")";
	protected static String AND = "and";
	protected static String OR = "or";

	protected Log log = LogFactory.getLog(getClass());

	private Map<String, Class<?>> fromTypes = new LinkedHashMap<String, Class<?>>();
	private boolean includeVoided = false;
	private List<String> columns = new ArrayList<String>();
	private List<String> joinClauses = new ArrayList<String>();
	private List<String> clauses = new ArrayList<String>();
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private List<String> groupBy = new ArrayList<String>();
	private List<String> orderBy = new ArrayList<String>();
	private Integer limit = null;
	private int positionIndex = 1;

	//***** CONSTRUCTORS *****

	public HqlQueryBuilder() {
		this(false);
	}

	public HqlQueryBuilder(boolean includeVoided) {
		this.includeVoided = includeVoided;
	}

	//***** BUILDER METHODS *****

	/**
	 * This is where you specify what columns you want your query to return
	 * You can specify the column in property dot notation relative to the typeToQuery specified in the Constructor
	 * You can also specify a column alias by appending it to the column name after a colon
	 * For example, if you wanted to return the birthdate column from person with alias "bd",
	 * you would add "birthdate:bd".  If you wanted to add the name of the encounter type associated with an
	 * Encounter, with alias "type", you would add "encounterType.name:type"
	 */
	public HqlQueryBuilder select(String... columnNames) {
		Collections.addAll(columns, columnNames);
		return this;
	}

	public HqlQueryBuilder from(Class<?> fromType) {
		return from(fromType, null);
	}

	public HqlQueryBuilder from(Class<?> fromType, String fromAlias) {
		fromTypes.put(fromAlias, fromType);
		if (!includeVoided && Voidable.class.isAssignableFrom(fromType)) {
			whereEqual((ObjectUtil.notNull(fromAlias) ? fromAlias + "." : "")+"voided", false);
		}
		return this;
	}

	public HqlQueryBuilder innerJoin(String property, String alias) {
		joinClauses.add("inner join " + property + " as " + alias);
		return this;
	}

	public HqlQueryBuilder leftOuterJoin(String property, String alias) {
		joinClauses.add("left outer join " + property + " as " + alias);
		return this;
	}

	/**
	 * Adds a new clause to the query
	 */
	public HqlQueryBuilder where(String constraint) {
		clauses.add(constraint);
		return this;
	}

	/**
	 * Restricts the query for where the value of the passed property name is null
	 */
	public HqlQueryBuilder whereNull(String propertyName) {
		where(propertyName + " is null");
		return this;
	}

	/**
	 * Restricts the query for where the value of the passed property name is not null
	 */
	public HqlQueryBuilder whereNotNull(String propertyName) {
		where(propertyName + " is not null");
		return this;
	}

	public HqlQueryBuilder startGroup() {
		clauses.add(PARENTHESIS_START);
		return this;
	}

	public HqlQueryBuilder and() {
		clauses.add(AND);
		return this;
	}

	public HqlQueryBuilder or() {
		clauses.add(OR);
		return this;
	}

	public HqlQueryBuilder endGroup() {
		clauses.add(PARENTHESIS_END);
		return this;
	}

	/**
	 * Generally you will not need to use this method, as the various "whereXyz" methods handle it for you,
	 * but in the event that you have added a custom where clause that requires parameters, you can specify the
	 * value(s) of these parameters using this method
	 */
	public HqlQueryBuilder withValue(String parameterName, Object value) {
		parameters.put(parameterName, value);
		return this;
	}

	/**
	 * Restricts the query for where the value of the passed property equals the passed value
	 */
	public HqlQueryBuilder whereEqual(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			if (propertyValue instanceof Date) {
				Date d = (Date) propertyValue;
				Date startOfDay = DateUtil.getStartOfDay(d);
				if (d.equals(startOfDay)) {
					whereGreaterOrEqualTo(propertyName, startOfDay);
					whereLess(propertyName, DateUtil.getEndOfDay(d));
				}
				else {
					where(propertyName + " = :" + nextPositionIndex()).withValue(d);
				}
			}
			else {
				if (propertyValue instanceof Cohort) {
					Cohort c = (Cohort) propertyValue;
					whereIdIn(propertyName, c.getMemberIds());
				}
				else if (propertyValue instanceof IdSet) {
					IdSet idSet = (IdSet) propertyValue;
					whereIdIn(propertyName, idSet.getMemberIds());
				}
				else if (propertyValue instanceof Object[]) {
					whereIn(propertyName, Arrays.asList((Object[]) propertyValue));
				}
				else if (propertyValue instanceof Collection) {
					whereIn(propertyName, (Collection)propertyValue);
				}
				else {
					where(propertyName + " = :" + nextPositionIndex()).withValue(propertyValue);
				}
			}
		}
		return this;
	}

	public HqlQueryBuilder whereIn(String propertyName, Collection<?> values) {
		if (values != null) {
			if (values.isEmpty()) {
				where("1=0");
			}
			else {
				where(propertyName + " in (:" + nextPositionIndex() + ")").withValue(values);
			}
		}
		return this;
	}

	public HqlQueryBuilder whereInAny(String propertyName, Object... values) {
		if (values != null) {
			if (values.length == 0) {
				where("1=0");
			}
			else {
				where(propertyName + " in (:" + nextPositionIndex() + ")").withValue(values);
			}
		}
		return this;
	}

	public HqlQueryBuilder wherePatientIn(String propertyName, EvaluationContext context) {
		if (context != null) {
			if (context.getBaseCohort() != null) {
				whereIdIn(propertyName, context.getBaseCohort().getMemberIds());
			}
		}
		return this;
	}

	public HqlQueryBuilder wherePersonIn(String propertyName, EvaluationContext context) {
		if (context != null) {
			Set<Integer> personIds = PersonDataUtil.getPersonIdsForContext(context, true);
			if (personIds != null) {
				whereIdIn(propertyName, personIds);
			}
		}
		return this;
	}

	public HqlQueryBuilder whereEncounterIn(String propertyName, EvaluationContext context) {
		if (context != null) {
			String baseProperty = prepareBaseIdProperty(propertyName, "encounterId");
			if (context.getBaseCohort() != null) {
				String patientProperty = baseProperty + "patient.patientId";
				whereIdIn(patientProperty, context.getBaseCohort().getMemberIds());
			}
			if (context instanceof EncounterEvaluationContext) {
				EncounterEvaluationContext eec = (EncounterEvaluationContext) context;
				if (eec.getBaseEncounters() != null) {
					whereIdIn(baseProperty + "encounterId", eec.getBaseEncounters().getMemberIds());
				}
			}
		}
		return this;
	}

    public HqlQueryBuilder whereVisitIn(String propertyName, EvaluationContext context) {
		if (context != null) {
			String baseProperty = prepareBaseIdProperty(propertyName, "visitId");
			if (context.getBaseCohort() != null) {
				String patientProperty = baseProperty + "patient.patientId";
				whereIdIn(patientProperty, context.getBaseCohort().getMemberIds());
			}
			if (context instanceof VisitEvaluationContext) {
				VisitEvaluationContext vec = (VisitEvaluationContext) context;
				if (vec.getBaseVisits() != null) {
					whereIdIn(baseProperty + "visitId", vec.getBaseVisits().getMemberIds());
				}
			}
		}
		return this;
    }

	public HqlQueryBuilder whereObsIn(String propertyName, EvaluationContext context) {
		if (context != null) {
			String baseProperty = prepareBaseIdProperty(propertyName, "obsId");
			if (context.getBaseCohort() != null) {
				String patientProperty = baseProperty + "personId";
				whereIdIn(patientProperty, context.getBaseCohort().getMemberIds());
			}
			if (context instanceof ObsEvaluationContext) {
				ObsEvaluationContext oec = (ObsEvaluationContext) context;
				if (oec.getBaseObs() != null) {
					whereIdIn(baseProperty + "obsId", oec.getBaseObs().getMemberIds());
				}
			}
		}
		return this;
	}

	/**
	 * Constrain the passed id property against a set of values.
	 * This method may only be called once per instance of HqlQueryBuilder.
	 */
	public HqlQueryBuilder whereIdIn(String propertyName, Collection<Integer> ids) {
		if (ids != null) {
			if (ids.isEmpty()) {
				where("1=0");
			}
			else {
				// Here we build the in clause manually due to some known hibernate performance issues
				where(propertyName + " in (" + OpenmrsUtil.join(ids, ",") + ")");
			}
		}
		return this;
	}

	public HqlQueryBuilder whereLike(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			String s = propertyValue.toString();
			if (s.indexOf("%") == -1) {
				s = "%" + s  + "%";
			}
			where(propertyName + " like :" + nextPositionIndex()).withValue(s);
		}
		return this;
	}

	public HqlQueryBuilder whereGreater(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			where(propertyName + " > :" + nextPositionIndex()).withValue(propertyValue);
		}
		return this;
	}

	public HqlQueryBuilder whereGreaterOrNull(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			where("(" + propertyName + " is null or " + propertyName + " > :" + nextPositionIndex() + ")").withValue(propertyValue);
		}
		return this;
	}

	public HqlQueryBuilder whereGreaterEqualOrNull(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			where("(" + propertyName + " is null or " + propertyName + " >= :" + nextPositionIndex() + ")").withValue(propertyValue);
		}
		return this;
	}

	public HqlQueryBuilder whereGreaterOrEqualTo(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			where(propertyName + " >= :" + nextPositionIndex()).withValue(propertyValue);
		}
		return this;
	}

	public HqlQueryBuilder whereLess(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			if (propertyValue instanceof Date) {
				propertyValue = DateUtil.getEndOfDayIfTimeExcluded((Date) propertyValue);
			}
			where(propertyName + " < :" + nextPositionIndex()).withValue(propertyValue);
		}
		return this;
	}

	public HqlQueryBuilder whereLessOrEqualTo(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			if (propertyValue instanceof Date) {
				propertyValue = DateUtil.getEndOfDayIfTimeExcluded((Date) propertyValue);
			}
			where(propertyName + " <= :" + nextPositionIndex()).withValue(propertyValue);
		}
		return this;
	}

	public HqlQueryBuilder whereLessOrEqualToOrNull(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			if (propertyValue instanceof Date) {
				propertyValue = DateUtil.getEndOfDayIfTimeExcluded((Date) propertyValue);
			}
			where("(" + propertyName + " is null or " + propertyName + " <= :" + nextPositionIndex() + ")").withValue(propertyValue);
		}
		return this;
	}

	public HqlQueryBuilder whereBetweenInclusive(String propertyName, Object minValue, Object maxValue) {
		if (minValue != null) {
			whereGreaterOrEqualTo(propertyName, minValue);
		}
		if (maxValue != null) {
			whereLessOrEqualTo(propertyName, maxValue);
		}
		return this;
	}

	public HqlQueryBuilder where(String propertyName, RangeComparator operator, Object value) {
		if (operator != null && value != null) {
			if (operator.equals(RangeComparator.EQUAL)) {
				whereEqual(propertyName, value);
			}
			else if (operator.equals(RangeComparator.GREATER_EQUAL)) {
				whereGreaterOrEqualTo(propertyName, value);
			}
			else if (operator.equals(RangeComparator.GREATER_THAN)) {
				whereGreater(propertyName, value);
			}
			else if (operator.equals(RangeComparator.LESS_EQUAL)) {
				whereLessOrEqualTo(propertyName, value);
			}
			else if (operator.equals(RangeComparator.LESS_THAN)) {
				whereLess(propertyName, value);
			}
			else {
				throw new IllegalArgumentException("Unknown operator: " + operator);
			}
		}
		return this;
	}

	public HqlQueryBuilder groupBy(String propertyName) {
		groupBy.add(propertyName);
		return this;
	}

	public HqlQueryBuilder orderAsc(String propertyName) {
		orderBy.add(propertyName + " asc");
		return this;
	}

	public HqlQueryBuilder orderDesc(String propertyName) {
		orderBy.add(propertyName + " desc");
		return this;
	}

	public HqlQueryBuilder limit(Integer limitNumber) {
		limit = limitNumber;
		return this;
	}

	@Override
	public List<DataSetColumn> getColumns(SessionFactory sessionFactory) {
		List<DataSetColumn> l = new ArrayList<DataSetColumn>();
		Query q = buildQuery(sessionFactory);
		String[] returnAliases = q.getReturnAliases();
		Type[] returnTypes = q.getReturnTypes();
		for (int i=0; i<returnAliases.length; i++) {
			DataSetColumn column = new DataSetColumn();
			column.setName(returnAliases[i]);
			column.setLabel(returnAliases[i]);
			column.setDataType(returnTypes[i].getReturnedClass());
			l.add(column);
		}
		return l;
	}

	@Override
	public List<Object[]> evaluateToList(SessionFactory sessionFactory, EvaluationContext context) {
		// Due to hibernate bug HHH-2166, we need to make sure the HqlSqlWalker logger is not at DEBUG or TRACE level
		OpenmrsUtil.applyLogLevel("org.hibernate.hql.ast.HqlSqlWalker", "WARN");
		EvaluationProfiler profiler = new EvaluationProfiler(context);
		profiler.logBefore("EXECUTING_QUERY", toString());
		List<Object[]> ret = new ArrayList<Object[]>();
		try {
			Query q = buildQuery(sessionFactory);
			for (Object resultRow : q.list()) {
				if (resultRow instanceof Object[]) {
					ret.add((Object[]) resultRow);
				}
				else {
					ret.add(new Object[]{resultRow});
				}
			}
		}
		catch (RuntimeException e) {
			profiler.logError("EXECUTING_QUERY", toString(), e);
			throw e;
		}
		profiler.logAfter("EXECUTING_QUERY", "Completed successfully with " + ret.size() + " results");
		return ret;
	}

	@Override
	public String toString() {
		String ret = getQueryString();
		for (String paramName : parameters.keySet()) {
			String paramVal = ObjectUtil.format(parameters.get(paramName));
			ret = ret.replace(":"+paramName, paramVal);
		}
		if (ret.length() > 500) {
			ret = ret.substring(0, 450) + " <...> " + ret.substring(ret.length() - 50);
		}
		return ret;
	}

	protected String getQueryString() {
		if ((positionIndex-1) > parameters.size()) {
			throw new IllegalStateException("You have not specified enough parameters for the specified constraints");
		}

		// Create query string
		StringBuilder q = new StringBuilder();
		for (String s : columns) {
			String[] split = s.split("\\:");
			q.append(q.length() == 0 ? "select " : ", ");
			String column = split[0];

			// Determine an alias to use if not supplied to derive appropriate column naming
			String columnAlias = null;
			if (split.length > 1) {
				columnAlias = split[1];
			}
			else if (!ObjectUtil.containsWhitespace(column)) {
				String[] propertySplit = split[0].split("\\.");
				columnAlias = propertySplit[propertySplit.length - 1];
			}
			q.append(column).append(columnAlias != null ? " as " + columnAlias : "");
		}

		List<String> aliases = new ArrayList<String>(fromTypes.keySet());
		for (int i=0; i<aliases.size(); i++) {
			String alias = aliases.get(i);
			q.append(i == 0 ? " from " : ", ");
			q.append(fromTypes.get(alias).getSimpleName());
			if (ObjectUtil.notNull(alias)) {
				q.append(" as ").append(alias);
			}
		}
		for (String join : joinClauses) {
			q.append(" ").append(join);
		}

		String nextOperator = "where";
		for (int i=0; i<clauses.size(); i++) {
			String clause = clauses.get(i);
			q.append(" ");
			if (!AND.equalsIgnoreCase(clause) && !OR.equalsIgnoreCase(clause)) {
				if (!PARENTHESIS_END.equalsIgnoreCase(clause)) {
					q.append(nextOperator);
				}
				q.append(" ").append(clause);
			}
			if (PARENTHESIS_START.equalsIgnoreCase(clause)) {
				nextOperator = "";
			}
			else if (OR.equalsIgnoreCase(clause)) {
				nextOperator = OR;
			}
			else {
				nextOperator = AND;
			}
		}

		for (int i=0; i<groupBy.size(); i++) {
			q.append(i == 0 ? " group by " : ", ").append(groupBy.get(i));
		}

		for (int i=0; i<orderBy.size(); i++) {
			q.append(i == 0 ? " order by " : ", ").append(orderBy.get(i));
		}

		return q.toString();
	}

    protected Query buildQuery(SessionFactory sessionFactory) {

		if ((positionIndex-1) > parameters.size()) {
			throw new IllegalStateException("You have not specified enough parameters for the specified constraints");
		}

		Query query = sessionFactory.getCurrentSession().createQuery(getQueryString());

		for (Map.Entry<String, Object> e : parameters.entrySet()) {
			if (e.getValue() instanceof Collection) {
				query.setParameterList(e.getKey(), (Collection)e.getValue());
			}
			else if (e.getValue() instanceof Object[]) {
				query.setParameterList(e.getKey(), (Object[])e.getValue());
			}
			else if (e.getValue() instanceof Cohort) {
				query.setParameterList(e.getKey(), ((Cohort)e.getValue()).getMemberIds());
			}
			else if (e.getValue() instanceof IdSet) {
				query.setParameterList(e.getKey(), ((IdSet)e.getValue()).getMemberIds());
			}
			else {
				query.setParameter(e.getKey(), e.getValue());
			}
		}

		if (limit != null) {
			query.setMaxResults(limit);
		}

		return query;
	}

	// Protected methods

	protected HqlQueryBuilder withValue(Object parameterValue) {
		parameters.put(lastPositionIndex(), parameterValue);
		return this;
	}

	protected String nextPositionIndex() {
		return "param"+positionIndex++;
	}

	protected String lastPositionIndex() {
		return "param"+(positionIndex-1);
	}

	protected String prepareBaseIdProperty(String pathToStrip, String idToStripIfPresent) {
		if (pathToStrip.endsWith(idToStripIfPresent)) {
			pathToStrip = pathToStrip.substring(0, pathToStrip.length() - idToStripIfPresent.length());
		}
		else if (pathToStrip.endsWith(".id")) {
			pathToStrip = pathToStrip.substring(0, pathToStrip.length() - 2);
		}
		else if (pathToStrip.equals("id")) {
			pathToStrip = "";
		}
		if (pathToStrip.length() > 0 && !pathToStrip.endsWith(".")) {
			pathToStrip += ".";
		}
		return pathToStrip;
	}
}
