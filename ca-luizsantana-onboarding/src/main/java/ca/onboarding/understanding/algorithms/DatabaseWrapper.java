package ca.onboarding.understanding.algorithms;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scitools.understand.Database;
import com.scitools.understand.Understand;
import com.scitools.understand.UnderstandException;

import ca.onboarding.deadcode.impl.DeadCodeAnalyzerImpl;

public class DatabaseWrapper {

	private static Logger logger = LoggerFactory.getLogger(DatabaseWrapper.class);
	
	private static Map<String, Database> databases = new HashMap<String, Database> ();
	
	public static Database open(String repositoryDatabase) throws UnderstandException{
		
		logger.info("Openning the repository {}", repositoryDatabase);
		
		Database database = Understand.open(repositoryDatabase);
		
		databases.put(repositoryDatabase, database);
		
		return database;
	}

	public static void close(String repositoryDatabase) throws UnderstandException{

		logger.info("Closing the repository {}", repositoryDatabase);
				
		databases.get(repositoryDatabase).close();
	}
	
}
