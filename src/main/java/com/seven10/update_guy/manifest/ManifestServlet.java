package com.seven10.update_guy.manifest;

import java.util.List;
import java.util.NoSuchElementException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.Globals;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;

@Path("/manifest")
public class ManifestServlet
{
	private static final Logger logger = LogManager.getFormatterLogger(ManifestServlet.class);
	
	private ManifestMgr getManifestMgr()
	{
		Globals globals = Globals.getGlobals();
		ManifestMgr manifestMgr = new ManifestMgr(globals);
		return manifestMgr;
	}
	
	public ManifestServlet()
	{
	}
	
	@GET
	@Path("/show")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getManifestAll()
	{
		ResponseBuilder resp = null;
		try
		{
			List<Manifest> manifests = getManifestMgr().getManifests();
			String json = GsonFactory.getGson().toJson(manifests);
			resp = Response.ok().entity(json);
		}
		catch (NoSuchElementException ex)
		{
			resp.status(Status.NOT_FOUND).entity(ex.getMessage());
		}
		catch (RepositoryException ex)
		{
			resp = Response.serverError().entity(ex.getMessage());
		}
		return resp.build();
	}
	
	@GET
	@Path("/show/{releaseFamily}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getManifest(@PathParam("releaseFamily") String releaseFamily)
	{
		logger.debug(".getManifest(): releaseFamily = %s", releaseFamily);
		ResponseBuilder resp = null;
		try
		{
			Manifest manifest = getManifestMgr().getManifest(releaseFamily);
			String json = GsonFactory.getGson().toJson(manifest);
			logger.debug(".getManifest(): manifest = %s", json);
			resp = Response.ok().entity(json);
		}
		catch (NoSuchElementException ex)
		{
			String msg = String.format("could not find manifest for release family '%s'. Reason: %s", releaseFamily,
										ex.getMessage());
			logger.debug(String.format(".getManifest(): %s",msg));
			resp.status(Status.NOT_FOUND).entity(GsonFactory.createJsonFromString("error", msg));
		}
		catch (RepositoryException ex)
		{
			String msg = String.format("could not create manifest for release family '%s'. Reason: %s", releaseFamily,
									ex.getMessage());
			logger.debug(String.format(".getManifest(): %s",msg));
			resp = Response.serverError().entity(GsonFactory.createJsonFromString("error", msg));
		}
		return resp.build();
	}
	
	@GET
	@Path("/activeVersion")
	@Produces("text/plain")
	public Response setActiveVersion(@QueryParam("version") String version)
	{
		logger.debug(".getManifest(): version = %s", version);
		ResponseBuilder resp;
		try
		{
			if (version != null)
			{
				getManifestMgr().setActiveVersion(version);
			}
			version = getManifestMgr().getActiveVersion();
			resp = Response.ok().entity(version);
		}
		catch (RepositoryException ex)
		{
			String msg = String.format("could not set active version to '%s'. Reason: %s", version,
										ex.getMessage());
			logger.debug(String.format(".setActiveVersion(): %s",msg));
			resp = Response.status(Status.NOT_MODIFIED).entity(msg);
		}
		return resp.build();
	}
	
}
