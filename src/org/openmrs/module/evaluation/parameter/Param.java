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
package org.openmrs.module.evaluation.parameter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a particular field in a Parameterizable 
 * as a "Parameter" of that class
 */
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Param {
	
	/**
	 * If supplied, this will use the given value as the parameter name
	 * If it is not supplied, consumers will use their default naming method,
	 * which is expected to be just the name of the field that the annotation is on
	 */
	public String name() default "";
	
	/**
	 * If supplied, this will use the given value as the display name
	 * for this Parameter.  labelCode will override labelText if both are specified.
	 */
	public String labelText() default "";
	
	/**
	 * If supplied, this will use a localized message that has been configured
	 * with the given code as the display name for this Parameter.
	 */
	public String labelCode() default "";
	
	/**
	 * If set to true, this indicates to a consumer of this Parameter that it must
	 * have a non-null value set to be valid
	 */
	public boolean required() default false;
}
