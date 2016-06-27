package com.seven10.update_guy.client.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.client.ClientSettings;
import com.seven10.update_guy.exceptions.ClientParameterException;

import org.apache.logging.log4j.LogManager;

public class CliMgr
{
	static final Logger logger = LogManager.getFormatterLogger(CliMgr.class.getName());
	
	public static final String executableName = "ug-client";
	/**
	 * Show some help
	 */
	
	public static final String helpCmd = "h";
	public static final String helpCmdLong = "help";
	public static final String helpDesc = "Show help";

	public static final String serverAddressCmd = "a";
	public static final String serverAddressCmdLong = "address";
	private static final String serverAddressDesc = "The address of the update-guy server";

	private static final String serverPortCmd = "p";
	private static final String serverPortCmdLong = "port";
	private static final String serverPortDesc = "The listening port of the update-guy server";

	private static final String repositoryIdCmd = "i";
	private static final String repositoryIdCmdLong = "repoid";
	private static final String repositoryIdDesc = "The id of the target repository";

	private static final String releaseFamilyCmd = "f";
	private static final String releaseFamilyCmdLong = "relfam";
	private static final String releaseFamilyDesc = "The release family to target";

	private static final String roleNameCmd = "r";
	private static final String roleNameCmdLong = "role";
	private static final String roleNameDesc = "The role name that should be launched";
	

	private static final String filePathCmd = "c";
	private static final String filePathCmdLong = "cache";
	private static final String filePathDesc = "The location to store the executable for this role";
	
	
	/**
	 * Options passed in and hydra settings resulting of options parse
	 */
	private Options options = new Options();
	
	/**
	 * Metadata
	 */
	private boolean isContinueExec = true;
	private ClientSettings clientSettings;
	
	
	
	public CliMgr(String[] args)
	{
		// help
		options.addOption(helpCmd, helpCmdLong, false, helpDesc);
		// update-guy server address
		options.addOption(serverAddressCmd, serverAddressCmdLong, true, serverAddressDesc);
		// update-guy server port
		options.addOption(serverPortCmd, serverPortCmdLong, true, serverPortDesc);
		// repositoryID
		options.addOption(repositoryIdCmd, repositoryIdCmdLong, true, repositoryIdDesc);
		// releaseFamilyName
		options.addOption(releaseFamilyCmd, releaseFamilyCmdLong, true, releaseFamilyDesc);
		// roleName
		options.addOption(roleNameCmd, roleNameCmdLong, true, roleNameDesc);
		// output
		options.addOption(filePathCmd, filePathCmdLong, true, filePathDesc);
		parse(args);
	}

	private void help()
	{
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp(executableName, options);
		stopExecution();
	}
	
	public void parse(String[] args)
	{
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try
		{			
			logger.debug(".parse(): params = %s", Arrays.toString(args));
			cmd = parser.parse(options, args);

			if (cmd.getOptions().length == 3)
			{
				throw new ParseException("Invalid argument list supplied");
			}
			
			if (cmd.hasOption(helpCmd))
			{
				help();
			}
			CliParamProcessor clip = new CliParamProcessor(cmd);
			// process address (not required)
			clip.processParam(serverAddressCmd,
							ParamValidator::validateAddress,
							clientSettings::setServerAddress,
							CliParamProcessor.reportUsingDefault("server address", clientSettings::getServerAddress));
			// process port (not required)
			clip.processParam(serverPortCmd, 
							ParamValidator::validatePort,
							clientSettings::setServerPort,
							CliParamProcessor.reportUsingDefault("port", clientSettings::getServerPort));
			// cache path (not required)
			clip.processParam(filePathCmd, 
							ParamValidator::validateCachePath,
							clientSettings::setCachePath,
							CliParamProcessor.reportUsingDefault("cache path", clientSettings::getCachePath));
			// process repoId
			clip.processParam(repositoryIdCmd,
							ParamValidator::validateRepositoryId,
							clientSettings::setRepoId,
							CliParamProcessor.reportNoDefault("repoId"));
			// process releaseFamily
			clip.processParam(releaseFamilyCmd,
							ParamValidator::validateReleaseFamily, 
							clientSettings::setReleaseFamily, 
							CliParamProcessor.reportNoDefault("releaseFamily")); 
			// process roleName
			clip.processParam(roleNameCmd,
							ParamValidator::validateRoleName,
							clientSettings::setRoleName,
							CliParamProcessor.reportNoDefault("roleName"));

			logger.trace(".parse(): All update-guy parameters parsed.");
		}
		catch (ParseException e)
		{
			logger.error(".parse(): Failed to parse comand line properties", e);
			help();
			stopExecution();
		}
		catch(ClientParameterException e)
		{
			logger.error(".parse(): command line error. Message: '%s'", e.getMessage());
			stopExecution();
		}
		catch(Exception e)
		{
			logger.error(".parse(): failed. Reason: %s", e.getMessage());
			stopExecution();
		}
	}
	
	public boolean getIsExecContinued()
	{
		return isContinueExec;
	}

	private void stopExecution()
	{
		logger.trace(".stopExecution(): stopping execution");
		isContinueExec = false;
	}
	
	public ClientSettings getClientSettings()
	{
		return clientSettings;
	}

	public String[] getRemainingParams()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
