package com.seven10.update_guy.server.manifest;

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

import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.manifest.Manifest;
import com.seven10.update_guy.common.manifest.ManifestEntry;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.exceptions.RepositoryException;

@Path("/manifest/{repoId}")
public class ManifestServlet
{
	private static final Logger logger = LogManager.getFormatterLogger(ManifestServlet.class);
	ManifestMgr manifestMgr;
	String repoId;

	public static Manifest getManifestById(String releaseFamily, String repoId, ManifestRefresher manifestRefresher) throws RepositoryException
	{
		java.nio.file.Path manifestPath = ServerGlobals.getManifestStorePath(repoId)
				.resolve(String.format("%s.manifest", releaseFamily));
		try
		{
			manifestRefresher.refreshLocalManifest(releaseFamily);
			return Manifest.loadFromFile(manifestPath);
		}
		catch (UpdateGuyException ex)
		{
			logger.error(".getManifestById(): could not load manifest for id '%s'. Reason: %s", manifestPath, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not load manifest for id '%s'", manifestPath);
		}
	}

	public ManifestServlet(@PathParam("repoId") String repoId)
	{
		this.repoId = repoId;
		java.nio.file.Path manifestPath = ServerGlobals.getManifestStorePath(repoId);
		manifestMgr = new ManifestMgr(manifestPath, repoId);
	}
	
	@GET
	@Path("/show")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getManifestAll()
	{
		ResponseBuilder resp = null;
		try
		{
			List<Manifest> manifests = manifestMgr.getManifests();
			String json = GsonFactory.getGson().toJson(manifests);
			resp = Response.ok().entity(json);
		}
		catch (NoSuchElementException ex)
		{
			resp = Response.status(Status.NOT_FOUND).entity(ex.getMessage());
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
		ResponseBuilder resp;
		try
		{
			Manifest manifest = manifestMgr.getManifest(releaseFamily);
			String json = GsonFactory.getGson().toJson(manifest);
			logger.debug(".getManifest(): manifest = %s", json);
			resp = Response.ok().entity(json);
		}
		catch (NoSuchElementException ex)
		{
			String msg = String.format("could not find manifest for release family '%s'. Reason: %s", releaseFamily,
										ex.getMessage());
			logger.debug(String.format(".getManifest(): %s",msg));
			resp = Response.status(Status.NOT_FOUND).entity(GsonFactory.createJsonFromString("error", msg));
		}
		catch (RepositoryException ex)
		{
			String msg = String.format("could not create manifest for release family '%s'. Reason: %s", releaseFamily,
									ex.getMessage());
			logger.debug(String.format(".getManifest(): %s",msg));
			resp = Response.status(ex.getStatusCode()).entity(GsonFactory.createJsonFromString("error", msg));
		}
		return resp.build();
	}
	
	@GET
	@Path("/active-release/{releaseFamily}/{activeVersId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveRelease( @PathParam("activeVersId") String activeVersId, @PathParam("releaseFamily") String releaseFamily, @QueryParam("newVersion") String newVersion)
	{
		logger.debug(".getActiveRelease(): activeVersId = '%s', releaseFamily = '%s', newVersion = '%s'",
						activeVersId, releaseFamily, newVersion == null ? "<null>" : newVersion);
		ResponseBuilder resp;
		try
		{
			ActiveVersionEncoder encoder = new ActiveVersionEncoder(repoId, releaseFamily);
			// if newVersion is set, process setVersion
			if(newVersion != null)
			{
				this.manifestMgr.setActiveVersion(newVersion, activeVersId, encoder);
			}
			ManifestEntry entry = manifestMgr.getActiveVersion(activeVersId, encoder);
			// return manifest entry
			String json = GsonFactory.getGson().toJson(entry);
			logger.debug(".getManifest(): activeVersion = %s", json);
			resp = Response.ok().entity(json);
		}
		catch (RepositoryException ex)
		{
			String msg = String.format("could not process active version for activeVersionId '%s'. Reason: %s", activeVersId,
									ex.getMessage());
			logger.debug(String.format(".getActiveRelease(): %s",msg));
			resp = Response.status(ex.getStatusCode()).entity(GsonFactory.createJsonFromString("error", msg));
		}
		return resp.build();
	}
	

	
}
