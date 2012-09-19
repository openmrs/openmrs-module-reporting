var dataset = reportData.getDataSets().get("allPatients");	
var columns = dataset.getMetaData().getColumns();

var output = "<?xml version=\"1.0\"?>\n";
output += "<dataset>\n";
output += "\t<rows>\n";

//for (var row in dataset) {	
var datasetIterator = dataset.iterator();
while (datasetIterator.hasNext()) {
	var row = datasetIterator.next();
	output += "\t\t<row>";
	
	var columnsIterator = columns.iterator();
	//for (var column in columns) {	
	while (columnsIterator.hasNext()) {
		var column = columnsIterator.next();		
		var colValue = row.getColumnValue(column);
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