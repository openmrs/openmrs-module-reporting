package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.PageContext;

/**
 * This represents a single widget on a form.
 */
public abstract class BaseWidget implements Widget {
	
	//******* PROPERTIES *************
	private String id;
	private String name;
	private Object defaultValue;
	private Set<Attribute> attributes;
	private List<String> resources;
	
	//******* CONSTRUCTORS *************
	
	/**
	 * Default Constructor
	 */
	public BaseWidget() { }
	
	/**
	 * @see Widget#configureAttributes(Map<String, String)
	 */
	public final void configure(Map<String, String> userAttributes) {
		setAttribute("id",getId(), false);
		setAttribute("name", getName(), false);
		configure();
		for (String att : userAttributes.keySet()) {
			configureAttribute(att, userAttributes.get(att));
		}
	}
	
	/** 
	 * @see Widget#render(PageContext)
	 */
	public void render(PageContext pageContext) throws IOException {
		HtmlUtil.renderResourceFiles(pageContext, resources);
		render(pageContext.getOut());
	}

	/**
	 * Configures the default behavior and resources for this Widget
	 */
	public abstract void configure();

	/**
	 * Renders the Widget to the passed Writer
	 */
	protected abstract void render(Writer w) throws IOException;

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
	 * Get the Attribute that matches the passed name
	 * @param name the name of the Attribute to get
	 * @return the Attribute that matches the passed name
	 */
	public Attribute getAttribute(String name) {
		for (Attribute a : getAttributes()) {
			if (a.getName().equalsIgnoreCase(name)) {
				return a;
			}
		}
		return null;
	}
	
	/**
	 * @return the value of the attribute with the given name
	 */
	public String getAttributeValue(String name) {
		Attribute att = getAttribute(name);
		if (att != null) {
			return att.getValue();
		}
		return null;
	}
	
	/**
	 * Adds an Attribute to this Widget
	 * @param name
	 * @param value
	 * @param configurable
	 */
	public void setAttribute(String name, String value, boolean configurable) {
		Attribute curr = getAttribute(name);
		if (curr != null) {
			curr.setValue(value);
			curr.setConfigurable(configurable);
		}
		else {
			getAttributes().add(new Attribute(name, value, configurable));
		}
	}
	
	/**
	 * Adds an Attribute to this Widget if it is able to be configured
	 * @param name
	 * @param value
	 * @param configurable
	 */
	public void configureAttribute(String name, String value) {
		Attribute curr = getAttribute(name);
		if (curr != null) {
			if (curr.isConfigurable()) {
				curr.setValue(value);
			}
		}
		else {
			setAttribute(name, value, true);
		}
	}
	
	/**
	 * Returns a cloned List of Attributes
	 * @return
	 */
	public List<Attribute> cloneAttributes() {
		List<Attribute> ret = new ArrayList<Attribute>();
		for (Attribute a : getAttributes()) {
			ret.add(new Attribute(a.getName(), a.getValue(), a.isConfigurable()));
		}
		return ret;
	}

	/**
	 * @return the resources
	 */
	public List<String> getResources() {
		if (resources == null) {
			resources = new ArrayList<String>();
		}
		return resources;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(List<String> resources) {
		this.resources = resources;
	}
	
	/**
	 * Adds a resource to the List
	 * @param resource
	 */
	public void addResource(String resource) {
		getResources().add(resource);
	}
}