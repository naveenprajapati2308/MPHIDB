package pageObjects.LandModule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class DriverBase {
	/* her i am setting the base constructer to the all child page classes 
	instead of doing multiple copies */

	   WebDriver driver;

	    public DriverBase(WebDriver driver)
	    {
	        this.driver = driver;
	        PageFactory.initElements(driver, this);
	    }
}

