package ca.onboarding.deadcode;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RepositoryAnalysis {
	
	private Date begin;
	
	private Date finished;
	
	private String name;
	
	private String owner;
	
	private String branch;
	
	private String id;
	
	
	private RepositoryStatus status;
	
	private List<Occurrence> deadCodeOccurrences;
	
	public RepositoryAnalysis(RepositoryModel repositoryModel) {
		this.name=repositoryModel.getRepositoryName();
		this.owner=repositoryModel.getOwner();
		this.branch=repositoryModel.getBranchName();
		this.begin=new Date();
		this.status=RepositoryStatus.ADDED;
		this.id=UUID.randomUUID().toString();
	}
	
	public String getId() {
		return id;
	}

	public String getOwner() {
		return owner;
	}

	public String getName() {
		return name;
	}

	public RepositoryStatus getStatus() {
		return status;
	}

	public void setStatus(RepositoryStatus status) {
		
		if(status.equals(RepositoryStatus.COMPLETED) || status.equals(RepositoryStatus.ADDED)){
			throw new RuntimeException("Cannot change the repository status to ADDED or COMPLETED, use correct method.");
		}
		
		this.status = status;
	}
	
	public Date getBegin() {
		return begin;
	}

	public Date getFinished() {
		return finished;
	}

	public void finish(List<Occurrence> deadCodeOccurrences) {
		this.deadCodeOccurrences=deadCodeOccurrences;
		this.finished=new Date();
		this.status=RepositoryStatus.COMPLETED;
	}

	public List<Occurrence> getDeadCodeOccurrences() {
		return deadCodeOccurrences;
	}


	public String getBranch() {
		return branch;
	}


	public void setBranch(String branch) {
		this.branch = branch;
	}

}
	
