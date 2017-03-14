<html>
<body>
	<table style="width: 100%">
		<caption>
			Repository
			<%= repoId %></caption>
		<tr>
			<th title="Repository type">Repository Type</th>
			<th><%= repoType %></th>
		</tr>
		<tr>
			<th title="a human readable description">Repository Description
			</th>
			<th><%= repoDesc %></th>
		</tr>
		<tr>
			<th
				title="The DNS-resolvable name or IP address for the repo. This should be 'localhost' for local repos">
				Repository Address</th>
			<th><%= repoAddress %></th>
		</tr>
		<tr>
			<th title="The port to use for this repo">Repository Connection
				Port</th>
			<th><%= repoPort %></th>
		</tr>
		<tr>
			<th
				title="The user account for this repo. This value is ignored for local repos">
				Repository User Account</th>
			<th><%= repoUser %></th>
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
			<th><%= repoRelFamilyPath %></th>
		</tr>
		<tr>
			<th
				title="order in which to look for information when autodetecting the correct repo">
				Repository Priority
			</th>
			<th><%=  repoPriority %></th>
		</tr>
	</table>
</body>
</html>