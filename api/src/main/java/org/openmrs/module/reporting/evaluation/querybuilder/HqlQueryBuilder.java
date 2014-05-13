package org.openmrs.module.reporting.evaluation.querybuilder;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Voidable;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.obs.ObsDataUtil;
import org.openmrs.module.reporting.data.visit.VisitDataUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.evaluation.service.IdsetMember;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.util.OpenmrsUtil;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for building and executing an HQL query with parameters
 */
public class HqlQueryBuilder implements QueryBuilder {

	public static int MAXIMUM_RECOMMENDED_IN_CLAUSE_SIZE = 5000;

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
					whereIn(propertyName, c.getMemberIds());
				}
				else if (propertyValue instanceof IdSet) {
					IdSet idSet = (IdSet) propertyValue;
					whereIn(propertyName, idSet.getMemberIds());
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
			Set<Integer> memberIds = null;
			if (context.getBaseCohort() != null) {
				memberIds = new HashSet<Integer>(context.getBaseCohort().getMemberIds());
			}
			if (context instanceof PersonEvaluationContext) {
				PersonEvaluationContext pec = (PersonEvaluationContext)context;
				if (pec.getBasePersons() != null) {
					if (memberIds != null) {
						memberIds.retainAll(pec.getBasePersons().getMemberIds());
					}
					else {
						memberIds = new HashSet<Integer>(pec.getBasePersons().getMemberIds());
					}
				}
			}
			whereIdIn(propertyName, memberIds);
		}
		return this;
	}

	public HqlQueryBuilder whereEncounterIn(String propertyName, EvaluationContext context) {
		if (context != null) {
			Set<Integer> encIds = EncounterDataUtil.getEncounterIdsForContext(context, true);
			whereIdIn(propertyName, encIds);
		}
		return this;
	}

    public HqlQueryBuilder whereVisitIn(String propertyName, EvaluationContext context) {
        if (context != null) {
            Set<Integer> visitIds = VisitDataUtil.getVisitIdsForContext(context, true);
            whereIdIn(propertyName, visitIds);
        }
        return this;
    }

	public HqlQueryBuilder whereObsIn(String propertyName, EvaluationContext context) {
		if (context != null) {
			Set<Integer> obsIds = ObsDataUtil.getObsIdsForContext(context, true);
			whereIdIn(propertyName, obsIds);
		}
		return this;
	}

	/**
	 * Constrain the passed id property against a set of values.
	 * This method may only be called once per instance of HqlQueryBuilder.
	 */
	protected HqlQueryBuilder whereIdIn(String propertyName, Set<Integer> ids) {
		if (ids != null) {
			if (idClauses.isEmpty()) {
				idClauses.put(propertyName, ids);
			}
			else {
				throw new IllegalStateException("You can only associate one IdSet per query.");
			}
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
		Query query = buildQuery(sessionFactory);
		stopWatch.split();
		log.debug("Query built in: " + stopWatch.toSplitString());
		List l = query.list();
		stopWatch.split();
		log.debug("Query executed in: " + stopWatch.toSplitString());
		stopWatch.stop();
		return l;
	}

	public Query buildQuery(SessionFactory sessionFactory) {

		if ((positionIndex-1) > parameters.size()) {
			throw new IllegalStateException("You have not specified enough parameters for the specified constraints");
		}

		// Apply the idset clauses to the query.  Happens here because the method used is determined at execution time.
		applyIdSetClausesToQuery(sessionFactory);

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

	protected void addSubQueryAgainstIdSetMember(String idProperty, String idSetKey) {
		where(idProperty + " in ( select memberId from IdsetMember where key = '" + idSetKey + "' )");
	}

	protected void applyIdSetClausesToQuery(SessionFactory sessionFactory) {

		List<Object[]> persistedIdSets = new ArrayList<Object[]>();

		// First apply any idsets that are empty or that are not persisted to the database
		for (String idProperty : idClauses.keySet()) {
			Set<Integer> idSet = idClauses.get(idProperty);
			if (idSet.isEmpty()) {
				where("1=0");
			}
			else {
				String idSetKey = Context.getService(EvaluationService.class).generateKey(idSet);
				boolean isPersisted = Context.getService(EvaluationService.class).isInUse(idSetKey);
				if (isPersisted) {
					persistedIdSets.add(new Object[]{idProperty, idSetKey, idSet});
				}
				else {
					if (idSet.size() > MAXIMUM_RECOMMENDED_IN_CLAUSE_SIZE) {
						log.warn("Adding in constraint against " + idSet.size() + " is not recommended.  This may fail.");
					}

					// Here we build the in clause manually due to some known hibernate performance issues
					where(idProperty + " in (" + OpenmrsUtil.join(idSet, ",") + ")");
				}
			}
		}

		// Now apply any constraints for idsets that are persisted to the database
		if (!persistedIdSets.isEmpty()) {

			// Try to handle as many constraints by use of in clauses, either with sub-queries or not

			boolean subQueriesPreferred = checkIfSubQueriesPreferred(sessionFactory);

			for (Iterator<Object[]> i = persistedIdSets.iterator(); i.hasNext();) {
				Object[] idSetData = i.next();
				String idProperty = (String)idSetData[0];
				String idSetKey = (String)idSetData[1];
				Set<Integer> idSet = (Set<Integer>)idSetData[2];

				if (subQueriesPreferred) {
					addSubQueryAgainstIdSetMember(idProperty, idSetKey);
					i.remove();
				}
				else if (idSet.size() <= MAXIMUM_RECOMMENDED_IN_CLAUSE_SIZE) {
					where(idProperty + " in (:" + nextPositionIndex() + ")").withValue(idSet);
					i.remove();
				}
			}

			// If there are any remaining, try using a cross-join if there is only one.
			// If there is more than one, default to using sub-queries

			if (!persistedIdSets.isEmpty()) {
				for (Iterator<Object[]> i = persistedIdSets.iterator(); i.hasNext();) {
					Object[] idSetData = i.next();
					String idProperty = (String) idSetData[0];
					String idSetKey = (String) idSetData[1];

					if (persistedIdSets.size() == 1) {
						String alias = "_idset_";
						from(IdsetMember.class, alias);
						where(idProperty + " = " + alias + ".memberId");
						whereEqual(alias + ".key", idSetKey);
					}
					else {
						log.warn("Using sub-query to constrain " + idProperty + ".  This is likely very slow.");
						addSubQueryAgainstIdSetMember(idProperty, idSetKey);
					}
				}
			}
		}
	}

	protected String nextPositionIndex() {
		return "param"+positionIndex++;
	}

	protected String lastPositionIndex() {
		return "param"+(positionIndex-1);
	}

	/**
	 * If there are any persisted id sets to join against, determine the optimal strategy for this
	 * We assume that a sub-query is optimal as the default.  However, for MySQL prior to 5.6, this is slow
	 * and a cross-join is much faster if we are only dealing with a single idset,
	 * so we will code in a particular special handling for this case.
	 * Other exceptions for other database types and versions can be added here subsequently as needed.
	 */
	protected boolean checkIfSubQueriesPreferred(SessionFactory sessionFactory) {
		try {
			DatabaseMetaData databaseMetaData = sessionFactory.getCurrentSession().connection().getMetaData();
			String dbName = databaseMetaData.getDatabaseProductName().toLowerCase().trim();
			int dbMajorVersion = databaseMetaData.getDatabaseMajorVersion();
			int dbMinorVersion = databaseMetaData.getDatabaseMinorVersion();
			log.debug("Creating sub-query for " + dbName + " version " + dbMajorVersion + "." + dbMinorVersion);
			if (dbName.contains("mysql") && dbMajorVersion <= 5 && dbMinorVersion < 6) {
				return false;
			}
		}
		catch (Exception e) {
			log.warn("Unable to retrieve database metadata for current session");
		}
		return true;
	}
}
