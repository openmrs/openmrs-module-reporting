package org.openmrs.module.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.common.DisplayLabel;

/**
 * A utility class for common messaging and localization methods
 */
public class MessageUtil {
	
	protected static Log log = LogFactory.getLog(MessageUtil.class);
	
	/**
	 * Return a display label for the passed class, as configured through the {@link DisplayLabel} annotation
	 * If no annotation is found, or the defaults resolve to null or empty String, return the full class name
	 * @param clazz
	 * @return
	 */
	public static String getDisplayLabel(Class<?> clazz) {
		DisplayLabel ann = clazz.getAnnotation(DisplayLabel.class);
		if (ann != null) {
			String label = null;
			if (ann.labelCode() != null) {
				label = Context.getMessageSourceService().getMessage(ann.labelCode());
				if (StringUtils.isNotEmpty(label)) {
					return label;
				}
			}
			if (StringUtils.isNotEmpty(ann.labelDefault())) {
				return ann.labelDefault();
			}
		}
		return clazz.getName();
	}
}
