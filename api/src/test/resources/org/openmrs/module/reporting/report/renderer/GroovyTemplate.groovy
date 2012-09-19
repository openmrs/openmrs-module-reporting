import org.openmrs.Cohort;

dataset = reportData.getDataSets().get("allPatients");
columns = dataset.getMetaData().getColumns();

output = "<?xml version=\"1.0\"?>\n";
output += "<dataset>\n";
output += "\t<rows>\n";

for (row in dataset) {
	output += "\t\t<row>";
	
	for (column in columns) {
		colValue = row.getColumnValue(column);
		output += "<" + column.getLabel() + ">";
		if (colValue != null) { 
			if (colValue instanceof java.util.Date) {
				output += util.format(colValue, 'dd/MMM/yyyy');
			}
			else {
				output += util.format(colValue);
			}
		}
		output += "</" + column.getLabel() + ">";
	}
	
	output += "</row>\n";
}
output += "\t</rows>\n";
output += "</dataset>\n";