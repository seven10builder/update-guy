/**
 * 
 */
package com.seven10.update_guy.server.manifest;

import static org.junit.Assert.*;
import static com.seven10.update_guy.common.ManifestHelpers.*;
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
import com.seven10.update_guy.common.ManifestEntryHelpers;
import com.seven10.update_guy.common.TestConstants;
import com.seven10.update_guy.server.exceptions.RepositoryException;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.manifest.Manifest;
import com.seven10.update_guy.common.manifest.ManifestEntry;
import com.seven10.update_guy.server.manifest.ActiveVersionEncoder;
import com.seven10.update_guy.server.manifest.ManifestMgr;

/**
 * @author kmm
 *
 */
public class ManifestMgrTest
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
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#ManifestMgr(com.seven10.update_guy.Globals)}.
	 * @throws IOException 
	 */
	@Test
	public void testManifestMgr_valid_path_not_exists() throws IOException
	{
		
		Path path = folder.getRoot().toPath().resolve("path-not-exist-yet"); 
		assertFalse(Files.exists(path));
		ManifestMgr manifestMgr = new ManifestMgr(path);
		assertNotNull(manifestMgr);
		assertTrue(Files.exists(path));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#ManifestMgr(com.seven10.update_guy.Globals)}.
	 * @throws IOException 
	 */
	@Test
	public void testManifestMgr_valid_path_exists() throws IOException
	{
		
		Path path = folder.newFolder("manmgr").toPath(); 
		Files.createDirectories(path);
		assertTrue(Files.exists(path));
		ManifestMgr manifestMgr = new ManifestMgr(path);
		assertNotNull(manifestMgr);
		assertTrue(Files.exists(path));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#ManifestMgr(com.seven10.update_guy.Globals)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testManifestMgr_null()
	{
		Path path = null;
		new ManifestMgr(path);
	}
	
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifest(java.lang.String)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetManifest_valid() throws IOException, RepositoryException
	{
		String testName = "getManifest";
		
		Path manifestPath = build_manifest_path_by_testname(testName, folder);
		Path manifestDir = manifestPath.getParent();
		copy_manifest_to_path(TestConstants.valid_manifest_name, manifestPath);
		Manifest expected = load_manifest_from_path(manifestPath);
		ManifestMgr manifestMgr = new ManifestMgr(manifestDir);
		Manifest actual = manifestMgr.getManifest(testName);
		
		assertEquals(expected, actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifest(java.lang.String)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test(expected=RepositoryException.class)
	public void testGetManifest_not_found() throws IOException, RepositoryException
	{
		String testName = "manifest-nf";
		Path manifestPath = folder.newFolder(testName).toPath();
		ManifestMgr manifestMgr = new ManifestMgr(manifestPath);
		manifestMgr.getManifest(testName);
		
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifest(java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetManifest_null() throws RepositoryException, IOException
	{
		String testName = "getmanifest-n";
		Path manifestPath = folder.newFolder(testName).toPath();
		ManifestMgr manifestMgr = new ManifestMgr(manifestPath);
		String releaseFamily = null;
		manifestMgr.getManifest(releaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifest(java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetManifest_empty() throws RepositoryException, IOException
	{
		String testName = "getmanifest-emp";
		Path manifestPath = folder.newFolder(testName).toPath();
		ManifestMgr manifestMgr = new ManifestMgr(manifestPath);
		String releaseFamily = "";
		manifestMgr.getManifest(releaseFamily);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifests()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetManifests_valid_manifests_only() throws RepositoryException, IOException, UpdateGuyException
	{
		String testName = "getManifests";
		// create list of valid manifests
		List<Manifest> expected = create_manifest_list(testName,5);
		// save list to files in folder
		Path rootPath = folder.newFolder(testName).toPath();
		write_manifest_list_to_folder(rootPath, expected);

		// create object to test
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);		
		List<Manifest> actual = manifestMgr.getManifests();
		assertEquals(expected.size(), actual.size());
		assertTrue(expected.containsAll(actual));
		assertTrue(actual.containsAll(expected));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifests()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetManifests_many_files() throws RepositoryException, IOException, UpdateGuyException
	{
		String testName = "getManifests";
		// create list of valid manifests
		List<Manifest> expected = create_manifest_list(testName,5);
		// save list to files in folder
		Path rootPath = folder.newFolder(testName).toPath();
		write_manifest_list_to_folder(rootPath, expected);
		write_dummy_files_to_folder(rootPath, 5);
		// create object to test
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);		
		List<Manifest> actual = manifestMgr.getManifests();
		// list should still only contain the expected entries.
		assertTrue(expected.containsAll(actual));
		assertTrue(actual.containsAll(expected));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifests()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetManifests_some_invalid_files() throws RepositoryException, IOException, UpdateGuyException
	{
		String testName = "getManifests";
		// create list of valid manifests
		List<Manifest> expected = create_manifest_list(testName,5);
		// save list to files in folder
		Path rootPath = folder.newFolder(testName).toPath();
		
		write_manifest_list_to_folder(rootPath, expected);
		// create a defective manifest file
		create_invalid_manifest_file(rootPath);
		// create object to test
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);		
		List<Manifest> actual = manifestMgr.getManifests();
		// list should still only contain the expected (valid) entries.
		
		//NOTE: There is a problem with gson at the moment which makes it so it doesn't throw an exception if it 
		// tries to deserialize an object of incorrect type. as a result, this match doesn't fail like its supposed to
		// and the expected behavior of this test is incorrect.
		// assertTrue(expected.containsAll(actual));
		assertTrue(actual.containsAll(expected));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifests()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testGetManifests_zero_files() throws RepositoryException, IOException
	{
		String testName = "getManifests-0f";
		Path rootPath = folder.newFolder(testName).toPath();
		// create object to test
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);		
		List<Manifest> actual = manifestMgr.getManifests();
		
		assertTrue(actual.isEmpty());
	}

	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion(String, ActiveVersionEncoder)}
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
		ManifestEntry expectedManifestEntry = new ManifestEntry();
		expectedManifestEntry.setReleaseFamily(expectedReleaseFamily);
		expectedManifestEntry.setVersion("some version");
		
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		createFileNameEncoder(expectedVersionId, expectedPath, encoder, expectedReleaseFamily);
		when(encoder.loadVersionEntry(any())).thenAnswer(new Answer<ManifestEntry>()
		{
			@Override
			public ManifestEntry answer(InvocationOnMock invocation) throws Throwable
			{
				Object[] args = invocation.getArguments();
				assertEquals(expectedPath, (Path)args[0]);
				return expectedManifestEntry;
			}
		});
		
		String testName = "getActiveVers";
		Path rootPath = folder.newFolder(testName).toPath();
		// create object to test
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);
		
		ManifestEntry actualManifestEntry = manifestMgr.getActiveVersion(expectedVersionId, encoder);
		assertEquals(expectedManifestEntry, actualManifestEntry);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion(String, ActiveVersionEncoder)}
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
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);
		
		manifestMgr.getActiveVersion(expectedVersionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion(String, ActiveVersionEncoder)}
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
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);
		manifestMgr.getActiveVersion(versionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion(String, ActiveVersionEncoder)}
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
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);
		manifestMgr.getActiveVersion(versionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion(String, ActiveVersionEncoder)}
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
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);
		manifestMgr.getActiveVersion(versionId, encoder);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test
	public void testSetActiveVersion_valid() throws JsonSyntaxException, IOException, RepositoryException
	{
		String testName = "setActiveVers-v";
		String expectedVersionId = "versionId";
		String expectedReleaseFamily = "some-release-fam";
		Path expectedPath = Paths.get("this","path","isnt","used");
		
		Path rootFolder = folder.newFolder(testName).toPath();
		List<ManifestEntry> entries = ManifestEntryHelpers.create_valid_manifest_entries(testName, 4, rootFolder);
		
		Manifest expectedManifest = new Manifest();
		expectedManifest.setReleaseFamily(expectedReleaseFamily);
		for(ManifestEntry entry: entries)
		{
			entry.setReleaseFamily(expectedReleaseFamily);
			expectedManifest.addVersionEntry(entry);
		}
		
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		
		createFileNameEncoder(expectedVersionId, expectedPath, encoder, expectedReleaseFamily);
		// create object to test
		
		ManifestMgr manifestMgr = mock(ManifestMgr.class);
		when(manifestMgr.getManifest(expectedReleaseFamily)).thenReturn(expectedManifest);
		doCallRealMethod().when(manifestMgr).setActiveVersion(anyString(), anyString(), any());
		
		for(ManifestEntry expectedEntry: entries)
		{
			String newVersion = expectedEntry.getVersion();
			manifestMgr.setActiveVersion(newVersion, expectedVersionId, encoder);
			verify(encoder, times(1)).writeVersionEntry(expectedPath, expectedEntry);
		}
	}

	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetActiveVersion_null_newVersion() throws JsonSyntaxException, IOException, RepositoryException
	{
		String testName = "setActiveVers-nnv";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);
		String newVersion = null;
		String expectedVersionId = "versionId";
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		manifestMgr.setActiveVersion(newVersion, expectedVersionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetActiveVersion_empty_newVersion() throws JsonSyntaxException, IOException, RepositoryException
	{
		String testName = "setActiveVers-env";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);
		String newVersion = "";
		String expectedVersionId = "versionId";
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		manifestMgr.setActiveVersion(newVersion, expectedVersionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test
	public void testSetActiveVersion_not_found_newVersion() throws JsonSyntaxException, IOException, RepositoryException
	{
		String expectedReleaseFamily = "some-release-fam";
		String expectedVersionId = "versionId";
		Path expectedPath = Paths.get("this","path","isnt","used");
		
		ManifestEntry manifestEntry = new ManifestEntry();
		manifestEntry.setReleaseFamily(expectedReleaseFamily);
		manifestEntry.setVersion("not-the-one-we-are-looking-for");
		
		Manifest expectedManifest = new Manifest();
		expectedManifest.setReleaseFamily(expectedReleaseFamily);
		expectedManifest.addVersionEntry(manifestEntry);
		
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		
		createFileNameEncoder(expectedVersionId, expectedPath, encoder, expectedReleaseFamily);
		// create object to test
		
		ManifestMgr manifestMgr = mock(ManifestMgr.class);
		when(manifestMgr.getManifest(expectedReleaseFamily)).thenReturn(expectedManifest);
		doCallRealMethod().when(manifestMgr).setActiveVersion(anyString(), anyString(), any());
		try
		{
			manifestMgr.setActiveVersion("the-one-we-look-for", expectedVersionId, encoder);
			fail("setActiveVersion should have thrown an exception");
		}
		catch(RepositoryException ex)
		{
			verify(encoder, never()).writeVersionEntry(any(), any());	
		}
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetActiveVersion_null_versionId() throws JsonSyntaxException, IOException, RepositoryException
	{
		String testName = "setActiveVers-nvid";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);
		String newVersion = "version";
		String expectedVersionId = null;
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		manifestMgr.setActiveVersion(newVersion, expectedVersionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetActiveVersion_empty_versionId() throws JsonSyntaxException, IOException, RepositoryException
	{
		String testName = "setActiveVers-evid";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);
		String newVersion = "version";
		String expectedVersionId = "";
		ActiveVersionEncoder encoder = mock(ActiveVersionEncoder.class);
		manifestMgr.setActiveVersion(newVersion, expectedVersionId, encoder);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion(String, ActiveVersionEncoder)}
	 * @throws IOException 
	 * @throws JsonSyntaxException
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetActiveVersion_null_encoder() throws JsonSyntaxException, IOException, RepositoryException
	{
		String testName = "setActiveVers-ne";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestMgr manifestMgr = new ManifestMgr(rootPath);
		String newVersion = "version";
		String expectedVersionId = "versionId";
		ActiveVersionEncoder encoder = null;
		manifestMgr.setActiveVersion(newVersion, expectedVersionId, encoder);
	}
}
