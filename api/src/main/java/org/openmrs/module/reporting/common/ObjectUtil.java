package org.openmrs.module.reporting.common;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.OpenmrsData;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.EmptyResult;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.data.BaseData;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.indicator.dimension.CohortDimensionResult;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.orm.hibernate3.HibernateTemplate;

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
     * Returns the locale of the passed OpenmrsMetadata object
     * @param o an OpenmrsMetadata
     * @return a String or null if no locale available
     */
    public static String getLocalization(OpenmrsMetadata o, Locale locale){
        if ( o != null ){
            String simpleName = o.getClass().getSimpleName();
            int underscoreIndex = simpleName.indexOf("_$");
            if (underscoreIndex > 0) {
                simpleName = simpleName.substring(0, underscoreIndex);
            }
            String code = "ui.i18n." + simpleName + ".name." + o.getUuid();
            String localization = Context.getMessageSourceService().getMessage(code, null, locale);
            if (localization == null || localization.equals(code)) {
                return null;
            } else {
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
		return format(o, null, null);
	}

    /**
     * @return a formatted version of the object suitable for display
     */
    public static String format(Object o, String format) {
        return format(o, format, null);
    }

	/**
	 * @return a formatted version of the object suitable for display
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String format(Object o, String format, Locale locale) {

        // if no locale passed in, use locale specified in global property, otherwise context default
        if (locale == null) {
            locale = ReportingConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE();
        }
        if (locale == null) {
            locale = Context.getLocale();
        }

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
			IndicatorResult r = (IndicatorResult)o;
			return format(r.getValue(), format, locale);
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
            String name = getLocalization((OpenmrsMetadata)o, locale);
            if (StringUtils.isBlank( name )){
                name = ((OpenmrsMetadata) o).getName();
                if (name == null) {
                    if (o instanceof ProgramWorkflow) {
                        name = ((ProgramWorkflow)o).getConcept().getDisplayString();
                    }
                    else if (o instanceof ProgramWorkflowState) {
                        name = ((ProgramWorkflowState)o).getConcept().getDisplayString();
                    }
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
						replacement = ObjectUtil.format(replacement, newFormat, locale);
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
			else {
				if (o instanceof Obs) {
					Obs obs = (Obs)o;
					if (obs.getValueNumeric() != null) {
						return format(obs.getValueNumeric());
					}
					else if (obs.getValueDatetime() != null) {
						return format(obs.getValueDatetime());
					}
                    else if (obs.getValueCoded() != null) {
                        return format(obs.getValueCoded().getBestName(locale));
                    }
					else {
						return obs.getValueAsString(locale);
					}
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
	

	/**
	 * Formats anything and prints it to sb. (Delegates to other methods here
	 * @param sb
	 * @param o
	 */
	public void printObject(StringBuilder sb, Object o) {
		try {
			if (o instanceof Result) {
				printResult(sb, (Result) o);
			} else if (o instanceof Collection) {
				for (Iterator<?> i = ((Collection) o).iterator(); i.hasNext(); ) {
					printObject(sb, i.next());
					if (i.hasNext())
						sb.append(", ");
				}
			} else if (o instanceof Date) {
				printDate(sb, (Date) o);
			} else if (o instanceof Concept) {
				printConcept(sb, (Concept) o);
			} else if (o instanceof Obs) {
				printObsValue(sb, (Obs) o);
			} else if (o instanceof User) {
				printUser(sb, (User) o);
			} else if (o instanceof Encounter) {
				printEncounter(sb, (Encounter) o);
			} else if (o instanceof EncounterType) {
				printEncounterType(sb, (EncounterType) o);
			} else if (o instanceof Location) {
				printLocation(sb, (Location) o);
			} else if (o instanceof ReportData) {
				printReportData(sb, (ReportData) o);
			} else if (o instanceof DataSet) {
				printDataSet(sb, null, (DataSet) o);
			} else if (o instanceof Cohort) {
				printCohort(sb, (Cohort) o);
			} else if (o instanceof CohortDimensionResult) {
				printCohortDimensionResult(sb, (CohortDimensionResult) o);
			} else if (o instanceof BaseData) {
				printMap(sb, ((BaseData)o).getData());
			} else if (o instanceof IdSet) {
				printCollection(sb, ((IdSet)o).getMemberIds());
			} 
			else {
				sb.append(ObjectUtil.format(o));
			}
		}
		catch (Exception e) {
			sb.append(o.toString());
		}
	}

	public void printResult(StringBuilder sb, Result result) {
	    if (result instanceof EmptyResult)
	    	return;
	    if (result.size() < 1) { // for some reason single results seem to have size 0
	    	sb.append(result.toString());
	    } else {
	    	for (Iterator<Result> i = result.iterator(); i.hasNext(); ) {
	    		sb.append(i.next().toString());
	    		if (i.hasNext())
	    			sb.append(", ");
	    	}
	    }
    }

	/**
     * formats a date and prints it to sb
     * 
     * @param sb
     * @param date
     */
	public void printDate(StringBuilder sb, Date date) {
	    sb.append(Context.getDateFormat().format(date));
    }

	/**
     * formats a location and prints it to sb
     * 
     * @param sb
     * @param location
     */
	public void printLocation(StringBuilder sb, Location location) {
	    sb.append(location.getName());
    }

	/**
     * formats an encounter type and prints it to sb
     * 
     * @param sb
     * @param encounterType
     */
	public void printEncounterType(StringBuilder sb, EncounterType encounterType) {
	    sb.append(encounterType.getName());
    }

	/**
     * formats a user and prints it to sb
     * 
     * @param sb
     * @param u
     */
	public void printUser(StringBuilder sb, User u) {
    	sb.append(u.getPersonName());
    }
    
    /**
     * formats a user and prints it to sb
     * 
     * @param sb
     * @param u
     */
	public void printUser(StringBuilder sb, Person u) {
        sb.append(u.getPersonName());
    }
    
	/**
	 * Formats a ReportData and prints it to sb
	 * 
	 * @param sb
	 * @param reportData
	 */
	public void printReportData(StringBuilder sb, ReportData reportData) {
	    sb.append("<h4>" + reportData.getDefinition().getName() + "</h4>");
	    for (Map.Entry<String, DataSet> ds : reportData.getDataSets().entrySet()) {
	    	printDataSet(sb, ds.getKey(), ds.getValue());
	    }
    }
    
	
	/**
	 * Formats a DataSet and prints it to sb
	 * 
	 * @param sb
	 * @param title
	 * @param dataSet
	 */
	public void printDataSet(StringBuilder sb, String title, DataSet dataSet) {
	    sb.append("<table cellspacing=\"0\" cellpadding=\"2\" border=\"1\">");
	    List<DataSetColumn> cols = dataSet.getMetaData().getColumns();
	    sb.append("<thead>");
	    if (org.springframework.util.StringUtils.hasText(title)) {
	    	sb.append("<tr bgcolor=\"#f0f0f0\"><th colspan=\"" + cols.size() + "\">" + title + "</th></tr>");
	    }
	    if (!(dataSet instanceof MapDataSet)) {
	    	sb.append("<tr>");
		    for (DataSetColumn col : cols) {
		    	sb.append("<th>" + col.getLabel() + "</th>");
		    }
		    sb.append("</tr>");
	    }
	    sb.append("</thead>");
	    sb.append("<tbody>");
	    if (dataSet instanceof MapDataSet) {
	    	MapDataSet map = (MapDataSet) dataSet;
	    	DataSetRow row = map.getData();
	    	for (DataSetColumn col : cols) {
	    		sb.append("<tr><th align=\"left\">")
	    			.append(Context.getMessageSourceService().getMessage(col.getLabel()))
	    			.append("</th><td>")
	    			.append(formatHelper(row.getColumnValue(col)))
	    			.append("</td></tr>");
	    	}
	    } else {
		    for (DataSetRow row : dataSet) {
		    	sb.append("<tr>");
		    	for (DataSetColumn col : cols) {
		    		sb.append("<td>").append(formatHelper(row.getColumnValue(col))).append("</td>");
		    	}
		    	sb.append("</tr>");
		    }
	    }
	    sb.append("</tbody>");
	    sb.append("</table>");
    }
	
	/**
	 * Formats a DataSet and prints it to sb
	 * 
	 * @param sb
	 * @param title
	 * @param dataSet
	 */
	public void printCohortDimensionResult(StringBuilder sb, CohortDimensionResult result) {
		sb.append("<table cellspacing=\"0\" cellpadding=\"2\" border=\"1\">");
		for (Map.Entry<String, Cohort> e : result.getOptionCohorts().entrySet()) {
			sb.append("<tr><th align=\"left\">" + e.getKey() + "</th><td>");
			printCohort(sb, e.getValue());
			sb.append("</td></tr>");
		}
		sb.append("</table>");
    }
	
	/**
	 * formats a cohort to sb
	 * 
	 * @param sb
	 * @param cohort
	 */
	public void printCohort(StringBuilder sb, Cohort cohort) {
		sb.append(cohort.size() + " patients");
    }
	
	public void printObsValue(StringBuilder sb, Obs obsValue) {
		sb.append(obsValue.getValueAsString(Context.getLocale()));
    }

	public void printConcept(StringBuilder sb, Concept concept) {
		if (concept.getName() != null)
			sb.append(concept.getName().getName());
    }

	public void printEncounter(StringBuilder sb, Encounter encounter) {
		printEncounterType(sb, encounter.getEncounterType());
		sb.append(" @");
		printLocation(sb, encounter.getLocation());
		sb.append(" | ");
		printDate(sb, encounter.getEncounterDatetime());
		sb.append(" | ");
		printUser(sb, encounter.getProvider());
    }
	
	public void printMap(StringBuilder sb, Map<?, ?> m) {
		if (m != null) {
			sb.append("<table cellspacing=\"0\" cellpadding=\"2\" border=\"1\">");
			for (Map.Entry<?, ?> e : m.entrySet()) {
				if(e.getValue() instanceof ArrayList) {
					ArrayList list = (ArrayList) e.getValue();
					if(list.get(0) != null && list.get(0) instanceof Obs) {
						StringBuilder temp = new StringBuilder();
						printObject(temp, e.getValue());
						sb.append("<tr><th align=\"left\">" + format(e.getKey()) + "</th><td align=\"left\">" + temp.toString() + "</td></tr>");
					}
					else {
						sb.append("<tr><th align=\"left\">" + format(e.getKey()) + "</th><td align=\"left\">" + format(e.getValue()) + "</td></tr>");
					}
				}
				else {
					sb.append("<tr><th align=\"left\">" + format(e.getKey()) + "</th><td align=\"left\">" + format(e.getValue()) + "</td></tr>");
				}
				
			}
			sb.append("</table>");
		}
	}
	
	private void printCollection(StringBuilder sb, Collection<?> c){
		if(c != null){
			sb.append("<table cellspacing=\"0\" cellpadding=\"2\" border=\"1\">");
			sb.append("<tr><th align=\"left\">" + Context.getMessageSourceService().getMessage("reporting.ids") + "</th></tr>");
			for (Object item : c) {
				sb.append("<tr><td align=\"left\">");
				printObject(sb, item);
				sb.append("</td></tr>");
			}
			sb.append("</table>");
		}
	}


	public String formatHelper(Object o) {
		if (o == null) {
			return "";
		}
		else if (o instanceof Cohort) {
			return ((Cohort)o).getSize() + " patients";
		}
		else if (o instanceof Obs) {
			StringBuilder sb = new StringBuilder();
			printObsValue(sb, (Obs) o);
			return sb.toString();
		}
		else if (o instanceof Collection) {
			StringBuilder sb = new StringBuilder();
			printObject(sb, o);
			return sb.toString();
		}
	    try {
	    	Method method = o.getClass().getMethod("getValue");
	    	return method.invoke(o).toString();
	    } catch (Exception ex) {
	    	return o.toString();
	    }
    }
	
	public void eagerInitializationObs(Obs obs)
	{
		if(obs != null && !Hibernate.isInitialized(obs)) {
			Hibernate.initialize(obs);
		}
		if(obs.getConcept() != null && !Hibernate.isInitialized(obs.getConcept())) {
			Hibernate.initialize(obs.getConcept());
		}
		if(obs.getValueCoded() != null && !Hibernate.isInitialized(obs.getValueCoded())) {
			Hibernate.initialize(obs.getValueCoded());
		}
		if(obs.getValueDrug() !=null && !Hibernate.isInitialized(obs.getValueDrug())) {
			Hibernate.initialize(obs.getValueDrug());
		}
		if(obs.getValueCodedName() !=null && !Hibernate.isInitialized(obs.getValueCodedName())) {
			Hibernate.initialize(obs.getValueCodedName());
		}
		if(obs.getConcept() != null && Hibernate.isInitialized(obs.getConcept())) {
			if(obs.getConcept().getDatatype() != null && !Hibernate.isInitialized(obs.getConcept().getDatatype())) {
				Hibernate.initialize(obs.getConcept().getDatatype());
			}
		}
		if(obs.getValueCoded() != null && Hibernate.isInitialized(obs.getValueCoded())) {
			if(obs.getValueCoded().getName() != null && !Hibernate.isInitialized(obs.getValueCoded().getName())) {
				Hibernate.initialize(obs.getValueCoded().getName());
			}
		}
	}
}
