package org.openmrs.module.reporting.evaluation.querybuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
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

	protected Log log = LogFactory.getLog(getClass());

	private Class<? extends OpenmrsObject> rootType;
	private String rootAlias;
	private List<DataSetColumn> registeredColumns = new ArrayList<DataSetColumn>();
	private List<String> clauses = new ArrayList<String>();
	private Map<String, Set<Integer>> idClauses = new LinkedHashMap<String, Set<Integer>>();
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private List<Object> orderBy = new ArrayList<Object>();
	private int positionIndex = 1;

	//***** CONSTRUCTORS *****

	public HqlQueryBuilder() { }

	//***** BUILDER METHODS *****

	/**
	 * Adds a new clause to the query
	 */
	public HqlQueryBuilder select(String...columns) {
		for (String column : columns) {
			registeredColumns.add(new DataSetColumn(column, column, Object.class)); // TODO: Figure out data type, alias
		}
		return this;
	}

	public HqlQueryBuilder from(Class<? extends OpenmrsObject> rootType) {
		return from(rootType, null);
	}

	public HqlQueryBuilder from(Class<? extends OpenmrsObject> rootType, String rootAlias) {
		this.rootType = rootType;
		this.rootAlias = rootAlias;
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
			where(propertyName + " in (:" + nextPositionIndex() + ")").withValue(values);
		}
		return this;
	}

	public HqlQueryBuilder whereIn(String propertyName, Collection<?> values) {
		if (values != null) {
			where(propertyName + " in (:" + nextPositionIndex() + ")").withValue(values);
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

	public HqlQueryBuilder whereGreaterOrEqualTo(String propertyName, Object propertyValue) {
		where(propertyName + " >= :" + nextPositionIndex()).withValue(propertyValue);
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
		return registeredColumns;
	}

	/**
	 * @see QueryBuilder#buildQuery(org.hibernate.SessionFactory)
	 */
	public Query buildQuery(SessionFactory sessionFactory) {

		if ((positionIndex-1) != parameters.size()) {
			throw new IllegalStateException("You have not specified enough parameters for the specified constraints");
		}

		for (String idProperty : idClauses.keySet()) {
			Set<Integer> idSet = idClauses.get(idProperty);
			String idSetKey = Context.getService(EvaluationService.class).generateKey(idSet);
			boolean isPersisted = Context.getService(EvaluationService.class).isInUse(idSetKey);
			if (isPersisted) {
				where(idProperty + " in ( select memberId from IdsetMember where key = '" + idSetKey + "' )");
			}
			else {
				where(idProperty + " in (:" + nextPositionIndex() + ")").withValue(idSet);
			}
		}

		// Create query string
		StringBuilder q = new StringBuilder();
		for (DataSetColumn c : registeredColumns) {
			q.append(q.length() == 0 ? "select " : ", ").append(c.getName());
		}
		q.append(" from ").append(rootType.getSimpleName());
		if (rootAlias != null) {
			q.append(" as ").append(rootAlias);
		}
		for (int i=0; i<clauses.size(); i++) {
			q.append(i == 0 ? " where " : " and ").append(clauses.get(i));
		}
		for (int i=0; i<orderBy.size(); i++) {
			q.append(i == 0 ? " order by " : ", ").append(orderBy.get(i));
		}

		String queryString = q.toString();
		log.debug("Building query: " + queryString);
		log.debug("With parameters: " + parameters);

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
