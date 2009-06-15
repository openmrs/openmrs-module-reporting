<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/datasetbuilder/index.htm" />
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<style>

legend { 
	font-size: 1.5em;
	
}

optgroup { 
	font-size: 20pt;
	text-decoration: none;
}

.left { 
	float: left; 
	/*overflow: auto;*/ 
	width: 20%; 
	height: 50%; 
	position: relative; 
	border: 5px solid #ccc;
}

.middle { 
	float: left; 
	width: 10%;
	height: 50%;
	position: relative;
	border: 5px solid red;
	vertical-align: middle;
	text-align: center;	
}

.right {  
	float: left;
	/*overflow: auto;*/ 
	width: 20%; 
	height: 50%;
	position: relative; 
	border: 5px solid blue;
}

.overflow { 
	overflow: auto;
}


.box { 
	position: absolute; 
	height: 90%; 
}

table.preview { 
	border: 0px solid black;
}
table.preview th { 
	text-align: center;
}
table.preview td { 
	border: 1px solid #ccc; 
	font-size: .8em;
	color: #ddd;
	text-align: center;
}
fieldset { 
	padding: 5px; 
}

</style>



<b class="boxHeader">Dataset Builder</b>
<div class="box">
	
	<h1>Choose your columns:</h1>
	
	<div class="left">
	
		Available columns:
		<select style="width:100%; height:100%" multiple="10">
			<optgroup label="Patient"></optgroup>
			<option value="">Primary Key</option>
			<option value="">Identifier</option>
			<option value="">Name</option>
			<option value="">Address</option>
			<option value=""></option>
			<option value=""></option>
			<option value=""></option>
			<optgroup label="Encounter"></optgroup>
			<option value=""></option>
			<option value=""></option>
			<optgroup label="Program"></optgroup>
			<option value=""></option>
			<option value=""></option>
			<optgroup label="Location"></optgroup>
			<option value=""></option>
			<option value=""></option>
		</select>
	</div>
	
	<div class="middle" valign="middle">
	
		<a href="">add >></a> <br/>
		<a href="">&lt;&lt;remove</a>
	
	
	</div>
	
	<div class="right">
	
		Selected columns
		<select style="width:100%; height:100%" multiple="10">
			<optgroup label="Patient"></optgroup>
			<option value="">Primary Key</option>
			<option value="">Identifier</option>
			<option value="">Name</option>
			<option value="">Address</option>
			<option value=""></option>
			<option value=""></option>
			<option value=""></option>
			<optgroup label="Encounter"></optgroup>
			<option value=""></option>
			<option value=""></option>
			<optgroup label="Program"></optgroup>
			<option value=""></option>
			<option value=""></option>
			<optgroup label="Location"></optgroup>
			<option value=""></option>
			<option value=""></option>
		</select>	
	</div>
</div>

<!--  
<div>
	<b class="boxHeader">Dataset Builder</b>
	<div class="box">
	
	<div>
		<span>Step 1</span><span>Pick your columns</span>
	
	
		<div class="left">
			<form>
				<fieldset>
					<legend>Advanced</legend>
					<div>
						<input type="checkbox" name=""/><label for="">LAST WEIGHT</label>
					</div>
					<div>
						<input type="checkbox" name=""/><label for="">LAST CD4</label>
					</div>
					<div>
						<input type="checkbox" name=""/>
						<input type="text" name=""/>
						<input type="submit" value="Search"/>
					</div>
				</fieldset>
				<fieldset>
					<legend>Patient</legend>
					<div>
						<input type="checkbox" name=""/><label for="">Identifier</label>
					</div>
					<div>
						<input type="checkbox" name=""/><label for="">First Name</label>
					</div>
					<div>
						<input type="checkbox" name=""/><label for="">Last Name</label>
					</div>					
				</fieldset>		
			
				<fieldset>
					<legend>Other columns</legend>
					<div>
						<input type="checkbox" name=""/><label for="">Encounter ID</label>
					</div>
					<div>
						<input type="checkbox" name=""/><label for="">Encounter date</label>
					</div>
					<div>
						<input type="checkbox" name=""/><label for="">Location</label>
					</div>					
					<div>
						<input type="checkbox" name=""/><label for="">Provider</label>
					</div>					
				</fieldset>		
			</form>		
		</div>		
	</div>
</div>
-->



<!--

<div>		
	
	
	<span>Step 2</span><span>Preview your Data</span>
	<div class="left">
	
		<fieldset>
			<legend>View your data</legend>
	
				<div class="overflow">
					<table class="preview" width="100%" border="0">
						<tr>
							<th>Row Numbe</th>
							<th>column 1</th>
							<th>column 2</th>
							<th>column 3</th>
							<th>column 4</th>
							<th>column 5</th>
							<th>column 6</th>
							<th>column 7</th>
							<th>column 8</th>
							<th>column 9</th>
						</tr>
						<tr>
							<td>1</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>
						<tr>
							<td>2</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>					
						<tr>
							<td>3</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>					
						<tr>
							<td>4</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>					
						<tr>
							<td>5</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>					
						<tr>
							<td>6</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>					
						<tr>
							<td>7</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>
						<tr>
							<td>8</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>								
						<tr>
							<td>9</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>								
						<tr>
							<td>10</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>								
						<tr>
							<td>11</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>								
						<tr>
							<td>12</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>								
						<tr>
							<td>13</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>								
						<tr>
							<td>14</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>								
						<tr>
							<td>...</td>
							<td>...</td>
							<td>...</td>
							<td>...</td>
							<td>...</td>
							<td>...</td>
							<td>...</td>
							<td>...</td>
							<td>...</td>
						</tr>								
						<tr>
							<td>25</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
							<td>data</td>
						</tr>								
					</table>
				</div>					
		</fieldset>		
	</div>
</div>
-->	

<%@ include file="/WEB-INF/template/footer.jsp"%>
