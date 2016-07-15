/**
 * 
 */
package com.seven10.update_guy.client.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLineParser;
import org.junit.Test;
import com.seven10.update_guy.client.cli.CliMgr.OnConfigFileCmd;
import com.seven10.update_guy.client.cli.CliMgr.OnShowHelp;
import com.seven10.update_guy.client.exceptions.ClientParameterException;

/**
 * @author kmm
 *
 */
public class CliMgrTest
{
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#CliMgr(java.lang.String[])}.
	 */
	@Test
	public void testCliMgr_valid()
	{
		String []args = new String[]{"arg1", "arg2", "arg3"};
		List<String> actualCmdline = Arrays.asList(args);
		CliMgr cliMgr = new CliMgr(args, CliMgr::showHelp, CliMgr::processCfgFile);
		assertNotNull(cliMgr);
		assertEquals(actualCmdline, cliMgr.cmdLine);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#CliMgr(java.lang.String[])}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCliMgr_null_args()
	{
		String []args = null;
		OnShowHelp onShowHelp = CliMgr::showHelp;
		OnConfigFileCmd onConfigFile = CliMgr::processCfgFile;
		new CliMgr(args, onShowHelp, onConfigFile);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#CliMgr(java.lang.String[])}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCliMgr_null_help()
	{
		String []args = new String[]{"arg1", "arg2", "arg3"};
		OnShowHelp onShowHelp = null;
		OnConfigFileCmd onConfigFile = CliMgr::processCfgFile;
		new CliMgr(args, onShowHelp, onConfigFile);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#CliMgr(java.lang.String[])}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCliMgr_null_processCfgFile()
	{
		String []args = new String[]{"arg1", "arg2", "arg3"};
		OnShowHelp onShowHelp = CliMgr::showHelp;
		OnConfigFileCmd onConfigFile = null;
		new CliMgr(args, onShowHelp, onConfigFile);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#parse()}.
	 * @throws ClientParameterException 
	 */
	@Test
	public void testParse_empty() throws ClientParameterException
	{
		String []args = new String[0];
		OnShowHelp onShowHelp = mock(OnShowHelp.class);
		
		OnConfigFileCmd onConfigFile = mock(OnConfigFileCmd.class);
		
		CliMgr cliMgr = new CliMgr(args, onShowHelp, onConfigFile);
		boolean actual = cliMgr.parse(CliMgr.getParser());
		
		assertFalse(actual);
		verify(onShowHelp, times(1)).showHelp(any());
		verify(onConfigFile, never()).doCommand(any());
		
		List<String> actualRemaining = Arrays.asList(cliMgr.getRemainingParams());
		assertEquals(0, actualRemaining.size());
	}
	
	/**
	 * @param expectedRemainingSize 
	 * @param args
	 * @throws ClientParameterException
	 */
	private void doHelpCmdTest(String cmd) throws ClientParameterException
	{
		String []args = new String[]{cmd};
		OnShowHelp onShowHelp = mock(OnShowHelp.class);
		
		OnConfigFileCmd onConfigFile = mock(OnConfigFileCmd.class);
		
		CliMgr cliMgr = new CliMgr(args, onShowHelp, onConfigFile);
		boolean actual = cliMgr.parse(CliMgr.getParser());
		
		assertFalse(actual);
		verify(onShowHelp, times(1)).showHelp(any());
		verify(onConfigFile, never()).doCommand(any());
		
		List<String> actualRemaining = Arrays.asList(cliMgr.getRemainingParams());
		assertEquals(0, actualRemaining.size());
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#parse()}.
	 * @throws ClientParameterException 
	 */
	@Test
	public void testParse_invalid_params() throws ClientParameterException
	{
		doHelpCmdTest("some-arg");
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#parse()}.
	 * @throws ClientParameterException 
	 */
	@Test
	public void testParse_help_short() throws ClientParameterException
	{
		doHelpCmdTest("-" + CliMgr.helpCmd);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#parse()}.
	 * @throws ClientParameterException 
	 */
	@Test
	public void testParse_help_long() throws ClientParameterException
	{
		doHelpCmdTest("--" + CliMgr.helpCmdLong);
	}
	
	/**
	 * @param cfgFilePath
	 * @param args
	 * @param expectedRemaining TODO
	 * @throws ClientParameterException
	 */
	private void checkConfigFileCmd(String cfgFilePath, String[] args) throws ClientParameterException
	{
		OnShowHelp onShowHelp = mock(OnShowHelp.class);
		
		OnConfigFileCmd onConfigFile = mock(OnConfigFileCmd.class);
		List<String> expectedRemaining = Arrays.asList(args).stream().skip(2).collect(Collectors.toList());
		CliMgr cliMgr = new CliMgr(args, onShowHelp, onConfigFile);
		boolean actual = cliMgr.parse(CliMgr.getParser());
		
		assertTrue(actual);
		verify(onShowHelp, never()).showHelp(any());
		verify(onConfigFile, times(1)).doCommand(cfgFilePath);
		
		List<String> actualRemaining = Arrays.asList(cliMgr.getRemainingParams());
		assertTrue(expectedRemaining.containsAll(actualRemaining));
		assertTrue(actualRemaining.containsAll(expectedRemaining));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#getClientSettings()}.
	 * @throws ClientParameterException 
	 */
	@Test
	public void testGetClientSettings_short_valid_no_extra() throws ClientParameterException
	{
		String cfgFilePath = "somePath";
		String []args = new String[]{"-" + CliMgr.configFileCmd, cfgFilePath};
		checkConfigFileCmd(cfgFilePath, args);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#getClientSettings()}.
	 * @throws ClientParameterException 
	 */
	@Test
	public void testGetClientSettings_long_valid_no_extra() throws ClientParameterException
	{
		String cfgFilePath = "somePath";
		String []args = new String[]{"--" + CliMgr.configFileCmdLong, cfgFilePath};
		checkConfigFileCmd(cfgFilePath, args);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#getClientSettings()}.
	 * @throws ClientParameterException 
	 */
	@Test
	public void testGetClientSettings_valid_extra() throws ClientParameterException
	{
		String cfgFilePath = "somePath";
		String []args = new String[]{"--" + CliMgr.configFileCmdLong, cfgFilePath, "bogus", "smoochie"};
		checkConfigFileCmd(cfgFilePath, args);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#getClientSettings()}.
	 * @throws ClientParameterException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetClientSettings_null_parser() throws ClientParameterException
	{
		String []args = new String[]{"--" + CliMgr.configFileCmdLong};
		
		OnShowHelp onShowHelp = mock(OnShowHelp.class);
		OnConfigFileCmd onConfigFile = mock(OnConfigFileCmd.class);
		
		CliMgr cliMgr = new CliMgr(args, onShowHelp, onConfigFile);
		CommandLineParser parser = null;
		cliMgr.parse(parser);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.cli.CliMgr#getClientSettings()}.
	 * @throws ClientParameterException 
	 */
	@Test
	public void testGetClientSettings_no_path() throws ClientParameterException
	{
		String []args = new String[]{"--" + CliMgr.configFileCmdLong};
		
		OnShowHelp onShowHelp = mock(OnShowHelp.class);
		OnConfigFileCmd onConfigFile = mock(OnConfigFileCmd.class);
		
		CliMgr cliMgr = new CliMgr(args, onShowHelp, onConfigFile);
		boolean actual = cliMgr.parse(CliMgr.getParser());
		
		assertFalse(actual);
		verify(onShowHelp, times(1)).showHelp(any());
		verify(onConfigFile, never()).doCommand(any());
		
		List<String> actualRemaining = Arrays.asList(cliMgr.getRemainingParams());
		assertEquals(0, actualRemaining.size());
	}
}
