package pageObjects.MPPMCL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DashboardPage extends DriverBase {
	public DashboardPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	

	@FindBy(xpath = "//h4[contains(text(),'Akriti')]")
	private WebElement userName1;
	
	@FindBy(xpath = "//h4[contains(text(),'Akriti')]")
	private WebElement userName2;
	
	@FindBy(xpath = "//h4[contains(text(),'Akriti')]")
	private WebElement userName3;
	

}
