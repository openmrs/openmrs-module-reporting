package org.openmrs.module.reporting.evaluation.querybuilder;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.OpenmrsObject;
import org.openmrs.Voidable;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.IdSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

	private Map<String, Class<? extends OpenmrsObject>> fromTypes = new LinkedHashMap<String, Class<? extends OpenmrsObject>>();
	private boolean includeVoided = false;
	private List<String> columns = new ArrayList<String>();
	private List<String> joinClauses = new ArrayList<String>();
	private List<String> clauses = new ArrayList<String>();
	private Map<String, Set<Integer>> idClauses = new LinkedHashMap<String, Set<Integer>>();
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private List<String> orderBy = new ArrayList<String>();
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
		for (String column : columnNames) {
			columns.add(column);
		}
		return this;
	}

	public HqlQueryBuilder from(Class<? extends OpenmrsObject> fromType) {
		return from(fromType, null);
	}

	public HqlQueryBuilder from(Class<? extends OpenmrsObject> fromType, String fromAlias) {
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
					whereIdIn(propertyName, c);
				}
				else if (propertyValue instanceof IdSet) {
					IdSet idSet = (IdSet) propertyValue;
					whereIdIn(propertyName, idSet);
				}
				else if (propertyValue instanceof Object[]) {
					whereIn(propertyName, Arrays.asList((Object[])propertyValue));
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

	public HqlQueryBuilder whereIn(String propertyName, Object... values) {
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

	public HqlQueryBuilder whereIdIn(String propertyName, Cohort cohort) {
		if (cohort != null) {
			whereIdIn(propertyName, cohort.getMemberIds());
		}
		return this;
	}

	public HqlQueryBuilder whereIdIn(String propertyName, IdSet idSet) {
		if (idSet != null) {
			whereIdIn(propertyName, idSet.getMemberIds());
		}
		return this;
	}

	public HqlQueryBuilder whereIdIn(String propertyName, Set<Integer> ids) {
		if (ids != null) {
			idClauses.put(propertyName, ids);
		}
		return this;
	}

	public HqlQueryBuilder whereLike(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			where(propertyName + " like :" + nextPositionIndex()).withValue(propertyValue);
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

	public HqlQueryBuilder orderAsc(String propertyName) {
		orderBy.add(propertyName + " asc");
		return this;
	}

	public HqlQueryBuilder orderDesc(String propertyName) {
		orderBy.add(propertyName + " desc");
		return this;
	}

	@Override
	public List<DataSetColumn> getColumns() {
		List<DataSetColumn> l = new ArrayList<DataSetColumn>();
		for (String s : columns) {
			String[] split = s.split("\\:");
			if (split.length > 1) {
				l.add(new DataSetColumn(split[1], split[1], Object.class));
			}
			else {
				l.add(new DataSetColumn(split[0], split[0], Object.class));
			}
		}
		return l;
	}

	@Override
	public List<?> listResults(SessionFactory sessionFactory) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		List l = buildQuery(sessionFactory).list();
		stopWatch.stop();
		log.debug("Primary query executed in: " + stopWatch.toString());
		return l;
	}

	public Query buildQuery(SessionFactory sessionFactory) {

		if ((positionIndex-1) != parameters.size()) {
			throw new IllegalStateException("You have not specified enough parameters for the specified constraints");
		}

		for (String idProperty : idClauses.keySet()) {
			Set<Integer> idSet = idClauses.get(idProperty);
			if (idSet.isEmpty()) {
				where("1=0");
			}
			else {
				String idSetKey = Context.getService(EvaluationService.class).generateKey(idSet);
				boolean isPersisted = Context.getService(EvaluationService.class).isInUse(idSetKey);
				if (isPersisted) {
					where(idProperty + " in ( select memberId from IdsetMember where key = '" + idSetKey + "' )");
				} else {
					where(idProperty + " in (:" + nextPositionIndex() + ")").withValue(idSet);
				}
			}
		}

		// Create query string
		StringBuilder q = new StringBuilder();
		for (String s : columns) {
			String[] split = s.split("\\:");
			q.append(q.length() == 0 ? "select " : ", ");
			q.append(split[0]);
			if (split.length > 1) {
				q.append(" as ").append(split[1]);
			}
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

		for (int i=0; i<orderBy.size(); i++) {
			q.append(i == 0 ? " order by " : ", ").append(orderBy.get(i));
		}

		String queryString = q.toString();
		if (log.isDebugEnabled()) {
			log.debug("Building query: " + queryString);
			log.debug("With parameters: " + parameters);
		}

		Query query = sessionFactory.getCurrentSession().createQuery(queryString);
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
}
