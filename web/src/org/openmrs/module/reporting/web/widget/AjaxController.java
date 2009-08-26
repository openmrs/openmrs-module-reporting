package org.openmrs.module.reporting.web.widget;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptWord;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AjaxController {

	protected static Log log = LogFactory.getLog(AjaxController.class);
	
	/**
	 * Default Constructor
	 */
	public AjaxController() { }

    /**
     * Concept Search
     */
    @RequestMapping("/module/reporting/widget/conceptSearch")
    public void conceptSearch(ModelMap model, HttpServletRequest request, HttpServletResponse response, 
		    		@RequestParam(required=true, value="q") String query) throws Exception {
    	
    	response.setContentType("text/plain");
    	ServletOutputStream out = response.getOutputStream();
    	List<Locale> l = new Vector<Locale>();
    	l.add(Context.getLocale());
    	List<ConceptWord> words = Context.getConceptService().getConceptWords(query, l, false, null, null, null, null, null, null, null);
    	for (Iterator<ConceptWord> i = words.iterator(); i.hasNext();) {
    		ConceptWord w = i.next();
    		String ds = w.getConcept().getDisplayString();
    		if (w.getConceptName().isPreferred() || w.getConceptName().getName().equalsIgnoreCase(ds)) {
    			out.print(w.getConceptName().getName());
    		}
    		else {
    			out.print( w.getConcept().getDisplayString() + " (" + w.getConceptName().getName() + ")");
    		}
    		out.print("|" + w.getConcept().getUuid() + (i.hasNext() ? "\n" : ""));
    	}
    }
}
