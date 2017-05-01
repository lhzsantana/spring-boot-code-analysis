package ca.onboarding.git;

public interface CodeRepositoryClient {

	public void getCode(String owner, String repository, String branch);
	
}
