<!--Generated by WebLogic Workshop-->
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="netui-tags-databinding.tld" prefix="netui-data"%>
<%@ taglib uri="netui-tags-html.tld" prefix="netui"%>
<%@ taglib uri="netui-tags-template.tld" prefix="netui-template"%>
<netui:html>
  <head>
    <title>Content Previewer</title>
  </head>
    <body>
        
        <netui:form action="upload">
            <table>
                <tr valign="top">
                    <td>AssessmentXML:</td>
                    <td>
                    <netui:textArea rows="40" cols="80" dataSource="{actionForm.assessmentXML}"/>
                    </td>
                </tr>
            </table>
            <br/>&nbsp;
            <netui:button value="upload" type="submit"/>
        </netui:form>
    </body>
</netui:html>
