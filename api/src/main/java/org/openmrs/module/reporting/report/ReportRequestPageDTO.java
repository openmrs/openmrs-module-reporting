/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report;

import java.util.List;

public class ReportRequestPageDTO {

  private List<ReportRequest> reportRequests;

  private long totalCount;

  public ReportRequestPageDTO(List<ReportRequest> reportRequests, long totalCount) {
    this.reportRequests = reportRequests;
    this.totalCount = totalCount;
  }

  public List<ReportRequest> getReportRequests() {
    return reportRequests;
  }

  public void setReportRequests(List<ReportRequest> reportRequests) {
    this.reportRequests = reportRequests;
  }

  public long getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(long totalCount) {
    this.totalCount = totalCount;
  }
}
