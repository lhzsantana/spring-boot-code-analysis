package ca.onboarding.understanding.und.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

import ca.onboarding.understanding.und.Understanding;

@Service
public class UndWrapper implements Understanding {

	private static Logger logger = LoggerFactory.getLogger(UndWrapper.class);

	@Value("${repositories.path}")
	private String REPOSITORIES_PATH;

	@Value("${understanding.path}")
	private String UNDERSTANDING_PATH;

	@Value("${understanding.output}")
	private String UNDERSTANDING_OUTPUT;

	@Value("${understanding.folderseparator}")
	private String UNDERSTANDING_FOLDER_SEPARATOR;

	private final String UNDERSTANDING_CREATE_DB = "und create -languages java ";

	private final String UNDERSTANDING_ADD_FILES = "und add ";

	private final String UNDERSTANDING_ANALYZE = "und analyze ";

	private final String DB_SUFIX = ".udb";

	public String runUnderstanding(String repository) throws Exception {

		logger.debug("Running understanding for the repository {}", repository);

		UUID uuid = UUID.randomUUID();
		String UDBpath = UNDERSTANDING_OUTPUT + repository + uuid.toString() + DB_SUFIX;

		try {

			runCommand(UNDERSTANDING_PATH + UNDERSTANDING_CREATE_DB + UDBpath);
			runCommand(UNDERSTANDING_PATH + UNDERSTANDING_ADD_FILES + " " + REPOSITORIES_PATH + repository
					+ UNDERSTANDING_FOLDER_SEPARATOR + " " + UDBpath);
			runCommand(UNDERSTANDING_PATH + UNDERSTANDING_ANALYZE + UDBpath);


			logger.info("Finishing runUnderstanding");
		} catch (Exception e) {

			logger.info("Error while running command {}", e);
			throw new RuntimeException("Error during running Understanding with for repository " + repository, e);
		}

		logger.info("Returning UDBpath");
		return UDBpath;
	}

	private void runCommand(String command) throws IOException {
		
		logger.info("Running command: {}", command);
		
		Process proc = Runtime.getRuntime().exec(command);

		boolean error = false;

		logger.info("Checking for successes");

		try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				logger.info("Success response {}", s);
				
				if(s.contains("Analyze Completed")){
					
					logger.info("Breaking analyzing");
					break;
				}
			}
		}

		logger.info("Checking for errors");
		
		try (BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()))) {
			
			String s = null;
			while ((s = stdError.readLine()) != null) {
				logger.error("Error command {}", s);
				error=true;
			}

			if (error) {
				throw new RuntimeException("Error during command " + command + " execution");
			}
		}

		logger.info("Finishing runCommand");
		
	}
}
