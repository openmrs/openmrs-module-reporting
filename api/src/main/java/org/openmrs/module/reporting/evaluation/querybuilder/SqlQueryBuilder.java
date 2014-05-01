package org.openmrs.module.reporting.evaluation.querybuilder;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for building and executing an HQL query with parameters
 */
public class SqlQueryBuilder implements QueryBuilder {

	protected Log log = LogFactory.getLog(getClass());

	private StringBuilder query = new StringBuilder();
	private Map<String, Object> parameters = new HashMap<String, Object>();

	//***** CONSTRUCTORS *****

	public SqlQueryBuilder() { }

	public SqlQueryBuilder append(String clause) {
		query.append(clause).append(" ");
		return this;
	}

	public void addParameter(String parameterName, Object parameterValue) {
		getParameters().put(parameterName, parameterValue);
	}

	public Map<String, Object> getParameters() {
		if (parameters == null) {
			parameters = new HashMap<String, Object>();
		}
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	@Override
	public List<DataSetColumn> getColumns() {
		List<DataSetColumn> l = new ArrayList<DataSetColumn>();
		int selectIndex = query.toString().toLowerCase().indexOf("select")+6;
		int fromIndex = query.toString().toLowerCase().indexOf("from");
		String selectString = query.toString().substring(selectIndex, fromIndex);
		for (String s : selectString.split(",")) {
			s = s.trim();
			int dotIndex = s.indexOf(".");
			if (dotIndex != -1) {
				s = s.substring(dotIndex+1);
			}
			String[] split = s.split("\\s");
			if (split.length == 1) {
				s = split[0];
			}
			else if (split.length == 2) {
				s = split[1];
			}
			else if (split.length == 3 && split[1].trim().equalsIgnoreCase("as")) {
				s = split[2];
			}
			else {
				throw new IllegalArgumentException("Unable to process column name: " + s);
			}
			l.add(new DataSetColumn(s, s, Object.class));
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

		String queryString = preProcessQuery();
		log.debug("Building query: " + queryString);
		log.debug("With parameters: " + getParameters());

		Query query = sessionFactory.getCurrentSession().createSQLQuery(queryString);
		for (Map.Entry<String, Object> e : getParameters().entrySet()) {
			Object value = normalizeParameterValue(e.getValue());
			if (value instanceof Collection) {
				query.setParameterList(e.getKey(), (Collection)value);
			}
			else {
				query.setParameter(e.getKey(), value);
			}
		}

		return query;
	}

	/**
	 * Here we pre-process id-set parameters such that we pass a constructed in clause
	 * to Hibernate, rather than relying on Hibernate to do this from the passed list
	 * The reason is due to some reported performance issues with this in Hibernate
	 */
	private String preProcessQuery() {
		String ret = query.toString();
		for (Iterator<String> i = getParameters().keySet().iterator(); i.hasNext();) {
			String paramName = i.next();
			Object paramValue = getParameters().get(paramName);
			if (paramValue != null) {
				Set<Integer> memberIds = null;
				if (paramValue instanceof Cohort) {
					memberIds = ((Cohort) paramValue).getMemberIds();
				}
				if (paramValue instanceof IdSet) {
					memberIds = ((IdSet) paramValue).getMemberIds();
				}
				if (memberIds != null) {
					String toMatch = ":"+paramName;
					if (ret.contains(toMatch)) {
						String idClause = "(" + OpenmrsUtil.join(memberIds, ",") + ")";
						ret = ret.replace("(" + toMatch + ")", idClause); // where id in (:ids)
						ret = ret.replace(toMatch, idClause); // where id in :ids
						i.remove();
					}
				}
			}
		}
		return ret;
	}

	private Object normalizeParameterValue(Object value) {
		if (value == null) {
			return null;
		}
		Collection c = null;
		if (value instanceof Collection) {
			c = (Collection)value;
		}
		else if (value instanceof Object[]) {
			c = Arrays.asList((Object[])value);
		}
		else if (value instanceof Cohort) {
			c = ((Cohort)value).getMemberIds();
		}
		else if (value instanceof IdSet) {
			c = ((IdSet)value).getMemberIds();
		}
		if (c != null) {
			List<Object> l = new ArrayList<Object>();
			for (Object o : c) {
				l.add(normalizeParameterValue(o));
			}
			return l;
		}
		else {
			if (value instanceof OpenmrsObject) {
				return ((OpenmrsObject)value).getId();
			}
			else {
				return value;
			}
		}
	}
}
