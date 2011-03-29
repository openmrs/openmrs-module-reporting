package org.openmrs.module.reporting.web.util;

import java.io.StringWriter;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.APIException;



public class AjaxUtil {

	public static String toJson(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		try {
	        mapper.writeValue(sw, obj);
        }
        catch (Exception e) {
	        throw new APIException("Error converting to JSON", e);
        }
		return sw.toString();
    }
	
}
