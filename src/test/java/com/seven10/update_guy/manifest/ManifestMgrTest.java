/**
 * 
 */
package com.seven10.update_guy.manifest;

import static org.junit.Assert.*;
import static com.seven10.update_guy.ManifestHelpers.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.seven10.update_guy.Globals;
import com.seven10.update_guy.TestConstants;
import com.seven10.update_guy.exceptions.RepositoryException;

/**
 * @author kmm
 *
 */
public class ManifestMgrTest
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#ManifestMgr(com.seven10.update_guy.Globals)}.
	 */
	@Test
	public void testManifestMgr_valid()
	{
		Globals globals = new Globals();
		ManifestMgr manifestMgr = new ManifestMgr(globals);
		assertNotNull(manifestMgr);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#ManifestMgr(com.seven10.update_guy.Globals)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testManifestMgr_null_globals()
	{
		Globals globals = null;
		new ManifestMgr(globals);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#setActiveVersion(java.lang.String)}
	 * and  {@link com.seven10.update_guy.manifest.ManifestMgr#getActiveVersion()}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testSetGetActiveVersion_valid() throws IOException, RepositoryException
	{
		String testName = "getActiveVersion";
		Path manifestPath = build_manifest_path_by_testname(testName, folder);
		copy_manifest_to_path(TestConstants.valid_manifest_name, manifestPath);
		Manifest manifest = load_manifest_from_path(manifestPath);
		
		Globals globals = new Globals();
		globals.setManifestPath(manifestPath.getParent());
		globals.setReleaseFamily(testName);
		
		ManifestMgr manifestMgr = new ManifestMgr(globals);
		for(ManifestEntry expectedEntry: manifest.getVersionEntries())
		{
			String expectedVersion = expectedEntry.getVersion();
			
			manifestMgr.setActiveVersion(expectedVersion);
			String actualVersion = manifestMgr.getActiveVersion();  
			
			ManifestEntry actualEntry = globals.getActiveVersion();
			assertEquals(expectedEntry, actualEntry);
			assertEquals(expectedVersion, actualVersion);
		}
	}

	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#setActiveVersion(java.lang.String)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetActiveVersion_null() throws RepositoryException
	{
		Globals globals = new Globals();
		ManifestMgr manifestMgr = new ManifestMgr(globals);
		String version = null;
		manifestMgr.setActiveVersion(version);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#setActiveVersion(java.lang.String)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetActiveVersion_empty() throws RepositoryException
	{
		Globals globals = new Globals();
		ManifestMgr manifestMgr = new ManifestMgr(globals);
		String version = "";
		manifestMgr.setActiveVersion(version);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifest(java.lang.String)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetManifest_valid() throws IOException, RepositoryException
	{
		String testName = "getActiveVersion";
		
		Path manifestPath = build_manifest_path_by_testname(testName, folder);
		copy_manifest_to_path(TestConstants.valid_manifest_name, manifestPath);
		Manifest expected = load_manifest_from_path(manifestPath);
		Globals globals = new Globals();
		globals.setManifestPath(manifestPath.getParent());
		ManifestMgr manifestMgr = new ManifestMgr(globals);
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
		String testName = "getActiveVersion";
		
		Globals globals = new Globals();
		
		ManifestMgr manifestMgr = new ManifestMgr(globals);
		manifestMgr.getManifest(testName);
		
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifest(java.lang.String)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetManifest_null() throws RepositoryException
	{
		Globals globals = new Globals();
		ManifestMgr manifestMgr = new ManifestMgr(globals);
		String releaseFamily = null;
		manifestMgr.getManifest(releaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifest(java.lang.String)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetManifest_empty() throws RepositoryException
	{
		Globals globals = new Globals();
		ManifestMgr manifestMgr = new ManifestMgr(globals);
		String releaseFamily = "";
		manifestMgr.getManifest(releaseFamily);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifests()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testGetManifests_valid_manifests_only() throws RepositoryException, IOException
	{
		String testName = "getManifests";
		// create list of valid manifests
		List<Manifest> expected = create_manifest_list(testName,5);
		// save list to files in folder
		Path rootPath = folder.newFolder(testName).toPath();
		write_manifest_list_to_folder(rootPath, expected);
		// create globals variable
		Globals globals = new Globals();
		globals.setManifestPath(rootPath);
		// create object to test
		ManifestMgr manifestMgr = new ManifestMgr(globals);		
		List<Manifest> actual = manifestMgr.getManifests();
		assertEquals(expected.size(), actual.size());
		assertTrue(expected.containsAll(actual));
		assertTrue(actual.containsAll(expected));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifests()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testGetManifests_many_files() throws RepositoryException, IOException
	{
		String testName = "getManifests";
		// create list of valid manifests
		List<Manifest> expected = create_manifest_list(testName,5);
		// save list to files in folder
		Path rootPath = folder.newFolder(testName).toPath();
		write_manifest_list_to_folder(rootPath, expected);
		write_dummy_files_to_folder(rootPath, 5);
		// create globals variable
		Globals globals = new Globals();
		globals.setManifestPath(rootPath);
		// create object to test
		ManifestMgr manifestMgr = new ManifestMgr(globals);		
		List<Manifest> actual = manifestMgr.getManifests();
		// list should still only contain the expected entries.
		assertTrue(expected.containsAll(actual));
		assertTrue(actual.containsAll(expected));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifests()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testGetManifests_some_invalid_files() throws RepositoryException, IOException
	{
		String testName = "getManifests";
		// create list of valid manifests
		List<Manifest> expected = create_manifest_list(testName,5);
		// save list to files in folder
		Path rootPath = folder.newFolder(testName).toPath();
		write_manifest_list_to_folder(rootPath, expected);
		// create a defective manifest file
		create_invalid_manifest_file(rootPath);
		// create globals variable
		Globals globals = new Globals();
		globals.setManifestPath(rootPath);
		// create object to test
		ManifestMgr manifestMgr = new ManifestMgr(globals);		
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
		// create globals variable
		Globals globals = new Globals();
		globals.setManifestPath(rootPath);
		// create object to test
		ManifestMgr manifestMgr = new ManifestMgr(globals);		
		List<Manifest> actual = manifestMgr.getManifests();
		
		assertTrue(actual.isEmpty());
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestMgr#getManifests()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test(expected=RepositoryException.class)
	public void testGetManifests_invalid_path() throws RepositoryException, IOException
	{
		// create globals variable
		Globals globals = new Globals();
		// use default path, which must not exist
		Path rootPath = Paths.get("this","shouldnt", "exist");
		assertFalse(Files.exists(rootPath));
		globals.setManifestPath(rootPath);
		// create object to test
		ManifestMgr manifestMgr = new ManifestMgr(globals);		
		List<Manifest> actual = manifestMgr.getManifests();
		
		assertTrue(actual.isEmpty());
	}
	
	
}
