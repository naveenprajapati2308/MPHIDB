package testCases.ArchitectEmpanelment;

import pageObjects.ArchitectEmpanelment.Application_Details;

/**
 * Flow Test 1 (excel TC_AR_010-021): Individual architect empanelment,
 * registration to final submit. No firm section, no shareholder section -
 * Other Details is reached via the tab switch (see EmpanelmentFlowBase).
 */
public class IndividualEmpanelmentFlow extends EmpanelmentFlowBase {

	@Override
	protected String orgType() {
		return "Individual";
	}

	@Override
	protected void selectFirmType(Application_Details app) {
		// Individual has no firm-type radios.
	}

	@Override
	protected boolean hasShareholders() {
		return false;
	}

	@Override
	protected String typeName() {
		return "Individual";
	}
}
