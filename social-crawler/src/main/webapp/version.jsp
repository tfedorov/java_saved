<%@ page language="java" pageEncoding="UTF-8"%>

<html>
<head>
<title>Build metainfo</title>
</head>
<body>
	<%
		java.io.InputStream stream = pageContext.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF");
		if (stream == null) {
			// There are no "/META-INF/MANIFEST.MF" in ServletContext(
			out.println("Required parameters are in \"META-INF/MANIFEST.MF\" file. Application could not find \"META-INF/MANIFEST.MF\"</br>This functionality does not supported for <span style=\'color: red\'>Jetty</span> app server");
		} else {
			java.util.jar.Manifest manifest = new java.util.jar.Manifest();
			manifest.read(stream);
			java.util.jar.Attributes attributes = manifest.getMainAttributes();
			out.println("<span style=\'font-size: large \'> Build information </span></br>");
			for (Object ob : attributes.keySet()) {
				out.println("<span style=\'font-weight: bold\'>" + ob + "</span> : " + attributes.get(ob) + "</br>");
			}
		}
	%>
</body>
</html>