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

import static java.sql.Types.VARCHAR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.openmrs.util.OpenmrsUtil;

/**
 *  A report definition type
 */
public class PropertiesType implements UserType {

	/** 
	 * @see UserType#assemble(Serializable, Object)
	 */
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		String s = (String) cached;
		Properties p = new Properties();
		try {
			OpenmrsUtil.loadProperties(p, new ByteArrayInputStream(s.getBytes("UTF-8")));
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to load properties from string", e);
		}
		return p;
	}

	/** 
	 * @see UserType#deepCopy(Object)
	 */
	public Object deepCopy(Object value) throws HibernateException {
		if (value != null) {
			return new Properties((Properties) value);
		} else {
			return null;
		}
	}

	/** 
	 * @see UserType#disassemble(Object)
	 */
	public Serializable disassemble(Object value) throws HibernateException {
		if (value != null) {
			Properties props = (Properties) value;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OpenmrsUtil.storeProperties(props, out, null);
			try {
				return out.toString("UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Unable to load properties from string", e);
			}
		}
		return null;
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
		return true;
	}

	/** 
	 * @see UserType#nullSafeGet(ResultSet, String[], Object)
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		String s = rs.getString(names[0]);
		if (s == null) {
			return null;
		}
		Properties p = new Properties();
		try {
			OpenmrsUtil.loadProperties(p, new ByteArrayInputStream(s.getBytes("UTF-8")));
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to load properties from string", e);
		}
		return p;
	}

	/** 
	 * @see UserType#nullSafeSet(PreparedStatement, Object, int)
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
		String val = null;
		if (value != null) {
			Properties props = (Properties) value;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OpenmrsUtil.storeProperties(props, out, null);
			try {
				val = out.toString("UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Unable to load properties from string", e);
			}
		}
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
	@SuppressWarnings("unchecked")
	public Class returnedClass() {
		return Properties.class;
	}

	/** 
	 * @see UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		return new int[] { VARCHAR };
	}
}
