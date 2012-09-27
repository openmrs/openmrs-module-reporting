<% 	dataset = reportData.dataSets.get("obs");
	for (row in dataset) { 
%>

		<% for (column in dataset.metaData.columns) { 
			colValue = row.getColumnValue(column);
			colValue = colValue.getConcept().getName();
		%>
			<$column.label>$colValue</$column.label>
		<% } %>
<% } %>

