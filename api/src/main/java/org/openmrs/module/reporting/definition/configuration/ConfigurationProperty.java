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
}
