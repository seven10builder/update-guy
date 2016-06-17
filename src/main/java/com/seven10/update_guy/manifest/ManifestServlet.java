package com.seven10.update_guy.manifest;

import java.util.List;
import java.util.NoSuchElementException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;

@Path("manifest")
public class ManifestServlet
{

	private ManifestMgr manifestMgr;

	public ManifestServlet(ManifestMgr mgr)
	{
		manifestMgr = mgr;
	}
	@GET
	@Path("show")
	public Response getManifest(@QueryParam("releaseFamily") String releaseFamily)
	{
		ResponseBuilder resp = null;
		try
		{
			Manifest manifest = manifestMgr.getManifest(releaseFamily);

			String json = GsonFactory.getGson().toJson(manifest);
			resp = Response.ok().entity(json);
		}
		catch(NoSuchElementException ex)
		{
			resp.status(Status.NOT_FOUND).entity(ex.getMessage());
		}
		catch(RepositoryException ex)
		{
			resp = Response.serverError().entity(ex.getMessage());
		}
		return resp.build();
	}
	@GET
	@Path("show")
	public Response getManifests()
	{
		ResponseBuilder resp = null;
		try
		{
			List<Manifest> manifests = manifestMgr.getManifests();
			String json = GsonFactory.getGson().toJson(manifests);
			resp = Response.ok().entity(json);
		}
		catch(RepositoryException ex)
		{
			resp = Response.serverError().entity(ex.getMessage());
		}
		return resp.build();
	}
	@GET
	@Path("activeVersion")
	public Response setActiveVersion(@QueryParam("version") String version)
	{
		ResponseBuilder resp;
		try
		{
			manifestMgr.setActiveVersion(version);
			resp = Response.ok();
		}
		catch (RepositoryException ex)
		{
			resp = Response.status(Status.NOT_MODIFIED).entity(ex);
		}
		return resp.build();
	}
	
	@GET
	@Path("activeVersion")
	public Response showActiveVersion()
	{
		String version = manifestMgr.getActiveVersion();
		ResponseBuilder resp = Response.ok().entity(version);
		return resp.build();
	}
	
}
