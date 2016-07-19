/**
 * 
 */
package com.seven10.update_guy.server.manifest;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.seven10.update_guy.common.ManifestEntryHelpers;
import com.seven10.update_guy.common.manifest.ManifestEntry;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.manifest.ActiveVersionEncoder;

/**
 * @author kmm
 *
 */
public class ActiveVersionEncoderTest
{
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#ActiveVersionEncoder(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testActiveVersionEncoder_valid()
	{
		String expectedRepoId = "repoId12345";
		String expectedReleaseFamily = "releaseFamily54321";
		ActiveVersionEncoder encoder = new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);
		assertNotNull(encoder);
		assertEquals(expectedRepoId, encoder.getRepoId());
		assertEquals(expectedReleaseFamily, encoder.getReleaseFamily());
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#ActiveVersionEncoder(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testActiveVersionEncoder_nullRepoId()
	{
		String expectedRepoId = null;
		String expectedReleaseFamily = "releaseFamily54321";
		new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);

	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#ActiveVersionEncoder(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testActiveVersionEncoder_empty_repoId()
	{
		String expectedRepoId = "";
		String expectedReleaseFamily = "releaseFamily54321";
		new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#ActiveVersionEncoder(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testActiveVersionEncoder_null_releaseFamily()
	{
		String expectedRepoId = "repoId12345";
		String expectedReleaseFamily = null;
		new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#ActiveVersionEncoder(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testActiveVersionEncoder_empty_releaseFamily()
	{
		String expectedRepoId = "repoId12345";
		String expectedReleaseFamily = "";
		new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#encodeFileName(java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testEncodeFileName_valid() throws IOException
	{
		
		Path rootFolder = folder.newFolder().toPath();
		System.setProperty(ServerGlobals.SETTING_LOCAL_PATH, rootFolder.toString());
		
		String expectedRepoId = "repoId";
		String expectedReleaseFamily = "releaseFamily";
		String expectedVersionId = "versionId";
		Path expectedPath = rootFolder
				.resolve("local")
				.resolve(ServerGlobals.ACTIVE_VERSIONS_FOLDER_NAME)
				.resolve(expectedRepoId)
				.resolve(expectedReleaseFamily)
				.resolve(expectedVersionId + ".json");
		
		ActiveVersionEncoder encoder = new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);
		Path actualPath = encoder.encodeFileName(expectedVersionId);
		assertEquals(expectedPath, actualPath);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#encodeFileName(java.lang.String)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testEncodeFileName_null_versionId() throws IOException
	{
		String expectedRepoId = "repoId";
		String expectedReleaseFamily = "releaseFamily";
		String expectedVersionId = null;
		
		ActiveVersionEncoder encoder = new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);
		encoder.encodeFileName(expectedVersionId);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#encodeFileName(java.lang.String)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testEncodeFileName_empty_versionId() throws IOException
	{
		String expectedRepoId = "repoId";
		String expectedReleaseFamily = "releaseFamily";
		String expectedVersionId = "";
		
		ActiveVersionEncoder encoder = new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);
		encoder.encodeFileName(expectedVersionId);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#loadVersionEntry(java.nio.file.Path)}
	 * and {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#writeVersionEntry(java.nio.file.Path, com.seven10.update_guy.common.manifest.ManifestEntry)}.
	 * @throws IOException 
	 */
	@Test
	public void testLoadAndWriteVersionEntry() throws IOException
	{
		String testName = "loadAndWrite";
		Path rootFolder = folder.newFolder(testName).toPath();
		Path testFile = rootFolder.resolve(testName+".json");
		
		List<ManifestEntry> entries = ManifestEntryHelpers.create_valid_manifest_entries(testName, 3, rootFolder);
		// the first time through the file is created because it doesn't exist, after the file is reused
		
		String expectedRepoId = "repoId";
		String expectedReleaseFamily = "releaseFamily";
		ActiveVersionEncoder encoder = new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);
		for(ManifestEntry expectedEntry: entries)
		{
			encoder.writeVersionEntry(testFile, expectedEntry);
			ManifestEntry actualEntry = encoder.loadVersionEntry(testFile);
			assertEquals(expectedEntry, actualEntry);
		}
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#loadVersionEntry(java.nio.file.Path)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testLoadVersionEntry_null_path() throws IOException
	{
		String expectedRepoId = "repoId";
		String expectedReleaseFamily = "releaseFamily";
		ActiveVersionEncoder encoder = new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);
		Path testFile = null;
		encoder.loadVersionEntry(testFile);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#loadVersionEntry(java.nio.file.Path)}.
	 * @throws IOException 
	 */
	@Test(expected=FileNotFoundException.class)
	public void testLoadVersionEntry_path_not_found() throws IOException
	{
		Path testFile = Paths.get("some", "nonextant", "path.json");
		
		String expectedRepoId = "repoId";
		String expectedReleaseFamily = "releaseFamily";
		ActiveVersionEncoder encoder = new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);
		encoder.loadVersionEntry(testFile);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#writeVersionEntry(java.nio.file.Path, com.seven10.update_guy.common.manifest.ManifestEntry)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testWriteVersionEntry_null_path() throws IOException
	{
		Path testFile = null;
		
		String expectedRepoId = "repoId";
		String expectedReleaseFamily = "releaseFamily";
		ManifestEntry expectedEntry = new ManifestEntry();
		ActiveVersionEncoder encoder = new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);
		
		encoder.writeVersionEntry(testFile, expectedEntry);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ActiveVersionEncoder#writeVersionEntry(java.nio.file.Path, com.seven10.update_guy.common.manifest.ManifestEntry)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testWriteVersionEntry_null_entry() throws IOException
	{
		String testName = "writeVersion_ne";
		Path testFile = folder.newFolder(testName).toPath().resolve(testName + ".json");
		String expectedRepoId = "repoId";
		String expectedReleaseFamily = "releaseFamily";
		ManifestEntry expectedEntry = null;
		ActiveVersionEncoder encoder = new ActiveVersionEncoder(expectedRepoId, expectedReleaseFamily);
		
		encoder.writeVersionEntry(testFile, expectedEntry);
	}
	
}
