/**
 * 
 */
package com.seven10.update_guy.release;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.seven10.update_guy.ManifestEntryHelpers;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.ManifestEntry;
import com.seven10.update_guy.repository.RepositoryInfo;

/**
 * @author kmm
 *
 */
public class ReleaseMgrTest
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#ReleaseMgr(com.seven10.update_guy.Globals)}.
	 */
	@Test
	public void testReleaseMgr_valid()
	{
		ManifestEntry activeVersion = new ManifestEntry();
		RepositoryInfo repoInfo = new RepositoryInfo();
		
		ReleaseMgr mgr = new ReleaseMgr(activeVersion, repoInfo);
		assertNotNull(mgr);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#ReleaseMgr(com.seven10.update_guy.Globals)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testReleaseMgr_null_version()
	{
		ManifestEntry activeVersion = null;
		RepositoryInfo repoInfo = new RepositoryInfo();
		
		ReleaseMgr mgr = new ReleaseMgr(activeVersion, repoInfo);
		assertNotNull(mgr);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#ReleaseMgr(com.seven10.update_guy.Globals)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testReleaseMgr_null_repoInfo()
	{
		ManifestEntry activeVersion = new ManifestEntry();
		RepositoryInfo repoInfo = null;
		
		ReleaseMgr mgr = new ReleaseMgr(activeVersion, repoInfo);
		assertNotNull(mgr);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#getAvailableRoles()}.
	 */
	@Test
	public void testGetAvailableRoles()
	{
		ManifestEntry activeVersion = mock(ManifestEntry.class);
		List<String> expectedRoles = new ArrayList<String>();
		for(int i = 1; i <= 5; i++)
		{
			expectedRoles.add("role"+i);
		}
		when(activeVersion.getRoles()).thenReturn(expectedRoles);
		
		ReleaseMgr mgr = new ReleaseMgr(activeVersion, new RepositoryInfo());
		
		List<String> actualRoles = mgr.getAllRoles();
		assertTrue(expectedRoles.containsAll(actualRoles));
		assertTrue(actualRoles.containsAll(expectedRoles));
	}
	
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#getFileForRole(java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testGetFileForRole_valid() throws RepositoryException, IOException
	{
		String testName = "getfilefr-v";
		Path rootFolder = folder.newFolder(testName).toPath();
		ManifestEntry activeVersion = ManifestEntryHelpers.create_valid_manifest_entry(testName, 1, rootFolder);
		RepositoryInfo repoInfo = new RepositoryInfo();
		ReleaseMgr mgr = new ReleaseMgr(activeVersion, repoInfo);
		String roleName = activeVersion.getRoles().get(0);
		File roleFile = mgr.getFileForRole(roleName);
		assertTrue(Files.exists(roleFile.toPath()));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#getFileForRole(java.lang.String)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetFileForRole_null_roleName() throws RepositoryException
	{
		
		ManifestEntry activeVersion = new ManifestEntry();
		RepositoryInfo repoInfo = new RepositoryInfo();
		ReleaseMgr mgr = new ReleaseMgr(activeVersion, repoInfo);
		String roleName = null;
		mgr.getFileForRole(roleName);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#getFileForRole(java.lang.String)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetFileForRole_empty_roleName() throws RepositoryException
	{
		ManifestEntry activeVersion = new ManifestEntry();
		RepositoryInfo repoInfo = new RepositoryInfo();
		ReleaseMgr mgr = new ReleaseMgr(activeVersion, repoInfo);
		String roleName = "";
		mgr.getFileForRole(roleName);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#cacheFiles(javax.ws.rs.container.AsyncResponse)}.
	 */
	@Test
	public void testCacheFiles()
	{
		fail("Not yet implemented");
	}
	
}
