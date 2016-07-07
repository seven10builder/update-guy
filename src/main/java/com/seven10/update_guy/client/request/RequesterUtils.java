package com.seven10.update_guy.client.request;

import java.nio.file.Path;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.client.ClientSettings;
import com.seven10.update_guy.client.exceptions.FatalClientException;
import com.seven10.update_guy.common.manifest.ManifestEntry;

public class RequesterUtils
{
	public static final Logger logger = LogManager.getFormatterLogger(RequesterUtils.class.getName());
	
	private final ClientSettings settings;
	public RequesterUtils(ClientSettings settings)
	{
		this.settings = settings;
	}
	
	public String buildPrefix()
	{
		String prefix = String.format("%s://%s:%d",
				Requester.defaultProtocol, 
				settings.serverAddress,
				settings.serverPort);
		logger.debug(".buildPrefix(): request url prefix = '%s'", prefix);
		return prefix;
	}

	public String buildManifestReq()
	{
		String url = String.format("%s/manifest/%s", buildPrefix(),	settings.repoId);
		logger.debug(".buildManifestReq(): request url = '%s'", url);
		
		return url;
	}
	
	public ManifestEntry requestActiveRelease() throws FatalClientException
	{
		String methodName = String.format("active-release/%s/%s", settings.releaseFamily, settings.activeVersionId);
		String url = buildManifestReq();		
		Requester requester = new Requester(url, methodName);
		
		ManifestEntry activeVersion = requester.get(ManifestEntry.class);
		logger.debug(".requestActiveRelease(): activeVersion = '%s'", activeVersion.getVersion());
		return activeVersion;
	}
	
	public String buildReleaseReq()
	{
		String url = String.format("%s/release/%s/%s",
								buildPrefix(),
								settings.repoId,
								settings.releaseFamily);
		logger.debug(".buildReleaseReq(): request url = '%s'", url);
		return url;
	}
	
	public void requestDownloadRoleFile(ManifestEntry release, Path jarFilePath) throws FatalClientException
	{
		String methodName = String.format("/download/%s", settings.roleName);
		String url = buildReleaseReq();
				
		Requester requester = new Requester(url, methodName);
		requester.addQueryParam("version", release.getVersion());
		requester.getFile(jarFilePath);
	}
	
	public String requestRemoteChecksum(ManifestEntry release) throws FatalClientException
	{
		String methodName = String.format("/fingerprint/%s", settings.roleName);
		String url = buildReleaseReq();
				
		Requester requester = new Requester(url, methodName);
		requester.addQueryParam("version", release.getVersion());
		String fingerPrint = requester.get(String.class);
		return fingerPrint;
	}
	
	public static <T> T  evaluateResponse(Response response, Status statusCode, Class<T> entityType)
			throws FatalClientException
	{
		T rval;
		logger.info(".evaluateResponse(): processing response. statusCode = %s", statusCode.name());
		switch(statusCode)
		{
		case OK:
			rval = response.readEntity(entityType);
			logger.info(".evaluateResponse(): response entity (type '%s') = '%s'", entityType.getName(), rval.toString());
			break;
		case INTERNAL_SERVER_ERROR:
			throw new FatalClientException("There was an internal problem with the update-guy. Cannot connect.");
		case NOT_FOUND:
			throw new FatalClientException("The update-guy cannot find the requested resource");
		default:
			throw new FatalClientException("The update-guy responded with an unexpected response: %s", statusCode.toString());
		}
		return rval;
	}


	

}
