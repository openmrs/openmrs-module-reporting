<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/datasetbuilder/index.htm" />
<%@ include file="../localHeader.jsp"%>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<div style="text-align:center;">
	<b class="boxHeader">Dataset Builder</b>
	<div class="box">
		
		
		
		<table width="100%">
			<tr>
				<th>Edit</th>
				<th>Dataset</th>
				<th>Type</th>
				<th>Created Date</th>
				<th>Author</th>
				<th>Delete</th>
			</tr>
			<tr>
				<td><a href="datasetBuilder.form">Edit</a></td>			
				<td>Dataset #1</td>			
				<td>Encounter</td>			
				<td>January 21, 2008</td>			
				<td>jmiranda</td>			
				<td><a href="datasetBuilder.list">Delete</a></td>			
			</tr>
			<tr>
				<td><a href="datasetBuilder.form">Edit</a></td>			
				<td>Dataset #2</td>			
				<td>Encounter</td>			
				<td>January 22, 2008</td>			
				<td>jmiranda</td>			
				<td><a href="datasetBuilder.list">Delete</a></td>			
			</tr>
			<tr>
				<td><a href="datasetBuilder.form">Edit</a></td>			
				<td>Dataset #3</td>			
				<td>Encounter</td>			
				<td>January 23, 2008</td>			
				<td>jmiranda</td>			
				<td><a href="datasetBuilder.list">Delete</a></td>			
			</tr>
			<tr>
				<td><a href="datasetBuilder.form">Edit</a></td>			
				<td>Dataset #4</td>			
				<td>Encounter</td>			
				<td>January 25, 2008</td>			
				<td>jmiranda</td>			
				<td><a href="datasetBuilder.list">Delete</a></td>			
			</tr>
		</table>
		
		
	</div>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
