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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.property.access.internal.PropertyAccessStrategyCompositeUserTypeImpl;
import org.hibernate.usertype.CompositeUserType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Custom User-Type for storing RenderingModes in a single table within 2 columns
 * Hibernate 7 no longer supports multi-column property types, so this is mapped as a component in the form:
 * <pre>
 *   <component name="renderingMode" class="org.openmrs.module.reporting.report.service.db.RenderingModeType">
 *     <property name="argument" column="renderer_argument" type="string" access="org.openmrs.module.reporting.report.service.db.RenderingModeType$Access"/>
 *     <property name="renderer" column="renderer_type" type="string" access="org.openmrs.module.reporting.report.service.db.RenderingModeType$Access"/>
 *   </component>
 * </pre>
 * The component properties must be declared in alphabetical order, as Hibernate indexes them that way, and must use
 * {@link Access} so that their values are read through {@link #getPropertyValue(RenderingMode, int)}.
 */
public class RenderingModeType implements CompositeUserType<RenderingMode> {

	/**
	 * @see CompositeUserType#embeddable()
	 */
	public Class<?> embeddable() {
		return RenderingModeColumns.class;
	}

	/**
	 * @see CompositeUserType#returnedClass()
	 */
	public Class<RenderingMode> returnedClass() {
		return RenderingMode.class;
	}
	
	/**
	 * @see CompositeUserType#isMutable()
	 */
	public boolean isMutable() {
		return true;
	}

	/**
	 * @return the column value for the given property index: 0 = argument, 1 = renderer class name
	 * @see CompositeUserType#getPropertyValue(Object, int)
	 */
	public Object getPropertyValue(RenderingMode mode, int property) throws HibernateException {
		if (mode == null) {
			return null;
		}
		if (property == 0) {
			return mode.getArgument();
		}
		return mode.getRenderer() == null ? null : mode.getRenderer().getClass().getName();
	}

	/**
	 * @see CompositeUserType#instantiate(ValueAccess)
	 */
	public RenderingMode instantiate(ValueAccess values) {
		String rendererClass = values.getValue(1, String.class);
		if (StringUtils.isEmpty(rendererClass)) { return null; }
		String argument = values.getValue(0, String.class);
		ReportRenderer r = null;
		try {
			r = (ReportRenderer) Context.loadClass(rendererClass).newInstance();
		}
		catch (Exception e) {
			throw new HibernateException("Error instantiating a new reporting renderer from " + rendererClass, e);
		}
		return new RenderingMode(r, r.getClass().getSimpleName(), argument, null);
	}
	
	/**
	 * @see CompositeUserType#deepCopy(Object)
	 */
	public RenderingMode deepCopy(RenderingMode value) throws HibernateException {
		if (value == null) return null;
		RenderingMode toCopy = value;
		return new RenderingMode(toCopy.getRenderer(), toCopy.getLabel(), toCopy.getArgument(), toCopy.getSortWeight());
	}

	/**
	 * @see CompositeUserType#replace(Object, Object, Object)
	 */
	public RenderingMode replace(RenderingMode original, RenderingMode target, Object owner) throws HibernateException {
		return original;
	}
	
	/** 
	 * @see CompositeUserType#equals(Object, Object)
	 */
	public boolean equals(RenderingMode x, RenderingMode y) throws HibernateException {
		return x != null && x.equals(y);
	}

	/** 
	 * @see CompositeUserType#hashCode(Object)
	 */
	public int hashCode(RenderingMode x) throws HibernateException {
		return x.hashCode();
	}
	
	/**
	 * @see CompositeUserType#disassemble(Object)
	 */
	public Serializable disassemble(RenderingMode value) throws HibernateException {
		return (Serializable) deepCopy(value);
	}

	/**
	 * @see CompositeUserType#assemble(Serializable, Object)
	 */
	public RenderingMode assemble(Serializable cached, Object owner) throws HibernateException {
		return deepCopy((RenderingMode) cached);
	}

	/**
	 * Describes the 2 columns that a RenderingMode is stored in
	 */
	public static class RenderingModeColumns {

		private String argument;

		private String renderer;
	}

	/**
	 * Reads the component properties through this type, as hbm.xml mappings cannot configure this themselves
	 */
	public static class Access extends PropertyAccessStrategyCompositeUserTypeImpl {

		public Access() {
			super(new RenderingModeType(), Arrays.asList("argument", "renderer"), Arrays.<Type>asList(String.class, String.class));
		}
	}
}