package testData;

public class SignUpDataArch {
    public String testCaseID;
	public String orgType;
	public String mobileType;
	public String emailType;
	public boolean checkbox;
	public String scenario;
	

	public SignUpDataArch(
			String testCaseID,
			String scenario,
			String orgType, 
			String mobileType,
			String emailType,
			boolean checkbox) {
		this.testCaseID = testCaseID;
		this.scenario = scenario;
		this.orgType = orgType;
		this.mobileType = mobileType;
		this.emailType = emailType;
		this.checkbox = checkbox;
		
	}
	 public String getTestCaseId() {
	        return testCaseID;
	    }

	    public String getTestCaseName() {
	        return scenario;
	    }

	@Override
	public String toString() {
	    return scenario;
	}
	
}