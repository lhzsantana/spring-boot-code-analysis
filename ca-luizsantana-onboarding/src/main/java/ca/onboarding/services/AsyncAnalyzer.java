package ca.onboarding.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ca.onboarding.deadcode.DeadCodeAnalyzer;
import ca.onboarding.deadcode.RepositoryAnalysis;
import ca.onboarding.git.CodeRepositoryClient;

@Service
public class AsyncAnalyzer {

	@Autowired
	private DeadCodeAnalyzer deadCodeAnalyzer;

	@Autowired
	private CodeRepositoryClient gitClient;
	
	@Async
	public void createAnalysisTask(String owner, String repositoryName, String branch, RepositoryAnalysis repositoryAnalysis) {

		gitClient.getCode(owner, repositoryName, branch);

		deadCodeAnalyzer.analyze(repositoryAnalysis);
	}

}
