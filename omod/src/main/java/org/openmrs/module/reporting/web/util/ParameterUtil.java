/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openmrs.annotation.Handler;
import org.openmrs.module.htmlwidgets.web.handler.WidgetHandler;
import org.openmrs.module.htmlwidgets.web.html.Option;
import org.openmrs.util.HandlerUtil;


public class ParameterUtil {

	/** 
	 * FIXME Not sure if this is where these methods belong.
	 */
	@SuppressWarnings("unchecked")
	public static List<Option> getSupportedCollectionTypes() {
		List<Option> collectionTypes = new ArrayList<Option>();
		collectionTypes.add(new Option(null, List.class.getSimpleName(), null, List.class.getName()));						
		collectionTypes.add(new Option(null, Set.class.getSimpleName(), null, Set.class.getName()));	
		return collectionTypes;
	}
	
	
	/** 
	 * FIXME Not sure if this is where these methods belong.
	 */
	public static List<Option> getSupportedTypes() {
		List<Option> ret = new ArrayList<Option>();
		for (WidgetHandler e : HandlerUtil.getHandlersForType(WidgetHandler.class, null)) {
			Handler handlerAnnotation = e.getClass().getAnnotation(Handler.class);
			if (handlerAnnotation != null) {
				Class<?>[] types = handlerAnnotation.supports();
				if (types != null) {
					for (Class<?> type : types) {						
						ret.add(new Option(null, type.getSimpleName(), null, type.getName()));						
					}
				}
			}
		}
		Collections.sort(ret);
		return ret;
	}
	
}