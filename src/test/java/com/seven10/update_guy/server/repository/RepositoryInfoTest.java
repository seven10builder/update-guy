/**
 * 
 */
package com.seven10.update_guy.server.repository;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.seven10.update_guy.common.RepoInfoHelpers;
import com.seven10.update_guy.server.exceptions.RepositoryException;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType;

/**
 * @author kmm
 *
 */
public class RepositoryInfoTest
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
		
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#equals(java.lang.Object)}.
	 * @throws IOException 
	 */
	@Test
	public void testEqualsObject_self() throws IOException
	{
		String testName = "testeEquals-self";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		assertTrue(repo1.equals(repo1));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#equals(java.lang.Object)}.
	 * @throws IOException 
	 */
	@Test
	public void testEqualsObject_clone() throws IOException
	{
		String testName = "testeEquals-clone";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		assertTrue(repo1.equals(repo2));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#equals(java.lang.Object)}.
	 * @throws IOException 
	 */
	@Test
	public void testEqualsObject_diff_obj_same_type() throws IOException
	{
		String testName = "testeEquals-do-st";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		List<RepositoryInfo> repoList = RepoInfoHelpers.load_repos_from_file(repoPath);
		RepositoryInfo repo1 = repoList.get(0);
		RepositoryInfo repo2 = repoList.get(1);
		assertFalse(repo1.equals(repo2));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#equals(java.lang.Object)}.
	 * @throws IOException 
	 */
	@Test
	public void testEqualsObject_diff_obj_diff_type() throws IOException
	{
		String testName = "testeEquals-do-dt";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		List<RepositoryInfo> repoList = RepoInfoHelpers.load_repos_from_file(repoPath);
		RepositoryInfo repo1 = repoList.get(0);
		RepositoryInfoTest repo2 = new RepositoryInfoTest();
		assertFalse(repo1.equals(repo2));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#equals(java.lang.Object)}.
	 * @throws IOException 
	 */
	@Test
	public void testEqualsObject_null() throws IOException
	{
		String testName = "testeEquals-do-dt";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		List<RepositoryInfo> repoList = RepoInfoHelpers.load_repos_from_file(repoPath);
		RepositoryInfo repo1 = repoList.get(0);
		RepositoryInfo repo2 = null;
		assertFalse(repo1.equals(repo2));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#equals(java.lang.Object)}.
	 * @throws IOException 
	 */
	@Test
	public void testEqualsObject_description() throws IOException
	{
		String testName = "testeEquals-do-dt";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		repo2.description = "new description";
		assertFalse(repo1.equals(repo2));
		repo2.description = repo1.description;
		assertTrue(repo1.equals(repo2));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#equals(java.lang.Object)}.
	 * @throws IOException 
	 */
	@Test
	public void testEqualsObject_manifestPath() throws IOException
	{
		String testName = "testeEquals-do-dt";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		repo2.manifestPath = Paths.get("new","cache","path").toString();
		assertFalse(repo1.equals(repo2));
		repo2.manifestPath = repo1.manifestPath;
		assertTrue(repo1.equals(repo2));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#equals(java.lang.Object)}.
	 * @throws IOException 
	 */
	@Test
	public void testEqualsObject_password() throws IOException
	{
		String testName = "testeEquals-do-dt";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		repo2.password = "new password";
		assertFalse(repo1.equals(repo2));
		repo2.password = repo1.password;
		assertTrue(repo1.equals(repo2));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#equals(java.lang.Object)}.
	 * @throws IOException 
	 */
	@Test
	public void testEqualsObject_port() throws IOException
	{
		String testName = "testeEquals-do-dt";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		repo2.port = (repo2.port + 1) * 57;
		assertFalse(repo1.equals(repo2));
		repo2.port = repo1.port;
		assertTrue(repo1.equals(repo2));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#equals(java.lang.Object)}.
	 * @throws IOException 
	 */
	@Test
	public void testEqualsObject_repoAddress() throws IOException
	{
		String testName = "testeEquals-do-dt";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		repo2.repoAddress = "new address";
		assertFalse(repo1.equals(repo2));
		repo2.repoAddress = repo1.repoAddress;
		assertTrue(repo1.equals(repo2));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#equals(java.lang.Object)}.
	 * @throws IOException 
	 */
	@Test
	public void testEqualsObject_repoType() throws IOException
	{
		String testName = "testeEquals-do-dt";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		repo2.repoType = (repo2.repoType == RepositoryType.ftp) ? RepositoryType.local : RepositoryType.ftp;
		assertFalse(repo1.equals(repo2));
		repo2.repoType = repo1.repoType;
		assertTrue(repo1.equals(repo2));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#equals(java.lang.Object)}.
	 * @throws IOException 
	 */
	@Test
	public void testEqualsObject_user() throws IOException
	{
		String testName = "testeEquals-do-dt";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		repo2.user = "new user";
		assertFalse(repo1.equals(repo2));
		repo2.user = repo1.user;
		assertTrue(repo1.equals(repo2));
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#getShaHash()}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetShaHash_indempotent() throws IOException, RepositoryException
	{
		String testName = "testShaHash";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		String firstSha = repo1.getShaHash();
		String secondSha = repo1.getShaHash();
		assertEquals(firstSha, secondSha);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#getShaHash()}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetShaHash_description() throws IOException, RepositoryException
	{
		String testName = "testShaHash_desc";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		String firstSha = repo1.getShaHash();
		repo2.description = "new desc";
		String secondSha = repo2.getShaHash();
		assertNotEquals(firstSha, secondSha);
		repo2.description = repo1.description;
		secondSha = repo2.getShaHash();
		assertEquals(firstSha, secondSha);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#getShaHash()}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetShaHash_manifestPath() throws IOException, RepositoryException
	{
		String testName = "testShaHash_mp";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		String firstSha = repo1.getShaHash();
		repo2.manifestPath = Paths.get("diff","path").toString();
		String secondSha = repo2.getShaHash();
		assertNotEquals(firstSha, secondSha);
		repo2.manifestPath = repo1.manifestPath;
		secondSha = repo2.getShaHash();
		assertEquals(firstSha, secondSha);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#getShaHash()}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetShaHash_password() throws IOException, RepositoryException
	{
		String testName = "testShaHash_pass";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		String firstSha = repo1.getShaHash();
		repo2.password = "new password";
		String secondSha = repo2.getShaHash();
		assertNotEquals(firstSha, secondSha);
		repo2.password = repo1.password;
		secondSha = repo2.getShaHash();
		assertEquals(firstSha, secondSha);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#getShaHash()}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetShaHash_user() throws IOException, RepositoryException
	{
		String testName = "testShaHash_u";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		String firstSha = repo1.getShaHash();
		repo2.user = "new user";
		String secondSha = repo2.getShaHash();
		assertNotEquals(firstSha, secondSha);
		repo2.user = repo1.user;
		secondSha = repo2.getShaHash();
		assertEquals(firstSha, secondSha);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#getShaHash()}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetShaHash_port() throws IOException, RepositoryException
	{
		String testName = "testShaHash_port";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		String firstSha = repo1.getShaHash();
		repo2.port = (repo2.port + 1) * 31;
		String secondSha = repo2.getShaHash();
		assertNotEquals(firstSha, secondSha);
		repo2.port = repo1.port;
		secondSha = repo2.getShaHash();
		assertEquals(firstSha, secondSha);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#getShaHash()}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetShaHash_address() throws IOException, RepositoryException
	{
		String testName = "testShaHash_add";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		String firstSha = repo1.getShaHash();
		repo2.repoAddress = "new address";
		String secondSha = repo2.getShaHash();
		assertNotEquals(firstSha, secondSha);
		repo2.repoAddress = repo1.repoAddress;
		secondSha = repo2.getShaHash();
		assertEquals(firstSha, secondSha);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfo#getShaHash()}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetShaHash_repoType() throws IOException, RepositoryException
	{
		String testName = "testShaHash_rt";
		Path repoPath = RepoInfoHelpers.build_repo_info_file_by_testname(testName, folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoPath);
		RepositoryInfo repo1 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		RepositoryInfo repo2 = RepoInfoHelpers.load_repos_from_file(repoPath).get(0);
		String firstSha = repo1.getShaHash();
		repo2.repoType = (repo2.repoType == RepositoryType.ftp) ? RepositoryType.local : RepositoryType.ftp;
		String secondSha = repo2.getShaHash();
		assertNotEquals(firstSha, secondSha);
		repo2.repoType = repo1.repoType;
		secondSha = repo2.getShaHash();
		assertEquals(firstSha, secondSha);
	}
	
}
