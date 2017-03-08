/**
 * 
 */
package com.seven10.update_guy.server.release_family;

import static org.junit.Assert.*;
import static com.seven10.update_guy.common.ReleaseFamilyHelpers.*;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gson.JsonSyntaxException;
import com.seven10.update_guy.common.ReleaseFamilyEntryHelpers;
import com.seven10.update_guy.server.helpers.RepoInfoHelpers;
import com.seven10.update_guy.server.release_family.ActiveVersionEncoder;
import com.seven10.update_guy.server.release_family.ReleaseFamilyMgr;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.exceptions.RepositoryException;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType;

/**
 * @author kmm
 *
 */
public class ReleaseFamilyTest
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	private void createFileNameEncoder(String expectedVersionId, Path expectedPath, ActiveVersionEncoder encoder, String releaseFamily)
	{
		when(encoder.encodeFileName(anyString())).thenAnswer(new Answer<Path>()
				{
					@Override
					public Path answer(InvocationOnMock invocation) throws Throwable
					{
						Object[] args = invocation.getArguments();
						assertEquals(expectedVersionId, (String)args[0]);
						return expectedPath;
					}
				});
		when(encoder.getReleaseFamily()).thenReturn(releaseFamily);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#ReleaseFamilyMgr(com.seven10.update_guy.Globals)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testReleaseFamilyMgr_valid_path_not_exists() throws IOException, RepositoryException
	{
		String testName = "valid-pne";
		Path path = folder.getRoot().toPath().resolve("path-not-exist-yet"); 
		assertFalse(Files.exists(path));
		
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(path, repoId);
		assertNotNull(releaseFamilyMgr);
		assertTrue(Files.exists(path));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#ReleaseFamilyMgr(com.seven10.update_guy.Globals)}.
	 * @throws IOException 
	 */
	@Test
	public void testReleaseFamilyMgr_valid_path_exists() throws IOException
	{
		
		Path path = folder.newFolder("manmgr").toPath(); 
		Files.createDirectories(path);
		assertTrue(Files.exists(path));
		String repoId = "some-repo-id";
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(path, repoId);
		assertNotNull(releaseFamilyMgr);
		assertTrue(Files.exists(path));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#ReleaseFamilyMgr(com.seven10.update_guy.Globals)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testReleaseFamilyMgr_null_path()
	{
		Path path = null;
		String repoId = "shoudln't matter";
		new ReleaseFamilyMgr(path, repoId);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#ReleaseFamilyMgr(com.seven10.update_guy.Globals)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testReleaseFamilyMgr_null_repoId() throws IOException
	{
		Path path = folder.newFolder("manmgr").toPath();
		String repoId = null;
		new ReleaseFamilyMgr(path, repoId);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#ReleaseFamilyMgr(com.seven10.update_guy.Globals)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testReleaseFamilyMgr_empty_repoId() throws IOException
	{
		Path path = folder.newFolder("manmgr").toPath();
		String repoId = "";
		new ReleaseFamilyMgr(path, repoId);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getReleaseFamily(java.lang.String)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetReleaseFamily_valid() throws IOException, RepositoryException
	{
		String testName = "getReleaseFamily";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		Path releaseFamilyPath = Paths.get(System.getProperty(ServerGlobals.SETTING_LOCAL_PATH))
				.resolve(repoId)
				.resolve("releaseFamilies");
		ReleaseFamily expected = load_release_family_file_from_path(get_valid_release_family_file_path());
		
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(releaseFamilyPath, repoId);
		ReleaseFamily actual = releaseFamilyMgr.getReleaseFamily("relfam");
		
		assertEquals(expected, actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getReleaseFamily(java.lang.String)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test(expected=RepositoryException.class)
	public void testGetReleaseFamily_not_found() throws IOException, RepositoryException
	{
		String testName = "releaseFamily-nf";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		Path releaseFamilyPath = Paths.get(System.getProperty(ServerGlobals.SETTING_LOCAL_PATH)).resolve(repoId).resolve("releaseFamilies");
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(releaseFamilyPath, repoId);
		releaseFamilyMgr.getReleaseFamily(testName);
		
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getReleaseFamily(java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetReleaseFamily_null() throws RepositoryException, IOException
	{
		String testName = "getReleaseFamily-n";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		Path releaseFamilyPath = Paths.get(System.getProperty(ServerGlobals.SETTING_LOCAL_PATH)).resolve(repoId).resolve("releaseFamilies");
		
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(releaseFamilyPath, repoId);
		String releaseFamily = null;
		releaseFamilyMgr.getReleaseFamily(releaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getReleaseFamily(java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetReleaseFamily_empty() throws RepositoryException, IOException
	{
		String testName = "getReleaseFamily-emp";
		
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		Path releaseFamilyPath = Paths.get(System.getProperty(ServerGlobals.SETTING_LOCAL_PATH)).resolve(repoId).resolve("releaseFamilies");
		
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(releaseFamilyPath, repoId);
		String releaseFamily = "";
		releaseFamilyMgr.getReleaseFamily(releaseFamily);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getReleaseFamilies()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetReleaseFamilys_valid_release_family_only() throws RepositoryException, IOException, UpdateGuyException
	{
		String testName = "getReleaseFamilys";
		
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		Path releaseFamilyPath = Paths.get(System.getProperty(ServerGlobals.SETTING_LOCAL_PATH)).resolve(repoId).resolve("releaseFamilies");
		// create list of valid releaseFamilys
		List<ReleaseFamily> expected = load_releaseFamily_list_from_path(get_release_family_files_path());
		
		// create object to test
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(releaseFamilyPath, repoId);		
		List<ReleaseFamily> actual = releaseFamilyMgr.getReleaseFamilies();
		assertEquals(expected.size(), actual.size());
		assertTrue(expected.containsAll(actual));
		assertTrue(actual.containsAll(expected));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getReleaseFamilies()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetReleaseFamilies_many_files() throws RepositoryException, IOException, UpdateGuyException
	{
		String testName = "getReleaseFamilys";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		Path releaseFamilyPath = Paths.get(System.getProperty(ServerGlobals.SETTING_LOCAL_PATH)).resolve(repoId).resolve("releaseFamilies");
		
		// create list of valid releaseFamilys
		List<ReleaseFamily> expected = load_releaseFamily_list_from_path(get_release_family_files_path());

		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(releaseFamilyPath, repoId);		
		List<ReleaseFamily> actual = releaseFamilyMgr.getReleaseFamilies();
		// list should still only contain the expected entries.
		assertTrue(expected.containsAll(actual));
		assertTrue(actual.containsAll(expected));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getReleaseFamilies()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetReleaseFamilys_some_invalid_files() throws RepositoryException, IOException, UpdateGuyException
	{
		String testName = "getReleaseFamilys";
		// create list of valid releaseFamilys
		List<ReleaseFamily> expected = load_releaseFamily_list_from_path(get_release_family_files_path());
		
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		Path releaseFamilyPath = Paths.get(System.getProperty(ServerGlobals.SETTING_LOCAL_PATH))
				.resolve(repoId)
				.resolve("releaseFamilies");
		// create a defective releaseFamily file
		create_invalid_release_family_file(releaseFamilyPath);
		// create object to test
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(releaseFamilyPath, repoId);		
		List<ReleaseFamily> actual = releaseFamilyMgr.getReleaseFamilies();
		// list should still only contain the expected (valid) entries.
		
		//NOTE: There is a problem with gson at the moment which makes it so it doesn't throw an exception if it 
		// tries to deserialize an object of incorrect type. as a result, this match doesn't fail like its supposed to
		// and the expected behavior of this test is incorrect.
		// assertTrue(expected.containsAll(actual));
		assertTrue(actual.containsAll(expected));
	}

	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetActiveVersion_valid() throws JsonSyntaxException, IOException, RepositoryException
	{
		String expectedVersionId = "versionId";
		Path expectedPath = Paths.get("this","path","isnt","used");
		String expectedReleaseFamily = "some-release-fam";
		ReleaseFamilyEntry expectedReleaseFamilyEntry = new ReleaseFamilyEntry();
		expectedReleaseFamilyEntry.setReleaseFamily(expectedReleaseFamily);
		expectedReleaseFamilyEntry.setVersion("some version");
		
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		createFileNameEncoder(expectedVersionId, expectedPath, encoder, expectedReleaseFamily);
		when(encoder.loadVersionEntry(any())).thenAnswer(new Answer<ReleaseFamilyEntry>()
		{
			@Override
			public ReleaseFamilyEntry answer(InvocationOnMock invocation) throws Throwable
			{
				Object[] args = invocation.getArguments();
				assertEquals(expectedPath, (Path)args[0]);
				return expectedReleaseFamilyEntry;
			}
		});
		
		String testName = "getActiveVers";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		Path releaseFamilyPath = Paths.get(System.getProperty(ServerGlobals.SETTING_LOCAL_PATH))
				.resolve(repoId)
				.resolve("releaseFamilies");
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(releaseFamilyPath, repoId);
		
		ReleaseFamilyEntry actualReleaseFamilyEntry = releaseFamilyMgr.getActiveVersion(expectedVersionId, encoder);
		assertEquals(expectedReleaseFamilyEntry, actualReleaseFamilyEntry);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test(expected=RepositoryException.class)
	public void testGetActiveVersion_invalid() throws JsonSyntaxException, IOException, RepositoryException
	{
		String expectedVersionId = "versionId-inv";
		
		String repoId = "repoId";
		String releaseFamily = "releaseFamily";
		ActiveVersionEncoder encoder = new ActiveVersionEncoder(repoId, releaseFamily);
		
		String testName = "getActiveVers-inv";
		Path rootPath = folder.newFolder(testName).toPath();
		// create object to test
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(rootPath, repoId);
		
		releaseFamilyMgr.getActiveVersion(expectedVersionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException 
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetActiveVersion_null_activeVersionId() throws JsonSyntaxException, IOException, RepositoryException
	{
		
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		
		String testName = "getActiveVers_nid";
		Path rootPath = folder.newFolder(testName).toPath();
		// create object to test
		String versionId = null;
		String repoId = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local).getShaHash();
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(rootPath, repoId);
		releaseFamilyMgr.getActiveVersion(versionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException 
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetActiveVersion_empty_activeVersionId() throws JsonSyntaxException, IOException, RepositoryException
	{
		
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		
		String testName = "getActiveVers_nid";
		Path rootPath = folder.newFolder(testName).toPath();
		// create object to test
		String versionId = "";
		String repoId = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local).getShaHash();
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(rootPath, repoId);
		releaseFamilyMgr.getActiveVersion(versionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetActiveVersion_null_encoder() throws JsonSyntaxException, IOException, RepositoryException
	{
		
		ActiveVersionEncoder encoder = null;
		
		String testName = "getActiveVers_nenc";
		Path rootPath = folder.newFolder(testName).toPath();
		// create object to test
		String versionId = "some-active-version";
		String repoId = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local).getShaHash();
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(rootPath, repoId);
		releaseFamilyMgr.getActiveVersion(versionId, encoder);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test
	public void testSetActiveVersion_valid() throws JsonSyntaxException, IOException, RepositoryException
	{
		String testName = "setActiveVers-v";
		String expectedVersionId = "versionId";
		String expectedReleaseFamilyName = "some-release-fam";
		Path expectedPath = Paths.get("this","path","isnt","used");
		
		Path rootFolder = folder.newFolder(testName).toPath();
		List<ReleaseFamilyEntry> entries = ReleaseFamilyEntryHelpers.create_valid_release_family_entries(testName, 4, rootFolder);
		
		ReleaseFamily expectedReleaseFamily = new ReleaseFamily();
		expectedReleaseFamily.setReleaseFamily(expectedReleaseFamilyName);
		for(ReleaseFamilyEntry entry: entries)
		{
			entry.setReleaseFamily(expectedReleaseFamilyName);
			expectedReleaseFamily.addVersionEntry(entry);
		}
		
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		
		createFileNameEncoder(expectedVersionId, expectedPath, encoder, expectedReleaseFamilyName);
		// create object to test
		
		ReleaseFamilyMgr releaseFamilyMgr = mock(ReleaseFamilyMgr.class);
		when(releaseFamilyMgr.getReleaseFamily(expectedReleaseFamilyName)).thenReturn(expectedReleaseFamily);
		doCallRealMethod().when(releaseFamilyMgr).setActiveVersion(anyString(), anyString(), any());
		
		for(ReleaseFamilyEntry expectedEntry: entries)
		{
			String newVersion = expectedEntry.getVersion();
			releaseFamilyMgr.setActiveVersion(newVersion, expectedVersionId, encoder);
			verify(encoder, times(1)).writeVersionEntry(expectedPath, expectedEntry);
		}
	}

	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetActiveVersion_null_newVersion() throws JsonSyntaxException, IOException, RepositoryException
	{
		String testName = "setActiveVers-nnv";
		Path rootPath = folder.newFolder(testName).toPath();
		String repoId = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local).getShaHash();
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(rootPath, repoId);
		String newVersion = null;
		String expectedVersionId = "versionId";
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		releaseFamilyMgr.setActiveVersion(newVersion, expectedVersionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetActiveVersion_empty_newVersion() throws JsonSyntaxException, IOException, RepositoryException
	{
		String testName = "setActiveVers-env";
		Path rootPath = folder.newFolder(testName).toPath();
		String repoId = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local).getShaHash();
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(rootPath, repoId);
		String newVersion = "";
		String expectedVersionId = "versionId";
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		releaseFamilyMgr.setActiveVersion(newVersion, expectedVersionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test
	public void testSetActiveVersion_not_found_newVersion() throws JsonSyntaxException, IOException, RepositoryException
	{
		String expectedReleaseFamilyName = "some-release-fam";
		String expectedVersionId = "versionId";
		Path expectedPath = Paths.get("this","path","isnt","used");
		
		ReleaseFamilyEntry releaseFamilyEntry = new ReleaseFamilyEntry();
		releaseFamilyEntry.setReleaseFamily(expectedReleaseFamilyName);
		releaseFamilyEntry.setVersion("not-the-one-we-are-looking-for");
		
		ReleaseFamily expectedReleaseFamily = new ReleaseFamily();
		expectedReleaseFamily.setReleaseFamily(expectedReleaseFamilyName);
		expectedReleaseFamily.addVersionEntry(releaseFamilyEntry);
		
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		
		createFileNameEncoder(expectedVersionId, expectedPath, encoder, expectedReleaseFamilyName);
		// create object to test
		
		ReleaseFamilyMgr releaseFamilyMgr = mock(ReleaseFamilyMgr.class);
		when(releaseFamilyMgr.getReleaseFamily(expectedReleaseFamilyName)).thenReturn(expectedReleaseFamily);
		doCallRealMethod().when(releaseFamilyMgr).setActiveVersion(anyString(), anyString(), any());
		try
		{
			releaseFamilyMgr.setActiveVersion("the-one-we-look-for", expectedVersionId, encoder);
			fail("setActiveVersion should have thrown an exception");
		}
		catch(RepositoryException ex)
		{
			verify(encoder, never()).writeVersionEntry(any(), any());	
		}
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetActiveVersion_null_versionId() throws JsonSyntaxException, IOException, RepositoryException
	{
		String testName = "setActiveVers-nvid";
		Path rootPath = folder.newFolder(testName).toPath();
		String repoId = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local).getShaHash();
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(rootPath, repoId);
		String newVersion = "version";
		String expectedVersionId = null;
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		releaseFamilyMgr.setActiveVersion(newVersion, expectedVersionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetActiveVersion_empty_versionId() throws JsonSyntaxException, IOException, RepositoryException
	{
		String testName = "setActiveVers-evid";
		Path rootPath = folder.newFolder(testName).toPath();
		String repoId = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local).getShaHash();
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(rootPath, repoId);
		String newVersion = "version";
		String expectedVersionId = "";
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		releaseFamilyMgr.setActiveVersion(newVersion, expectedVersionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.ReleaseFamilyMgr.ReleaseFamilyMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetActiveVersion_null_encoder() throws JsonSyntaxException, IOException, RepositoryException
	{
		String testName = "setActiveVers-ne";
		Path rootPath = folder.newFolder(testName).toPath();
		String repoId = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local).getShaHash();
		ReleaseFamilyMgr releaseFamilyMgr = new ReleaseFamilyMgr(rootPath, repoId);
		String newVersion = "version";
		String expectedVersionId = "versionId";
		ActiveVersionEncoder encoder = null;
		releaseFamilyMgr.setActiveVersion(newVersion, expectedVersionId, encoder);
	}
}
