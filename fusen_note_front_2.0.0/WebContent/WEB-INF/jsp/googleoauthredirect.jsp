<%@page import="com.rmat.fusen.bl.function.GoogleOAuthManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
</head>
<body>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%
	String authurl = (String)request.getAttribute(GoogleOAuthManager.REDIRECT_URL_KEY);
%>
<logic:redirect href="<%=authurl %>"/>

</body>
</html>