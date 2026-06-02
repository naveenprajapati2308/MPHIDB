package pageObjects.ArchitectEmpanelment;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.ClickUtils;

public class DashBoardPage extends DriverBase {
	// TODO Auto-generated constructor stub
	public DashBoardPage(WebDriver driver) {
		super(driver);
		
	}
	

	    // ================= WebElements =================

	    @FindBy(xpath="//div[@class='card-body d-sm-flex d-block align-items-center']")
	    private WebElement cardBody;

	    @FindBy(xpath="//a[@href='/MPHB_dev/frontend/website/user/manage-application-form']")
	    private WebElement manageApplicationForm;

	    @FindBy(xpath="//a[@class='has-arrow ai-icon']")
	    private WebElement menuArrow;

	    @FindBy(xpath="//a[normalize-space()='Competition List']")
	    private WebElement competitionList;

	    @FindBy(xpath="//a[normalize-space()='Assigned Competition']")
	    private WebElement assignedCompetition;

	    @FindBy(xpath="//span[normalize-space()='Dashboard']")
	    private WebElement dashboard;

	    @FindBy(xpath="//span[normalize-space()='Manage Empanelment']")
	    private WebElement manageEmpanelment;


	    // ================= Actions (Smart Clicks) =================

	    public void clickCardBody() {
	        ClickUtils.smartClick(driver, cardBody);
	    }

	    public void clickManageApplicationForm() {
	        ClickUtils.smartClick(driver, manageApplicationForm);
	    }

	    public void clickMenuArrow() {
	        ClickUtils.smartClick(driver, menuArrow);
	    }

	    public void clickCompetitionList() {
	        ClickUtils.smartClick(driver, competitionList);
	    }

	    public void clickAssignedCompetition() {
	        ClickUtils.smartClick(driver, assignedCompetition);
	    }

	    public void clickDashboard() {
	        ClickUtils.smartClick(driver, dashboard);
	    }

	    public void clickManageEmpanelment() {
	        ClickUtils.smartClick(driver, manageEmpanelment);
	    }
	}


