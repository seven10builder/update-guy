package com.seven10.update_guy.client;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.client.FunctionalInterfaces.UpdateGuyClientFactory;
import com.seven10.update_guy.client.FunctionalInterfaces.JavaLauncherFactory;
import com.seven10.update_guy.client.FunctionalInterfaces.LocalCacheUtilsFactory;
import com.seven10.update_guy.client.FunctionalInterfaces.RequesterUtilsFactory;
import com.seven10.update_guy.client.cli.CliMgr;
import com.seven10.update_guy.client.exceptions.FatalClientException;
import com.seven10.update_guy.client.local.JavaLauncher;
import com.seven10.update_guy.client.local.LocalCacheUtils;
import com.seven10.update_guy.client.request.RequesterUtils;
import com.seven10.update_guy.common.manifest.ManifestEntry;
import com.seven10.update_guy.common.manifest.UpdateGuyRole.ClientRoleInfo;

public class UpdateGuyClient
{
	private static final Logger logger = LogManager.getFormatterLogger(UpdateGuyClient.class.getName());
	
	public static void main(String[] args)
	{
		// get cli options
		CliMgr mgr = new CliMgr(args, CliMgr::showHelp, CliMgr::processCfgFile);
		
		// create the dependency inject factories
		RequesterUtilsFactory requesterUtilsFactory = RequesterUtils::new;
		LocalCacheUtilsFactory localCacheUtilsFactory = LocalCacheUtils::new;
		JavaLauncherFactory launcherFactory = (settings)->new JavaLauncher();
		UpdateGuyClientFactory clientFactory = UpdateGuyClient::new;
		
		doClient(mgr, requesterUtilsFactory, localCacheUtilsFactory, launcherFactory, clientFactory);
	}

	/**
	 * @param cliMgr
	 */
	public static void doClient(CliMgr cliMgr, 
					RequesterUtilsFactory requesterUtilsFactory, 
					LocalCacheUtilsFactory localCacheUtilsFactory,
					JavaLauncherFactory javaLauncherFactory,
					UpdateGuyClientFactory clientFactory)
	{
		if(cliMgr == null)
		{
			throw new IllegalArgumentException("cliMgr must not be null");
		}
		if(requesterUtilsFactory == null)
		{
			throw new IllegalArgumentException("requesterUtilsFactory must not be null");
		}
		if(localCacheUtilsFactory == null)
		{
			throw new IllegalArgumentException("localCacheUtilsFactory must not be null");
		}
		if(javaLauncherFactory == null)
		{
			throw new IllegalArgumentException("javaLauncherFactory must not be null");
		}
		if(clientFactory == null)
		{
			throw new IllegalArgumentException("clientFactory must not be null");
		}
		if(cliMgr.parse(CliMgr.getParser()))
		{
			ClientSettings settings = cliMgr.getClientSettings();
			
			UpdateGuyClient client =  clientFactory.build(cliMgr.getRemainingParams());
			RequesterUtils requesterUtils = requesterUtilsFactory.build(settings);
			LocalCacheUtils localCacheUtils = localCacheUtilsFactory.build(settings);
			JavaLauncher javaLauncher = javaLauncherFactory.build(settings);
			
			try
			{
				boolean keepGoing;
				do
				{
					
					keepGoing = client.executeClientLoop(requesterUtils,
														localCacheUtils, 
														javaLauncher);
				}while(keepGoing);
			}
			catch(FatalClientException ex)
			{
				logger.fatal("%s encountered a fatal error: %s", CliMgr.executableName, ex.getMessage());
			}
		}
	}
	
	public final List<String> remainingParams;
	
	public UpdateGuyClient(String[] remainingParams)
	{
		if(remainingParams == null)
		{
			throw new IllegalArgumentException("remainingParams must not be null");
		}
		this.remainingParams = Arrays.asList(remainingParams);
		logger.debug(".ctor(): remainingParams = %s", String.join(remainingParams.toString()));
	}
	
	public boolean executeClientLoop(RequesterUtils requestUtils, LocalCacheUtils localCacheUtils, JavaLauncher launcher) throws FatalClientException
	{
		if(requestUtils == null)
		{
			throw new IllegalArgumentException("requestUtils must not be null");
		}
		if(localCacheUtils == null)
		{
			throw new IllegalArgumentException("localCacheUtils must not be null");
		}
		if(launcher == null)
		{
			throw new IllegalArgumentException("launcher must not be null");
		}
		// get current active releaseId from server
		ManifestEntry release = requestUtils.requestActiveRelease(RequesterUtils::getDefaultRequester);
		Path jarFilePath = localCacheUtils.buildTargetPath(release);
		
		// request checksum for activeRelease->role->file
		ClientRoleInfo remoteRoleInfo = requestUtils.requestRemoteClientRoleInfo(release, RequesterUtils::getDefaultRequester);
		logger.debug(".executeClientLoop(): remoteChecksum = '%s', role cli = '%s'", 
				remoteRoleInfo.fingerPrint, String.join(", ", remoteRoleInfo.commandLine));
		
		// get checksum for role->localFile
		String localChecksum = localCacheUtils.getLocalChecksum(jarFilePath);
		logger.debug(".executeClientLoop(): localChecksum = '%s'", localChecksum);
		
		// if checksums don't match, download role file
		if(localChecksum.equals(remoteRoleInfo.fingerPrint) == false)
		{
			logger.info(".executeClientLoop(): checksums did not match. Will download", localChecksum);
			requestUtils.requestDownloadRoleFile(release, jarFilePath, RequesterUtils::getDefaultRequester);
		}
		else
		{
			logger.info(".executeClientLoop(): checksums matched, no need to download", localChecksum);
		}
		
		//	launch role file with remaining cli options
		List<String> paramsList = launcher.buildParamList(remainingParams, jarFilePath, remoteRoleInfo.commandLine);
		ProcessBuilder processBuilder = launcher.createProcessBuilder(paramsList, jarFilePath.getParent());
		return launcher.launchExecutable(processBuilder);
	}
}
