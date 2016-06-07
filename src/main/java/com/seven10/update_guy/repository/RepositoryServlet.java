package com.seven10.update_guy.repository;

import java.nio.file.Paths;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.repository.connection.RepoConnection;
import com.seven10.update_guy.repository.connection.RepoConnectionFactory;


@Path("repository")
public class RepositoryServlet
{
	final private RepositoryInfoMgr repoInfoMgr;
	private String activeRepo;
	
	public RepositoryServlet() throws RepositoryException
	{
		repoInfoMgr = new RepositoryInfoMgr(Paths.get("repos.dat"));
	}
	private final RepositoryInfo getActiveRepo()
	{
		return repoInfoMgr.getRepoMap().get(activeRepo);
	}
	@GET
	@Path("manifest")
	@Produces(MediaType.APPLICATION_JSON)
	public Manifest getManifest(@QueryParam("releaseFamily") String releaseFamily)
	{
		// open repository connection
		Manifest manifest; 
		try
		{
			RepoConnection repoConnection = RepoConnectionFactory.connect(getActiveRepo());
			// download manifest file
			manifest = repoConnection.getManifest(releaseFamily);
		}
		catch (RepositoryException e)
		{
			manifest = null;
		}
		
		// return manifest object
		return manifest;
	}
	
	@GET
	@Path("showRepos")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<Integer, RepositoryInfo> showRepos()
	{
		Map<Integer, RepositoryInfo> repoMap = repoInfoMgr.getRepoMap();
		return repoMap;
		
	}
	
	
	@POST
	@Path("createRepository")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
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
			resp = Response.notModified(ex.getMessage());
		}
		return resp.build();
	}
	
	@DELETE
	@Path("deleteRepository")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteRepository(@PathParam("repositoryId") int repositoryId)
	{
		ResponseBuilder resp;
		try
		{
			repoInfoMgr.deleteRepository(repositoryId);
			resp = Response.ok();
		}
		catch(RepositoryException ex)
		{
			resp = Response.notModified();
		}
		return resp.build();

	}
}
