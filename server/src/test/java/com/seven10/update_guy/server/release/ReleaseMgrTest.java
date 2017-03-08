/**
 * 
 */
package com.seven10.update_guy.server.release;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.mockito.Mockito.*;
import static com.seven10.update_guy.common.ReleaseFamilyHelpers.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.seven10.update_guy.common.FileFingerPrint;
import com.seven10.update_guy.common.ReleaseFamilyEntryHelpers;

import com.seven10.update_guy.server.helpers.RepoInfoHelpers;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.common.release_family.UpdateGuyRole;
import com.seven10.update_guy.server.exceptions.RepositoryException;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.SpyablePathConsumer;
import com.seven10.update_guy.server.repository.SpyableRunnable;
import com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType;
import com.seven10.update_guy.server.release.ReleaseMgr;

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
		ReleaseFamily releaseFamily = new ReleaseFamily();
		RepositoryInfo repoInfo = new RepositoryInfo();
		
		ReleaseMgr mgr = new ReleaseMgr(releaseFamily, repoInfo);
		assertNotNull(mgr);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#ReleaseMgr(com.seven10.update_guy.Globals)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testReleaseMgr_null_releaseFamily()
	{
		ReleaseFamily releaseFamily = null;
		RepositoryInfo repoInfo = new RepositoryInfo();
		
		ReleaseMgr mgr = new ReleaseMgr(releaseFamily, repoInfo);
		assertNotNull(mgr);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#ReleaseMgr(com.seven10.update_guy.Globals)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testReleaseMgr_null_repoInfo()
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		RepositoryInfo repoInfo = null;
		
		ReleaseMgr mgr = new ReleaseMgr(releaseFamily, repoInfo);
		assertNotNull(mgr);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#getAvailableRoles()}.
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetAvailableRoles() throws RepositoryException
	{
		ReleaseFamilyEntry activeVersion = mock(ReleaseFamilyEntry.class);
		List<String> expectedRoles = new ArrayList<String>();
		for(int i = 1; i <= 5; i++)
		{
			expectedRoles.add("role"+i);
		}
		String version = "availRoles";
		when(activeVersion.getVersion()).thenReturn(version);
		when(activeVersion.getRoles()).thenReturn(expectedRoles);
		ReleaseFamily releaseFamily = new ReleaseFamily();
		releaseFamily.addVersionEntry(activeVersion);
		ReleaseMgr mgr = new ReleaseMgr(releaseFamily, new RepositoryInfo());
		
		List<String> actualRoles = mgr.getAllRoles(version);
		assertTrue(expectedRoles.containsAll(actualRoles));
		assertTrue(actualRoles.containsAll(expectedRoles));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#getAvailableRoles()}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetAvailableRoles_null_version() throws RepositoryException
	{
		ReleaseMgr mgr = new ReleaseMgr(new ReleaseFamily(), new RepositoryInfo());
		String version = null;
		mgr.getAllRoles(version);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#GetRoleInfoForRole(java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetRoleInfoForRole_valid() throws RepositoryException, IOException, UpdateGuyException
	{
		String testName = "getfilefr-v";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		
		for(ReleaseFamily releaseFamily: load_releaseFamily_list_from_path(get_release_family_files_path()))
		{
			ReleaseMgr mgr = new ReleaseMgr(releaseFamily, repoInfo);
		
			for(ReleaseFamilyEntry activeVersion: releaseFamily.getVersionEntries())
			{
				for(String roleName: activeVersion.getRoles())
				{
					UpdateGuyRole roleFile = mgr.getRoleInfoForRole(activeVersion.getVersion(), roleName);
					Path rolePath = roleFile.getFilePath();
					String expectedFingerPrint = FileFingerPrint.create(rolePath);
					assertTrue(Files.exists(rolePath));
					assertEquals("(" + releaseFamily.getReleaseFamily() + "," + activeVersion.getVersion()+ ", " + roleName + ")" ,
							expectedFingerPrint, roleFile.getFingerPrint());
				}
			}
		}
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#GetRoleInfoForRole(java.lang.String)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetRoleInfoForRole_null_roleName() throws RepositoryException
	{
		
		ReleaseFamily activeVersion = new ReleaseFamily();
		RepositoryInfo repoInfo = new RepositoryInfo();
		ReleaseMgr mgr = new ReleaseMgr(activeVersion, repoInfo);
		String version = "version";
		String roleName = null;
		mgr.getRoleInfoForRole(version, roleName);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#GetRoleInfoForRole(java.lang.String)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetRoleInfoForRole_empty_roleName() throws RepositoryException
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		RepositoryInfo repoInfo = new RepositoryInfo();
		ReleaseMgr mgr = new ReleaseMgr(releaseFamily, repoInfo);
		String version = "version";
		String roleName = "";
		mgr.getRoleInfoForRole(version, roleName);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#GetRoleInfoForRole(java.lang.String)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetRoleInfoForRole_null_version() throws RepositoryException
	{
		
		ReleaseFamily activeVersion = new ReleaseFamily();
		RepositoryInfo repoInfo = new RepositoryInfo();
		ReleaseMgr mgr = new ReleaseMgr(activeVersion, repoInfo);
		String version = null;
		String roleName = "roleName";
		mgr.getRoleInfoForRole(version, roleName);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#GetRoleInfoForRole(java.lang.String)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetRoleInfoForRole_empty_version() throws RepositoryException
	{
		
		ReleaseFamily activeVersion = new ReleaseFamily();
		RepositoryInfo repoInfo = new RepositoryInfo();
		ReleaseMgr mgr = new ReleaseMgr(activeVersion, repoInfo);
		String version = "";
		String roleName = "roleName";
		mgr.getRoleInfoForRole(version, roleName);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#cacheFiles(javax.ws.rs.container.AsyncResponse)}.
	 * @throws Exception 
	 */
	@Test
	public void testCacheFiles_valid() throws Exception
	{
		String testName = "cachefiles-v";
		Path rootFolder = folder.newFolder(testName).toPath();
		ReleaseFamilyEntry activeVersion = ReleaseFamilyEntryHelpers.create_valid_release_family_entry(testName, 1, rootFolder);
		ReleaseFamily releaseFamily = new ReleaseFamily();
		releaseFamily.addVersionEntry(activeVersion);
		int roleCount = activeVersion.getRoles().size();
		String version = activeVersion.getVersion();
		RepositoryInfo repoInfo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		
		ReleaseMgr mgr = new ReleaseMgr(releaseFamily, repoInfo);
		
		Consumer<Path> onFileComplete = spy(new SpyablePathConsumer());
		Runnable onDownloadComplete = spy(new SpyableRunnable());
		mgr.cacheFiles(version, onFileComplete, onDownloadComplete);
		verify(onFileComplete, times(roleCount)).accept(any());
		verify(onDownloadComplete, atLeastOnce()).run();
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#cacheFiles(javax.ws.rs.container.AsyncResponse)}.
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCacheFiles_null_version() throws Exception
	{
		String testName = "cachefiles-v";
		Path rootFolder = folder.newFolder(testName).toPath();
		ReleaseFamilyEntry activeVersion = ReleaseFamilyEntryHelpers.create_valid_release_family_entry(testName, 1, rootFolder);
		ReleaseFamily releaseFamily = new ReleaseFamily();
		releaseFamily.addVersionEntry(activeVersion);
		String version = null;
		RepositoryInfo repoInfo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		
		ReleaseMgr mgr = new ReleaseMgr(releaseFamily, repoInfo);
		
		Consumer<Path> onFileComplete = spy(new SpyablePathConsumer());
		Runnable onDownloadComplete = spy(new SpyableRunnable());
		mgr.cacheFiles(version, onFileComplete, onDownloadComplete);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#cacheFiles(javax.ws.rs.container.AsyncResponse)}.
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCacheFiles_empty_version() throws Exception
	{
		String testName = "cachefiles-v";
		Path rootFolder = folder.newFolder(testName).toPath();
		ReleaseFamilyEntry activeVersion = ReleaseFamilyEntryHelpers.create_valid_release_family_entry(testName, 1, rootFolder);
		ReleaseFamily releaseFamily = new ReleaseFamily();
		releaseFamily.addVersionEntry(activeVersion);
		String version = "";
		RepositoryInfo repoInfo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		
		ReleaseMgr mgr = new ReleaseMgr(releaseFamily, repoInfo);
		
		Consumer<Path> onFileComplete = spy(new SpyablePathConsumer());
		Runnable onDownloadComplete = spy(new SpyableRunnable());
		mgr.cacheFiles(version, onFileComplete, onDownloadComplete);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#cacheFiles(javax.ws.rs.container.AsyncResponse)}.
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCacheFiles_null_onFileComplete() throws Exception
	{
		String testName = "cachefiles-v";
		Path rootFolder = folder.newFolder(testName).toPath();
		ReleaseFamilyEntry activeVersion = ReleaseFamilyEntryHelpers.create_valid_release_family_entry(testName, 1, rootFolder);
		ReleaseFamily releaseFamily = new ReleaseFamily();
		releaseFamily.addVersionEntry(activeVersion);
		String version = activeVersion.getVersion();
		RepositoryInfo repoInfo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		
		ReleaseMgr mgr = new ReleaseMgr( releaseFamily, repoInfo);
		
		Consumer<Path> onFileComplete = null;
		Runnable onDownloadComplete = spy(new SpyableRunnable());
		mgr.cacheFiles(version, onFileComplete, onDownloadComplete);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseMgr#cacheFiles(javax.ws.rs.container.AsyncResponse)}.
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCacheFiles_null_onDownloadComplete() throws Exception
	{
		String testName = "cachefiles-v";
		Path rootFolder = folder.newFolder(testName).toPath();
		ReleaseFamilyEntry activeVersion = ReleaseFamilyEntryHelpers.create_valid_release_family_entry(testName, 1, rootFolder);
		ReleaseFamily releaseFamily = new ReleaseFamily();
		releaseFamily.addVersionEntry(activeVersion);
		String version = activeVersion.getVersion();
		RepositoryInfo repoInfo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		
		ReleaseMgr mgr = new ReleaseMgr(releaseFamily, repoInfo);
		
		Consumer<Path> onFileComplete = spy(new SpyablePathConsumer());
		Runnable onDownloadComplete = null;
		mgr.cacheFiles(version, onFileComplete, onDownloadComplete);
	}
	
}
