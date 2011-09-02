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

import java.util.HashMap;
import java.util.Map;

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
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.column.EvaluatedColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.JoinColumnDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

public class HibernateDataSetQueryDAO implements DataSetQueryDAO {

	protected static final Log log = LogFactory.getLog(HibernateDataSetQueryDAO.class);

	//***** PROPERTIES *****
	
	private SessionFactory sessionFactory;

	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataSetQueryDAO#getPropertyValues(Class, String, EvaluationContext)
	 */
	public Map<Integer, Object> getPropertyValues(Class<? extends OpenmrsObject> type, String property, EvaluationContext context) {
		Map<Integer, Object> ret = new HashMap<Integer, Object>();
		
		Cohort baseCohort = context.getBaseCohort();
		if (baseCohort == null) {
			baseCohort = Context.getPatientSetService().getAllPatients();
		}
		
		ClassMetadata metadata = sessionFactory.getClassMetadata(type);
		String idPropertyName = metadata.getIdentifierPropertyName();
		String entityName = type.getSimpleName();
		String alias = entityName.toLowerCase();
		
		// TODO: Need to get path to patient so that join against base cohort can happen
		// Can try to make this more clever through reflection.  Going to brute force it for now...
		
		Map<Class<? extends OpenmrsData>, String> patientJoinProperties = new HashMap<Class<? extends OpenmrsData>, String>();
		patientJoinProperties.put(Patient.class, "patientId");
		patientJoinProperties.put(Encounter.class, "patient.patientId");
		patientJoinProperties.put(Obs.class, "patient.patientId");
		patientJoinProperties.put(Order.class, "patient.patientId");
		patientJoinProperties.put(PatientProgram.class, "patient.patientId");
		patientJoinProperties.put(PatientState.class, "patientProgram.patient.patientId");
		patientJoinProperties.put(PersonName.class, "person.personId");
		patientJoinProperties.put(PatientIdentifier.class, "patient.patientId");
		patientJoinProperties.put(Relationship.class, "personA.personId");
		
		String patientIdConstraint = "";
		for (Class<? extends OpenmrsData> clazz : patientJoinProperties.keySet()) {
			if (clazz.isAssignableFrom(type)) {
				patientIdConstraint = " where " + alias + "." + patientJoinProperties.get(clazz) + " in (:ids)";
			}
		}
		
		String hql = "select " + idPropertyName + ", " + property + " from " + entityName + " " + alias + patientIdConstraint;
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		if(ObjectUtil.notNull(patientIdConstraint)) {
			query.setParameterList("ids", baseCohort.getMemberIds());
		}
		
		for (Object o : query.list()) {
			Object[] vals = (Object[]) o;
			ret.put((Integer)vals[0], vals[1]);
		}
		return ret;
	}
	
	/** 
	 * @see DataSetQueryDAO#convertColumn(DataSetColumn, JoinColumnDefinition)
	 */
	public EvaluatedColumnDefinition convertColumn(EvaluatedColumnDefinition originalColumn, JoinColumnDefinition<?> joinColumnDefinition) {
		
		Class<? extends OpenmrsData> fromType = joinColumnDefinition.getColumnDefinition().getBaseType();
		Class<? extends OpenmrsData> toType = joinColumnDefinition.getBaseType();
		
		if (originalColumn == null || originalColumn.getColumnValues().isEmpty() || fromType.equals(toType)) {
			return originalColumn;
		}
		
		String initialEntity = fromType.getSimpleName();
		String initialAlias = initialEntity.toLowerCase();
		String initialIdProperty = joinColumnDefinition.getColumnDefinition().getIdProperty();
		
		String newEntity = toType.getSimpleName();
		String newAlias = newEntity.toLowerCase();
		String newIdProperty = joinColumnDefinition.getIdProperty();
		String joinProperty = joinColumnDefinition.getJoinProperty();
		
		StringBuilder hql = new StringBuilder();
		hql.append("select 	" + newAlias + "." + newIdProperty + ", " + initialAlias + "." + initialIdProperty + " ");
		hql.append("from	" + initialEntity + " " + initialAlias + ", " + newEntity + " " + newAlias + " ");
		hql.append("where 	" + newAlias + "." + joinProperty + " = " + initialAlias + " ");
		hql.append("and 	" + initialAlias + "." + initialIdProperty + " in (:initialIds)");
		
		Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
		query.setParameterList("initialIds", originalColumn.getColumnValues().keySet());
		
		Map<Integer, Object> columnValues = new HashMap<Integer, Object>();
		for (Object o : query.list()) {
			Object[] vals = (Object[]) o;
			Integer newId = (Integer) vals[0];
			Integer initialId = (Integer) vals[1];
			columnValues.put(newId, originalColumn.getColumnValues().get(initialId));
		}
		originalColumn.setColumnValues(columnValues);

		return originalColumn;
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
