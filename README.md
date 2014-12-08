Reporting Module
================

  Overview
  ================
  
   The Reporting Module was designed to provide a feature-rich and user-friendly web interface for managing reports within OpenMRS. In addition, the Reporting Module provides a flexible and extensible API that module developers can develop against to build their own reports and tools. The core idea behind the Reporting Module is to provide a solid foundation so that other developers can use the framework to implement new features.
   
  Download
  ================
  We encourage all OpenMRS users and developers to download the reporting module, use it frequently, create new tickets for bug fixes and feature requests, and provide feedback to dev@openmrs.org .
  
  Requirements
  ================
  
  OpenMRS 1.5.2 or higher
  Required Modules
  HTML Widgets 1.5.5
  Serialization XStream 0.1.8.1
  Recommended modules
  Reporting Compatibility 1.5.0.3
  View, download or fork source code: on GitHub
  
  Installation
  ================
  
  To install the Reporting Module download the Reporting Module .omod (above), along with its dependencies (see box above) and upload them into your system.
  
  Upgrade
  ================
  
  To upgrade from Reporting Module 0.3.x:
    Check to make sure that you have all of the Required Modules installed on your system.
    Double-check to make sure that you have all of the Required Modules installed on your system.
    Log into OpenMRS
    Navigate to Admin > Manage Modules.
    Click the trash icon next to Reporting 0.3.x to remove the module from your system.
    Navigate to the newer omod on your system using the browse button and click Upload.
  
  Privileges
  ================
  
  In order for users to be able to run most reports, you need to put them in a group with the privileges:
  (this is accurate as of 0.4.1.2)
    View Reports
    Run Reports
    View Patient Cohorts
      technically this is only required if you want to be able to run reports including cohorts of patients, which is basically all reports
    (up until 0.4.1.1) Manage Scheduler
    To be able to edit and configure reports, you need: 
    (this is accurate as of 0.4.1.3)
      Manage Reports (this just enables you to see the menu items)
      Manage Report Definitions
    Manage Data Set Definitions
    Manage Indicator Definitions
    Manage Dimension Definitions
    Manage Cohort Definitions
    Manage Report Designs
    You also need to grant View privileges related to the base objects your reporting definitions use. For example you need "View Programs" to create and use an "In Program" Cohort Query
  
  Report Types
  ================
  
  There are many different types of reports but these can be categorized into two main types.
  
  Row-Per-Domain Object Reports: These reports export data in a multi-column format where each row represents the object and each column represents an attribute associated with the object. Currently, only Row-Per-Patient reports are natively supported but more objects (for example Row-Per-Encounter and Row-Per-Program) are in planning.
  
  Indicator Reports: Indicator reports aggregate groups of people for each question. Below is a Period Indicator Report. Each row contains a question and the corresponding column contains the answer. The answer to each question is a link to the members of the group that fulfills the question.
  
  Limitations
  ================
  
  Currently, reporting compatibility is being used to bridge the gap between the old and new (e.g., combining cohort builder and data exports). This reporting module has many core features for evaluating parts of a report but does not have a good UI for designing a full report. Cohort builder is best for adhoc querying, though for unsupported data entry, you must use the cohort query editor in the reporting module. Data exports can only be designed/exported using reporting compatibility. This module contains a feature that allows you to define simple dataset definitions, such as a SQL-based dataset, but other definitions are not available.
  
  Project Resources
  ================
   
  [Wiki page]: https://wiki.openmrs.org/display/docs/Reporting+Module
  [View/download source code for Reporting Module]: https://github.com/openmrs/openmrs-module-reporting
