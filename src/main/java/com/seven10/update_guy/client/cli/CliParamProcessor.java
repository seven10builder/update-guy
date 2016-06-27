package com.seven10.update_guy.client.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.exceptions.ClientParameterException;

public class CliParamProcessor
{
	static final Logger logger = LogManager.getFormatterLogger(CliParamProcessor.class.getName());
	
	@FunctionalInterface interface Validator
	{
		void validate(String value) throws ClientParameterException;
	}
	@FunctionalInterface interface ValueSetter
	{
		void set(String value);
	}
	@FunctionalInterface interface ValueGetter<T>
	{
		T get();
	}
	@FunctionalInterface interface ParamNotFoundReactor
	{
		void react() throws ClientParameterException;
	}

	public static ParamNotFoundReactor reportNoDefault(String name)
	{
		ParamNotFoundReactor reporter = ()->{ throw new ClientParameterException(".parse(): No default available for %s. It must be specified", name); };
		return reporter;
	}

	public static <T> ParamNotFoundReactor reportUsingDefault(String name, ValueGetter<T> getter)
	{
		 ParamNotFoundReactor reactor = ()->logger.info(".parse(): Using default value for %s ('%s')", name, getter.get().toString());
		 return reactor;
	}
	
	private CommandLine cmd;

	public CliParamProcessor(CommandLine cmd)
	{
		this.cmd = cmd;
	}
	public void processParam(String cmdId, Validator validator, ValueSetter setter, ParamNotFoundReactor paramNotFoundReactor) throws ClientParameterException
	{
		if (cmd.hasOption(cmdId))
		{
			String value = cmd.getOptionValue(cmdId);
			validator.validate(value);
			setter.set(value);
		}
		else
		{
			paramNotFoundReactor.react();
		}
	}
}
