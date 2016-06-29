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
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.client.ClientSettings;
import com.seven10.update_guy.exceptions.ClientParameterException;
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
	private List<String> cmdLine;

	private void help(Options options)
	{
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp(executableName, options);
	}
	private ClientSettings processCfgFile(CommandLine cmd) throws ClientParameterException
	{
		if (cmd.hasOption(configFileCmd))
		{
			String path = cmd.getOptionValue(configFileCmd);
			if(StringUtils.isBlank(path))
			{
				throw new ClientParameterException("roleName must not be blank");
			}
			
			Path filePath = Paths.get(path);
			return ClientSettings.loadConfig(filePath);
		}
		else
		{
			throw new ClientParameterException(".parse(): No default available for config file. It must be specified");
		}
	}
	
	public CliMgr(String[] args)
	{
		this.cmdLine = Arrays.asList(args);
	}
	
	public boolean parse()
	{
		boolean rval;
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		Options options = buildOptions();
		try
		{			
			logger.debug(".parse(): params = %s", String.join(", ", cmdLine));
			
			cmd = parser.parse(options, cmdLine.toArray(new String[cmdLine.size()]));

			if (cmd.getOptions().length == 0)
			{
				throw new ParseException("Invalid argument list supplied");
			}
			
			if (cmd.hasOption(helpCmd))
			{
				help(options);
			}
			else
			{
				processCfgFile(cmd);
			}
			logger.trace(".parse(): All update-guy parameters parsed.");
			rval = true;
		}
		catch (ParseException e)
		{
			logger.error(".parse(): Failed to parse comand line properties", e);
			help(options);
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
		// TODO Auto-generated method stub
		return null;
	}
}
