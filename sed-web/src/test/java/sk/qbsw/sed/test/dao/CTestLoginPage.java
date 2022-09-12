package sk.qbsw.sed.test.dao;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.LoggerFactory;

/**
 * Class tests login page functionality.
 * @author moravcik
 *
 */
public class CTestLoginPage extends ASeleniumTest {
	
	@Before
	public void setUp() {
		super.setUp();
		LOGGER = LoggerFactory.getLogger(CTestLoginPage.class);
		LOGGER.info("Setting up DashboardTest.");
	}
	
	@After
	public void tearDown() {
		super.tearDown();
	}
	
	/**
	 * Simple log in and log out test.
	 * @throws InterruptedException 
	 */
	@Test
	public void testSimpleLogInLogOut() {
		LOGGER.info("Simple log in test.");
		//log in
		super.logInAsTestUser();	
		assertTrue(driver.getCurrentUrl().contains(HOME_PAGE_URL));
		//log out
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='logoutLink']"))).click();
		assertTrue(driver.getCurrentUrl().contains(LOGIN_PAGE_URL));
	}
	
	/**
	 * Test with changing user name.
	 * @throws InterruptedException 
	 */
	@Test
	public void testChangeUsername() {
		LOGGER.info("Change username test.");
		//get login page
		driver.get(baseUrl + "q-timer/login");
		//fill fields
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys(ADMIN_USER_USERNAME);
		driver.findElement(By.name("password")).sendKeys(ADMIN_USER_PASSWORD);
		//change fields
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys(TEST_USER_USERNAME);
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys(TEST_USER_PASSWORD);
		//log in
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[id*='submitBtn']"))).click();
		wait.until(ExpectedConditions.urlContains(HOME_PAGE_URL));
	}
	
	/**
	 * Test using incorrect password.
	 * @throws InterruptedException 
	 */
	@Test
	public void testIncorrectPassword() {
		LOGGER.info("Incorrect password test.");
		//get login page
		driver.get(baseUrl + "q-timer/login");
		//fill fields
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys(TEST_USER_USERNAME);
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("incorrect password");
		driver.findElement(By.cssSelector("button[id*='submitBtn']")).click();
		//must do alert
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='alert alert-danger']")));
		//still in login page
		assertTrue(driver.getCurrentUrl().contains(LOGIN_PAGE_URL));
	}
	
	/**
	 * Test with empty password field.
	 * @throws InterruptedException 
	 */
	@Test
	public void testEmpltyPasswordField() {
		LOGGER.info("Empty password field test.");
		//get login page
		driver.get(baseUrl + "q-timer/login");
		//fill fields
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys(TEST_USER_USERNAME);
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.cssSelector("button[id*='submitBtn']")).click();
		//must do alert
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='alert alert-danger']")));
		//didn't log in, still login page
		assertTrue(driver.getCurrentUrl().contains(LOGIN_PAGE_URL));
	}
	
	/**
	 * Test language change.
	 * @throws InterruptedException 
	 */
	@Test
	public void testLanguageChange() {
		LOGGER.info("Language change test.");
		//get login page
		driver.get(baseUrl + "q-timer/login");
		String defLangHeader = driver.findElement(By.tagName("h3")).getText();
		String defLangLink = driver.findElement(By.cssSelector("a[href*='changeLanguage']")).getText();
		driver.findElement(By.cssSelector("a[href*='changeLanguage']")).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[id*='submitBtn']")));
		String newLangHeader = driver.findElement(By.tagName("h3")).getText();
		String newLangLink = driver.findElement(By.cssSelector("a[href*='changeLanguage']")).getText();
		assertFalse(defLangHeader.equals(newLangHeader));
		assertFalse(defLangLink.equals(newLangLink));
	}
	
	/**
	 * Test switching between login, forgot password and registration forms.
	 * @throws InterruptedException 
	 */
	@Test
	public void testSwitchForms() throws InterruptedException {
		LOGGER.info("Switching between forms test.");
		//get login page
		driver.get(baseUrl + "q-timer/login");
		//assert login seen others not
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='box-login'][style='display: block;']")));
		//forgot password.
		driver.findElement(By.cssSelector("a[class='forgot']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='box-forgot'][style='display: block;']")));
		//back to login
		driver.findElement(By.cssSelector("a[id*='goBackButton']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='box-login'][style='display: block;']")));
		//to register
		driver.findElement(By.cssSelector("a[class='register']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='box-register'][style='display: block;']")));
		//to license
		driver.findElement(By.cssSelector("a[class='licence']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='box-licence'][style='display: block;']")));
		//back to register
		driver.findElement(By.cssSelector("a[class='registerR']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='box-register'][style='display: block;']")));
		//back to login
		driver.findElement(By.cssSelector("a[class='go-back']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='box-login'][style='display: block;']")));
	}
	
	/**
	 * Test new client registration.
	 * @throws InterruptedException 
	 */
	@Test
	public void testClientRegistration() {
		LOGGER.info("New client registration test.");
		String uniqueCode = String.valueOf(System.currentTimeMillis());
		//get login page
		driver.get(baseUrl + "q-timer/login");
		driver.findElement(By.cssSelector("a[class='register']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='box-register'][style='display: block;']")));
		
		//fill input fields
		driver.findElement(By.name("orgName")).sendKeys("TestOrg"+uniqueCode);
		//typ organizacie ...
		driver.findElement(By.name("street")).sendKeys("TestOrg");
		driver.findElement(By.name("streetNo")).sendKeys("19");
		driver.findElement(By.name("city")).sendKeys("TestOrg");
		driver.findElement(By.name("zip")).sendKeys("987 54");
		driver.findElement(By.name("name")).sendKeys("OrgMan");	
		driver.findElement(By.name("surname")).sendKeys("OrgManson");
		driver.findElement(By.xpath("//*[@class='form-register']/descendant::input[@name='login']")).sendKeys("testorg"+uniqueCode);
		driver.findElement(By.xpath("//*[@class='form-register']/descendant::input[@name='email']")).sendKeys("moravcik@qbsw.sk");
		driver.findElement(By.xpath("//*[@class='form-register']/descendant::input[@name='password']")).sendKeys("Heslo1");
		driver.findElement(By.name("repeatedPass")).sendKeys("Heslo1");
		driver.findElement(By.name("agreeLicence")).sendKeys(Keys.SPACE);
		driver.findElement(By.name("agreeQBSW")).sendKeys(Keys.SPACE);
		driver.findElement(By.name("submitRegBtn")).click();
		//is sending email
		WebElement alert = driver.findElement(By.cssSelector("div[class='alert alert-info']"));
		assertNotNull(alert);
	}
}
