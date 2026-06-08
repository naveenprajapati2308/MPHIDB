
package testCases.LandModule;

import org.testng.annotations.Test;

import pageObjects.LandModule.Add_khasra;
import pageObjects.LandModule.LandManagement;
import testBase.AuthorityLogin;
import utilities.FileUtils;

public class AddKhasra{

	public String EE;
	public String password;
	public String Captcha;

	@Test(dependsOnMethods = { "TC_001" })
	public void TC_005() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		
		land.searchLand("Land_acquired_type21");
		
		land.clickSearchBtn();
		
      land.clickEditButton("Land_acquired_type21");
		

	}
	@Test(dependsOnMethods = { "TC_005" })
	public void TC_006() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		
		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);
		
		khasra.clickAddKhashra();
		
		khasra.selectKhasraType("New");
		
//		khasra.selectKhasraDistrict("Narsinghpur");
		
		khasra.selectKhasratehsil("Kareli");
		
		khasra.selectAreaType("Rural");
		
		khasra.selectVillageORward("Bamhori");

		khasra.selectKhasraNumber("2");
		
		khasra.enterAreainHactare("1000");
		
		khasra.enterlatitude("28");
		
		khasra.enterlongitude("30");
		
		khasra.enterPurchaseDate("2026-06-02");
		
		
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
		
		
		
		
		
		
		



	}

}
