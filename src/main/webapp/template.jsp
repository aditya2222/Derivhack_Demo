<html>
	<body>
		<form method="post" action="NetworkCommit">
<%
String result= request.getAttribute("template").toString();
out.println("<textarea rows='50' cols='50' name='editTemplate'>"+result+"</textarea>");
%>
			<input type = "submit" value = "submit" />
		</form>
	</body>
</html>