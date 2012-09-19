package org.openmrs.module.reporting.web.taglib;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.EmptyResult;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.BaseData;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.indicator.dimension.CohortDimensionResult;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.module.reporting.report.ReportData;
import org.springframework.util.StringUtils;


public class FormatTag extends TagSupport {

	public static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());
	
	private String var;
	
	private Object object;
	
	private Date date;
	
	private Integer conceptId;
	
	private Concept concept;
	
	private Obs obsValue;
	
	private Integer userId;
	
	private User user;
	
	private Integer encounterId;
	
	private Encounter encounter;
	
	private Integer encounterTypeId;
	
	private EncounterType encounterType;
	
	private Integer locationId;
	
	private Location location;
	
	private String string;
	
	private ReportData reportData;
	
	private DataSet dataSet;
	
	private Cohort cohort;
			
	public static String format(Object object) {
		StringBuilder sb = new StringBuilder();
		new FormatTag().printObject(sb, object);
		return sb.toString();
	}
	
	public int doStartTag() {
		StringBuilder sb = new StringBuilder();
		if (object != null) {
			printObject(sb, object);
		}
		
		if (date != null)
			printDate(sb, date);

		if (conceptId != null)
			concept = Context.getConceptService().getConcept(conceptId);
		if (concept != null) {
			printConcept(sb, concept);
		}
		
		if (obsValue != null)
			printObsValue(sb, obsValue);
		
		if (userId != null)
			user = Context.getUserService().getUser(userId);
		if (user != null)
			printUser(sb, user);
		
		if (encounterId != null)
			encounter = Context.getEncounterService().getEncounter(encounterId);
		if (encounter != null) {
			printEncounter(sb, encounter);
		}
		
		if (encounterTypeId != null)
			encounterType = Context.getEncounterService().getEncounterType(encounterTypeId);
		if (encounterType != null) {
			printEncounterType(sb, encounterType);
		}
		
		if (locationId != null)
			location = Context.getLocationService().getLocation(locationId);
		if (location != null) {
			printLocation(sb, location);
		}
		
		if (reportData != null) {
			printReportData(sb, reportData);
		}
		
		if (dataSet != null) {
			printDataSet(sb, null, dataSet);
		}
		
		if (cohort != null) {
			printCohort(sb, cohort);
		}
		
		if (string != null)
			sb.append(string);
		
		if (StringUtils.hasText(var)) {
			pageContext.setAttribute(var, sb.toString());
		} else {
			try {
				pageContext.getOut().write(sb.toString());
			} catch (IOException e) {
				log.error("Failed to write to pageContext.getOut()", e);
			}
		}
		return SKIP_BODY;
	}

	/**
	 * Formats anything and prints it to sb. (Delegates to other methods here
	 * @param sb
	 * @param o
	 */
	private void printObject(StringBuilder sb, Object o) {
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

	private void printResult(StringBuilder sb, Result result) {
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
    private void printDate(StringBuilder sb, Date date) {
	    sb.append(Context.getDateFormat().format(date));
    }

	/**
     * formats a location and prints it to sb
     * 
     * @param sb
     * @param location
     */
    private void printLocation(StringBuilder sb, Location location) {
	    sb.append(location.getName());
    }

	/**
     * formats an encounter type and prints it to sb
     * 
     * @param sb
     * @param encounterType
     */
    private void printEncounterType(StringBuilder sb, EncounterType encounterType) {
	    sb.append(encounterType.getName());
    }

	/**
     * formats a user and prints it to sb
     * 
     * @param sb
     * @param u
     */
    private void printUser(StringBuilder sb, User u) {
    	sb.append(u.getPersonName());
    }
    
    /**
     * formats a user and prints it to sb
     * 
     * @param sb
     * @param u
     */
    private void printUser(StringBuilder sb, Person u) {
        sb.append(u.getPersonName());
    }
    
	/**
	 * Formats a ReportData and prints it to sb
	 * 
	 * @param sb
	 * @param reportData
	 */
	private void printReportData(StringBuilder sb, ReportData reportData) {
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
	private void printDataSet(StringBuilder sb, String title, DataSet dataSet) {
	    sb.append("<table cellspacing=\"0\" cellpadding=\"2\" border=\"1\">");
	    List<DataSetColumn> cols = dataSet.getMetaData().getColumns();
	    sb.append("<thead>");
	    if (StringUtils.hasText(title)) {
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
	private void printCohortDimensionResult(StringBuilder sb, CohortDimensionResult result) {
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
	private void printCohort(StringBuilder sb, Cohort cohort) {
		sb.append(cohort.size() + " patients");
    }
	
	private void printObsValue(StringBuilder sb, Obs obsValue) {
		sb.append(obsValue.getValueAsString(Context.getLocale()));
    }

	private void printConcept(StringBuilder sb, Concept concept) {
		if (concept.getName() != null)
			sb.append(concept.getName().getName());
    }

	private void printEncounter(StringBuilder sb, Encounter encounter) {
		printEncounterType(sb, encounter.getEncounterType());
		sb.append(" @");
		printLocation(sb, encounter.getLocation());
		sb.append(" | ");
		printDate(sb, encounter.getEncounterDatetime());
		sb.append(" | ");
		printUser(sb, encounter.getProvider());
    }
	
	private void printMap(StringBuilder sb, Map<?, ?> m) {
		if (m != null) {
			sb.append("<table cellspacing=\"0\" cellpadding=\"2\" border=\"1\">");
			for (Map.Entry<?, ?> e : m.entrySet()) {
				sb.append("<tr><th align=\"left\">" + format(e.getKey()) + "</th><td align=\"left\">" + format(e.getValue()) + "</td></tr>");
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


	private String formatHelper(Object o) {
		if (o == null) {
			return "";
		}
		else if (o instanceof Cohort) {
			return ((Cohort)o).getSize() + " patients";
		}
	    try {
	    	Method method = o.getClass().getMethod("getValue");
	    	return method.invoke(o).toString();
	    } catch (Exception ex) {
	    	return o.toString();
	    }
    }

	public int doEndTag() {
		reset();
		return EVAL_PAGE;
	}
	
	private void reset() {
		var = null;
		object = null;
		date = null;
		conceptId = null;
		concept = null;
		obsValue = null;
		userId = null;
		user = null;
		encounterId = null;
		encounter = null;
		encounterTypeId = null;
		encounterType = null;
		locationId = null;
		location = null;
		reportData = null;
		dataSet = null;
		cohort = null;
		string = null;
	}
	
	public Integer getConceptId() {
		return conceptId;
	}
	
	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}
	
	public Concept getConcept() {
		return concept;
	}
	
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	
	public Obs getObsValue() {
		return obsValue;
	}
	
	public void setObsValue(Obs obsValue) {
		this.obsValue = obsValue;
	}
	
	public Integer getUserId() {
		return userId;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

    public Integer getEncounterId() {
    	return encounterId;
    }

    public void setEncounterId(Integer encounterId) {
    	this.encounterId = encounterId;
    }

    public Encounter getEncounter() {
    	return encounter;
    }

    public void setEncounter(Encounter encounter) {
    	this.encounter = encounter;
    }
	
    public Integer getEncounterTypeId() {
    	return encounterTypeId;
    }
	
    public void setEncounterTypeId(Integer encounterTypeId) {
    	this.encounterTypeId = encounterTypeId;
    }
	
    public EncounterType getEncounterType() {
    	return encounterType;
    }

    public void setEncounterType(EncounterType encounterType) {
    	this.encounterType = encounterType;
    }
	
    public Integer getLocationId() {
    	return locationId;
    }

    public void setLocationId(Integer locationId) {
    	this.locationId = locationId;
    }
    
    public Location getLocation() {
    	return location;
    }

    public void setLocation(Location location) {
    	this.location = location;
    }
	
    public String getVar() {
    	return var;
    }
	
    public void setVar(String var) {
    	this.var = var;
    }

    public Object getObject() {
    	return object;
    }

    public void setObject(Object object) {
    	this.object = object;
    }

    public Date getDate() {
    	return date;
    }

    public void setDate(Date date) {
    	this.date = date;
    }
	
}
