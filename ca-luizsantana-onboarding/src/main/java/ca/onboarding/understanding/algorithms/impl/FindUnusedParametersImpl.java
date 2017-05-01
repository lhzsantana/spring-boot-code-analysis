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
import ca.onboarding.understanding.algorithms.FindUnusedParameters;

/*
 * This algorithm uses a Understand database to find unused parameters. The general idea
 * is finding all the used parameters in methods. And later iterate over all the 
 * parameters to find the not used ones.
 * 
 * Refer to:
 * https://scitools.com/support/java-api/
 * 
 * Install this version:
 * http://builds.scitools.com/all_builds/b882/Understand/Understand-4.0.882-Windows-64bit.exe
 */

@Service
public class FindUnusedParametersImpl implements FindUnusedParameters {

	private static Logger logger = LoggerFactory.getLogger(FindUnusedParametersImpl.class);

	public List<Occurrence> findUnusedParameters(Database database) throws UnderstandException {
		
		List<Occurrence> occurrences = new ArrayList<Occurrence>();

		Entity[] methods = database.ents("method  ~unknown ~unresolved");

		Set<Entity> usedParameters = new HashSet<Entity>();

		for (Entity method : methods) {

			if (!method.longname(true).startsWith("java") && !method.longname(true).startsWith("sun")) {

				for (Reference reference : method.refs("set, use", "parameter", true)) {

					Entity parameter = reference.ent();
					
					usedParameters.add(parameter);
				}
			}

		}

		Entity[] parameters = database.ents("parameter ~unknown ~unresolved");

		for (Entity parameter : parameters) {

			if (!parameter.longname(true).startsWith("java") && !parameter.longname(true).startsWith("sun")) {

				if (!usedParameters.contains(parameter)) {
					
					Reference [] definition = parameter.refs(null, null, true);

					Occurrence occurrence = new Occurrence();
					occurrence.setFile(parameter.longname(true));
					occurrence.setLine(definition[0].line());
					occurrence.setColumn(definition[0].column());
					occurrence.setOccurrenceType(OccurrenceType.PARAMETER);
					
					occurrences.add(occurrence);

					logger.info("Unused parameter {} kind {} {}", parameter.longname(true), parameter.kind());
				}
			}
		}

		return occurrences;
	}
}
