package com.seven10.update_guy.release;

import java.io.File;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;

@Path("release")
public class ReleaseServlet
{
	CacheManager cacheMgr;
	
	public ReleaseServlet(String s)
	{
		
	}
	
	@GET
	@Path("activeVersion")
	public Response setActiveVersion(@QueryParam("version") String version, @Suspended AsyncResponse response)
	{
		ResponseBuilder resp;
		try
		{
			cacheMgr.setActiveVersion(version, response);
			resp = Response.ok();
		}
		catch (RepositoryException ex)
		{
			resp = Response.status(Status.NOT_MODIFIED).entity(ex);
		}
		return resp.build();
	}
	
	@GET
	@Path("activeVersion/list")
	public Response showActiveVersion()
	{
		String version = cacheMgr.getActiveVersion();
		ResponseBuilder resp = Response.ok().entity(version);
		return resp.build();
	}
	
	/**
	 * Gets the list of roles that the active version entry provides files for
	 * 
	 * @return
	 */
	@GET
	@Path("roles")
	public Response getRoles()
	{
		ResponseBuilder resp = null;
		List<String> roles = cacheMgr.getAvailableRoles();
		
		String json = GsonFactory.getGson().toJson(roles);
		resp = Response.ok().entity(json);
		
		return resp.build();
	}
	
	@GET
	@Path("fingerprint")
	public Response getFingerprint(@QueryParam("roleName") String roleName)
	{
		ResponseBuilder resp = null;
		try
		{
			String fingerPrint = cacheMgr.getFingerPrintForRole(roleName);
			resp = Response.ok().entity(fingerPrint);
		}
		catch (RepositoryException ex)
		{
			resp = Response.status(Status.NOT_MODIFIED).entity(ex);
		}
		
		return resp.build();
	}
	
	@GET
	@Path("download")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@QueryParam("roleName") String roleName)
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
}
