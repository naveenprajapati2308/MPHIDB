package testCases.LandModule;

import org.testng.annotations.Test;

import pageObjects.LandModule.Survey_details;
import testBase.AuthorityLogin;
import utilities.FileUtils;

public class SurveyDetails extends AuthorityLogin {


	// TC_007 (AddKhasra.java) clicks "Other Details" once a khasra has actually
	// persisted, landing on a 5-tab page (Survey/Dispute/Amenities/Approval
	// Details/Payment Details) with "Survey" active by default. The DTS Survey
	// section is required and visible immediately - its field locators (confirmed
	// against the live UI) are unchanged from before the redesign. Location Survey
	// and Field Survey are optional and hidden until clickShowOptional() reveals
	// them.
	@Test(dependsOnMethods = { "TC_007" })
	public void TC_008() {

	    Survey_details survey = new Survey_details(AuthorityLogin.driver);

	    // DTS Survey
	    String surveyPdf = FileUtils.getFilePath("Survey.pdf");
	    String surveyCad = FileUtils.getFilePath("SurveyAutoCAD.pdf");

	    survey.Upload_survey_pdf_1(surveyPdf);
	    survey.Upload_survey_cad_1(surveyCad);
	    survey.survey_details_1("DTS Survey Details 1");
	    survey.survey_discrepancy_1("No Discrepancy");

	    // Location Survey and Field Survey are optional sections, collapsed by
	    // default - reveal them before filling.
	    survey.clickShowOptional();

	    // Location Survey
	    survey.Upload_survey_pdf_2(surveyPdf);
	    survey.Upload_survey_cad_2(surveyCad);
	    survey.survey_details_2("Location Survey Details2");
	    survey.survey_discrepancy_2("No Discrepancy");

	    // Field Survey
	    survey.Upload_survey_pdf_3(surveyPdf);
	    survey.Upload_survey_cad_3(surveyCad);
	    survey.survey_details_3("Field Survey Details 3");
	    survey.survey_discrepancy_3("No Discrepancy");

	}
	
	@Test(dependsOnMethods = { "TC_008" })
	public void TC_009() {

	    Survey_details survey = new Survey_details(AuthorityLogin.driver);

	    survey.enterAgencyName("ABC Survey Agency");

	    survey.enterSurveyedBy("Naveen Prajapati");

	    survey.enterContactNo("9876543210");

	    survey.enterAddress("Bhopal Madhya Pradesh");
	    
	    survey.clickSaveBtn();

	}

}
