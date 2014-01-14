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
		new ObjectUtil().printObject(sb, object);
		return sb.toString();
	}
	
	public int doStartTag() {
		StringBuilder sb = new StringBuilder();
		ObjectUtil objUtil = new ObjectUtil();
		if (object != null) {
			objUtil.printObject(sb, object);
		}
		
		if (date != null)
			objUtil.printDate(sb, date);

		if (conceptId != null)
			concept = Context.getConceptService().getConcept(conceptId);
		if (concept != null) {
			objUtil.printConcept(sb, concept);
		}
		
		if (obsValue != null)
			objUtil.printObsValue(sb, obsValue);
		
		if (userId != null)
			user = Context.getUserService().getUser(userId);
		if (user != null)
			objUtil.printUser(sb, user);
		
		if (encounterId != null)
			encounter = Context.getEncounterService().getEncounter(encounterId);
		if (encounter != null) {
			objUtil.printEncounter(sb, encounter);
		}
		
		if (encounterTypeId != null)
			encounterType = Context.getEncounterService().getEncounterType(encounterTypeId);
		if (encounterType != null) {
			objUtil.printEncounterType(sb, encounterType);
		}
		
		if (locationId != null)
			location = Context.getLocationService().getLocation(locationId);
		if (location != null) {
			objUtil.printLocation(sb, location);
		}
		
		if (reportData != null) {
			objUtil.printReportData(sb, reportData);
		}
		
		if (dataSet != null) {
			objUtil.printDataSet(sb, null, dataSet);
		}
		
		if (cohort != null) {
			objUtil.printCohort(sb, cohort);
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
