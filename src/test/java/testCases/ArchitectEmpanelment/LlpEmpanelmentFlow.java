package testCases.ArchitectEmpanelment;

import pageObjects.ArchitectEmpanelment.Application_Details;

/**
 * Flow Test 6 (excel TC_AR_078-091): Limited Liability Partnership (LLP)
 * empanelment. Shareholders mandatory (TC_AR_087: at least one).
 */
public class LlpEmpanelmentFlow extends EmpanelmentFlowBase {

	@Override
	protected String orgType() {
		return "Firm";
	}

	@Override
	protected void selectFirmType(Application_Details app) {
		app.selectFirmType5();
	}

	@Override
	protected boolean hasShareholders() {
		return true;
	}

	@Override
	protected String typeName() {
		return "LLP";
	}
}
