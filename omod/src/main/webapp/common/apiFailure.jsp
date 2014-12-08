<%@page isErrorPage="true" %>
<%@ page import="org.openmrs.web.WebUtil" %>
<%@ page import="org.openmrs.web.WebConstants" %>
<%@ page import="org.openmrs.api.context.UserContext" %>
<%@ page import="org.openmrs.util.OpenmrsConstants" %>
<%@ page import="org.openmrs.api.APIAuthenticationException" %>

<%@ include file="/WEB-INF/template/include.jsp" %>
<spring:message var="pageTitle" code="reporting.api.error.title" scope="page"/>
<%@ include file="../localHeader.jsp" %>

<h2><spring:message code="reporting.api.error.title"/></h2>
<spring:message code="reporting.weHaveFailed"/><br/>

<script>
	function showOrHide() {
		var link = document.getElementById("toggleLink");
		var trace = document.getElementById("stackTrace");
		if (link.innerHTML == "Show stack trace") {
			link.innerHTML = "Hide stack trace";
			trace.style.display = "block";
		}
		else {
			link.innerHTML = "Show stack trace";
			trace.style.display = "none";
		}
	}
</script>


<div style="margin: 10px;">

	<h2><spring:message code="reporting.api.error.title"/></h2>
	<spring:message code="reporting.weHaveFailed"/><br/>
	
		

	<ul>
		<li><spring:message code="reporting.statusCode"/></li>
		<li><spring:message code="reporting.requestURI"/></li>
		<li><spring:message code="reporting.exceptionType"/></li>
		<li><spring:message code="reporting.exception"/></li>
		<li><spring:message code="reporting.failureMessage"/></li>
	
	<% 
			// MSR/ERROR Session attributes are removed after being displayed
			// If they weren't displayed/removed because of this error, remove them
			session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
			session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR); 
			
		try {
			// The Servlet spec guarantees this attribute will be available
			//Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception"); 
			// Display current version
			out.println("<li>OpenMRS Version: <strong>" + OpenmrsConstants.OPENMRS_VERSION + "</strong></li>");
	
			if (exception != null) {			
				out.println("<li>Stacktrace: <a href=\"#\" onclick=\"showOrHide()\" id=\"toggleLink\" style=\"font-size: 12px;\">Show stack trace</a><br/>");
				if (exception.getMessage() != null)
					out.println("<pre id='exceptionMessage'>" + WebUtil.escapeHTML(exception.getMessage()) + "</pre>"); 
			}
			%>		
			
			<div id="stackTrace">
			
			<%
				// check to see if the current user is authenticated
				// this logic copied from the OpenmrsFilter because this
				// page isn't passed through that filter like all other pages
				UserContext userContext = (UserContext) session.getAttribute(WebConstants.OPENMRS_USER_CONTEXT_HTTPSESSION_ATTR);
				if (exception != null) {
					if (exception instanceof APIAuthenticationException) {
						// If they are not authorized to use a function
						session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, exception.getMessage());
						String uri = (String)request.getAttribute("javax.servlet.error.request_uri");
						if (request.getQueryString() != null) {
							uri = uri + "?" + request.getQueryString();
						}
						session.setAttribute(WebConstants.OPENMRS_LOGIN_REDIRECT_HTTPSESSION_ATTR, uri);
						response.sendRedirect(request.getContextPath() + "/login.htm");
					}
					else if (userContext == null || userContext.getAuthenticatedUser() == null) {
						out.println("You must be logged in to view the stack trace");
						// print the stack trace to the servlet container's error logs
						exception.printStackTrace();
					}
					else {
						java.lang.StackTraceElement[] elements;
						
						if (exception instanceof ServletException) {
							// It's a ServletException: we should extract the root cause
							ServletException sEx = (ServletException) exception;
							Throwable rootCause = sEx.getRootCause();
							if (rootCause == null)
								rootCause = sEx;
							out.println("<br/><br/>** Root cause is: "+ rootCause.getMessage());
							elements = rootCause.getStackTrace();
						}
						else {
							// It's not a ServletException, so we'll just show it
							elements = exception.getStackTrace(); 
						}
						for (StackTraceElement element : elements) {
							if (element.getClassName().contains("openmrs"))
								out.println("<b>" + element + "</b><br/>");
							else
								out.println(element + "<br/>");
						}
					}
				} 
				else  {
			    	out.println("<li>No error information available</li>");
				}
				
			    
			%>
			</div> <!-- close stack trace box -->	
		</li>
	</ul>

	<br /><br />
	<spring:message code="reporting.helpDocs"/><br />
	<spring:message code="reporting.contactAdmin"/>

<%		
	} catch (Exception ex) { 
		ex.printStackTrace(new java.io.PrintWriter(out));
	}
%>
	


<%@ include file="/WEB-INF/template/footer.jsp" %> 