/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import org.hibernate.Hibernate;
import org.openmrs.api.db.hibernate.DbSessionFactory;  
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;


/**
 * A utility class for Hibernate-related functionality
 */
public class HibernateUtil {
	
	/**
	 * Hibernate made a non-backwards-compatible change in version 3.6 (which we use starting in
	 * OpenMRS 1.9). See https://hibernate.onjira.com/browse/HHH-5138.
	 * For example Hibernate.STRING is now StandardBasicTypes.STRING.
	 * 
	 * @param typeName
	 * @return the org.hibernate.type.Type, fetched as a static constant from either the Hibernate class or
	 * the StandardBasicTypes class, depending on which is available.
	 */
	public static Type standardType(String typeName) {
		try {
            try {
            	Field field = Hibernate.class.getField(typeName);
				return (Type) field.get(null);
            }
            catch (NoSuchFieldException ex) {
            	Field field = Context.loadClass("org.hibernate.type.StandardBasicTypes").getField(typeName);
            	return (Type) field.get(null);
            }
		} catch (Exception ex) {
			throw new RuntimeException("Cannot get Hibernate type: " + typeName, ex);
		}
	}

	/**
	 * Hibernate made a non-backwards-compatible change in version 3.6 (which we use starting in
	 * OpenMRS 1.9). See https://hibernate.onjira.com/browse/HHH-5138.
	 * TypeFactory.basic no longer exists.
	 * (I don't know if we need both this method and {@link #standardType(String)}, but I'm hackily
	 * replacing bits of code that I don't understand deeply, and they do two things.)
	 * @param name
     * @return Given the name of a Hibernate basic type, return an instance of org.hibernate.type.Type
     */
    public static Type getBasicType(String name, Properties parameters) {
    	try {
    		// use reflection to do: return TypeFactory.basic(name);
    		Class<?> clazz = Context.loadClass("org.hibernate.type.TypeFactory");
    		Method method = clazz.getMethod("basic", String.class);
    		return (Type) method.invoke(null, name);
    	} catch (Exception ex) {
    		// use reflection to do: return new TypeResolver().heuristicType(name, parameters);
			try {
	    		Object typeResolver = Context.loadClass("org.hibernate.type.TypeResolver").newInstance();
	    		Method method = typeResolver.getClass().getMethod("heuristicType", String.class, Properties.class);
	    		return (Type) method.invoke(typeResolver, name, parameters);
			} catch (Exception e) {
				throw new RuntimeException("Error getting hibernate type", e);
			}
    	}
    }

	/**
     * Hibernate made a non-backwards-compatible change in version 3.6 (which we use starting in
	 * OpenMRS 1.9). See https://hibernate.onjira.com/browse/HHH-5138.
     * There are a few places where in 3.2.5 we'd have a NullableType, but in 3.6 we'll have something
     * like a AbstractSingleColumnStandardBasicType, but either way there's a sqlType() method
     * 
     * @param type
     * @return The JDBC type associated with the given Hibernate type
     */
    public static Integer sqlType(Type type) {
    	try {
	        return (Integer) type.getClass().getMethod("sqlType").invoke(type);
        }
        catch (Exception ex) {
	        throw new RuntimeException("Error calling sqlType() method on " + type, ex);
        }
    }

	/**
	 * @return true if the current hibernate session contains the passed object
	 */
	public static boolean sessionContains(OpenmrsObject object) {
		DbSessionFactory sf = Context.getRegisteredComponents(DbSessionFactory.class).get(0);
		return sf != null && sf.getCurrentSession() != null && sf.getCurrentSession().contains(object);
	}
}
