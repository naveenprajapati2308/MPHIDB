


package testDataProviders;

import org.testng.annotations.DataProvider;
import testData.SignUpDataArch;

public class SignupDataProvider {

@DataProvider(name = "SignUpDataArch")
public Object[][] signupData() {

    return new Object[][] {

        //Negative cases
        { new SignUpDataArch("TC001","Invalid Mobile Number", "Firm", "INVALID", "VALID", true) },
        { new SignUpDataArch("TC002","Invalid Email Id", "Firm", "VALID", "INVALID", true) },
        { new SignUpDataArch("TC003","Mobile Number Blank", "Firm", "BLANK", "VALID", true) },
        { new SignUpDataArch("TC004","Email Id Blank", "Firm", "VALID", "BLANK", true) },
        { new SignUpDataArch("TC005","Checkbox Unchecked", "Firm", "VALID", "VALID", false) },

        // STEP 1: Create User1 (Individual) and user 2 firmm
        { new SignUpDataArch("TC006","Create User1 Firm", "Firm", "VALID", "VALID", true) },
        { new SignUpDataArch("TC009","Create User2 Firm", "Firm", "VALID", "VALID", true) },

        // STEP 2: Duplicate tests (based on User1)
        { new SignUpDataArch("TC007","Duplicate Mobile User1", "Firm", "DUPLICATE", "VALID", true) },
        { new SignUpDataArch("TC008","Duplicate Email User1", "Firm", "VALID", "DUPLICATE", true) },
  // STEP 4: Create User3
        { new SignUpDataArch("TC0010","Create User3 Individual", "Individual", "VALID", "VALID", true) }
    };
}
}




