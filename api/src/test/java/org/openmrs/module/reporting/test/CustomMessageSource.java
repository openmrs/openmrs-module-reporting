/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.messagesource.MutableMessageSource;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.messagesource.PresentationMessageMap;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 * Registers the custom message source service
 */
@Component
public class CustomMessageSource extends AbstractMessageSource implements MutableMessageSource, ApplicationContextAware {
	
	protected static final Log log = LogFactory.getLog(CustomMessageSource.class);
	private Map<Locale, PresentationMessageMap> cache = null;
	private boolean showMessageCode = false;
	
	public static final String GLOBAL_PROPERTY_SHOW_MESSAGE_CODES = "custommessage.showMessageCodes";
	
	/**
	 * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		MessageSourceService svc = (MessageSourceService)context.getBean("messageSourceServiceTarget");
		MessageSource activeSource = svc.getActiveMessageSource();
		setParentMessageSource(activeSource);
		svc.setActiveMessageSource(this);
	}
	
	/**
	 * @return the cached messages, merged from the custom source and the parent source
	 */
	public synchronized Map<Locale, PresentationMessageMap> getCachedMessages() {
		if (cache == null) {
			refreshCache();
		}
		return cache;
	}
	
	/**
	 * @return all message codes defined in the system
	 */
	public Set<String> getAllMessageCodes() {
		return getAllMessagesByCode().keySet();
	}
	
	/**
	 * @return a Map from code to Map of Locale string to message
	 */
	public Map<String, Map<Locale, PresentationMessage>> getAllMessagesByCode() {
		Map<String, Map<Locale, PresentationMessage>> ret = new TreeMap<String, Map<Locale, PresentationMessage>>();
		Map<Locale, PresentationMessageMap> m = getCachedMessages();
		for (Locale locale : m.keySet()) {
			PresentationMessageMap pmm = m.get(locale);
			for (String code : pmm.keySet()) {
				Map<Locale, PresentationMessage> messagesForCode = ret.get(code);
				if (messagesForCode == null) {
					messagesForCode = new LinkedHashMap<Locale, PresentationMessage>();
					ret.put(code, messagesForCode);
				}
				messagesForCode.put(locale, pmm.get(code));
			}
		}
		return ret;
	}
	
	/**
	 * @param pm the presentation message to add to the cache
	 * @param override if true, should override any existing message
	 */
	public void addPresentationMessageToCache(PresentationMessage pm, boolean override) {
		PresentationMessageMap pmm = getCachedMessages().get(pm.getLocale());
		if (pmm == null) {
			pmm = new PresentationMessageMap(pm.getLocale());
			getCachedMessages().put(pm.getLocale(), pmm);
		}
		if (pmm.get(pm.getCode()) == null || override) {
			pmm.put(pm.getCode(), pm);
		}
	}
	
	/**
	 * Refreshes the cache, merged from the custom source and the parent source
	 */
	public synchronized void refreshCache() {
	    Map<String, Locale> messageProperties = new LinkedHashMap<String, Locale>();
	    messageProperties.put("messages.properties", Locale.ENGLISH);
        messageProperties.put("messages_fr.properties", Locale.FRENCH);
		cache = new HashMap<Locale, PresentationMessageMap>();
		for (Map.Entry<String, Locale> entry : messageProperties.entrySet()) {
            PresentationMessageMap pmm = new PresentationMessageMap(entry.getValue());
            Properties messages = ObjectUtil.loadPropertiesFromClasspath(entry.getKey());
            for (String code : messages.stringPropertyNames()) {
                String message = messages.getProperty(code);
                message = message.replace("{{", "'{{'");
                message = message.replace("}}", "'}}'");
                pmm.put(code, new PresentationMessage(code, entry.getValue(), message, null));
            }
            cache.put(entry.getValue(), pmm);
        }
	}

	/**
	 * @see MutableMessageSource#getLocales()
	 */
	@Override
	public Collection<Locale> getLocales() {
		MutableMessageSource m = getMutableParentSource();
		Set<Locale> s = new HashSet<Locale>(m.getLocales());
		s.addAll(cache.keySet());
		return s;
	}

	/**
	 * @see MutableMessageSource#publishProperties(Properties, String, String, String, String)
	 */
	@SuppressWarnings("deprecation")
	public void publishProperties(Properties props, String locale, String namespace, String name, String version) {
        try {
            Class c = getMutableParentSource().getClass();
            Method m = c.getMethod("publishProperties", Properties.class, String.class, String.class, String.class, String.class);
            m.invoke(getMutableParentSource(), props, locale, namespace, name, version);
        }
        catch (Exception e) {
            // DO NOTHING
        }
	}

	/**
	 * @see MutableMessageSource#getPresentations()
	 */
	@Override
	public Collection<PresentationMessage> getPresentations() {
		Collection<PresentationMessage> ret = new ArrayList<PresentationMessage>();
		for (PresentationMessageMap pmm : getCachedMessages().values()) {
			ret.addAll(pmm.values());
		}
		return ret;
	}

	/**
	 * @see MutableMessageSource#getPresentationsInLocale(Locale)
	 */
	@Override
	public Collection<PresentationMessage> getPresentationsInLocale(Locale locale) {
		PresentationMessageMap pmm = getCachedMessages().get(locale);
		if (pmm == null) {
			return new HashSet<PresentationMessage>();
		}
		return pmm.values();
	}

	/**
	 * @see MutableMessageSource#addPresentation(PresentationMessage)
	 */
	@Override
	public void addPresentation(PresentationMessage message) {
		addPresentationMessageToCache(message, true);
	}

	/**
	 * @see MutableMessageSource#getPresentation(String, Locale)
	 */
	@Override
	public PresentationMessage getPresentation(String code, Locale locale) {
		PresentationMessageMap pmm = getCachedMessages().get(locale);
		if (pmm == null) {
			return null;
		}
		return pmm.get(code);
	}

	/**
	 * @see MutableMessageSource#removePresentation(PresentationMessage)
	 */
	@Override
	public void removePresentation(PresentationMessage message) {
		PresentationMessageMap pmm = getCachedMessages().get(message.getLocale());
		if (pmm != null) {
			pmm.remove(message.getCode());
		}
		getMutableParentSource().removePresentation(message);
	}

	/**
	 * @see MutableMessageSource#merge(MutableMessageSource, boolean)
	 */
	@Override
	public void merge(MutableMessageSource fromSource, boolean overwrite) {
		getMutableParentSource().merge(fromSource, overwrite);
	}

	/**
	 * @see AbstractMessageSource#resolveCode(String, Locale)
	 */
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		if (showMessageCode) {
			return new MessageFormat(code);
		}
		PresentationMessage pm = getPresentation(code, locale); // Check exact match
		if (pm == null) {
			if (locale.getVariant() != null) {
				pm = getPresentation(code, new Locale(locale.getLanguage(), locale.getCountry())); // Try to match language and country
				if (pm == null) {
					pm = getPresentation(code, new Locale(locale.getLanguage())); // Try to match language only
				}
			}
		}
		if (pm != null) {
			return new MessageFormat(pm.getMessage());
		}
		return null;
	}

	/**
	 * For some reason, this is needed to get the default text option in message tags working properly
	 * @see AbstractMessageSource#getMessageInternal(String, Object[], Locale)
	 */
	@Override
	protected String getMessageInternal(String code, Object[] args, Locale locale) {
		String s = super.getMessageInternal(code, args, locale);
		if (s == null || s.equals(code)) {
			return null;
		}
		return s;
	}
	
	/**
	 * Convenience method to get the parent message source as a MutableMessageSource
	 */
	public MutableMessageSource getMutableParentSource() {
		return (MutableMessageSource) getParentMessageSource();
	}
}
