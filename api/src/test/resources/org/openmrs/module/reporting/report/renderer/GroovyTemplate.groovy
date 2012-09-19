<?xml version="1.0"?>
<dataset>
	<rows>
		<% 	dataset = reportData.dataSets.get("allPatients");
			for (row in dataset) { 
		%>
			<row>
				<% for (column in dataset.metaData.columns) { 
					colValue = row.getColumnValue(column);
					colValue = (colValue instanceof java.util.Date) ? util.format(colValue, 'dd/MMM/yyyy') : util.format(colValue);
				%>
					<$column.label>$colValue</$column.label>
				<% } %>
			</row>
		<% } %>
	</rows>
</dataset>
