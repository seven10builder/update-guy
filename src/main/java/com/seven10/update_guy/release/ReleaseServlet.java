package com.seven10.update_guy.release;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestVersionEntry;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.connection.RepoConnection;
import com.seven10.update_guy.repository.connection.RepoConnectionFactory;

@Path("release")
public class ReleaseServlet
{
	RepositoryInfo activeRepoInfo;
	ManifestVersionEntry versionEntry;
	
	public ReleaseServlet()
	{
		
	}
	
	@GET
	@Path("setActiveVersion")
	public Response setActiveVersion(@QueryParam("version") String version)
	{
		
	}
	/**
	 * Gets the list of roles that the active version entry provides files for
	 * @return
	 */
	@GET
	@Path("roles")
	public Response getRoles()
	{
		ResponseBuilder resp = null;
		List<String> roles = versionEntry.getRoles();
			
		String json = GsonFactory.getGson().toJson(roles);
		resp = Response.ok().entity(json);

		return resp.build();
	}
	
	@GET
	@Path("fingerprint")
	public Response getFingerprint(@QueryParam("roleName") String roleName)
	{
		ResponseBuilder resp = null;
		List<String> roles = versionEntry.getRoles();
			
		String json = GsonFactory.getGson().toJson(roles);
		resp = Response.ok().entity(json);

		return resp.build();
	}
	@GET
	@Path("download")
	public Response getFile(@QueryParam("roleName") String roleName)
	{
		
	}
}
