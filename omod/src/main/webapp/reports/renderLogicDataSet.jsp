<%@ include file="/WEB-INF/template/include.jsp"%>
<c:set var="__openmrs_hide_report_link" value="true"/>
<%@ include file="../run/localHeader.jsp"%>

<div style="border: 1px black solid; background-color: #e0e0e0">
	<c:if test="${start > 0}">
		<a href="?start=0&size=${size}">
	</c:if>
	Start
	<c:if test="${start > 0}">
		</a>
	</c:if>
	&nbsp;&nbsp;
	<c:if test="${start > 0}">
		<a href="?start=${start - size}&size=${size}">
	</c:if>
	Previous
	<c:if test="${start > 0}">
		</a>
	</c:if>
	&nbsp;&nbsp;&nbsp;&nbsp;
	
	Showing <b>${start + 1}</b> to <b>${start + fn:length(rows)}</b> of ${cohortSize}
	
	&nbsp;&nbsp;&nbsp;&nbsp;
	<c:if test="${fn:length(rows) == size}">
		<a href="?start=${start + size}&size=${size}">
	</c:if>
	Next
	<c:if test="${fn:length(rows) == size}">
		</a>
	</c:if>
	&nbsp;&nbsp;
	<c:if test="${fn:length(rows) == size}">
		<a href="?start=${startOfLast}&size=${size}">
	</c:if>
	End
	<c:if test="${fn:length(rows) == size}">
		</a>
	</c:if>
</div>

<table>
	<thead>
		<tr>
			<c:forEach var="col" items="${columns.columns}">
				<th>${col.label}</th>
			</c:forEach>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="row" items="${rows}">
			<tr>
				<c:forEach var="cell" items="${row.columnValues}">
					<td><rpt:format object="${cell.value}"/></td>
				</c:forEach>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>