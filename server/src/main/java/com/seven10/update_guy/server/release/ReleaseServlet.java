package com.seven10.update_guy.server.release;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.common.release_family.UpdateGuyRole;
import com.seven10.update_guy.common.release_family.UpdateGuyRole.ClientRoleInfo;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.exceptions.RepositoryException;
import com.seven10.update_guy.server.release_family.ReleaseFamilyRefresher;
import com.seven10.update_guy.server.release_family.ReleaseFamilyServlet;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryServlet;


@Path("/release/{repoId}/{releaseFamily}")
public class ReleaseServlet
{
	private static final Logger logger = LogManager.getFormatterLogger(ReleaseServlet.class);
	private final ReleaseMgr releaseMgr;

	private static class CacheFileRunnable implements Runnable
	{

		private final ReleaseMgr releaseMgr;
		private final String version;
		private final AsyncResponse asyncResponse;
		
		
		public CacheFileRunnable(final String version, final ReleaseMgr releaseMgr, final AsyncResponse asyncResponse)
		{
			this.version = version;
			this.releaseMgr = releaseMgr;
			this.asyncResponse = asyncResponse;
		}
		@Override
		public void run()
		{
	    	try
	    	{
	    		Runnable onDownloadComplete = ()->asyncResponse.resume(Response.ok().build());
	    		releaseMgr.cacheFiles(version, ReleaseServlet::onFileComplete, onDownloadComplete);
	    	}
	    	catch(RepositoryException ex)
	    	{
	    		asyncResponse.resume(ex);
	    		return;
	    	}
		}
	}
	
	private static void onFileComplete(java.nio.file.Path fileName)
	{
		logger.info("file '%s' was successfully cached at '%s'", fileName.getFileName(), fileName.getParent());
	}

	public ReleaseServlet(@PathParam("repoId") String repoId, @PathParam("releaseFamily") String releaseFamilyName) throws RepositoryException
	{
		RepositoryInfo repoInfo = RepositoryServlet.getRepoInfoById(repoId);
		ReleaseFamily releaseFamily = ReleaseFamilyServlet.getReleaseFamilyById(releaseFamilyName, repoId, new ReleaseFamilyRefresher(repoId, ServerGlobals.getReleaseFamilyStorePath(repoId)));
		this.releaseMgr = new ReleaseMgr(releaseFamily, repoInfo);
	}

	/**
	 * Gets the list of roles that the active version entry provides files for
	 * 
	 * @return
	 * @throws RepositoryException 
	 */
	@GET
	@Path("/roles")
	public Response getRoles(@QueryParam("version") String version) throws RepositoryException
	{
		ResponseBuilder resp = null;
		List<String> roles = releaseMgr.getAllRoles(version);
		
		String json = GsonFactory.getGson().toJson(roles);
		resp = Response.ok().entity(json);
		
		return resp.build();
	}
	
	@GET
	@Path("/roleInfo/{roleName}")
	public Response getRoleInfo(@PathParam("roleName") String roleName, @QueryParam("version") String version)
	{
		ResponseBuilder resp = null;
		try
		{
			ClientRoleInfo roleInfo = releaseMgr.getRoleInfoForRole(version, roleName).toClientRoleInfo();
			String json = GsonFactory.getGson().toJson(roleInfo);
			logger.info(".getRoleInfo(): returning roleInfo as a json obect - %s", json);
			resp = Response.ok().entity(json);
		}
		catch (RepositoryException ex)
		{
			resp = Response.status(ex.getStatusCode()).entity(ex);
		}
		return resp.build();
	}
	
	
	@GET
	@Path("/download/{roleName}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam("roleName") String roleName, @QueryParam("version") String version)
	{
		ResponseBuilder resp = null;
		try
		{
			UpdateGuyRole roleInfo = releaseMgr.getRoleInfoForRole(version, roleName);
			
			resp = Response.ok(roleInfo.getFilePath().toFile(), MediaType.APPLICATION_OCTET_STREAM)
		      .header("Content-Disposition", "attachment; filename=\"" + roleInfo.getFilePath().getFileName() + "\"" );
		}
		catch (RepositoryException ex)
		{
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex);
		}
		return resp.build();
	}
	
	@GET
	@Path("/update-cache")
	public Response doUpdateCache(final @Suspended AsyncResponse response, @QueryParam("version") String version) throws RepositoryException
	{
		new Thread(new CacheFileRunnable(version, releaseMgr, response)).run();
		ResponseBuilder	resp = Response.ok();
		return resp.build();
	}
}
