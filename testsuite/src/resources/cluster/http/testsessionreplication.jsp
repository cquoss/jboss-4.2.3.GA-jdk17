<html>
<!-- <body bgcolor=blue> -->
<center>
<% String id=request.getSession().getId();
   session.setAttribute("TEST_HTTP",id);
   // Expire after 20 secs so we can more promptly run timeout tests 
   session.setMaxInactiveInterval(20);
%>
<p>Storing session id in attribute with id: <%=id%>

<h1><%=application.getServerInfo()%>:<%=request.getServerPort()%></h1>
</body>
</html>
