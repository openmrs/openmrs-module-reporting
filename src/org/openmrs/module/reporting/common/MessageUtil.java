package org.openmrs.module.reporting.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;

/**
 * A utility class for common messaging and localization methods
 */
public class MessageUtil {
	
	protected static Log log = LogFactory.getLog(MessageUtil.class);
	
	/**
	 * Return the translation for the given key
	 * @param s the key to lookup
	 * @return the translation of the given key
	 */
	public static String translate(String s) {
    	return Context.getMessageSourceService().getMessage(s);
    }
	
	/**
	 * Return the translation for the given key
	 * Returns the replacement value if no suitable translation is found
	 */
	public static String translate(String s, String replacement) {
		if (ObjectUtil.notNull(s)) {
			String t = translate(s);
			if (ObjectUtil.notNull(t) && !ObjectUtil.areEqualStr(s, t)) {
				return t;
			}
		}
		return replacement;
	}
	
	/**
	 * Utility Method to return a display label for a class annotated as Localized
	 */
	public static String getDisplayLabel(Class<?> c) {
		String label = c.getSimpleName();
		Localized l = c.getAnnotation(Localized.class);
		if (l != null && ObjectUtil.notNull(l.value())) {
			label = MessageUtil.translate(l.value(), label);
		}
		return label;
	}
}
