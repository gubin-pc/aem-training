<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.epam.aem_training.core.GoogleAuthHelperService" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<cq:includeClientLib categories="aemnews"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${ currentPage.template.title }</title>
</head>
<body>
	<cq:include path="title"
		resourceType="aemtraining/components/content/title" />
		<%
			GoogleAuthHelperService helper = sling.getService(GoogleAuthHelperService.class);
			Cookie[] cookies = request.getCookies();
	       	Cookie userIdCookie = null;
	       
	       	if( cookies != null ) {
	        	for (int i = 0; i < cookies.length; i++) {
	            	if (cookies[i].getName().equals("userId") && cookies[i].getValue() != null)
	                        userIdCookie = cookies[i];                      
	             }
	         }
	       	
	       	if (userIdCookie == null) {
	            out.println("<a href='" + helper.buildLoginUrl() + "'><img src='http://uu.appsforall.ru/52b85db9394901.28865868.jpeg' width=50 ></a>");
	        } else {
	            out.print("Hi, ");
	            out.println(helper.getUserName(userIdCookie.getValue()));
	            out.println(" <span style='color:grey;'>");
	            out.println(helper.getUserEmail(userIdCookie.getValue()));
	            out.println("</span>");
	            out.println(" <a href='/services/googlelogout'>logout</a>");
	            
	    %>
	    	<cq:include path="par" resourceType="foundation/components/parsys" />
	    <%
	        }
		%>
		
	
</body>
</html>