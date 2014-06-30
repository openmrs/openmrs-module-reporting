/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.report.util;

import java.util.List;

import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.module.reporting.data.GraphData;

/**
 * Provides utility functions for graphing
 */
public class GraphUtil {

	public static GraphData getData(List<Obs> observations, String conceptBoxDivId, String graphDivId, String graphTitleSpanClass) {
		
		ConceptNumeric concept = (ConceptNumeric)observations.get(0).getConcept();
		
		GraphData graph = new GraphData();
		graph.setCriticalHigh(concept.getHiCritical());
		graph.setCriticalLow(concept.getLowCritical());
		graph.setAbsoluteHigh(concept.getHiAbsolute());
		graph.setAbsoluteLow(concept.getLowAbsolute());
		graph.setNormalHigh(concept.getHiNormal());
		graph.setNormalLow(concept.getLowNormal());
		graph.setUnits(concept.getUnits());
		graph.setConceptName(concept.getName().getName());
		
		for (Obs obs : observations) {
			graph.addValue(obs.getObsDatetime().getTime(), obs.getValueNumeric());
		}
		
		return graph;
	}
	
	public static String render(List<Obs> observations, String conceptBoxDivId, String graphDivId, String graphTitleSpanClass) {
		return "<script type=\"text/javascript\">" +
		"function findMaxAndMin(dataset) {" +
		"	if(undefined == dataset)return undefined;" +
		"		var arr = [];" +
		"		for( var i=0;i<dataset.length;i++){" +
		"		   arr[i] = dataset[i][1];" +
		"	}" +
		"	arr.sort(function(p1,p2){return p1-p2});" +
		"	return { min:arr[0],max:arr[arr.length-1]};" +
		"}" +
		"  " +
		"function showToolTip(x, y, contents){" +
		"    $j('<div id=\"tooltip\">' + contents + '</div>').css( {" +
		"           position: 'absolute'," +
		"           display: 'none'," +
		"           top: y + 5," +
		"           left: x + 5," +
		"           border: '1px solid #fdd'," +
		"           padding: '2px'," +
		"           'background-color': '#fee'," +
		"           opacity: 0.80" +
		"       }).appendTo(\"body\").fadeIn(200);" +
		"}" +
		"" +
		"function formatDateForGraph(dateToFormat) {" +
		"	return \"\" + dateToFormat.getDate() + \"/\" + (dateToFormat.getMonth() + 1) + \"/\" + dateToFormat.getFullYear();" +
		"}" +
		"" +
		"" +
		"function loadGraph() {" +
		"	<c:if test=\"${conceptId != ''}\">" +
		"		<openmrs:globalProperty var=\"colorAbsolute\" key=\"graph.color.absolute\"/>" +
		"		<openmrs:globalProperty var=\"colorNormal\" key=\"graph.color.normal\"/>" +
		"		<openmrs:globalProperty var=\"colorCritical\" key=\"graph.color.critical\"/>	" +		
		"$j.getJSON(\"patientGraphJson.form?patientId=${patient.patientId}&conceptId=${conceptId}\", function(json){" +
		"	  $j(\"#" + conceptBoxDivId + "." + graphTitleSpanClass + "\").html(json.name);" +
		"	" +
		"	  var plot = $j.plot($j('#" + graphDivId + "')," +
		"	  [{" +
		"	  	data: json.data, " +
		"	  	lines:{show:true}, " +
		"	  	points: { show: true }, " +
		"	  	color:\"rgb(0,0,0)\"," +
		"	  	constraints: [{" +
        "          	    threshold: {above:json.normal.high}," +
        "          	    color: \"${colorNormal}\"" +
        "         	}, {" +
        "             	threshold: {below:json.normal.low}," +
        "          	    color: \"${colorNormal}\"" +
        "         	}, {" +
        "          	    threshold: {above:json.absolute.high}," +
        "          	    color: \"${colorAbsolute}\"" +
        "         	}, {" +
        "          	    threshold: {below:json.absolute.low}," +
        "          	    color: \"${colorAbsolute}\"" +
        "         	}, {" +
        "          	    threshold: {above:json.critical.high}," +
        "          	    color: \"${colorCritical}\"" +
        "         	}, {" +
        "          	    threshold: {below:json.critical.low}," +
        "          	    color: \"${colorCritical}\"" +
        "         	}]" +
        "         }], {" +
        "             xaxis: {mode: \"time\", timeformat: \"%b %y\", minTickSize: [1, \"month\"]}," +
		"		  yaxis: {min: findMaxAndMin(json.data).min-10, max: findMaxAndMin(json.data).max+10, tickFormatter: function (v, axis) { return v.toFixed(axis.tickDecimals) + \" \" + json.units }}," +
		"	  	  grid: { hoverable: true, clickable: true }" +
		"		});							  " +
		"	" +
		"	  $j(\"#" + conceptBoxDivId + "\").bind(\"plothover\", function (event, pos, item) {" +
		"		 $j(\"#tooltip\").remove();" +
		"		 plot.unhighlight();" +
		"		 if (item) {" +
		"		  	showToolTip(item.pageX, item.pageY, \"\" + formatDateForGraph(new Date(item.datapoint[0])) + \": \" + item.datapoint[1] + \" \" + json.units);" +
		"		  	plot.highlight(item.series, item.datapoint);" +
		"		 }" +
		"	  });" +
		"});" +
		"	</c:if>" +
	    "" +
		"}" +
        "" +
		"$j(document).ready(function () {" +
        "" +
		"	window.setTimeout(loadGraph, 1000);" +
		"});" +
		"" +
		"</script>" ;
	}
}
