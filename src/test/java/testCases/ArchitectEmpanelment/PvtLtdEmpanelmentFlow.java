package testCases.ArchitectEmpanelment;

import pageObjects.ArchitectEmpanelment.Application_Details;

/**
 * Flow Test 4 (excel TC_AR_050-063): Private Limited Company (Pvt. Ltd.)
 * empanelment. Shareholders mandatory (TC_AR_059: at least one).
 */
public class PvtLtdEmpanelmentFlow extends EmpanelmentFlowBase {

	@Override
	protected String orgType() {
		return "Firm";
	}

	@Override
	protected void selectFirmType(Application_Details app) {
		app.selectFirmType3();
	}

	@Override
	protected boolean hasShareholders() {
		return true;
	}

	@Override
	protected String typeName() {
		return "PvtLtd";
	}
}
