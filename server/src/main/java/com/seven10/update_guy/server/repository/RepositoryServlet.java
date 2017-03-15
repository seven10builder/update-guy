package com.seven10.update_guy.server.repository;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
import org.glassfish.jersey.server.mvc.Viewable;

import com.google.gson.Gson;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.exceptions.RepositoryException;


@Path("/repository")
public class RepositoryServlet
{
	private static final Logger logger = LogManager.getFormatterLogger(RepositoryServlet.class);
	final private RepositoryInfoMgr repoInfoMgr;
	
	private static boolean compareHashForRepoInfo(RepositoryInfo repoInfo, String target)
	{
		boolean rval = false;
		try
		{
			rval = (repoInfo.getShaHash().equals(target));
		}
		catch(RepositoryException ex)
		{
			logger.error(".compareHashForRepoInfo(): Could not test hash for repoInfo '%s'. Skipping. ", repoInfo.repoAddress);
		}
		return rval;
	}
	
	public static RepositoryInfo getRepoInfoById(String repoId) throws RepositoryException
	{
		java.nio.file.Path repoFile = ServerGlobals.getRepoFile();
		List<RepositoryInfo> repoList = RepositoryInfoMgr.loadRepos(repoFile);
		Supplier<? extends RepositoryException> exceptionSupplier = 
				()->new RepositoryException(Status.NOT_FOUND, "Could not find repository with ID '%s'", repoId);
		RepositoryInfo repoInfo = repoList.stream()
									.filter(ri->compareHashForRepoInfo(ri,repoId))
									.findFirst()
									.orElseThrow(exceptionSupplier);
		return repoInfo;
	}

	public RepositoryServlet() throws RepositoryException
	{
		logger.trace(".ctor(): creating repository servlet");
		// blech conflicting type names
		java.nio.file.Path repoFilePath = ServerGlobals.getRepoFile();
		logger.info(".ctor(): repo config file path = %s", repoFilePath.toString());
		this.repoInfoMgr = new RepositoryInfoMgr(repoFilePath);

		logger.trace(".ctor(): created repository servlet");
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
		logger.trace(".showRepo(): start showRepo, repoId = '%s'", repoId);
		ResponseBuilder resp = null;
		Map<String, RepositoryInfo> repoMap = repoInfoMgr.getRepoMap();
		
		RepositoryInfo repoInfo = repoMap.get(repoId);
		if(repoInfo == null)
		{
			String msg = String.format("could not find repo identified by '%s'", repoId.toString());
			logger.error(".showRepo(): %s", msg);			
			resp = Response.status(Status.NOT_FOUND)
					.entity( GsonFactory.createJsonFromString("error", msg));
		}
		else
		{
			logger.info(".showRepo(): found repo identified by '%s'", repoId);
			
		    Map<String, String> model = new HashMap<>();
		    repoInfo.setToMap(model);		    
			resp = Response.ok().entity(new Viewable("repoInfo-show.jsp", model));	
		}
		return resp.build();
	}
	
	@GET
	@Path("/create")
	@Produces(MediaType.TEXT_HTML)
	public Response showCreatePage()
	{
		logger.trace(".showRepo(): start createRepo");
		ResponseBuilder resp = null;
		URL resource = getClass().getResource("/repoInfo-create.jsp");
		String jsp = resource.getFile();
		resp = Response.ok().entity(new Viewable(jsp));	
		
		return resp.build();
	}
	
	
	@POST
	@Path("/create-do")
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
