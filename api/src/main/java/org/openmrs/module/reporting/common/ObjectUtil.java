package org.openmrs.module.reporting.common;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.OpenmrsData;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.PersonName;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.util.OpenmrsUtil;

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
		    sb.append(e.getKey() + keyValueSeparator + e.getValue());
		}
		return sb.toString();
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
		return format(o, null);
	}
	
	/**
	 * @return a formatted version of the object suitable for display
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String format(Object o, String format) {
		if (o == null) { return ""; }
		if (o instanceof Date) {
			DateFormat df = Context.getDateFormat();
			if (ObjectUtil.notNull(format)) {
				df = new SimpleDateFormat(format);
			}
			return df.format((Date)o);
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
			if (notNull(format)) {
				return format(((IndicatorResult)o).getValue(), format);
			}
		}
		if (o instanceof Cohort) {
			return Integer.toString(((Cohort)o).getSize());
		}
		if (o instanceof Number) {
			if (notNull(format)) {
				NumberFormat nf = NumberFormat.getInstance();
				nf.setGroupingUsed(false);
				try {
					nf.setMinimumFractionDigits(Integer.parseInt(format));
					nf.setMaximumFractionDigits(Integer.parseInt(format));
				}
				catch (Exception e) {}
				return nf.format((Number)o);
			}
		}
		if (o instanceof OpenmrsMetadata) {
			String name = ((OpenmrsMetadata) o).getName();
			if (name == null) {
				if (o instanceof ProgramWorkflow) {
					name = ((ProgramWorkflow)o).getConcept().getDisplayString();
				}
				else if (o instanceof ProgramWorkflowState) {
					name = ((ProgramWorkflowState)o).getConcept().getDisplayString();
				}
			}
			return name;
		}
		if (o instanceof OpenmrsData) {
			if (ObjectUtil.notNull(format)) {
				String[] formatSplit = format.split("\\|");
				String ret = formatSplit[0];
				try {
					int startIndex = ret.indexOf("{");
					int endIndex = ret.indexOf("}", startIndex+1);
					while (startIndex != -1 && endIndex != -1) {
						String propertyName = ret.substring(startIndex+1, endIndex);
						Object replacement = ReflectionUtil.getPropertyValue(o, propertyName);
						String newFormat = (replacement != null && formatSplit.length > 1 ? formatSplit[1] : null);
						replacement = ObjectUtil.format(replacement, newFormat);
						ret = ret.replace("{"+propertyName+"}", nvlStr(replacement, ""));
						startIndex = ret.indexOf("{");
						endIndex = ret.indexOf("}", startIndex+1);
					}
					return ret;
				}
				catch (Exception e) {
					log.warn("Unable to get property using converter with format: " + format, e);
				}
			}
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
	
	public static void sort(List collection, String sortSpecification) throws Exception {
		
		//If sort specification is null or "asc", we use natural order ascending.
		if (sortSpecification == null || sortSpecification.equalsIgnoreCase("asc")) {
			Collections.sort(collection);
			return;
		}
		
		//If sort specification is "desc", we use natural order descending.
		if (sortSpecification.equalsIgnoreCase("desc")) {
			Collections.sort(collection, Collections.reverseOrder());
			return;
		}
		
		//sort specification contains property name/s
		final String tokens[] = sortSpecification.split(",");
		
		Collections.sort(collection, new Comparator<Object>() {
			public int compare(Object left, Object right) {
				try{
					boolean sortAsc = true;
					for (String token : tokens) {
						String[] values = token.trim().split(" ");
						if (values.length > 1 && values[1].equalsIgnoreCase("desc")) {
							sortAsc = false;
						}
						
						String property = values[0];
						Object valueLeft = PropertyUtils.getNestedProperty(left, property);
						Object valueRight = PropertyUtils.getNestedProperty(right, property);
						
						//We put NULLs at the bottom.
						if (valueLeft == null)
							return 1;
						
						if (valueRight == null)
							return -1;
						
						if (!valueLeft.equals(valueRight)) {
							int ret = ((Comparable)valueLeft).compareTo(valueRight);
							if (!sortAsc) {
								ret = (ret == 1 ? -1 : 1);
							}
							return ret;
						}
						
						//else values are equal. Try next sort property
					}
				}
				catch(Exception ex) {
					log.error("Failed to compare: " + left + " and " + right, ex);
				}
				
				return 0; //values are equal for all sort properties
			}
			});
	}
}
