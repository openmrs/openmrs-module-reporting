package org.openmrs.module.reporting.web.widget.html;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This represents a single widget on a form.
 */
public abstract class BaseWidget implements Widget {
	
	//******* PROPERTIES *************
	private String id;
	private String name;
	private Object defaultValue;
	private Set<Attribute> attributes;
	
	//******* CONSTRUCTORS *************
	
	/**
	 * Default Constructor
	 */
	public BaseWidget() { }
	
	/**
	 * @see Widget#configureAttributes(Map<String, String)
	 */
	public final void configureAttributes(Map<String, String> userAttributes) {
		setAttribute("id",getId());
		setAttribute("name", getName());
		configureAttributes();
		for (String att : userAttributes.keySet()) {
			addAttribute(att, userAttributes.get(att));
		}
	}
	
	/**
	 * 
	 * @see Widget#configureAttributes()
	 */
	public abstract void configureAttributes();
	
	/**
	 * Returns the defaultValue as a String
	 */
	public String getDefaultValueString() {
		return getDefaultValue() == null ? "" : getDefaultValue().toString();
	}

	//******** PROPERTY ACCESS ************

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
    /**
	 * @return the defaultValue
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	/**
	 * @return the attributes
	 */
	public Set<Attribute> getAttributes() {
		if (attributes == null) {
			attributes = new HashSet<Attribute>();
		}
		return attributes;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * Adds an Attribute to this Widget
	 * @param name
	 * @param value
	 * @param configurable
	 */
	public void setAttribute(String name, String value) {
		getAttributes().add(new Attribute(name, value, false));
	}
	
	/**
	 * Adds an Attribute to this Widget if it is able to be configured
	 * @param name
	 * @param value
	 * @param configurable
	 */
	public void addAttribute(String name, String value) {
		Attribute newAtt = new Attribute(name, value, true);
		for (Attribute curr : getAttributes()) {
			if (curr.equals(newAtt)) {
				if (curr.isConfigurable()) {
					curr.setValue(value);
				}
				return;
			}
		}
		getAttributes().add(newAtt);
	}
	
	public List<Attribute> cloneAttributes() {
		List<Attribute> ret = new ArrayList<Attribute>();
		for (Attribute a : getAttributes()) {
			ret.add(new Attribute(a.getName(), a.getValue(), a.isConfigurable()));
		}
		return ret;
	}
	
	/**
	 * @return the value of the attribute with the given name
	 */
	public String getAttribute(String name) {
		for (Attribute a : getAttributes()) {
			if (a.getName().equalsIgnoreCase(name)) {
				return a.getValue();
			}
		}
		return null;
	}
}