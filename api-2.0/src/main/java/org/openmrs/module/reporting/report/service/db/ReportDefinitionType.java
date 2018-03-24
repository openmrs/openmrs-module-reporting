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
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
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
		if(cached == null){
			return null;
		}
		return Context.getService(ReportDefinitionService.class).getDefinitionByUuid(cached.toString());
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
		return ((ReportDefinition)value).getUuid();
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
	 * @see UserType#isMutable()
	 */
	public boolean isMutable() {
		return false;
	}

	/** 
	 * @see UserType#nullSafeGet(ResultSet, String[], Object)
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		String uuid = rs.getString(names[0]);
		if (uuid == null) {
			return null;
		}
		return Context.getService(ReportDefinitionService.class).getDefinitionByUuid(uuid);
	}

	/** 
	 * @see UserType#nullSafeSet(PreparedStatement, Object, int, SessionImplementor)
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		ReportDefinition d = (ReportDefinition) value;
		String val = (d == null ? null : d.getUuid());
		st.setString(index, val);
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
	@SuppressWarnings("rawtypes")
	public Class returnedClass() {
		return ReportDefinition.class;
	}

	/** 
	 * @see UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		return new int[] { Types.VARCHAR };
	}
}
