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
import org.hibernate.usertype.ParameterizedType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.serializer.ReportingSerializer;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Custom User-Type for storing Mapped objects in a single table within 2 columns
 * Hibernate 7 no longer supports multi-column property types, so this is mapped as a component whose class is a
 * subclass of this type bound to the type of the Mapped Parameterizable, in the form:
 * <pre>
 *		<component name="reportDefinition" class="org.openmrs.module.reporting.report.service.db.MappedDefinitionType$MappedReportDefinitionType">
 *			<property name="definition" column="report_definition_uuid" type="string" access="org.openmrs.module.reporting.report.service.db.MappedDefinitionType$Access"/>
 *			<property name="parameterMappings" column="report_definition_parameters" type="text" access="org.openmrs.module.reporting.report.service.db.MappedDefinitionType$Access"/>
 *		</component>
 * </pre>
 * The component properties must be declared in alphabetical order, as Hibernate indexes them that way, and must use
 * {@link Access} so that their values are read through {@link #getPropertyValue(Mapped, int)}.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MappedDefinitionType implements CompositeUserType<Mapped>, ParameterizedType {
	
	/**
	 * Property via ParameterizedType for storing the type of the Mapped Parameterizable
	 */
	private Class<? extends Definition> mappedType;

	public MappedDefinitionType() {
	}

	protected MappedDefinitionType(Class<? extends Definition> mappedType) {
		this.mappedType = mappedType;
	}

	/**
	 * @see CompositeUserType#embeddable()
	 */
	public Class<?> embeddable() {
		return MappedColumns.class;
	}

	/**
	 * @see CompositeUserType#returnedClass()
	 */
	public Class<Mapped> returnedClass() {
		return Mapped.class;
	}
	
	/**
	 * @see CompositeUserType#isMutable()
	 */
	public boolean isMutable() {
		return true;
	}

	/**
	 * @return the column value for the given property index: 0 = definition uuid, 1 = serialized parameter mappings
	 * @see CompositeUserType#getPropertyValue(Object, int)
	 */
	public Object getPropertyValue(Mapped m, int property) throws HibernateException {
		if (m == null || m.getParameterizable() == null) {
			return null;
		}
		if (property == 0) {
			return m.getParameterizable().getUuid();
		}
		if (m.getParameterMappings() != null && !m.getParameterMappings().isEmpty()) {
			try {
				return Context.getSerializationService().serialize(m.getParameterMappings(), ReportingSerializer.class);
			}
			catch (Exception e) {
				throw new HibernateException("Unable to serialize mappings for definition", e);
			}
		}
		return null;
	}

	/**
	 * @see CompositeUserType#instantiate(ValueAccess)
	 */
	public Mapped instantiate(ValueAccess values) {
		String parameterizableUuid = values.getValue(0, String.class);
		if (StringUtils.isEmpty(parameterizableUuid)) { return null; }
		String serializedMappings = values.getValue(1, String.class);
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
	 * @see CompositeUserType#deepCopy(Object)
	 */
	public Mapped deepCopy(Mapped value) throws HibernateException {
		if (value == null) return null;
		Mapped toCopy = value;
		Mapped m = new Mapped();
		m.setParameterizable(toCopy.getParameterizable());
		m.setParameterMappings(new HashMap<String, Object>(toCopy.getParameterMappings()));
		return m;
	}

	/**
	 * @see CompositeUserType#replace(Object, Object, Object)
	 */
	public Mapped replace(Mapped original, Mapped target, Object owner) throws HibernateException {
		return original;
	}
	
	/** 
	 * @see CompositeUserType#equals(Object, Object)
	 */
	public boolean equals(Mapped x, Mapped y) throws HibernateException {
		return x != null && x.equals(y);
	}

	/** 
	 * @see CompositeUserType#hashCode(Object)
	 */
	public int hashCode(Mapped x) throws HibernateException {
		return x.hashCode();
	}
	
	/**
	 * @see CompositeUserType#disassemble(Object)
	 */
	public Serializable disassemble(Mapped value) throws HibernateException {
		return deepCopy(value);
	}

	/**
	 * @see CompositeUserType#assemble(Serializable, Object)
	 */
	public Mapped assemble(Serializable cached, Object owner) throws HibernateException {
		return deepCopy((Mapped) cached);
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

	/**
	 * Describes the 2 columns that a Mapped definition is stored in
	 */
	public static class MappedColumns {

		private String definition;

		private String parameterMappings;
	}

	/**
	 * Reads the component properties through this type, as hbm.xml mappings cannot configure this themselves
	 */
	public static class Access extends PropertyAccessStrategyCompositeUserTypeImpl {

		public Access() {
			super(new MappedDefinitionType(), Arrays.asList("definition", "parameterMappings"), Arrays.<Type>asList(String.class, String.class));
		}
	}

	/**
	 * Type for storing a Mapped CohortDefinition
	 */
	public static class MappedCohortDefinitionType extends MappedDefinitionType {

		public MappedCohortDefinitionType() {
			super(CohortDefinition.class);
		}
	}

	/**
	 * Type for storing a Mapped ReportDefinition
	 */
	public static class MappedReportDefinitionType extends MappedDefinitionType {

		public MappedReportDefinitionType() {
			super(ReportDefinition.class);
		}
	}
}