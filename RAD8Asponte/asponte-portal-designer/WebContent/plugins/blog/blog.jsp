<%@ page session="false" isELIgnored="false" import="java.util.*,javax.portlet.*,com.asponte.portal.designer.*" 
	%><%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"
	%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"
	%><fmt:setBundle basename="com.asponte.portal.designer.nl.PageElementPortletResource" 
	/><portlet:defineObjects/><%
String ns=renderResponse.getNamespace();
final String METHOD_NAME="blog.jsp";
if(Jsp.LOGGER.isLoggable(java.util.logging.Level.FINER)){Jsp.LOGGER.finest(METHOD_NAME+": ENTRY ("+ns+")");}
%><script type="text/javascript" language="JavaScript">
dojo.require('dijit.form.ValidationTextBox');
</script>
<div id="<%=ns%>blogBuilder">
	<div>
		<label for="<%=ns%>blogPageTitle" title="<fmt:message key="blog_builder_title_label_tip" />"><fmt:message key="blog_builder_title_label" /></label><br />
		<input type="text" name="blog_title" id="<%=ns%>blogPageTitle" value="" dojoType="dijit.form.ValidationTextBox" trim="true" propercase="true" required="true" invalidMessage="<fmt:message key="blog_builder_invalid_title_message" />" regExp="[^\s].*" />
	</div>
</div><%
if(Jsp.LOGGER.isLoggable(java.util.logging.Level.FINER)){Jsp.LOGGER.finest(METHOD_NAME+": EXIT ("+ns+")");}
%>