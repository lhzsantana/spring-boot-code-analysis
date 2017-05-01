package ca.onboarding.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.onboarding.deadcode.RepositoryAnalysis;
import ca.onboarding.deadcode.RepositoryModel;
import ca.onboarding.deadcode.RepositoryStatus;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class ServicesController {

	private static Logger logger = LoggerFactory.getLogger(ServicesController.class);

	private static List<RepositoryAnalysis> all = new ArrayList<RepositoryAnalysis>();
	private static Map<String, RepositoryAnalysis> runningControl = new HashMap<String, RepositoryAnalysis>();
	private static Map<String, RepositoryAnalysis> history = new HashMap<String, RepositoryAnalysis>();

	@Autowired
	private AsyncAnalyzer asyncAnalyzer;

	/**
	 * 
	 * 
	 * @param newName
	 *            This Student's new name. Should include both first and last
	 *            name.
	 * @return the repository analysis including its UUID.
	 */
	@ApiOperation(value = "Triggers a new repository analysis.", notes = "If an analysis is still running, this previously added repository (including its UUID) will be returned;", response = RepositoryModel.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "repositoryName", value = "The repository name for analysis", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "branchName", value = "The branch name for analysis", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "owner", value = "The owner of the repository", required = true, dataType = "string", paramType = "query") })
	@RequestMapping(value = "/repository", method = RequestMethod.POST)
	public ResponseEntity<RepositoryAnalysis> addRepository(@ModelAttribute RepositoryModel repositoryModel) {

		logger.debug("Adding repository {}/{} of {}", repositoryModel.getOwner(), repositoryModel.getRepositoryName(),
				repositoryModel.getOwner());

		RepositoryAnalysis runningRepositoryAnalysis = runningControl.get(getShortName(repositoryModel));

		if (runningRepositoryAnalysis != null && (runningRepositoryAnalysis.getStatus().equals(RepositoryStatus.ADDED)
				|| runningRepositoryAnalysis.getStatus().equals(RepositoryStatus.PROCESSING))) {

			logger.info("The last analysis for the repository {}-{} is still running", repositoryModel.toString());

			return ResponseEntity.status(HttpStatus.OK).body(runningRepositoryAnalysis);
		} else {

			RepositoryAnalysis repositoryAnalysis = new RepositoryAnalysis(repositoryModel);

			history.put(repositoryAnalysis.getId(), repositoryAnalysis);
			runningControl.put(getShortName(repositoryModel), repositoryAnalysis);
			all.add(repositoryAnalysis);

			asyncAnalyzer.createAnalysisTask(repositoryModel.getOwner(), repositoryModel.getRepositoryName(),
					repositoryModel.getBranchName(), repositoryAnalysis);

			if (repositoryAnalysis.getStatus().equals(RepositoryStatus.FAILED)) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(repositoryAnalysis);
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(repositoryAnalysis);
			}
		}

	}

	@RequestMapping(value = "/repository/{page}/{pageSize}", method = RequestMethod.GET)
	@ApiOperation(value = "List all analysis for all repositories.", notes = "Can include multiple analysis for the same repository", response = RepositoryAnalysis.class)
	public @ResponseBody ResponseEntity<Collection<RepositoryAnalysis>> listRepositories(
			@ApiParam(value = "The size number of repositories analysis per page (values range starts in 1). If this value is bigger than the number of pages, it will be returned the first page.", required = true) @PathVariable("page") Integer page,
			@ApiParam(value = "The size for each page. If less than 1 or bigger than the number of results, it will be default to 20.", required = true) @PathVariable("pageSize") Integer pageSize) {
		
		if(all.size()==0){
			return ResponseEntity.status(HttpStatus.OK).body(all);
		}
		
		if(pageSize<1){
			logger.info("Page size less than 1, defaulting to 20");
			pageSize=20;
		}
		
		if(pageSize>all.size()){
			logger.info("Page size bigger that the number of registers, defaulting to the total number of registers");
			pageSize=all.size();
		}
		
		List<List<RepositoryAnalysis>> paginated = getPages(all, pageSize);  
		
		if(page<1){
			logger.info("Page less than 1, defaulting to 1");
			page=1;
		}
		
		if(page>paginated.size()){
			logger.info("Page less than the number of pages, defaulting to the total number of pages");
			page=paginated.size();
		}

		logger.info("All size {}",all.size());
		logger.info("Paginated size {}",paginated.size());
		
		logger.info("Listing all respositories");
		return ResponseEntity.status(HttpStatus.OK).body(paginated.get(page-1));
	}

	@RequestMapping(value = "/analyze/{uuid}", method = RequestMethod.GET)
	@ApiOperation(value = "Return the analysis by uuid.", response = RepositoryAnalysis.class)
	public @ResponseBody ResponseEntity<RepositoryAnalysis> getAnalyze(
			@ApiParam(value = "The UUID for the analysis", required = true) @PathVariable("uuid") String uuid) {

		logger.info("Getting deadcode analysis {}", uuid);

		RepositoryAnalysis runningRepositoryAnalysis = history.get(uuid);

		if (runningRepositoryAnalysis != null) {
			return ResponseEntity.status(HttpStatus.OK).body(runningRepositoryAnalysis);
		}

		return new ResponseEntity<RepositoryAnalysis>(HttpStatus.INTERNAL_SERVER_ERROR);

	}

	private String getShortName(RepositoryModel repositoryModel) {
		return repositoryModel.getOwner() + repositoryModel.getRepositoryName() + repositoryModel.getBranchName();
	}

	public static <T> List<List<T>> getPages(Collection<T> c, Integer pageSize) {
		if (c == null)
			return Collections.emptyList();
		List<T> list = new ArrayList<T>(c);
		if (pageSize == null || pageSize <= 0 || pageSize > list.size())
			pageSize = list.size();
		int numPages = (int) Math.ceil((double) list.size() / (double) pageSize);
		List<List<T>> pages = new ArrayList<List<T>>(numPages);
		for (int pageNum = 0; pageNum < numPages;)
			pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
		return pages;
	}
}