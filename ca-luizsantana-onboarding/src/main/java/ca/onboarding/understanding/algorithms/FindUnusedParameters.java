package ca.onboarding.understanding.algorithms;

import java.util.List;

import com.scitools.understand.Database;
import com.scitools.understand.UnderstandException;

import ca.onboarding.deadcode.Occurrence;

public interface FindUnusedParameters {

	public List<Occurrence> findUnusedParameters(Database database) throws UnderstandException;
	
}
