package org.openmrs.module.reporting.common;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DisplayLabel;

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
		if (ann != null && StringUtils.isNotEmpty(ann.value())) {
			String translation = Context.getMessageSourceService().getMessage(ann.value());
			if (StringUtils.isNotEmpty(translation)) {
				return translation;
			}
			return ann.value();
		}
		return clazz.getName();
	}
	
	/**
	 * Return the translation for the given key
	 * @param s the key to lookup
	 * @return the translation of the given key
	 */
	public static String translate(String s) {
    	return Context.getMessageSourceService().getMessage(s);
    }
}
