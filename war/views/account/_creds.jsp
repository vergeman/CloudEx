<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>
<%
	BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();
%>

<fieldset>
<legend><h2>Amazon Credentials</h2></legend>

<a>Current File:&nbsp ${fileName}</a>
<br>
<br>

<form action="<%= blobstoreService.createUploadUrl("/upload") %>"
	method="post" enctype="multipart/form-data">
	
	<input type="file" name="myFile"> <br> <br> <input
		id="upload_submit" type="submit" value="Submit">
</form>
<br>



<form action="/account" method="post">
	<tr>
		<b>Default Key Pair</b> <br> 
		<input type="text" name="keyPair" value="${keyPair}"> 
		<br>
		</td>
	</tr>

	<tr>
		<td><b>Default Security Group</b> <br> <input type="text"
			name="securityGroup" value="${securityGroup}"> <br></td>
	</tr>
<br>
	<tr>
		<td><input id="save_submit" type="submit" value="Save"></td>
	</tr>
</form>


</fieldset>

