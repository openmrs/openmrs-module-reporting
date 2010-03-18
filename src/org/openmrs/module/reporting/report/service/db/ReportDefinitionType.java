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
package org.openmrs.module.reporting.report.service.db;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

/**
 *  A report definition type
 */
public class ReportDefinitionType implements UserType {

	/** 
	 * @see UserType#assemble(Serializable, Object)
	 */
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		Integer id = (Integer) cached;
		return Context.getService(ReportDefinitionService.class).getDefinition(id);
	}

	/** 
	 * @see UserType#deepCopy(Object)
	 */
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	/** 
	 * @see UserType#disassemble(Object)
	 */
	public Serializable disassemble(Object value) throws HibernateException {
		if (value == null) {
			return null;
		}
		return ((ReportDefinition)value).getId();
	}

	/** 
	 * @see UserType#equals(Object, Object)
	 */
	public boolean equals(Object x, Object y) throws HibernateException {
		return x.equals(y);
	}

	/** 
	 * @see UserType#hashCode(Object)
	 */
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	/** 
	 * @see UserType#isMutable()
	 */
	public boolean isMutable() {
		return false;
	}

	/** 
	 * @see UserType#nullSafeGet(ResultSet, String[], Object)
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		Integer id = rs.getInt(names[0]);
		if (id == null) {
			return null;
		}
		return Context.getService(ReportDefinitionService.class).getDefinition(id);
	}

	/** 
	 * @see UserType#nullSafeSet(PreparedStatement, Object, int)
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
		ReportDefinition d = (ReportDefinition) value;
		Integer val = (d == null ? null : d.getId());
		st.setInt(index, val);
	}

	/** 
	 * @see UserType#replace(Object, Object, Object)
	 */
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	/** 
	 * @see UserType#returnedClass()
	 */
	@SuppressWarnings("unchecked")
	public Class returnedClass() {
		return ReportDefinition.class;
	}

	/** 
	 * @see UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		return new int[] {Hibernate.INTEGER.sqlType()};
	}
}
