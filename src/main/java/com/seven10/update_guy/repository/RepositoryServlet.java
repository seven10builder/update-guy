package com.seven10.update_guy.repository;

import java.nio.file.FileSystems;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.seven10.update_guy.Globals;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;


@Path("/repository")
public class RepositoryServlet
{
	private static final Logger logger = LogManager.getFormatterLogger(RepositoryServlet.class);
	final private RepositoryInfoMgr repoInfoMgr;
	

	public RepositoryServlet() throws RepositoryException
	{
	
		// blech conflicting type names
		java.nio.file.Path repoFilePath = getRepoInfoPath();
		this.repoInfoMgr = new RepositoryInfoMgr(repoFilePath);
	}
	
	public java.nio.file.Path getRepoInfoPath()
	{
		String repoFileName = System.getProperty(Globals.SETTING_REPO_FILENAME, Globals.DEFAULT_REPO_FILENAME);
		logger.debug(".getRepoInfoPath(): Using '%s' for repo info file name", repoFileName);
		
		java.nio.file.Path localRootPath = FileSystems.getDefault().getPath(System.getProperty(Globals.SETTING_LOCAL_PATH, Globals.DEFAULT_LOCAL_PATH));
		logger.debug(".getRepoInfoPath(): Using path '%s' for local root", localRootPath.toString());
		java.nio.file.Path repoPath = localRootPath.resolve(repoFileName);
		logger.debug(".getRepoInfoPath(): Using path '%s' for repo file info", repoPath.toString());
		return repoPath;
	}
	
	@GET
	@Path("/show")
	public Response showAllRepos()
	{
		Map<String, RepositoryInfo> repoMap = repoInfoMgr.getRepoMap();
		Gson gson = GsonFactory.getGson();
		String repoMapJson = gson.toJson(repoMap);
		ResponseBuilder resp = Response.ok().entity(repoMapJson);
		return resp.build();
	}
	@GET
	@Path("/show/{repoId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response showRepo(@PathParam("repoId") String repoId)
	{
		Map<String, RepositoryInfo> repoMap = repoInfoMgr.getRepoMap();
		RepositoryInfo repoInfo = repoMap.get(repoId);
		
		ResponseBuilder resp = null;
		if(repoInfo == null)
		{
			String msg = String.format("Could not find repo identified by '%s'", repoId.toString());
			resp = Response.status(Status.NOT_FOUND)
					.entity( GsonFactory.createJsonFromString("error", msg));
		}
		else
		{
			Gson gson = GsonFactory.getGson();
			String repoMapJson = gson.toJson(repoInfo);
			resp = Response.ok().entity(repoMapJson);	
		}
		return resp.build();
	}
	
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createRepository(final RepositoryInfo repoInfo)
	{
		ResponseBuilder resp;
		try
		{
			repoInfoMgr.addRepository(repoInfo);
			resp = Response.ok();
		}
		catch(RepositoryException ex)
		{
			resp = Response.status(ex.getStatusCode()).entity(String.format("{\"error\": \"%s\"", ex.getMessage()));
		}
		return resp.build();
	}
	
	@DELETE
	@Path("/delete/{repositoryId}")
	public Response deleteRepository(@PathParam("repositoryId") String repositoryId)
	{
		ResponseBuilder resp;
		try
		{
			repoInfoMgr.deleteRepository(repositoryId);
			resp = Response.ok();
		}
		catch(RepositoryException ex)
		{
			resp = Response.status(ex.getStatusCode()).entity(ex.getMessage());
		}
		return resp.build();

	}

	
}
