package org.openmrs.module.reporting.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * A utility class for common messaging and localization methods
 */
public class MessageUtil {
	
	protected static Log log = LogFactory.getLog(MessageUtil.class);

	private static MessageSource messageSource;
	
	/**
	 * @param messageCode the messageCode to lookup
	 * @should return the translation for the given messageCode for the context locale
	 * @should return the messageCode if no translation is found
	 */
	public static String translate(String messageCode) {
    	return translate(messageCode, null, messageCode, Context.getLocale());
    }

	/**
	 * @param messageCode the messageCode to lookup
	 * @should return the translation for the given messageCode for the context locale
	 * @should return the messageCode if no translation is found
	 */
	public static String translate(String messageCode, Locale locale) {
		return translate(messageCode, null, messageCode, locale);
	}

	/**
	 * @param messageCode the messageCode to lookup
	 * @should return the translation for the given messageCode for the context locale
	 * @should return the defaultMessage if no translation is found
	 */
	public static String translate(String messageCode, String defaultMessage) {
		return translate(messageCode, null, defaultMessage, Context.getLocale());
	}

	/**
	 * @param messageCode the messageCode to lookup
	 * @should return the translation for the given messageCode for the given locale
	 * @should return the defaultMessage if no translation is found
	 */
	public static String translate(String messageCode, String defaultMessage, Locale locale) {
		return translate(messageCode, null, defaultMessage, locale);
	}

	/**
	 * @param messageCode the messageCode to lookup
	 * @should Return the translation for the given messageCode for the given arguments and locale
	 * @should return the defaultValue if no translation is found
	 */
	public static String translate(String messageCode, Object[] args, String defaultMessage, Locale locale) {
		return getMessageSource().getMessage(messageCode, args, defaultMessage, locale);
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

	/**
	 * @return the active message source, returning from the cache if present, otherwise from the service
	 */
	public static MessageSource getMessageSource() {
		if (messageSource == null) {
			messageSource = Context.getMessageSourceService().getActiveMessageSource();
		}
		return messageSource;
	}

	/**
	 * @param messageSource enables configuring a specific message source to be used, or to reset the cached value
	 */
	public static void setMessageSource(MessageSource messageSource) {
		MessageUtil.messageSource = messageSource;
	}
}
