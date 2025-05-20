/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a particular field on an Object as one that
 * affects how it is evaluated.
 */
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ConfigurationProperty {
	
	/**
	 * If set to true, it indicates that this field must be
	 * populated prior to Evaluation of the object
	 */
	public boolean required() default false;
	
	/**
	 * Optional text which can be set as a description for this property.  
	 * This can be either a message code or a simple string.
	 * The intention is for this to allow user interfaces to provide better information for users as to what this
	 * configuration property represents.
	 */
	public String value() default "";
	
	/**
	 * Optional grouping category which can be used to organize related ConfigurationProperties together.
	 */
	public String group() default "";
	
	/**
	 * Optional display format. e.g textarea
	 */
	public String displayFormat() default "";
	
	/**
	 * Optional display attributes. e.g rows=5|cols=100
	 */
	public String displayAttributes() default "";
}
