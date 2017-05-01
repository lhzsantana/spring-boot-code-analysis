package ca.onboarding.git.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ca.onboarding.git.CodeRepositoryClient;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

@Service
public class GitConnector implements CodeRepositoryClient {

	private static Logger logger = LoggerFactory.getLogger(GitConnector.class);

	private final String ZIP_SUFIX = ".zip";
	private final String URL_ARCHIVE = "archive";
	private final String URL_SEPARATOR = "/";
	private final String DIRECTORY_SEPARATOR = "/";

	@Value("${repositories.path}")
	private String REPOSITORIES_PATH;

	@Value("${github.token}")
	private String GITHUB_TOKEN;

	@Value("${github.username}")
	private String GITHUB_USERNAME;

	@Override
	public void getCode(String owner, String repositoryName, String branchName) {

		logger.info("2 Getting code from repository {} and branch {} owned by {}", repositoryName, branchName, owner);
		
		try {
			
			URL repository = new URL(getRepositoryURL(owner, repositoryName, branchName));

			String filePath = REPOSITORIES_PATH + repositoryName + ZIP_SUFIX;

			logger.info("Unziping file {}", filePath);
			
			FileUtils.copyURLToFile(repository, new File(filePath));

			unzip(filePath, owner, repositoryName, branchName);

			logger.info("Unziped file {}", filePath);
			
			FileUtils.forceDelete(new File(filePath));
			
			logger.info("Getting code from repository {} / {} owned by {} successfully donwloaded", repositoryName, branchName, owner);
			
		} catch (Exception e) {
			logger.error("Error during getting the code from repository {} onwed by {}", repositoryName, owner, e);
		}
	}
	
	private String getRepositoryURL(String owner, String repositoryName, String branchName) throws IOException {

		logger.info("Accessing Github with for the user {} and token {}", GITHUB_USERNAME, GITHUB_TOKEN);
		
		RepositoryService service = new RepositoryService();

		service.getClient().setCredentials(GITHUB_USERNAME, GITHUB_TOKEN);

		Repository repository = service.getRepository(owner, repositoryName);
		
		if (!repository.getLanguage().toUpperCase().equals("JAVA")) {
			throw new RuntimeException("This repository code is not written in Java");
		}

		String repositoryURL = repository.getHtmlUrl()+URL_SEPARATOR+URL_ARCHIVE+URL_SEPARATOR+branchName+ZIP_SUFIX;
		
		logger.info("Repository URL is {}", repositoryURL);

		return repositoryURL;
	}
	
	private void unzip(String file, String owner, String repositoryName, String branchName) throws ZipException{

		logger.debug("Unziping file {}", file);

        String path = REPOSITORIES_PATH + owner + repositoryName + branchName + DIRECTORY_SEPARATOR;

		logger.debug("Trying to extract {}", path);
		
        ZipFile zipFile = new ZipFile(file);
        zipFile.extractAll(path);

		logger.debug("Extracted to {}", path);
        
	}
	
	public static void main(String arsg[]) throws ZipException{
		GitConnector GitHubConnector = new GitConnector();
		
		GitHubConnector.unzip("","","","");
	}
}
