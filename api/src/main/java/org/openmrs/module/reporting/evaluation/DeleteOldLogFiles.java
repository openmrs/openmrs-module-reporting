/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.reporting.evaluation;
import java.io.File;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.util.OpenmrsUtil;

	

public class DeleteOldLogFiles extends HttpServlet {

	private static final long serialVersionUID = 1L;
	// Servlet to delete old  Logs on module startup
	protected static final Log log = LogFactory.getLog(DeleteOldLogFiles.class);
	
	
	 public void deleteOldLogs(){
          //maximum number of days to keep log files is 7
	       long alloweddays = 7;   
	       File baseDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ReportingConstants.REPORT_RESULTS_DIRECTORY_NAME);
	        File[] fList = baseDir.listFiles();

	        if (fList != null){
	            for (File file : fList){
	            	String fileExt = FilenameUtils.getExtension(file.toString()); 
	                if (file.isFile() && StringUtils.equals(fileExt, "reportlog")){

                    long diff = new Date().getTime() - file.lastModified();
	                long maximumTime = (alloweddays * (24 * 60 * 60 * 1000)); 
                 
	                    if (diff > maximumTime) {
	                      file.delete();
	                    }

	                }
	            }
	     
	        }
	    }
	 
	 @Override
	 public void init() throws ServletException {
		 deleteOldLogs();
	 }
	 
}
	

