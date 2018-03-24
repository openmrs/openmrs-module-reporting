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

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import static java.sql.Types.VARCHAR;

/**
 *  A report definition type
 */
public class PropertiesType implements UserType {

	/** 
	 * @see UserType#assemble(Serializable, Object)
	 */
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
        if (cached == null) {
            return null;
        }
		try {
            String s = (String) cached;
            Properties p = new Properties();
            p.load(new StringReader(s));
            return p;
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Unable to load properties from string", e);
        }
	}

	/** 
	 * @see UserType#deepCopy(Object)
	 */
	public Object deepCopy(Object value) throws HibernateException {
		if (value != null) {
			Properties val = (Properties) value;
			Properties copy = new Properties();
			for ( Map.Entry<Object, Object> e : val.entrySet() ) {
				copy.setProperty((String) e.getKey(), (String) e.getValue());
			}
			return copy;
		} else {
			return null;
		}
	}

	/** 
	 * @see UserType#disassemble(Object)
	 */
	public Serializable disassemble(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        try {
            Properties props = (Properties) value;
            StringWriter sw = new StringWriter();
            props.store(sw, null);
            return sw.toString();
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Unable to store properties as string", e);
        }
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
        return assemble(s, null);
	}

	/** 
	 * @see UserType#nullSafeSet(PreparedStatement, Object, int)
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
		String val = (String) disassemble(value);
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
