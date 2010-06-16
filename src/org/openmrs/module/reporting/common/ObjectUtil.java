package org.openmrs.module.reporting.common;

import java.util.Collection;
import java.util.Map;

/**
 * Generically useful utility class for working with Objects
 */
public class ObjectUtil {
    
	/**
	 * Returns a String representation of the passed Map
	 */
    public static String toString(Map<?, ?> m, String sep) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<?, ?> e : m.entrySet()) {
			if (sb.length() > 0) {
				sb.append(sep);
			}
		    sb.append(e.getKey() + " -> " + e.getValue());
		}
		return sb.toString();
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
}
