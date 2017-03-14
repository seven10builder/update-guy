<%@ page language="java" contentType="text/html" %>
<%@ page import="com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType,java.util.stream.Collectors,java.util.*" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%
	List<String> repoTypes = Arrays.asList(RepositoryType.values()).stream().map(v->v.toString()).collect(Collectors.toList());
	pageContext.setAttribute("repoTypes", repoTypes);
%>

<html>
<body>
	<table style="width: 100%">
		<caption>
			Repository (No ID assigned yet)"
		</caption>
		<tr>
			<th title="Repository type">Repository Type</th>
			<th>
				<select>
					<c:forEach items="${repoTypes}" var="current">
						<option value="${current}">${current}</option>
					</c:forEach>
				</select>
			</th>
		</tr>
		<tr>
			<th title="a human readable description">Repository Description
			</th>
			<th></th>
		</tr>
		<tr>
			<th
				title="The DNS-resolvable name or IP address for the repo. This should be 'localhost' for local repos">
				Repository Address</th>
			<th></th>
		</tr>
		<tr>
			<th title="The port to use for this repo">Repository Connection
				Port</th>
			<th></th>
		</tr>
		<tr>
			<th
				title="The user account for this repo. This value is ignored for local repos">
				Repository User Account</th>
			<th></th>
		</tr>
		<tr>
			<th
				title="The password for this repo. This value is ignored for local repos">
				Repository Password (not shown)</th>
			<th />
		</tr>
		<tr>
			<th
				title="The path on the repo where any release family files are stored">
				Release Family Remote Storage Path</th>
			<th></th>
		</tr>
		<tr>
			<th
				title="order in which to look for information when autodetecting the correct repo">
				Repository Priority
			</th>
			<th></th>
		</tr>
	</table>
</body>
</html>