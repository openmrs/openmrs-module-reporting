/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.dataset.query.service.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.OpenmrsData;
import org.openmrs.OpenmrsObject;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.IdSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Deprecated
public class HibernateDataSetQueryDAO implements DataSetQueryDAO {

	protected static final Log log = LogFactory.getLog(HibernateDataSetQueryDAO.class);

	//***** PROPERTIES *****
	
	private SessionFactory sessionFactory;

	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataSetQueryDAO#executeHqlQuery(String, Map<String, Object>)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object> executeHqlQuery(String hqlQuery, Map<String, Object> parameterValues) {
		Query q = sessionFactory.getCurrentSession().createQuery(hqlQuery);
		for (Map.Entry<String, Object> e : parameterValues.entrySet()) {
			if (e.getValue() instanceof Collection) {
				q.setParameterList(e.getKey(), (Collection)e.getValue());
			}
			else if (e.getValue() instanceof Object[]) {
				q.setParameterList(e.getKey(), (Object[])e.getValue());
			}
			else if (e.getValue() instanceof Cohort) {
				q.setParameterList(e.getKey(), ((Cohort)e.getValue()).getMemberIds());
			}
			else if (e.getValue() instanceof IdSet) {
				q.setParameterList(e.getKey(), ((IdSet)e.getValue()).getMemberIds());
			}
			else {
				q.setParameter(e.getKey(), e.getValue());
			}
		}
		return q.list();
	}
	
	/** 
	 * @see DataSetQueryDAO#getPropertyValues(Class, String, EvaluationContext)
	 */
	public Map<Integer, Object> getPropertyValues(Class<? extends OpenmrsObject> type, String property, EvaluationContext context) {
		Map<Integer, Object> ret = new HashMap<Integer, Object>();
		
		Cohort baseCohort = context.getBaseCohort();
		if (baseCohort != null && baseCohort.isEmpty()) {
			return ret;
		}
		
		ClassMetadata metadata = sessionFactory.getClassMetadata(type);
		String idPropertyName = metadata.getIdentifierPropertyName();
		String entityName = type.getSimpleName();
		String alias = entityName.toLowerCase();
		
		// TODO: Need to get path to patient so that join against base cohort can happen
		// Can try to make this more clever through reflection.  Going to brute force it for now...
		
		Map<Class<? extends OpenmrsData>, String> patientJoinProperties = new HashMap<Class<? extends OpenmrsData>, String>();
		patientJoinProperties.put(Person.class, "personId");
		patientJoinProperties.put(Patient.class, "patientId");
		patientJoinProperties.put(Encounter.class, "patient.patientId");
		patientJoinProperties.put(Obs.class, "patient.patientId");
		patientJoinProperties.put(Order.class, "patient.patientId");
		patientJoinProperties.put(PatientProgram.class, "patient.patientId");
		patientJoinProperties.put(PatientState.class, "patientProgram.patient.patientId");
		patientJoinProperties.put(PersonName.class, "person.personId");
		patientJoinProperties.put(PatientIdentifier.class, "patient.patientId");
		patientJoinProperties.put(Relationship.class, "personA.personId");
		
		String voidedProperty = (type == Person.class ? "personVoided" : "voided");

        // the special-case code for filtering in Java if the baseCohort is too big only handles Person or Patient queries
        // (though we may eventually want to extend it)
        boolean personOrPatientQuery = type.equals(Patient.class) || type.equals(Person.class);
        boolean filterInQuery = !personOrPatientQuery || (baseCohort != null && baseCohort.size() < 2000);  // TODO: Change to batch if it is good
        boolean doNotFilterInJava = baseCohort == null || filterInQuery;
		
		StringBuilder hql = new StringBuilder();
		hql.append("select 	" + idPropertyName + ", " + property + " ");
		hql.append("from	" + entityName + " " + alias + " ");
		hql.append("where	" + alias + "." + voidedProperty + " = false ");
        if (filterInQuery) {
            for (Class<? extends OpenmrsData> clazz : patientJoinProperties.keySet()) {
                if (clazz.isAssignableFrom(type) && baseCohort != null) {
                    hql.append(" and " + alias + "." + patientJoinProperties.get(clazz) + " in (:ids)");
                }
            }
        }
		
		Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
		if(hql.toString().contains(":ids")) {
			query.setParameterList("ids", baseCohort.getMemberIds());
		}

		for (Object o : query.list()) {
			Object[] vals = (Object[]) o;
            Integer ptId = (Integer) vals[0];
            if (doNotFilterInJava || baseCohort.contains(ptId)) {
                ret.put(ptId, vals[1]);
            }
		}
		return ret;
	}
	
	/** 
	 * @see DataSetQueryDAO#convertData(Class, String, java.util.Set, Class, String, java.util.Set)
	*/
	public Map<Integer, Integer> convertData(Class<?> fromType, String fromJoin, Set<Integer> fromIds, Class<?> toType, String toJoin, Set<Integer> toIds) {

        if ((fromIds != null && fromIds.size() == 0) || (toIds != null && toIds.size() == 0)) {
            return new HashMap<Integer, Integer>();
        }

        ClassMetadata fromMetadata = sessionFactory.getClassMetadata(fromType);
        String fromIdProperty = (Patient.class.isAssignableFrom(fromType) ? "patientId" : fromMetadata.getIdentifierPropertyName());
        String fromEntity = fromType.getSimpleName();
        String fromAlias = fromEntity.toLowerCase();

        ClassMetadata toMetadata = sessionFactory.getClassMetadata(toType);
        String toIdProperty = toMetadata.getIdentifierPropertyName();
        String toEntity = toType.getSimpleName();
        String toAlias = toEntity.toLowerCase();

        StringBuilder hql = new StringBuilder();
        hql.append("select 	" + toAlias + "." + toIdProperty + ", " + fromAlias + "." + fromIdProperty + " ");
        hql.append("from	" + fromEntity + " " + fromAlias + ", " + toEntity + " " + toAlias + " ");
        hql.append("where 	" + fromAlias + "." + fromJoin + " = " + toAlias + "." + toJoin + " ");
        if (fromIds != null) {
            hql.append("and " + fromAlias + "." + fromIdProperty + " in (:fromIds) ");
        }
        if (toIds != null) {
            hql.append("and " + toAlias + "." + toIdProperty + " in (:toIds) ");
        }

        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());

        if (fromIds != null) {
            query.setParameterList("fromIds", fromIds);
        }
        if (toIds != null) {
            query.setParameterList("toIds", toIds);
        }

        Map<Integer, Integer> m = new HashMap<Integer, Integer>();
        for (Object o : query.list()) {
            Object[] vals = (Object[]) o;
            m.put((Integer) vals[0], (Integer) vals[1]);
        }
        return m;
    }

    //***** PROPERTY ACCESS *****

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
