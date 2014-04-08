package org.openmrs.module.reporting.evaluation.querybuilder;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openmrs.Cohort;
import org.openmrs.OpenmrsObject;
import org.openmrs.Voidable;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.evaluation.service.IdsetMember;
import org.openmrs.module.reporting.query.IdSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for building and executing an HQL query with parameters
 */
public class CriteriaQueryBuilder implements QueryBuilder {

	protected Log log = LogFactory.getLog(getClass());

	private Class<? extends OpenmrsObject> rootType;
	private String rootAlias;
	private boolean includeVoided = false;
	private List<String> columns = new ArrayList<String>();
	private Map<String, String> innerJoins = new LinkedHashMap<String, String>();
	private Map<String, String> leftOuterJoins = new LinkedHashMap<String, String>();
	private List<Criterion> constraints = new ArrayList<Criterion>();
	private Map<String, Set<Integer>> idConstraints = new LinkedHashMap<String, Set<Integer>>();
	private List<Order> orderBy = new ArrayList<Order>();

	//***** CONSTRUCTORS *****

	public CriteriaQueryBuilder() {
		this(false);
	}

	public CriteriaQueryBuilder(boolean includeVoided) {
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
	public CriteriaQueryBuilder select(String... columnNames) {
		for (String column : columnNames) {
			columns.add(column);
		}
		return this;
	}

	public CriteriaQueryBuilder from(Class<? extends OpenmrsObject> rootType) {
		return from(rootType, null);
	}

	public CriteriaQueryBuilder from(Class<? extends OpenmrsObject> rootType, String rootAlias) {
		this.rootType = rootType;
		this.rootAlias = rootAlias;
		if (!includeVoided && Voidable.class.isAssignableFrom(rootType)) {
			whereEqual((ObjectUtil.notNull(rootAlias) ? rootAlias + "." : "")+"voided", false);
		}
		return this;
	}

	/**
	 * In order to reference any nested properties of the primary typeToQuery referenced in the Constructor,
	 * you first need to explicitly join against them, providing an optional alias.  You must do this
	 * before you reference these nested properties in any columns, constraints, or orderings
	 */
	public CriteriaQueryBuilder innerJoin(String property, String alias) {
		innerJoins.put(property, alias);
		return this;
	}

	/**
	 * In order to reference any nested properties of the primary typeToQuery referenced in the Constructor,
	 * you first need to explicitly join against them, providing an optional alias.  You must do this
	 * before you reference these nested properties in any columns, constraints, or orderings
	 */
	public CriteriaQueryBuilder leftOuterJoin(String property, String alias) {
		leftOuterJoins.put(property, alias);
		return this;
	}

	/**
	 * Adds a new clause to the query
	 */
	public CriteriaQueryBuilder where(Criterion criterion) {
		constraints.add(criterion);
		return this;
	}

	/**
	 * Restricts the query for where the value of the passed property name is null
	 */
	public CriteriaQueryBuilder whereNull(String propertyName) {
		where(Restrictions.isNull(propertyName));
		return this;
	}

	/**
	 * Restricts the query for where the value of the passed property equals the passed value
	 */
	public CriteriaQueryBuilder whereEqual(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			if (propertyValue instanceof Date) {
				Date d = (Date) propertyValue;
				Date startOfDay = DateUtil.getStartOfDay(d);
				if (d.equals(startOfDay)) {
					whereGreaterOrEqualTo(propertyName, startOfDay);
					whereLess(propertyName, DateUtil.getEndOfDay(d));
				}
				else {
					where(Restrictions.eq(propertyName, d));
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
					where(Restrictions.eq(propertyName, propertyValue));
				}
			}
		}
		return this;
	}

	public CriteriaQueryBuilder whereIn(String propertyName, Object... values) {
		if (values != null) {
			where(Restrictions.in(propertyName, values));
		}
		return this;
	}

	public CriteriaQueryBuilder whereIn(String propertyName, Collection<?> values) {
		if (values != null) {
			where(Restrictions.in(propertyName, values));
		}
		return this;
	}

	public CriteriaQueryBuilder whereIdIn(String propertyName, Cohort cohort) {
		if (cohort != null) {
			whereIdIn(propertyName, cohort.getMemberIds());
		}
		return this;
	}

	public CriteriaQueryBuilder whereIdIn(String propertyName, IdSet idSet) {
		if (idSet != null) {
			whereIdIn(propertyName, idSet.getMemberIds());
		}
		return this;
	}

	public CriteriaQueryBuilder whereIdIn(String propertyName, Set<Integer> ids) {
		if (ids != null) {
			idConstraints.put(propertyName, ids);
		}
		return this;
	}

	public CriteriaQueryBuilder whereLike(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			where(Restrictions.like(propertyName, propertyValue));
		}
		return this;
	}

	public CriteriaQueryBuilder whereLike(String propertyName, Object propertyValue, MatchMode matchMode) {
		if (propertyValue != null) {
			where(Restrictions.like(propertyName, propertyValue.toString(), matchMode));
		}
		return this;
	}

	public CriteriaQueryBuilder whereGreater(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			where(Restrictions.gt(propertyName, propertyValue));
		}
		return this;
	}

	public CriteriaQueryBuilder whereGreaterOrEqualTo(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			where(Restrictions.ge(propertyName, propertyValue));
		}
		return this;
	}

	public CriteriaQueryBuilder whereLess(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			if (propertyValue instanceof Date) {
				propertyValue = DateUtil.getEndOfDayIfTimeExcluded((Date) propertyValue);
			}
			where(Restrictions.lt(propertyName, propertyValue));
		}
		return this;
	}

	public CriteriaQueryBuilder whereLessOrEqualTo(String propertyName, Object propertyValue) {
		if (propertyValue != null) {
			if (propertyValue instanceof Date) {
				propertyValue = DateUtil.getEndOfDayIfTimeExcluded((Date) propertyValue);
			}
			where(Restrictions.le(propertyName, propertyValue));
		}
		return this;
	}

	public CriteriaQueryBuilder whereBetweenInclusive(String propertyName, Object minValue, Object maxValue) {
		if (minValue != null) {
			whereGreaterOrEqualTo(propertyName, minValue);
		}
		if (maxValue != null) {
			whereLessOrEqualTo(propertyName, maxValue);
		}
		return this;
	}

	public CriteriaQueryBuilder orderAsc(String propertyName) {
		orderBy.add(Order.asc(propertyName));
		return this;
	}

	public CriteriaQueryBuilder orderDesc(String propertyName) {
		orderBy.add(Order.desc(propertyName));
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

	/**
	 * @return the Criteria to evaluate
	 */
	protected Criteria buildQuery(SessionFactory sessionFactory) {

		Criteria criteria;
		if (ObjectUtil.isNull(rootAlias)) {
			criteria = sessionFactory.getCurrentSession().createCriteria(rootType);
		}
		else {
			criteria = sessionFactory.getCurrentSession().createCriteria(rootType, rootAlias);
		}

		for (Map.Entry<String, String> e : innerJoins.entrySet()) {
			criteria.createAlias(e.getKey(), e.getValue());
		}

		for (Map.Entry<String, String> e : leftOuterJoins.entrySet()) {
			criteria.createAlias(e.getKey(), e.getValue(), Criteria.LEFT_JOIN);
		}

		if (!columns.isEmpty()) {
			ProjectionList projectionList = Projections.projectionList();
			for (String s : columns) {
				String[] split = s.split("\\:");
				if (split.length > 1) {
					projectionList.add(Projections.property(split[0]), split[1]);
				} else {
					projectionList.add(Projections.property(s));
				}
			}
			criteria.setProjection(projectionList);
		}

		for (String idProperty : idConstraints.keySet()) {
			Set<Integer> idSet = idConstraints.get(idProperty);
			addIdConstraint(criteria, idProperty, idSet);
		}

		for (Criterion constraint : constraints) {
			criteria.add(constraint);
		}

		for (Order order : orderBy) {
			criteria.addOrder(order);
		}

		return criteria;
	}

	//***** STATIC HELPER METHODS *****

	/**
	 * Constrains a particular property against the passed ids
	 */
	public static void addIdConstraint(Criteria criteria, String propertyName, Set<Integer> ids) {
		String idSetKey = Context.getService(EvaluationService.class).generateKey(ids);
		boolean isPersisted = Context.getService(EvaluationService.class).isInUse(idSetKey);
		if (isPersisted) {
			DetachedCriteria subQuery = DetachedCriteria.forClass(IdsetMember.class);
			subQuery.setProjection(Projections.property("memberId"));
			subQuery.add(Restrictions.eq("key", idSetKey));
			criteria.add(Subqueries.propertyIn(propertyName, subQuery));
		}
		else {
			criteria.add(Restrictions.in(propertyName, ids));
		}
	}
}
