<%@page isErrorPage="true" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<spring:message var="pageTitle" code="dao.error.title" scope="page"/>
<%@ include file="../localHeader.jsp" %>

<h2><spring:message code="dao.error.title"/></h2>
We have failed you.  We're sorry.<br/>

<ul>
	<li>Error Code: <strong><%= request.getAttribute("javax.servlet.error.status_code") %></strong></li>
	<li>Request URI: <strong><%= request.getAttribute("javax.servlet.error.request_uri") %></strong></li>
	<li>Message: <strong><%= request.getAttribute("javax.servlet.error.message") %></strong></li>
	<li>Exception Type: <strong><%= request.getAttribute("javax.servlet.error.exception_type") %></strong></li>
	<li>Exception: <strong><%= request.getAttribute("javax.servlet.error.exception") %></strong></li>
</ul>

<%@ include file="/WEB-INF/template/footer.jsp" %> 