package assignment2.prog5121;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginNGTest {
    private Login login;

    @BeforeMethod
    public void setup() {
        login = new Login("Kyle", "Smith");
    }

    @Test
    public void testValidUsername() {
        Assert.assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    public void testInvalidUsername() {
        Assert.assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void testValidPassword() {
        Assert.assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testInvalidPassword() {
        Assert.assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testValidCellPhone() {
        Assert.assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testInvalidCellPhone() {
        Assert.assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    @Test
    public void testRegisterAndLoginSuccess() {
        String r = login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        Assert.assertEquals(r, "User successfully registered.");
        Assert.assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testReturnLoginStatusFailure() {
        String r = login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        Assert.assertEquals(r, "User successfully registered.");
        Assert.assertEquals(login.returnLoginStatus("kyl_1", "wrongPass"), "Username or password incorrect, please try again.");
    }
}
