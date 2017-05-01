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
import ca.onboarding.understanding.algorithms.FindUnusedMethods;

/*
 *
 * This algorithm uses a Understand database to find unused methods. The general idea
 * is finding all the used methods in methods (including recursive cases) and classes. And later 
 * iterate over all the methods to find the not used ones.
 * 
 * Refer to:
 * https://scitools.com/support/java-api/
 * 
 * Install this version:
 * http://builds.scitools.com/all_builds/b882/Understand/Understand-4.0.882-Windows-64bit.exe
 */

@Service
public class FindUnusedMethodsImpl implements FindUnusedMethods  {

	private static Logger logger = LoggerFactory.getLogger(FindUnusedMethodsImpl.class);

	public List<Occurrence> findUnusedMethods(Database database) throws UnderstandException {

		logger.info("Finding unused methods");

		List<Occurrence> occurrences = new ArrayList<Occurrence>();

		Entity[] methodsClasses = database.ents("method class ~unknown ~unresolved");

		Set<Entity> usedMethods = new HashSet<Entity>();

		for (Entity methodClass : methodsClasses) {

			if (!methodClass.longname(true).startsWith("java") && !methodClass.longname(true).startsWith("sun")) {

				for (Reference reference : methodClass.refs("call", "method  ~unknown ~unresolved", true)) {

					Entity method = reference.ent();

					usedMethods.add(method);
				}
			}
		}

		Entity[] methods = database.ents("private method ~unknown ~unresolved");

		for (Entity method : methods) {

			if (!method.longname(true).startsWith("java") && !method.longname(true).startsWith("sun")) {

				if (!usedMethods.contains(method)) {
					
					Reference [] definition = method.refs(null, null, true);

					Occurrence occurrence = new Occurrence();
					occurrence.setFile(method.longname(true));
					occurrence.setLine(definition[0].line());
					occurrence.setColumn(definition[0].column());
					occurrence.setOccurrenceType(OccurrenceType.METHOD);
					
					occurrences.add(occurrence);

					logger.info("Unused method {} ", method.longname(true));
				}
			}
		}

		return occurrences;
	}
}
