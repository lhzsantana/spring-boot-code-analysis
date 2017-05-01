package ca.onboarding.deadcode;

public class RepositoryModel {

	private String repositoryName;
	private String branchName;
	private String owner;
	
	public String getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	@Override
	public String toString(){
		return "["+repositoryName +", " + owner +", "+ branchName+"]";
		
	}
}
