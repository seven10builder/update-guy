package com.seven10.update_guy.client.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.client.ClientSettings;
import com.seven10.update_guy.client.FunctionalInterfaces;
import com.seven10.update_guy.client.exceptions.ClientParameterException;

import org.apache.logging.log4j.LogManager;

public class CliMgr
{
	static final Logger logger = LogManager.getFormatterLogger(CliMgr.class.getName());
	
	private static Options buildOptions()
	{
		Options options = new Options();
		// help
		options.addOption(helpCmd, helpCmdLong, false, helpDesc);
		// config File
		options.addOption(configFileCmd, configFileCmdLong, true, configFileDesc);
		return options;
	}
	
	public static final String executableName = "ug-client";
	
	public static final String helpCmd = "h";
	public static final String helpCmdLong = "help";
	public static final String helpDesc = "Show help";
	
	public static final String configFileCmd = "f";
	public static final String configFileCmdLong = "file";
	public static final String configFileDesc = "The settings file to use";

	
	private ClientSettings clientSettings;
	public final List<String> cmdLine;
	public final List<String> remainingOptions;
	private final FunctionalInterfaces.OnShowHelp onShowHelp;
	private final FunctionalInterfaces.OnConfigFileCmd onConfigFile;

	public static CommandLineParser getParser()
	{
		return new DefaultParser();
	}
	
	public static void showHelp(Options options)
	{
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp(executableName, options);
	}
	
	public static ClientSettings processCfgFile(String path) throws ClientParameterException
	{
	
			if(StringUtils.isBlank(path))
			{
				throw new ClientParameterException("roleName must not be blank");
			}
			
			Path filePath = Paths.get(path);
			ClientSettings clientSettings = ClientSettings.loadConfig(filePath);
			logger.info(".processCfgFile(): read client settings - %s", clientSettings);
			return clientSettings;
	}
	
	/**
	 * @param cmd
	 * @throws ClientParameterException
	 */
	private void checkConfigFileCmd(CommandLine cmd) throws ClientParameterException
	{
		if (cmd.hasOption(configFileCmd))
		{
			String path = cmd.getOptionValue(configFileCmd);
			logger.info(".checkConfigFileCmd(): config file option found. Path = %s", path);
			clientSettings = onConfigFile.doCommand(path);
		}
		else
		{
			String message = ".parse(): No default available for config file. It must be specified";
			logger.error(message);
			throw new ClientParameterException(message);
		}
	}
	
	public CliMgr(String[] args, FunctionalInterfaces.OnShowHelp onShowHelp, FunctionalInterfaces.OnConfigFileCmd onConfigFile)
	{
		if(args == null)
		{
			throw new IllegalArgumentException("argument list must not be null");
		}
		if(onShowHelp == null)
		{
			throw new IllegalArgumentException("onShowHelp must not be null");
		}
		if(onConfigFile == null)
		{
			throw new IllegalArgumentException("onConfigFile must not be null");
		}
		this.cmdLine = Arrays.asList(args);
		this.remainingOptions = new ArrayList<String>();
		clientSettings = new ClientSettings();
		this.onShowHelp = onShowHelp;
		this.onConfigFile = onConfigFile;
	}
	
	public boolean parse(CommandLineParser parser)
	{
		if(parser == null)
		{
			throw new IllegalArgumentException("parser must not be null");
		}
		boolean rval = false;
		CommandLine cmd = null;
		Options options = buildOptions();
		try
		{			
			logger.debug(".parse(): params = %s", String.join(", ", cmdLine));
			
			cmd = parser.parse(options, cmdLine.toArray(new String[cmdLine.size()]));

			if (cmd.getOptions().length == 0)
			{
				logger.error(".parse(): No valid arguments found in command line");
				throw new ParseException("Invalid argument list supplied");
			}
			
			if (cmd.hasOption(helpCmd))
			{
				onShowHelp.showHelp(options);
			}
			else // do all the other non-help commands
			{
				checkConfigFileCmd(cmd);
				rval = true;
			}
			remainingOptions.addAll(cmd.getArgList());
			if(remainingOptions.size() != 0)
			{
				logger.info(".parse(): The following remaining parameters will be passed to the application - %s",
					String.join(", ", remainingOptions));
			}
			logger.trace(".parse(): All update-guy parameters parsed.");
		}
		catch (ParseException e)
		{
			logger.error(".parse(): Failed to parse comand line properties", e);
			onShowHelp.showHelp(options);
			rval = false;
		}
		catch(ClientParameterException e)
		{
			logger.error(".parse(): command line error. Message: '%s'", e.getMessage());
			rval = false;
		}
		catch(Exception e)
		{
			logger.error(".parse(): failed. Reason: %s", e.getMessage());
			rval = false;
		}
		return rval;
	}
	
	public ClientSettings getClientSettings()
	{
		return clientSettings;
	}

	public String[] getRemainingParams()
	{
		return remainingOptions.toArray(new String[0]);
		
	}

	
}
