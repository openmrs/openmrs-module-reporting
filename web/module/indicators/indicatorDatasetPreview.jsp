<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View Orders" otherwise="/login.htm"
	redirect="/module/datasetbuilder/Create-Patient-Dataset.htm" />
<%@ include file="../localHeader.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/jquery-ui-1.7.1.custom.css" rel="stylesheet" />
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/customStyle.css" rel="stylesheet" />
<script type="text/javascript" language="javascript"
	src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/jquery.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/jquery.dataTables.js"></script>
<style type="text/css" title="currentStyle">
@import "${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/page.css";
@import "${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/table.css";
</style>

<script type="text/javascript">

function init(){
	document.getElementById('dialog').style.display='none';
	document.getElementById('edit-title').style.display='none';
         $('#tabs').load("${pageContext.request.contextPath}/moduleServlet/datasetbuilder/DatasetBuilderAddColumnsController",{});
         $('#table-dataset').dataTable();
}
	function popup(){
		$('#dialog').dialog({
			bgiframe: true,			
			autoOpen: false,
			width: 600,
			modal: true,
			buttons: {
				"Ok": function() { 
					$(this).dialog("close"); 					
				}, 
				"Cancel": function() { 
					$(this).dialog("close"); 
				} 
			}
		});
                //load data in to tabs, uses post
                
                 // Tabs
		$('#tabs').tabs();
                 
                 //hover states on the static widgets
		$('#dialog_link, ul#icons li').hover(
			function() { $(this).addClass('ui-state-hover'); }, 
			function() { $(this).removeClass('ui-state-hover'); }
		);
		
		$('#dialog').dialog('open');		
	}        
        
	function showEdit(){
		document.getElementById('edit-title').style.display='';
		document.getElementById('title').style.display='none';
		document.getElementById('save-button').style.display='none';
	}

	function doSave(form){
		document.getElementById('title').innerHTML = form.titletextbox.value;
		document.getElementById('edit-title').style.display='none';
		document.getElementById('title').style.display='';
		document.getElementById('save-button').style.display='';	
	}

	function doCancel(){
		document.getElementById('edit-title').style.display='none';
		document.getElementById('title').style.display='';
		document.getElementById('save-button').style.display='';		
	}
</script>
<title>Indicator Details</title>
</head>
<body onLoad="init()" class="body">
<h2>
<div id="title-edit" style="float: left"
	class="ui-widget ui-widget-content ui-corner-all">
<div class="display" id="title" onclick="showEdit()">Patient
Dataset #1</div>
</h2>
<div class="ui-icon ui-icon-pencil" onclick="showEdit()"
	id="save-button"></div>
<div id="edit-title">
<form><input type="text" id="title-textbox" name="titletextbox"
	value="Patient Dataset #1" /><input type="button" value="Save"
	class="ui-state-default ui-corner-all " onclick="doSave(this.form)" />
<input type="button" value="Cancel"
	class="ui-state-default ui-corner-all" onClick="doCancel()" /></form>
</div>
<br />
</div>

<span /> <br />
<!--  Option bar -->
<div class="option-bar"><span id="show"><b>Show: </b><a
	href="#">HIV positive patients</a></span> <span id="display-option"><b>Display:
</b><i>25</i> , <a href="#">50</a>, <a href="#">100</a> patients at a time</span><span
	id="save-option"><input class="ui-state-default ui-corner-all"
	type="button" width="50px" value="Save" /></span> <span id="order-option"><b>Sort
By: <a href="#">Patient ID</a> </b></span> 
<span id="export-option"><b>Export As: </b>
	<a href="#">CSV</a> , 
	<a href="#">TSV</a>, 
	<a href="#">XML</a></span></div>

<span /> <!--<br/>-->

<table id="table-dataset" class="display">
	<thead>
		<tr>
			<th><u>PATIENT ID</u></th>
			<th><u>GIVEN NAME</u></th>
			<th><u>LAST NAME</u></th>
			<th><u>GENDER</u></th>
			<th><u>BIRTHDATE</u></th>
			<th><u>HEALTH CENTER</u></th>
			<th><u><a href="#" id="dialog_link" onClick="popup()">ADD</a></u></th>
		</tr>
		<tr class="center">
			<td>NUMERIC</td>
			<td>STRING</td>
			<td>STRING</td>
			<td>STRING</td>
			<td>DATE/TIME</td>
			<td>STRING</td>
			<td>..</td>
		</tr>
	</thead>
	<tbody>
		
		<c:forEach var="i" begin="1" end="20" step="1" varStatus="status">
			<tr>
			<td>${i}</td>
			<td>
				<c:choose>
					<c:when test="${i % 2 == 0}">
						Matt
					</c:when> 
					<c:otherwise>
		           		Kingsted
		          	</c:otherwise>
	          	</c:choose>
          	</td>
			<td><c:choose>
					<c:when test="${i % 2 == 0}">
						Kurt
					</c:when> 
					<c:otherwise>
		           		Broomer
		          	</c:otherwise>
	          	</c:choose></td>
			<td><c:choose>
					<c:when test="${i % 2 == 0}">
						Male
					</c:when> 
					<c:otherwise>
		           		Female
		          	</c:otherwise>
	          	</c:choose></td>
			<td>${i}-08-1978</td>
			<td><c:choose>
					<c:when test="${i % 2 == 0}">
						Madison
					</c:when> 
					<c:otherwise>
		           		Boston
		          	</c:otherwise>
	          	</c:choose></td>
			<td>..</td>
		</tr>
		</c:forEach>
		<c:forEach var="i" begin="1" end="20" step="1" varStatus="status">
			<tr>
			<td>${i}</td>
			<td>
				<c:choose>
					<c:when test="${i % 2 == 0}">
						Matt
					</c:when> 
					<c:otherwise>
		           		Kingsted
		          	</c:otherwise>
	          	</c:choose>
          	</td>
			<td><c:choose>
					<c:when test="${i % 2 == 0}">
						Broomer
					</c:when> 
					<c:otherwise>
		           		Kurt
		          	</c:otherwise>
	          	</c:choose></td>
			<td><c:choose>
					<c:when test="${i % 2 == 0}">
						Male
					</c:when> 
					<c:otherwise>
		           		Female
		          	</c:otherwise>
	          	</c:choose></td>
			<td>${i}-08-1978</td>
			<td><c:choose>
					<c:when test="${i % 2 == 0}">
						Boston
					</c:when> 
					<c:otherwise>
		           		Madison
		          	</c:otherwise>
	          	</c:choose></td>
			<td>..</td>
		</tr>
		</c:forEach>		
	</tbody>
</table>

<span><br />
</span>


<div class="div-popup">
	<div id="dialog" title="Add a new column">
	<div id="tabs"></div>
</div>
</div>
</body>
</html>
<%@ include file="/WEB-INF/template/footer.jsp"%>