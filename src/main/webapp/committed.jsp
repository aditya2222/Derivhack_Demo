<html>
	<body>
<%
String result= request.getAttribute("txdetails").toString();
result = result.replaceAll("(\r\n|\n)", "<br>");
out.println("<br>"+result+"<br>");
%>
	<br><br>
<p>Here is a link to your committed CDM object</p>
<%
String link = request.getAttribute("txlink").toString();
out.println("<a href=\"" + link + "\">" + link + "</a>");
%>
	</body>
</html>