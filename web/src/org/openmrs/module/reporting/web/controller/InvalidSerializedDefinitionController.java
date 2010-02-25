package org.openmrs.module.reporting.web.controller;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.evaluation.Definition;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.util.ObjectUtil;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InvalidSerializedDefinitionController {

	protected Log log = LogFactory.getLog(this.getClass()); 
	
    /**
     * View Invalid Serialized Definition Page
     */
    @RequestMapping("/module/reporting/definition/invalidSerializedDefinitions")
    public void invalidSerializedDefinitions(
            @RequestParam(required=false, value="type") Class<? extends Definition> type,
    		ModelMap model) {
    	
    	SerializedDefinitionService service = Context.getService(SerializedDefinitionService.class);
    	model.addAttribute("type", type);
    	List<SerializedObject> sds = new ArrayList<SerializedObject>();
    	if (ObjectUtil.notNull(type)) {
    		sds = service.getInvalidDefinitions(type, true);
    	}
    	model.addAttribute("serializedDefinitions", sds);
    	model.addAttribute("supportedTypes", service.getSupportedDefinitionTypes());
    }
    
    /**
     * Edit Invalid Serialized Definition Page
     */
    @RequestMapping("/module/reporting/definition/editInvalidSerializedDefinition")
    public void editInvalidSerializedDefinition(
            @RequestParam(required=true, value="type") Class<? extends Definition> type,
            @RequestParam(required=true, value="uuid") String uuid,
    		ModelMap model) {
    	
    	SerializedDefinitionService service = Context.getService(SerializedDefinitionService.class);
    	SerializedObject definition = service.getSerializedDefinitionByUuid(uuid);
    	model.addAttribute("type", type);
    	model.addAttribute("definition", definition);
    }
    
    /**
     * Edit Invalid Serialized Definition Page
     */
    @RequestMapping("/module/reporting/definition/saveSerializedDefinition")
    public String saveSerializedDefinition(ModelMap model, HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(required=true, value="uuid") String uuid,
    		@RequestParam(required=true, value="name") String name,
    		@RequestParam(required=false, value="description") String description,
            @RequestParam(required=true, value="type") String type,
            @RequestParam(required=true, value="subtype") String subtype,
            @RequestParam(required=true, value="serializationClass") Class<? extends OpenmrsSerializer> serializationClass,
            @RequestParam(required=true, value="serializedData") String serializedData) throws Exception {
    	
    	SerializedDefinitionService service = Context.getService(SerializedDefinitionService.class);
    	SerializedObject definition = service.getSerializedDefinitionByUuid(uuid);
    	definition.setName(name);
    	definition.setDescription(description);
    	definition.setType(type);
    	definition.setSubtype(subtype);
    	definition.setSerializationClass(serializationClass);
    	definition.setSerializedData(serializedData);
    	service.saveSerializedDefinition(definition);
    	
    	return "redirect:/module/reporting/definition/invalidSerializedDefinitions.form?type="+type;
    }
    
    /**
     * Edit Invalid Serialized Definition Page
     */
    @RequestMapping("/module/reporting/definition/testSerializedDefinition")
    @SuppressWarnings("unchecked")
    public void testSerializedDefinition(ModelMap model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required=true, value="type") String type,
            @RequestParam(required=true, value="serializationClass") String serializationClass,
            @RequestParam(required=true, value="data") String data) throws Exception {
    	
    	Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
    	response.setContentType("text/plain");
    	ServletOutputStream out = response.getOutputStream();
    	try {
    		Class<? extends Definition> typeClass = (Class<? extends Definition>)Context.loadClass(type);
    		Class<? extends OpenmrsSerializer> s = (Class<? extends OpenmrsSerializer>)Context.loadClass(serializationClass);
    		Context.getSerializationService().deserialize(data, typeClass, s);
    		out.print("Success");
    	}
    	catch (Exception e) {
    		e.printStackTrace(new PrintStream(out));
    	}
    }
    
    /**
     * Purges the definition represented by the given uuid.
    */
    @RequestMapping("/module/reporting/definition/purgeSerializedDefinition")
    public String purgeSerializedDefinition(@RequestParam(required=true, value="uuid") String uuid) {
    	SerializedDefinitionService sds = Context.getService(SerializedDefinitionService.class);
    	sds.purgeDefinition(uuid);
        return "redirect:/module/reporting/definition/invalidSerializedDefinitions.form";
    }  
}
