package testCases.ArchitectEmpanelment;

import pageObjects.ArchitectEmpanelment.Application_Details;

/**
 * Flow Test 5 (excel TC_AR_064-077): Public Limited Company (Ltd.)
 * empanelment. Shareholders mandatory (TC_AR_073: at least one).
 */
public class PublicLtdEmpanelmentFlow extends EmpanelmentFlowBase {

	@Override
	protected String orgType() {
		return "Firm";
	}

	@Override
	protected void selectFirmType(Application_Details app) {
		app.selectFirmType4();
	}

	@Override
	protected boolean hasShareholders() {
		return true;
	}

	@Override
	protected String typeName() {
		return "PublicLtd";
	}
}
