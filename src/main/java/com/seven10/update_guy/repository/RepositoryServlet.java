package com.seven10.update_guy.repository;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.seven10.update_guy.exceptions.RepositoryException;


@Path("repository")
public class RepositoryServlet
{
	final private RepositoryInfoMgr repoInfoMgr;
	
	public RepositoryServlet()
	{
		repoInfoMgr = new RepositoryInfoMgr("repos.dat");
	}

	@GET
	@Path("manifest")
	@Produces(MediaType.APPLICATION_JSON)
	public Manifest getManifest(@QueryParam("releaseFamily") String releaseFamily)
	{
		// open repository connection
		// download manifest file
		// render as manifest object
		// return manifest object
		return new Manifest("");
	}
	
	@GET
	@Path("setActiveVersion")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setActiveVersion(@QueryParam("targetVersion") String targetVersion)
	{
		// get manifest object
		// get VersionEntry from manifest
		// download to cache each file
		// return result
		return Response.ok().build();
	}
	
	@GET
	@Path("showRepos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response showRepos()
	{
		Map<Integer, RepositoryInfo> repoMap = repoInfoMgr.getRepoMap();
		//repoMap.forEach((repoId,repoInfo)->resp);
		
		//return Response.ok(json, MediaType.APPLICATION_JSON).build();
		return Response.ok().build();
		
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
