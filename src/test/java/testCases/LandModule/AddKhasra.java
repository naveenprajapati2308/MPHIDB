
package testCases.LandModule;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.LandModule.Add_khasra;
import pageObjects.LandModule.LandManagement;
import testBase.AuthorityLogin;
import utilities.FileUtils;
import utilities.testData;

public class AddKhasra extends AuthorityLogin{

	@Test(dependsOnMethods = { "TC_001" })
	public void TC_005() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);

		land.searchLand(testData.landName());

		land.clickSearchBtn();

      land.clickEditButton(testData.landName());

		Assert.assertTrue(AuthorityLogin.driver.getCurrentUrl().contains("land/update"),
				"Expected to land on the Update Land page after clicking edit, but URL was: "
						+ AuthorityLogin.driver.getCurrentUrl());

	}
	@Test(dependsOnMethods = { "TC_005" })
	public void TC_006() {

		//LandManagement land = new LandManagement(AuthorityLogin.driver);

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);

		khasra.clickAddKhashra();

		khasra.selectKhasraType("New");

//		khasra.selectKhasraDistrict("Narsinghpur");

		khasra.selectAreaType("Rural");

		// Khasra No. is fetched from a backend lookup per village, and the app
		// rejects duplicates without live-filtering the dropdown - a number that's
		// already registered for a village still shows as selectable and only fails
		// on selection with a "Duplicate Khasra" popup. Escalate systematically:
		// within a tehsil, try each village's khasra numbers first-to-last; once a
		// village's khasras are all taken, move to the next village; once every
		// village under a tehsil is exhausted, move to the next tehsil.
		String selected = khasra.selectAvailableKhasraAcrossTehsilsAndVillages(
				"Kareli", "Gadarwara", "Gotegaon", "Narsinghpur", "Tendukheda");

		Assert.assertNotNull(selected,
				"No tehsil/village combination had an available Khasra No. - "
				+ "the lookup may be down, or every seeded khasra number is already used.");

		khasra.enterAreainHactare("1000");

		khasra.enterlatitude("28");

		khasra.enterlongitude("30");

		khasra.enterPurchaseDate("02-06-2026");


		String Demarcation = FileUtils.getFilePath("Demarcation.pdf");

		String Possession = FileUtils.getFilePath("Possession.pdf");

		String FieldBook = FileUtils.getFilePath("FieldBook.pdf");

		String MapLayout = FileUtils.getFilePath("MapLayout.pdf");

		String KhasraCopy = FileUtils.getFilePath("KhasraCopy.pdf");


		khasra.uploadDemarcation(Demarcation);
		khasra.uploadPossession_of_Land(Possession);
		khasra.uploadField_Book(FieldBook);
		khasra.uploadMap_Layout(MapLayout);
		khasra.uploadKhasra_Copy(KhasraCopy);


		khasra.clickSubmitBtn();
		khasra.clickOkBtn();
//		khasra.clickCancelBtn();

		// Persist the khasra onto the land record itself (page-level Save/Update,
		// distinct from the khasra modal's own Submit/OK).
		khasra.clickUpdateBtn();

	}

	@Test(dependsOnMethods = { "TC_006" })
	public void TC_007() {

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);

		khasra.clickOtherDetailsBtn();

	}


}
