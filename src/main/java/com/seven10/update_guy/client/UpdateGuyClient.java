package com.seven10.update_guy.client;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.client.cli.CliMgr;
import com.seven10.update_guy.client.exceptions.FatalClientException;
import com.seven10.update_guy.client.local.JavaLauncher;
import com.seven10.update_guy.client.local.LocalCacheUtils;
import com.seven10.update_guy.client.request.RequesterUtils;
import com.seven10.update_guy.common.manifest.ManifestEntry;

public class UpdateGuyClient
{
	private static final Logger logger = LogManager.getFormatterLogger(UpdateGuyClient.class.getName());
	
	public static void main(String[] args)
	{
		// get cli options
		CliMgr mgr = new CliMgr(args, CliMgr::showHelp, CliMgr::processCfgFile);
		
		if(mgr.parse(CliMgr.getParser()))
		{
			ClientSettings settings = mgr.getClientSettings();
			
			final RequesterUtils requesterUtils = new RequesterUtils(settings);
			final LocalCacheUtils localCacheUtils = new LocalCacheUtils(settings);
			final JavaLauncher javaLauncher = new JavaLauncher();
			UpdateGuyClient client = new UpdateGuyClient(mgr.getRemainingParams());
			try
			{
				while(true)
				{
					client.executeClientLoop(requesterUtils, localCacheUtils, javaLauncher);
				}
			}
			catch(FatalClientException ex)
			{
				logger.fatal("%s encountered a fatal error: %s", CliMgr.executableName, ex.getMessage());
			}
		}
	}
	
	private final List<String> remainingParams;
	
	public UpdateGuyClient(String[] remainingParams)
	{
		this.remainingParams = Arrays.asList(remainingParams);
		logger.debug(".ctor(): remainingParams = %s", String.join(remainingParams.toString()));
	}
	
	public void executeClientLoop(RequesterUtils requestUtils, LocalCacheUtils localCacheUtils, JavaLauncher launcher) throws FatalClientException
	{
		// get current active releaseId from server
		ManifestEntry release = requestUtils.requestActiveRelease(RequesterUtils::getDefaultRequester);
		Path jarFilePath = localCacheUtils.buildTargetPath(release);
		
		// request checksum for activeRelease->role->file
		String remoteChecksum = requestUtils.requestRemoteChecksum(release, RequesterUtils::getDefaultRequester);
		logger.debug(".executeClientLoop(): remoteChecksum = '%s'", remoteChecksum);
		
		// get checksum for role->localFile
		String localChecksum = localCacheUtils.getLocalChecksum(jarFilePath);
		logger.debug(".executeClientLoop(): localChecksum = '%s'", localChecksum);
		
		// if checksums don't match, download role file
		if(localChecksum.equals(remoteChecksum) == false)
		{
			logger.info(".executeClientLoop(): checksums did not match. Will download", localChecksum);
			requestUtils.requestDownloadRoleFile(release, jarFilePath, RequesterUtils::getDefaultRequester);
		}
		else
		{
			logger.info(".executeClientLoop(): checksums matched, no need to download", localChecksum);
		}
		
		//	launch role file with remaining cli options
		ProcessBuilder processBuilder = launcher.createProcessBuilder(
											launcher.buildParamList(remainingParams, jarFilePath),
											jarFilePath.getParent());
		launcher.launchExecutable(processBuilder);
	}
}
