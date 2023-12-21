package org.openmrs.module.reporting.report;

import java.util.List;

public class ReportRequestDTO {

  private List<ReportRequest> reportRequests;

  private Long reportRequestCount;

  public ReportRequestDTO(List<ReportRequest> reportRequests, Long reportRequestCount) {
    this.reportRequests = reportRequests;
    this.reportRequestCount = reportRequestCount;
  }

  public List<ReportRequest> getReportRequests() {
    return reportRequests;
  }

  public void setReportRequests(List<ReportRequest> reportRequests) {
    this.reportRequests = reportRequests;
  }

  public Long getReportRequestCount() {
    return reportRequestCount;
  }

  public void setReportRequestCount(Long reportRequestCount) {
    this.reportRequestCount = reportRequestCount;
  }
}
