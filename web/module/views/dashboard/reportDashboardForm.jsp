<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/reportDashboard.form" />

<spring:message var="pageTitle" code="reportDashboard.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>


<div id="reportDashboard">

	<ul>
		<li><a href="${pageContext.request.contextPath}/analysis/reportDashboard.form">Report Dashboard</a></li>
		<li><a href="${pageContext.request.contextPath}/analysis/dataSetViewer.form">Data Set Viewer</a></li>
		<li><a href="${pageContext.request.contextPath}/analysis/dataSetBuilder.form">Data Set Builder</a></li>
	</ul>


	<h1>Patient Graph Example</h1>
	<form>
		<fieldset>
		
			<span align="center" id="conceptBox-5497">
				<spring:message code="general.loading"/>
				<div align="center" valign="top" style="font-size: .9em">
					<a href="?patientId=12259&patientGraphConceptRemove=true&patientGraphConcept=5497">
					<br/><br/><spring:message code="general.remove"/></a>
				</div>
			</span>

			<span align="center" id="conceptBox-5089">
				<spring:message code="general.loading"/>
				<div align="center" valign="top" style="font-size: .9em">
					<a href="?patientId=12259&patientGraphConceptRemove=true&patientGraphConcept=5089">
					<br/><br/><spring:message code="general.remove"/></a>
				</div>
			</span>
		
		</fieldset>
		
		
	</form>

	
</div>

	<script type="text/javascript">
		function loadGraphs() {			
			document.getElementById('conceptBox-5497').innerHTML = 
				'<img src="${pageContext.request.contextPath}/showGraphServlet?patientId=12259&conceptId=5497&width=300&height=150&minRange=<c:out value="${num.lowAbsolute}" default="0.0"/>&maxRange=<c:out value="${num.hiAbsolute}" default="200.0"/>" />';
			document.getElementById('conceptBox-5089').innerHTML = 
				'<img src="${pageContext.request.contextPath}/showGraphServlet?patientId=12259&conceptId=5089&width=300&height=150&minRange=<c:out value="${num.lowAbsolute}" default="0.0"/>&maxRange=<c:out value="${num.hiAbsolute}" default="200.0"/>" />';
		}
		window.setTimeout(loadGraphs, 1000);		
	</script>


<%@ include file="/WEB-INF/template/footer.jsp" %>