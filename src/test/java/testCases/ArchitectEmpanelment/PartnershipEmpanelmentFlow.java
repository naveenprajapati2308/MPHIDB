package testCases.ArchitectEmpanelment;

import pageObjects.ArchitectEmpanelment.Application_Details;

/**
 * Flow Test 3 (excel TC_AR_036-049): Partnership Firm empanelment.
 * Shareholders mandatory (TC_AR_045: at least one). Runs the duplicate
 * Firm Reg No/GST/PAN negative against the Proprietorship flow's values.
 */
public class PartnershipEmpanelmentFlow extends EmpanelmentFlowBase {

	@Override
	protected String orgType() {
		return "Firm";
	}

	@Override
	protected void selectFirmType(Application_Details app) {
		app.selectFirmType2();
	}

	@Override
	protected boolean hasShareholders() {
		return true;
	}

	@Override
	protected String typeName() {
		return "Partnership";
	}
}
