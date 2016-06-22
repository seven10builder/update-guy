package com.seven10.update_guy.release;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.seven10.update_guy.FileFingerPrint;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.ManifestEntry;
import com.seven10.update_guy.repository.RepositoryInfo;

@Path("/release/{repoId}/{releaseId}")
public class ReleaseServlet
{
	private final ReleaseMgr cacheMgr;
	
	private ManifestEntry loadManifestEntry(String releaseId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private RepositoryInfo loadRepoInfo(String repoId)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public ReleaseServlet(@PathParam("repoId") String repoId, @PathParam("releaseId") String releaseId)
	{
		RepositoryInfo repoInfo = loadRepoInfo(repoId);
		ManifestEntry manifestEntry = loadManifestEntry(releaseId);

		this.cacheMgr = new ReleaseMgr(manifestEntry, repoInfo);
	}
	
	

	/**
	 * Gets the list of roles that the active version entry provides files for
	 * 
	 * @return
	 */
	@GET
	@Path("/roles")
	public Response getRoles()
	{
		ResponseBuilder resp = null;
		List<String> roles = cacheMgr.getAllRoles();
		
		String json = GsonFactory.getGson().toJson(roles);
		resp = Response.ok().entity(json);
		
		return resp.build();
	}
	
	@GET
	@Path("/fingerprint/{roleName}")
	public Response getFingerprint(@PathParam("roleName") String roleName)
	{
		ResponseBuilder resp = null;
		try
		{
			File file = cacheMgr.getFileForRole(roleName);
			String fingerPrint = FileFingerPrint.create(file);
			resp = Response.ok().entity(fingerPrint);
		}
		catch (RepositoryException ex)
		{
			resp = Response.status(ex.getStatusCode()).entity(ex);
		}
		catch (IOException ex)
		{
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex);
		}
		
		return resp.build();
	}
	
	@GET
	@Path("/download/{roleName}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam("roleName") String roleName)
	{
		ResponseBuilder resp = null;
		try
		{
			File file = cacheMgr.getFileForRole(roleName);
			
			resp = Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
		      .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" );
		}
		catch (RepositoryException ex)
		{
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex);
		}
		return resp.build();
	}
	

	@GET
	@Path("/update-cache")
	public Response doUpdateCache(final @Suspended AsyncResponse response) throws RepositoryException
	{
		cacheMgr.cacheFiles(response);
		ResponseBuilder	resp = Response.ok();
		return resp.build();
	}
}
