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
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.util.OpenmrsUtil;

public class DeleteOldLogsTest {
	
	File baseDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ReportingConstants.REPORT_RESULTS_DIRECTORY_NAME);
	
	Path test= Paths.get(baseDir.getAbsolutePath(),"test.reportlog");
	File test_file = test.toFile();

	@Before
	public void init() throws IOException {
	 test_file.createNewFile();	
	}
	
	@Test
	public void Should_DeleteLogFlesExceedingSevendays() throws IOException {
	//Setting the file to exist over seven days
	long exceedingDays =  8 ;
	test_file.setLastModified(exceedingDays * (24 * 60 * 60 * 1000));
	
	// asserting that the file exists befor the servlet DeleteOldLogs is called	
	Assert.assertTrue(test_file.exists());
		
	DeleteOldLogFiles delete = new DeleteOldLogFiles ();
	delete.deleteOldLogs();
	
	//Asserting that the Log file will be deleted after seven days 
	Assert.assertFalse(test_file.exists());
	}
	
	@Test
	public void Should_NotDeleteLogFilesBeforeSevenDayPass() throws IOException {
      
		// asserting that the file exists befor the servlet DeleteOldLogs is called	
		Assert.assertTrue(test_file.exists());
			
		DeleteOldLogFiles delete = new DeleteOldLogFiles ();
		delete.deleteOldLogs();
		
		//Asserting that the Log file will Not be deleted after seven days 
		Assert.assertTrue(test_file.exists());	
	}
	
	}

    
   
   
	
