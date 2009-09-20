package org.openmrs.module.reporting.web.taglib;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
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
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.EvaluatedCohort;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.MapDataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.report.ReportData;
import org.springframework.util.StringUtils;


public class FormatTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	
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
	
	private DataSet<?> dataSet;
	
	private Cohort cohort;
		
	public int doStartTag() {
		StringBuilder sb = new StringBuilder();
		if (object != null) {
			if (object instanceof Date)
				date = (Date) object;
			else if (object instanceof Concept)
				concept = (Concept) object;
			else if (object instanceof Obs)
				obsValue = (Obs) object;
			else if (object instanceof User)
				user = (User) object;
			else if (object instanceof Encounter)
				encounter = (Encounter) object;
			else if (object instanceof EncounterType)
				encounterType = (EncounterType) object;
			else if (object instanceof Location)
				location = (Location) object;
			else if (object instanceof ReportData)
				reportData = (ReportData) object;
			else if (object instanceof DataSet)
				dataSet = (DataSet<?>) object;
			else if (object instanceof Cohort)
				cohort = (Cohort) object;
			else
				string = "" + object;
		}
		
		if (date != null)
			printDate(sb, date);

		if (conceptId != null)
			concept = Context.getConceptService().getConcept(conceptId);
		if (concept != null) {
			if (concept.getName() != null)
				sb.append(concept.getName().getName());
		}
		
		if (obsValue != null)
			sb.append(obsValue.getValueAsString(Context.getLocale()));
		
		if (userId != null)
			user = Context.getUserService().getUser(userId);
		if (user != null)
			printUser(sb, user);
		
		if (encounterId != null)
			encounter = Context.getEncounterService().getEncounter(encounterId);
		if (encounter != null) {
			printEncounterType(sb, encounter.getEncounterType());
			sb.append(" @");
			printLocation(sb, encounter.getLocation());
			sb.append(" | ");
			printDate(sb, encounter.getEncounterDatetime());
			sb.append(" | ");
			printUser(sb, encounter.getProvider());
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
	 * Formats a ReportData and prints it to sb
	 * 
	 * @param sb
	 * @param reportData
	 */
	private void printReportData(StringBuilder sb, ReportData reportData) {
	    sb.append("<h4>" + reportData.getDefinition().getName() + "</h4>");
	    for (Map.Entry<String, DataSet<?>> ds : reportData.getDataSets().entrySet()) {
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
	private void printDataSet(StringBuilder sb, String title, DataSet<?> dataSet) {
	    sb.append("<table cellspacing=\"0\" cellpadding=\"2\" border=\"1\">");
	    List<DataSetColumn> cols = dataSet.getDefinition().getColumns();
	    sb.append("<thead>");
	    if (StringUtils.hasText(title)) {
	    	sb.append("<tr bgcolor=\"#f0f0f0\"><th colspan=\"" + cols.size() + "\">" + title + "</th></tr>");
	    }
	    if (!(dataSet instanceof MapDataSet)) {
	    	sb.append("<tr>");
		    for (DataSetColumn col : cols) {
		    	sb.append("<th>" + col.getDisplayName() + "</th>");
		    }
		    sb.append("</tr>");
	    }
	    sb.append("</thead>");
	    sb.append("<tbody>");
	    if (dataSet instanceof MapDataSet) {
	    	MapDataSet<?> map = (MapDataSet<?>) dataSet;
	    	DataSetRow<?> row = map.getData();
	    	for (DataSetColumn col : cols) {
	    		sb.append("<tr><th>")
	    			.append(col.getDisplayName())
	    			.append("</th><td>")
	    			.append(formatHelper(row.getColumnValue(col)))
	    			.append("</td></tr>");
	    	}
	    } else {
		    for (DataSetRow<?> row : dataSet) {
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
	 * formats a cohort to sb
	 * 
	 * @param sb
	 * @param cohort
	 */
	private void printCohort(StringBuilder sb, Cohort cohort) {
		sb.append("Cohort of " + cohort.size() + " patients");
		if (cohort instanceof EvaluatedCohort) {
			EvaluatedCohort eval = (EvaluatedCohort) cohort;
			if (StringUtils.hasText(eval.getDefinition().getName()))
				sb.append(" (evaluated from " + eval.getDefinition().getName() + ")");
			else
				sb.append(" (evaluated from a " + eval.getDefinition().getClass().getSimpleName() + ")");
		}
    }

	private String formatHelper(Object o) {
		if (o == null)
			return "";
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
