package org.openmrs.module.reporting.common;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Obs;
import org.openmrs.OpenmrsData;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.OpenmrsObject;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Generically useful utility class for working with Objects
 */
public class ObjectUtil {
	
	protected static Log log = LogFactory.getLog(ObjectUtil.class);

	/**
	 * Returns a String representation of the passed Map
	 */
    public static String toString(Map<?, ?> m, String keyValueSeparator, String entrySeparator) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<?, ?> e : m.entrySet()) {
			if (sb.length() > 0) {
				sb.append(entrySeparator);
			}
		    sb.append(ObjectUtil.format(e.getKey()) + keyValueSeparator + ObjectUtil.format(e.getValue()));
		}
		return sb.toString();
    }

	/**
	 * @param toParse the string to parse into a Map<String, String>
	 * @param keyValueSeparator the string that separates the entries for the Map, if null defaults to "="
	 * @param entrySeparator the string that separates each key/value pair in the Map, if null defaults to ","
	 * @return
	 */
	public static Map<String, String> toMap(String toParse, String keyValueSeparator, String entrySeparator) {
		Map<String, String> ret = new LinkedHashMap<String, String>();
		if (notNull(toParse)) {
			for (String entry : StringUtils.splitByWholeSeparator(toParse, nvlStr(entrySeparator, ","))) {
				String[] keyValue = StringUtils.splitByWholeSeparator(entry, nvlStr(keyValueSeparator, "="), 2);
				ret.put(keyValue[0], keyValue[1]);
			}
		}
		return ret;
	}

	/**
	 * @param toParse the string to parse into a Map<String, String>. Expected format is key1=value1,key2=value2...
	 * @return
	 */
	public static Map<String, String> toMap(String toParse) {
		return toMap(toParse, "=", ",");
	}

	/**
	 * @return Map<String, Object> given passed keys and values.  Will convert keys to String if needed
	 */
	public static Map<String, Object> toMap(Object...keysAndValues) {
		Map<String, Object> m = new HashMap<String, Object>();
		for (int i=0; i<keysAndValues.length; i+=2) {
			m.put(keysAndValues[i].toString(), keysAndValues[i+1]);
		}
		return m;
	}
    
	/**
	 * Returns a String representation of the passed Map
	 */
    public static String toString(Map<?, ?> m, String sep) {
    	return toString(m, " -> ", sep);
    }
    
    /**
     * Returns a String representation of the passed Object[]
     */
    public static String toString(String separator, Object...elements) {
    	StringBuffer sb = new StringBuffer();
    	for (Object o : elements) {
    		if (notNull(o)) {
    			if (sb.length() > 0) {
    				sb.append(separator);
    			}
    			sb.append(o.toString());
    		}
    	}
    	return sb.toString();
    }
    
    /**
     * Returns true if object is null or empty String
     */
    public static boolean isNull(Object o) {
    	return o == null || o.equals("");
    }
    
    /**
     * Returns true if object is not null and not empty string
     */
    public static boolean notNull(Object o) {
    	return !isNull(o);
    }
    
    /**
     * Returns the passed object, if not null, or replacement otherwise
     */
    public static <T extends Object> T nvl(T o, T replacement) {
    	return (isNull(o) ? replacement : o);
    }

    /**
     * Returns the localized name of the passed OpenmrsMetadata object
     * @param o
     * @return a String or null if no localized name is available
     */
    public static String getLocalization(OpenmrsMetadata o, Locale locale) {
        if (o != null) {
            String simpleName = o.getClass().getSimpleName();
            int underscoreIndex = simpleName.indexOf("_$");
            if (underscoreIndex > 0) {
                simpleName = simpleName.substring(0, underscoreIndex);
            }
            String code = "ui.i18n." + simpleName + ".name." + o.getUuid();
            String localization = MessageUtil.translate(code, null, locale);
            if (localization != null && !localization.equals(code)) {
				return localization;
			}
        }
        return null;
    }

    /**
     * Returns toString on the passed object if not null, or on replacement otherwise
     */
    public static String nvlStr(Object o, Object replacement) {
    	return (isNull(o) ? nvl(replacement, "").toString() : o.toString());
    }
    
    /**
     * Checks whether the testIfNull parameter is null, if so returns valueIfNull, otherwise returns valueIfNotNull
     */
    public static <T extends Object> T decode(Object testIfNull, T valueIfNull, T valueIfNotNull) {
    	return (isNull(testIfNull) ? valueIfNull : valueIfNotNull);
    }
    
    /**
     * Checks whether the testIfNull parameter is null, if so returns valueIfNull as String, otherwise returns valueIfNotNull as String
     */
    public static String decodeStr(Object testIfNull, Object valueIfNull, Object valueIfNotNull) {
    	return (isNull(testIfNull) ? nvlStr(valueIfNull, "") : nvlStr(valueIfNotNull, ""));
    }
    
    /**
     * Checks whether two objects are equal.  This is null-safe and considers null and empty string to be equal
     */
    public static boolean areEqual(Object o1, Object o2) {
    	Object obj1 = nvl(o1, "");
    	Object obj2 = nvl(o2, "");
    	return obj1.equals(obj2);
    }
    
    /**
     * @return a null safe comparison between objects
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static int nullSafeCompare(Object o1, Object o2) {
    	if (o1 == null) {
    		return (o2 == null ? 0 : -1);
    	}
    	else if (o2 == null) {
    		return 1;
    	}
    	Comparable c1 = (o1 instanceof Comparable ? (Comparable) o1 : o1.toString());
    	Comparable c2 = (o2 instanceof Comparable ? (Comparable) o2 : o2.toString());
    	return c1.compareTo(c2);
    }
    
    /**
     * Checks whether two objects have equal toString representations.  
     * This is null-safe and considers null and empty string to be equal
     */
    public static boolean areEqualStr(Object o1, Object o2) {
    	String obj1 = nvlStr(o1, "");
    	String obj2 = nvlStr(o2, "");
    	return obj1.equals(obj2);
    }
    
    /**
     * Checks whether o1 is equal to any of the objects in the passed array o2
     */
    public static boolean equalToAny(Object o1, Object...o2) {
    	if (o1 != null && o2 != null) {
    		for (Object o : o2) {
    			if (o1.equals(o)) {
    				return true;
    			}
    		}
    	}
    	return false;
    }

	/**
	 * Return a String in which the first occurrence of toReplace was replaced with replaceWith
	 */
	public static String replaceFirst(String inputString, String toReplace, String replaceWith) {
		return StringUtils.replaceOnce(inputString, toReplace, replaceWith);
	}
    
    /**
     * Returns the first non-null value in the passed array
     */
    public static <T extends Object> T coalesce(T...tests) {
    	if (tests != null) {
	    	for (int i=0; i<tests.length; i++) {
	    		if (notNull(tests[i])) {
	    			return tests[i];
	    		}
	    	}
    	}
    	return null;
    }
    
    /**
     * Returns true if any value in the passed array is non-null
     */
    public static boolean anyNotNull(Object...tests) {
    	return coalesce(tests) != null;
    }
    
    /**
     * Returns the number of non-null objects in the passed array
     */
    public static int numNotNull(Object...tests) {
    	int count=0;
    	if (tests != null) {
	    	for (int i=0; i<tests.length; i++) {
	    		if (notNull(tests[i])) {
	    			count++;
	    		}
	    	}
    	}
    	return count;
    }
    
    /**
     * Returns true if the passed collection contains any String in the passed String[]
     */
	public static boolean containsAny(Collection<String> c, String...toCheck) {
		if (c != null) {
			for (String s : toCheck) {
				if (c.contains(s)) {
					return true;
				}
			}
		}
		return false;
	}
	
    /**
     * Returns true if the passed collection contains all Strings in the passed String[]
     */
	public static boolean containsAll(Collection<String> c, String...toCheck) {
		if (c != null) {
			for (String s : toCheck) {
				if (!c.contains(s)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
    /**
     * Returns a camelCase representation of the passed String
     */
	public static String toCamelCase(String s) {
		StringBuilder sb = new StringBuilder();
		boolean nextUpper = false;
		for (char c : s.toCharArray()) {
			if (Character.isWhitespace(c)) {
				nextUpper = true;
			}
			else {
				if (Character.isLetterOrDigit(c)) {
					sb.append((nextUpper ? Character.toUpperCase(c) : Character.toLowerCase(c)));
					nextUpper = false;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Returns a readable string based on the passed camelCase representation
	 */
	public static String fromCamelCase(String s) {
		StringBuilder sb = new StringBuilder();
		if (ObjectUtil.notNull(s)) {
			char[] chars = s.toCharArray();
			boolean inNumber = false;
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				if (i == 0) {
					sb.append(Character.toUpperCase(c));
				}
				else {
					boolean isLetter = Character.isLetter(c);
					if (Character.isUpperCase(c) || (!isLetter && !inNumber) || (isLetter && inNumber)) {
						sb.append(" ");
					}
					inNumber = !Character.isLetter(c);
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Check whether the given String contains whitespace
	 * @param s the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not empty and contains at least 1 whitespace character
	 * @see java.lang.Character#isWhitespace
	 */
	public static boolean containsWhitespace(String s) {
		if (s != null) {
			for (char c : s.toCharArray()) {
				if (Character.isWhitespace(c)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Utility method to add an Object to a List if another passed Object is not null
	 */
	public static <T> void addIfNotNull(Collection<T> listToAddTo, T itemToAddToList, Object objectToCheckIfNull) {
		if (objectToCheckIfNull != null) {
			listToAddTo.add(itemToAddToList);
		}
	}
	
	/**
	 * Utility method to trim a string without knowing whether it needs trimming beforehand
	 */
	public static String trimStringIfNeeded(String value, Integer maxLength) {
		if (maxLength != null && value.length() > maxLength) {
			return value.substring(0, maxLength);
		} else {
			return value;
		}
	}
	
	/**
	 * Utility method to verify a string's length without knowing whether it is null or not
	 */
	public static String verifyStringLength(String value, Integer maxLength) {
		if (maxLength != null && value.length() > maxLength) {
			throw new RuntimeException(
					"Maximum width for column with value '"+value+"' has been exceeded by "+ (value.length()-maxLength)+" characters.");
		} else {
			return value;
		}
	}
	
	/**
	 * @return a formatted version of the object suitable for display
	 */
	public static String format(Object o) {
		return format(o, null, null);
	}

    /**
     * @return a formatted version of the object suitable for display
     */
    public static String format(Object o, String format) {
        return format(o, format, null);
    }

	public static String formatDate(Date d, String format, Locale locale) {
		DateFormat df = null;
		String formatString = ObjectUtil.nvl(format, ReportingConstants.GLOBAL_PROPERTY_DEFAULT_DATE_FORMAT());
		if (ObjectUtil.notNull(formatString)) {
			try {
				df = new SimpleDateFormat(formatString, locale);
			}
			catch (Exception e) {
				log.warn("Invalid date format <" + format + "> specified, using defaults.");
			}
		}
		if (df == null) {
			df = Context.getDateFormat();
		}
		return df.format(d);
	}

	public static String formatNumber(Number n, String format, Locale locale) {
		if (notNull(format)) {
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			try {
				nf.setMinimumFractionDigits(Integer.parseInt(format));
				nf.setMaximumFractionDigits(Integer.parseInt(format));
				return nf.format(n);
			}
			catch (Exception e) {
				log.warn("Invalid number format <" + format + "> specified, using defaults.");
			}
		}
		return n.toString();
	}

	public static String formatMetadata(OpenmrsMetadata metadata, String format, Locale locale) {
		String name = getLocalization(metadata, locale);
		if (StringUtils.isBlank(name)) {
			name = metadata.getName();
			if (name == null) {
				if (metadata instanceof ProgramWorkflow) {
					name = formatConcept(((ProgramWorkflow)metadata).getConcept(), format, locale);
				}
				else if (metadata instanceof ProgramWorkflowState) {
					name = formatConcept(((ProgramWorkflowState)metadata).getConcept(), format, locale);
				}
			}
		}
		if (StringUtils.isBlank(name)) {
			return nullSafeToString(metadata);
		}
		return name;
	}

	public static String formatConcept(Concept c, String format, Locale locale) {
		if (!HibernateUtil.sessionContains(c)) {
			return "Concept#"+c.getId();
		}
		ConceptName name = c.getName(locale, false);
		if (name != null) {
			return name.getName();
		}
		else {
			return "Concept#"+c.getId();
		}
	}

	public static String formatOpenmrsData(OpenmrsData d, String format, Locale locale) {
		if (ObjectUtil.notNull(format)) {
			String ret = format;
			try {
				int startIndex = ret.indexOf("{");
				int endIndex = ret.indexOf("}", startIndex+1);
				while (startIndex != -1 && endIndex != -1) {
					String propertyAndFormat = ret.substring(startIndex+1, endIndex);
					String[] formatSplit = propertyAndFormat.split("\\|");
					String propertyName = formatSplit[0];
					Object replacement = ReflectionUtil.getPropertyValue(d, propertyName);
					String newFormat = (replacement != null && formatSplit.length > 1 ? formatSplit[1] : null);
					replacement = ObjectUtil.format(replacement, newFormat, locale);
					ret = ret.replace("{"+propertyAndFormat+"}", nvlStr(replacement, ""));
					startIndex = ret.indexOf("{");
					endIndex = ret.indexOf("}", startIndex+1);
				}
				return ret;
			}
			catch (Exception e) {
				log.warn("Unable to get property using converter with format: " + format, e);
			}
		}
		else if (d instanceof Person) {
			Person p = (Person)d;
			if (p.getPersonName() != null) {
				return p.getPersonName().getFullName();
			}
		}
		else {
			if (d instanceof Obs) {
				Obs obs = (Obs)d;
				if (obs.getValueNumeric() != null) {
					return formatNumber(obs.getValueNumeric(), format, locale);
				}
				else if (obs.getValueDatetime() != null) {
					return formatDate(obs.getValueDatetime(), format, locale);
				}
				else if (obs.getValueCoded() != null) {
					return formatConcept(obs.getValueCoded(), format, locale);
				}
				else {
					return obs.getValueAsString(locale);
				}
			}
		}
		return nullSafeToString(d);
	}

	public static String nullSafeToString(OpenmrsObject o) {
		try {
			return o.toString();
		}
		catch (Exception e) {
			log.debug("Exception calling toString on " + o.getClass().getSimpleName(), e);
		}
		return o.getUuid();
	}

	/**
	 * @return a formatted version of the object suitable for display
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String format(Object o, String format, Locale locale) {

		try {
			// if no locale passed in, use locale specified in global property, otherwise context default
			if (locale == null) {
				locale = ReportingConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE();
			}
			if (locale == null) {
				locale = Context.getLocale();
			}
			if (o == null) {
				return "";
			}
			if (o instanceof Date) {
				return formatDate((Date)o, format, locale);
			}
			if (o instanceof Map) {
				return toString((Map)o, nvl(format, ","));
			}
			if (o instanceof Collection) {
				return OpenmrsUtil.join((Collection)o, nvl(format, ","));
			}
			if (o instanceof Object[]) {
				return toString(nvl(format, ","), (Object[])o);
			}
			if (o instanceof IndicatorResult) {
				return format(((IndicatorResult)o).getValue(), format, locale);
			}
			if (o instanceof Cohort) {
				return Integer.toString(((Cohort)o).getSize());
			}
			if (o instanceof Number) {
				return formatNumber((Number)o, format, locale);
			}
			if (o instanceof OpenmrsMetadata) {
				return formatMetadata((OpenmrsMetadata)o, format, locale);
			}
			if (o instanceof Concept) {
				return formatConcept((Concept)o, format, locale);
			}
			if (o instanceof OpenmrsData) {
				return formatOpenmrsData((OpenmrsData)o, format, locale);
			}
		}
		catch (Exception e) {
			log.warn("Unable to format " + o.getClass().getSimpleName());
		}
		if (o instanceof OpenmrsObject) {
			return nullSafeToString((OpenmrsObject)o);
		}
		return o.toString();
	}
	
	public static String getNameOfCurrentUser() {
		return getNameOfUser(Context.getAuthenticatedUser());
	}
	
	public static String getNameOfUser(User user) {
		if (user != null) {
			PersonName pn = user.getPersonName();
			if (pn != null) {
				return pn.getFullName();
			}
			else {
				if (user.getUsername() != null) {
					return user.getUsername();
				}
			}
		}
		return "Unknown User";
	}
	
	public static boolean instanceOf(Object o, String className) {
		try {
			Class<?> c = Context.loadClass(className);
			if (c.isAssignableFrom(o.getClass())) {
				return true;
			}
		}
		catch (Exception e) {
			log.warn("Error performing instanceof check.  Object " + o + "; class: " + className, e);
		}
		return false;
	}
	
	/**
	 * Sorts a given Collection given the passed sortSpecification and returns it in a new List
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends Object> List<T> sort(Collection<T> collection, String sortSpecification) throws Exception {
		
		if (collection == null) {
			return null;
		}
		
		List ret = new ArrayList(collection);
		//If sort specification is null or "asc", we use natural order ascending.
		if (sortSpecification == null || sortSpecification.equalsIgnoreCase("asc")) {
			Collections.sort(ret);
		}
		//If sort specification is "desc", we use natural order descending.
		else if (sortSpecification.equalsIgnoreCase("desc")) {
			Collections.sort(ret, Collections.reverseOrder());
		}
		else {
			//sort specification based on property name/s
			BeanPropertyComparator comparator = new BeanPropertyComparator(sortSpecification);
			Collections.sort(ret, comparator);
		}
		return ret;
	}
	
	/**
	 * Simple utility method to return the full stack trace as a String for a Throwable
	 */
	public static String getStackTrace(Throwable t) {
	    Writer sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    t.printStackTrace(pw);
		return sw.toString();
	}
	
	/**
	 * @return a Properties object based on a properties file on the classpath at the specified location
	 */
	public static Properties loadPropertiesFromClasspath(String location) {
		Properties ret = new Properties();
		InputStream is = null;
		try {
			is = OpenmrsClassLoader.getInstance().getResourceAsStream(location);
			ret.load(is);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to load properties from classpath at " + location, e);
		}
		finally {
			IOUtils.closeQuietly(is);
		}
		return ret;
	}
}
