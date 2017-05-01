package ca.onboarding.deadcode.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scitools.understand.Database;
import com.scitools.understand.Understand;

import ca.onboarding.deadcode.DeadCodeAnalyzer;
import ca.onboarding.deadcode.Occurrence;
import ca.onboarding.deadcode.RepositoryAnalysis;
import ca.onboarding.deadcode.RepositoryStatus;
import ca.onboarding.understanding.algorithms.DatabaseWrapper;
import ca.onboarding.understanding.algorithms.FindUnusedMethods;
import ca.onboarding.understanding.algorithms.FindUnusedParameters;
import ca.onboarding.understanding.algorithms.FindUnusedVariables;
import ca.onboarding.understanding.und.Understanding;

@Service
public class DeadCodeAnalyzerImpl implements DeadCodeAnalyzer {

	private static Logger logger = LoggerFactory.getLogger(DeadCodeAnalyzerImpl.class);

	@Autowired
	private Understanding understanding;

	@Autowired
	private FindUnusedMethods findUnusedMethods;

	@Autowired
	private FindUnusedParameters findUnusedParameters;

	@Autowired
	private FindUnusedVariables findUnusedVariables;

	@Override
	public void analyze(RepositoryAnalysis repository) {

		logger.debug("Analyzing dead code for repository {}", repository.getName());

		repository.setStatus(RepositoryStatus.PROCESSING);

		List<Occurrence> occurrences = new ArrayList<Occurrence>();

		try {

			logger.info("Analyzing dead code for repository {}", repository.getName());

			String repositoryDatabase = understanding.runUnderstanding(repository.getOwner() + repository.getName() + repository.getBranch());

			Database database = DatabaseWrapper.open(repositoryDatabase);

			logger.info("Running algorithms {}", repository.getName());

			occurrences.addAll(findUnusedMethods.findUnusedMethods(database));
			occurrences.addAll(findUnusedParameters.findUnusedParameters(database));
			occurrences.addAll(findUnusedVariables.findUnusedVariables(database));

			repository.finish(occurrences);
			
			 DatabaseWrapper.close(repositoryDatabase);

		} catch (Exception e) {
			logger.error("An error occurred while analyzing {}/{}", repository.getOwner(), repository.getName(), e);

			repository.setStatus(RepositoryStatus.FAILED);
		}

		logger.info("Finished the analysis of repository {}/{}", repository.getOwner(), repository.getName());
	}
}
