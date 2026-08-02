package testCases.ArchitectEmpanelment;

import pageObjects.ArchitectEmpanelment.Application_Details;

/**
 * Flow Test 2 (excel TC_AR_022-035): Proprietorship Firm empanelment.
 * First firm in the suite - its saved Firm Reg No/GST/PAN become the
 * duplicate SOURCE (TestContext.firstFirm*) that later firm flows use for
 * the TC_AR_026/093/094 duplicacy negatives. No shareholder section
 * (TC_AR_031) - Other Details reached via tab switch.
 */
public class ProprietorshipEmpanelmentFlow extends EmpanelmentFlowBase {

	@Override
	protected String orgType() {
		return "Firm";
	}

	@Override
	protected void selectFirmType(Application_Details app) {
		app.selectFirmType1();
	}

	@Override
	protected boolean hasShareholders() {
		return false;
	}

	@Override
	protected String typeName() {
		return "Proprietorship";
	}
}
