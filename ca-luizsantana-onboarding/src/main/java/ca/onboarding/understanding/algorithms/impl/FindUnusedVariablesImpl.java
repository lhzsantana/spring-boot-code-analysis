package ca.onboarding.understanding.algorithms.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Reference;
import com.scitools.understand.UnderstandException;

import ca.onboarding.deadcode.Occurrence;
import ca.onboarding.deadcode.OccurrenceType;
import ca.onboarding.understanding.algorithms.FindUnusedVariables;

/*
 * This algorithm uses a Understand database to find unused variables. The general idea
 * is finding all the used variables in classes and methods. And later iterate over all the 
 * variables to find the not used ones.
 * 
 * 
 * Refer to:
 * https://scitools.com/support/java-api/
 * 
 * Install this version:
 * http://builds.scitools.com/all_builds/b882/Understand/Understand-4.0.882-Windows-64bit.exe
 * 
 * 
 * 
 */

@Service
public class FindUnusedVariablesImpl implements FindUnusedVariables {

	private static Logger logger = LoggerFactory.getLogger(FindUnusedVariablesImpl.class);

	public List<Occurrence> findUnusedVariables(Database database) throws UnderstandException {

		logger.info("Finding unused parameters for variables ");

		List<Occurrence> occurrences = new ArrayList<Occurrence>();

		Entity[] methodsClasses = database.ents("method class ~unknown ~unresolved");

		Set<Entity> usedVariables = new HashSet<Entity>();

		for (Entity methodClass : methodsClasses) {

			if (!methodClass.longname(true).startsWith("java") && !methodClass.longname(true).startsWith("sun")) {

				for (Reference reference : methodClass.refs("set, use", "variable", true)) {

					Entity variable = reference.ent();

					usedVariables.add(variable);
				}
			}

		}

		Entity[] variables = database.ents("variable ~unknown ~unresolved");

		for (Entity variable : variables) {

			if (!variable.longname(true).startsWith("java") && !variable.longname(true).startsWith("sun")) {

				if (!usedVariables.contains(variable) && !variable.kind().toString().toLowerCase().contains("public")) {
			
					Reference [] definition = variable.refs(null, null, true);

					Occurrence occurrence = new Occurrence();
					occurrence.setFile(variable.longname(true));
					occurrence.setLine(definition[0].line());
					occurrence.setColumn(definition[0].column());
					occurrence.setOccurrenceType(OccurrenceType.VARIABLE);
					
					occurrences.add(occurrence);

					logger.info("Unused variable {} kind {} {}", variable.longname(true), variable.kind());
				}
			}
		}
		return occurrences;
	}
}
