
<%@page import="com.rmat.fusen.util.AppProperty"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<logic:redirect href="http://fusen.rmatsu.com/top.do"/>

<%--

Redirect default requests to Welcome global ActionForward.
By using a redirect, the user-agent will change address to match the path of our Welcome ActionForward. 

--%>