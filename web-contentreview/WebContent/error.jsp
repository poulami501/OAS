<%@ page import="java.io.*, java.util.*"%>
<!--Generated by WebLogic Workshop-->
<%@ page language="java" contentType="text/html;charset=UTF-8" isErrorPage="true"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-databinding-1.0" prefix="netui-data"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-html-1.0" prefix="netui"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-template-1.0" prefix="netui-template"%>
<netui:html>
  <head>
    <title>Error</title>
  </head>
  <body>
   
    <p>
      An error has occurred: Error 
    </p>
    <%= request.getAttribute("errorMessage") %>
  </body>
</netui:html>


<!-- Return status 404 -->
<% response.setStatus(404); %>