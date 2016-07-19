package com.seven10.update_guy.client.request;

import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.client.ClientSettings;
import com.seven10.update_guy.client.FunctionalInterfaces.RequesterFactory;
import com.seven10.update_guy.client.exceptions.FatalClientException;
import com.seven10.update_guy.common.manifest.ManifestEntry;
import com.seven10.update_guy.common.manifest.UpdateGuyRole.ClientRoleInfo;

public class RequesterUtils
{
	private static final Logger logger = LogManager.getFormatterLogger(RequesterUtils.class.getName());
	
	public static Requester getDefaultRequester(String url, String methodName)
	{
		return new Requester(url, methodName);
	}
	
	public static String buildMethodName(String roleName)
	{
		String methodName = String.format("/download/%s", roleName);
		return methodName;
	}
	
	private final ClientSettings settings;
	public RequesterUtils(ClientSettings settings)
	{
		if(settings == null)
		{
			throw new IllegalArgumentException("settings must not be null");
		}
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
	
	public ManifestEntry requestActiveRelease(RequesterFactory requesterFactory) throws FatalClientException
	{
		if(requesterFactory == null)
		{
			throw new IllegalArgumentException("requesterFactory must not be null");
		}
		String methodName = String.format("active-release/%s/%s", settings.releaseFamily, settings.activeVersionId);
		String url = buildManifestReq();		
		Requester requester = requesterFactory.getRequester(url, methodName);
		
		ManifestEntry activeVersion = requester.get(
				requester::buildRequest,
				new ResponseEvaluator<ManifestEntry>(ManifestEntry.class));
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
	
	public void requestDownloadRoleFile(ManifestEntry release, Path jarFilePath, RequesterFactory requesterFactory) throws FatalClientException
	{
		if(release == null)
		{
			throw new IllegalArgumentException("release must not be null");
		}
		if(jarFilePath == null)
		{
			throw new IllegalArgumentException("jarFilePath must not be null");
		}
		if(requesterFactory == null)
		{
			throw new IllegalArgumentException("requesterFactory must not be null");
		}
		String methodName = String.format("/download/%s", settings.roleName);
		String url = buildReleaseReq();
				
		Requester requester = requesterFactory.getRequester(url, methodName);
		requester.addQueryParam("version", release.getVersion());
		requester.getFile(
				jarFilePath, 
				requester::buildRequest, 
				new ResponseEvaluator<ClientRoleInfo>(ClientRoleInfo.class),  Requester::copyFileFromResponse);
	}
	
	public ClientRoleInfo requestRemoteClientRoleInfo(ManifestEntry release, RequesterFactory requesterFactory) throws FatalClientException
	{
		if(release == null)
		{
			throw new IllegalArgumentException("release must not be null");
		}
		if(requesterFactory == null)
		{
			throw new IllegalArgumentException("requesterFactory must not be null");
		}
		String methodName = String.format("/roleInfo/%s", settings.roleName);
		String url = buildReleaseReq();
				
		Requester requester = requesterFactory.getRequester(url, methodName);
		requester.addQueryParam("version", release.getVersion());
		ClientRoleInfo clientRoleInfo = requester.get(requester::buildRequest,
					new ResponseEvaluator<ClientRoleInfo>(ClientRoleInfo.class));
		return clientRoleInfo;
	}
}
