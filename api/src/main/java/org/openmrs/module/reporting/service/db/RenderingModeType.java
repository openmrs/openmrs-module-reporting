/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.service.db;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.openmrs.module.reporting.common.HibernateUtil;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

/**
 * Custom User-Type for storing RenderingModes in a single table within 2 columns
 * This type takes in 2 properties in the form:
 * <pre>
 *   <property name="renderingMode" type="org.openmrs.module.reporting.service.db.RenderingModeType">
 *     <column name="renderer_type"/>
 *     <column name="renderer_argument"/>
 *   </property>
 * </pre>
 */
@SuppressWarnings({"rawtypes"})
public class RenderingModeType implements CompositeUserType {

	/**
	 * @see CompositeUserType#returnedClass()
	 */
	public Class returnedClass() {
		return RenderingMode.class;
	}
	
	/**
	 * @see CompositeUserType#getPropertyNames()
	 */
	public String[] getPropertyNames() {
		return new String[] {"renderer", "argument"};
	}
	
	/**
	 * @see CompositeUserType#getPropertyTypes()
	 */
	public Type[] getPropertyTypes() {
		return new Type[] { HibernateUtil.standardType("CLASS"), HibernateUtil.standardType("STRING") };
	}
	
	/**
	 * @see CompositeUserType#isMutable()
	 */
	public boolean isMutable() {
		return true;
	}

	@Override
	public Serializable disassemble(Object value, SharedSessionContractImplementor session) throws HibernateException {
		return (Serializable) deepCopy(value);
	}

	@Override
	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
		return deepCopy(cached);
	}

	@Override
	public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner) throws HibernateException {
		return original;
	}


	/**
	 * @see CompositeUserType#getPropertyValue(java.lang.Object, int)
	 */
	public Object getPropertyValue(Object component, int property) throws HibernateException {
		RenderingMode m = (RenderingMode) component;
		return (property == 0 ? m.getRenderer().getClass() : m.getArgument());
	}

	/**
	 * @see CompositeUserType#setPropertyValue(java.lang.Object, int, java.lang.Object)
	 */
	public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
		RenderingMode m = (RenderingMode) component;
		if (property == 0) {
			ReportRenderer r = null;
			if (value != null) {
				try {
					r = (ReportRenderer)((Class) value).newInstance();
				}
				catch (Exception e) {
					throw new HibernateException("Error instantiating a new reporting renderer from " + value, e);
				}
			}
			m.setRenderer(r);
		}
		else {
			m.setArgument((String)value);
		}
	}
	
	/**
	 * @see CompositeUserType#deepCopy(java.lang.Object)
	 */
	public Object deepCopy(Object value) throws HibernateException {
		if (value == null) return null;
		RenderingMode toCopy = (RenderingMode) value;
		return new RenderingMode(toCopy.getRenderer(), toCopy.getLabel(), toCopy.getArgument(), toCopy.getSortWeight());
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
		Class<?> rendererClass = (Class<?>) HibernateUtil.standardType("CLASS").nullSafeGet(rs, names[0], session, owner);
		if (rendererClass == null) {
			return null;
		}
		String argument = (String) HibernateUtil.standardType("STRING").nullSafeGet(rs, names[1], session, owner);
		try {
			ReportRenderer renderer = (ReportRenderer) rendererClass.getDeclaredConstructor().newInstance();
			return new RenderingMode(renderer, renderer.getClass().getSimpleName(), argument, null);
		} catch (Exception e) {
			throw new HibernateException("Error instantiating ReportRenderer class: " + rendererClass, e);
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
		RenderingMode mode = (RenderingMode) value;
		Class<?> rendererClass = (mode == null ? null : mode.getRenderer().getClass());
		String argument = (mode == null ? null : mode.getArgument());

		HibernateUtil.standardType("CLASS").nullSafeSet(st, rendererClass, index, session);
		HibernateUtil.standardType("STRING").nullSafeSet(st, argument, index + 1, session);
	}

	/**
	 * @see CompositeUserType#replace(java.lang.Object, java.lang.Object, org.hibernate.engine.SessionImplementor, java.lang.Object)
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
}