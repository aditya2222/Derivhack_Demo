<%@page import="com.algorand.demo.JSONTemplate"%>

<html>
<head>
	<title>Transact Financial Derivatives</title>
	<link rel="stylesheet" type="text/css" href="resources/index.css">
	<script src="resources/index.js"></script>
</head>
<body>
<p>Choose CDM object to edit:</p>

<form autocomplete="off" method="post" action="InputObject">
  <div class="autocomplete" style="width:300px;">
    <input id="cdminput" type="text" name="cdminput" placeholder="CDMObject">
  </div>
  <input type="submit">
</form>

</body>
</html>

<script>
	var arrayString = <%out.println("\""+JSONTemplate.getCDMClassPaths().toString()+"\"");%>;
	arrayString = arrayString.replace(/\s+/g, '');
	arrayString = arrayString.replace("[","");
	arrayString = arrayString.replace("]","");
	arrayString = arrayString.replace(/org.isda.cdm./g,"");
	console.log(arrayString);
	var array = arrayString.split(",");
	autocomplete(document.getElementById("cdminput"), array);
</script>