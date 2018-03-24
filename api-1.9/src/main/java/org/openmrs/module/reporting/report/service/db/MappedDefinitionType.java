/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.service.db;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.HibernateUtil;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.serializer.ReportingSerializer;

/**
 * Custom User-Type for storing Mapped objects in a single table within 2 columns
 * This type takes in 2 properties and 1 parameter in the form:
 * <pre>
 *		<property name="reportDefinition">
 *			<column name="report_definition_uuid"/>
 *			<column name="report_definition_parameters"/>
 *			<type name="org.openmrs.module.reporting.report.service.db.MappedDefinitionType">
 *				<param name="mappedType">org.openmrs.module.reporting.report.definition.ReportDefinition</param>
 *			</type>
 *		</property>
 * </pre>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MappedDefinitionType implements CompositeUserType, ParameterizedType {
	
	/**
	 * Property via ParameterizedType for storing the type of the Mapped Parameterizable
	 */
	private Class<? extends Definition> mappedType;

	/**
	 * @see CompositeUserType#returnedClass()
	 */
	public Class returnedClass() {
		return Mapped.class;
	}
	
	/**
	 * @see CompositeUserType#getPropertyNames()
	 */
	public String[] getPropertyNames() {
		return new String[] {"definition", "parameterMappings"};
	}

	/**
	 * @see CompositeUserType#getPropertyTypes()
	 */
	public Type[] getPropertyTypes() {
		return new Type[] { HibernateUtil.standardType("STRING"), HibernateUtil.standardType("TEXT") };
	}
	
	/**
	 * @see CompositeUserType#isMutable()
	 */
	public boolean isMutable() {
		return true;
	}

	/**
	 * @see CompositeUserType#getPropertyValue(java.lang.Object, int)
	 */
	public Object getPropertyValue(Object component, int property) throws HibernateException {
		Mapped m = (Mapped) component;
		return (property == 0 ? m.getParameterizable() : m.getParameterMappings());
	}

	/**
	 * @see CompositeUserType#setPropertyValue(java.lang.Object, int, java.lang.Object)
	 */
	public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
		Mapped m = (Mapped) component;
		if (property == 0) {
			m.setParameterizable((Parameterizable)value);
		}
		else {
			m.setParameterMappings((Map)value);
		}
	}
	
	/**
	 * @see CompositeUserType#deepCopy(java.lang.Object)
	 */
	public Object deepCopy(Object value) throws HibernateException {
		if (value == null) return null;
		Mapped toCopy = (Mapped) value;
		Mapped m = new Mapped();
		m.setParameterizable(toCopy.getParameterizable());
		m.setParameterMappings(new HashMap<String, Object>(toCopy.getParameterMappings()));
		return m;
	}

	/**
	 * @see CompositeUserType#nullSafeGet(ResultSet, String[], SessionImplementor, Object)
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		String parameterizableUuid = (String) HibernateUtil.standardType("STRING").nullSafeGet(rs, names[0], session, owner);
		if (StringUtils.isEmpty(parameterizableUuid)) { return null; }
		String serializedMappings = (String) HibernateUtil.standardType("STRING").nullSafeGet(rs, names[1], session, owner);
		Definition d = DefinitionContext.getDefinitionByUuid(mappedType, parameterizableUuid);
		Map<String, Object> mappings = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(serializedMappings)) {
			try {
				mappings = Context.getSerializationService().deserialize(serializedMappings, Map.class, ReportingSerializer.class);
			}
			catch (Exception e) {
				throw new HibernateException("Unable to deserialize parameter mappings for definition", e);
			}
		}
		return new Mapped(d, mappings);
	}

	/**
	 * @see CompositeUserType#nullSafeSet(PreparedStatement, Object, int, SessionImplementor)
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		String definitionUuid = null;
		String serializedMappings = null;
		if (value != null) {
			Mapped m = (Mapped) value;
			if (m.getParameterizable() != null) {
				definitionUuid = m.getParameterizable().getUuid();
				if (m.getParameterMappings() != null && !m.getParameterMappings().isEmpty()) {
					try {
						serializedMappings = Context.getSerializationService().serialize(m.getParameterMappings(), ReportingSerializer.class);
					}
					catch (Exception e) {
						throw new HibernateException("Unable to serialize mappings for definition", e);
					}
				}
			}
		}
		HibernateUtil.standardType("STRING").nullSafeSet(st, definitionUuid, index, session);
		HibernateUtil.standardType("STRING").nullSafeSet(st, serializedMappings, index+1, session);
	}

	/**
	 * @see CompositeUserType#replace(Object, Object, SessionImplementor, Object)
	 */
	public Object replace(Object original, Object target, SessionImplementor session, Object owner) throws HibernateException {
		return original;
	}
	
	/** 
	 * @see UserType#equals(Object, Object)
	 */
	public boolean equals(Object x, Object y) throws HibernateException {
		return x != null && x.equals(y);
	}

	/** 
	 * @see UserType#hashCode(Object)
	 */
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}
	
	/**
	 * @see CompositeUserType#disassemble(Object, SessionImplementor)
	 */
	public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
		return (Serializable) deepCopy(value);
	}

	/**
	 * @see CompositeUserType#assemble(Serializable, SessionImplementor, Object)
	 */
	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
		return deepCopy(cached);
	}
	
	/**
	 * @see ParameterizedType#setParameterValues(Properties)
	 */
	public void setParameterValues(Properties parameters) {
		String mappedTypeStr = parameters.getProperty("mappedType");
		try {
			mappedType = (Class<? extends Definition>)Context.loadClass(mappedTypeStr);
		}
		catch (Exception e) {
			throw new HibernateException("Error setting the mappedType property to " + mappedTypeStr, e);
		}
	}
}