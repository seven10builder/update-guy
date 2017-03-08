package com.seven10.update_guy.server.release_family;

import java.util.List;
import java.util.NoSuchElementException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import com.seven10.update_guy.common.Globals;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.exceptions.RepositoryException;

@Path("/release-family/{repoId}")
public class ReleaseFamilyServlet
{
	private static final Logger logger = LogManager.getFormatterLogger(ReleaseFamilyServlet.class);
	ReleaseFamilyMgr releaseFamilyMgr;
	String repoId;

	public static ReleaseFamily getReleaseFamilyById(String releaseFamily, String repoId, ReleaseFamilyRefresher releaseFamilyRefresher) throws RepositoryException
	{
		java.nio.file.Path releaseFamilyPath = ServerGlobals.getReleaseFamilyStorePath(repoId)
				.resolve(Globals.buildRelFamFileName(releaseFamily));
		try
		{
			releaseFamilyRefresher.refreshLocalReleaseFamilyFiles(releaseFamily);
			return ReleaseFamily.loadFromFile(releaseFamilyPath);
		}
		catch (UpdateGuyException ex)
		{
			logger.error(".getReleaseFamilyById(): could not load release family for id '%s'. Reason: %s", releaseFamilyPath, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not load release family for id '%s'", releaseFamilyPath);
		}
	}

	public ReleaseFamilyServlet(@PathParam("repoId") String repoId)
	{
		this.repoId = repoId;
		java.nio.file.Path releaseFamilyPath = ServerGlobals.getReleaseFamilyStorePath(repoId);
		releaseFamilyMgr = new ReleaseFamilyMgr(releaseFamilyPath, repoId);
	}
	
	@GET
	@Path("/show")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReleaseFamilyAll()
	{
		ResponseBuilder resp = null;
		try
		{
			List<ReleaseFamily> releaseFamilys = releaseFamilyMgr.getReleaseFamilies();
			String json = GsonFactory.getGson().toJson(releaseFamilys);
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
	public Response getReleaseFamily(@PathParam("releaseFamily") String releaseFamilyName)
	{
		logger.debug(".getReleaseFamily(): releaseFamily = %s", releaseFamilyName);
		ResponseBuilder resp;
		try
		{
			ReleaseFamily releaseFamily = releaseFamilyMgr.getReleaseFamily(releaseFamilyName);
			String json = GsonFactory.getGson().toJson(releaseFamily);
			logger.debug(".getReleaseFamily(): release family = %s", json);
			resp = Response.ok().entity(json);
		}
		catch (NoSuchElementException ex)
		{
			String msg = String.format("could not find release family for release family '%s'. Reason: %s", releaseFamilyName,
										ex.getMessage());
			logger.debug(String.format(".getReleaseFamily(): %s",msg));
			resp = Response.status(Status.NOT_FOUND).entity(GsonFactory.createJsonFromString("error", msg));
		}
		catch (RepositoryException ex)
		{
			String msg = String.format("could not create release family for release family '%s'. Reason: %s", releaseFamilyName,
									ex.getMessage());
			logger.debug(String.format(".getReleaseFamily(): %s",msg));
			resp = Response.status(ex.getStatusCode()).entity(GsonFactory.createJsonFromString("error", msg));
		}
		return resp.build();
	}
	
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createReleaseFamily(final ReleaseFamily releaseFamily)
	{
		ResponseBuilder resp;
		try
		{
			releaseFamilyMgr.addReleaseFamily(releaseFamily);
			resp = Response.ok();
		}
		catch(RepositoryException ex)
		{
			resp = Response.status(ex.getStatusCode()).entity(String.format("{\"error\": \"%s\"", ex.getMessage()));
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
				this.releaseFamilyMgr.setActiveVersion(newVersion, activeVersId, encoder);
			}
			ReleaseFamilyEntry entry = releaseFamilyMgr.getActiveVersion(activeVersId, encoder);
			// return release family entry
			String json = GsonFactory.getGson().toJson(entry);
			logger.debug(".getReleaseFamily(): activeVersion = %s", json);
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
