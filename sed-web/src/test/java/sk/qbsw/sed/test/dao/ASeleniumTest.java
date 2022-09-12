package sk.qbsw.sed.test.dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sk.qbsw.sed.model.CSystemSettings;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/test-app-context.xml" })
public abstract class ASeleniumTest {

	protected WebDriver driver;
	protected WebDriverWait wait; 
	protected String baseUrl;
	protected StringBuffer verificationErrors = new StringBuffer();
	protected static Logger LOGGER; 
	protected final static String TEST_USER_USERNAME = "turing@qbsw.sk";
	protected final static String TEST_USER_PASSWORD = "Heslo1";
	protected final static String ADMIN_USER_USERNAME = "qbsw.admin";
	protected final static String ADMIN_USER_PASSWORD = "Heslo1";
	protected final static String LOGIN_PAGE_URL = "q-timer/login";
	protected final static String HOME_PAGE_URL = "q-timer/home";
	protected final static String TIMESHEET_PAGE_URL = "q-timer/timesheet";
	protected final static String REQUEST_PAGE_URL = "q-timer/requests";
	protected final static String USERPROJECTS_PAGE_URL = "q-timer/userprojects";
	protected final static String TIMESTAMP_GENERATE_PAGE_URL = "q-timer/timestampGenerate";
	protected final static String ELEMENT_SEEN = "display: block;";
	protected final static String ELEMENT_UNSEEN = "display: none;";
	
	@Autowired
	protected CSystemSettings settings;
	
	@Before
	public void setUp() {
		LOGGER = LoggerFactory.getLogger(ASeleniumTest.class);
		LOGGER.info("Setting up ASeleniumTest.");
		String apiUrl = settings.getApiUrl();
		baseUrl = apiUrl.replace("sed-api/api/rest", "");

		System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		
		wait = new WebDriverWait(driver, 10);
	}
	
	@After
	public void tearDown() {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
	
	/**
	 * Logs in as test user.
	 */
	protected void logInAsTestUser() {
		LOGGER.info("Logging as test user.");
		driver.get(baseUrl + "q-timer/login");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys(TEST_USER_USERNAME);
		driver.findElement(By.name("password")).sendKeys(TEST_USER_PASSWORD);
		driver.findElement(By.id("submitBtn2")).click();
		wait.until(ExpectedConditions.urlContains(HOME_PAGE_URL));
	}
	
	/**
	 * Logs in as admin user.
	 */
	protected void logInAsAdminUser() {
		LOGGER.info("Logging as test user.");
		driver.get(baseUrl + "q-timer/login");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys(ADMIN_USER_USERNAME);
		driver.findElement(By.name("password")).sendKeys(ADMIN_USER_PASSWORD);
		driver.findElement(By.id("submitBtn2")).click();
		wait.until(ExpectedConditions.urlContains(TIMESHEET_PAGE_URL));
	}
	
	/**
	 * Goes to a timesheet page and deletes all timestamps in that day.
	 * If parameter 'check' is true, asserts that there are exactly 3 timestamps.
	 * @param check
	 */
	protected void deleteAllTimestamps(boolean checkForThree) {
		//go to timesheet page, check if 3 timestamps are generated, delete generated timestamps
		driver.findElement(By.name("CTimesheetPage")).click();
		wait.until(ExpectedConditions.urlContains(TIMESHEET_PAGE_URL));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheetTable_tab'][class*='active in']")));
		
		//list all timestamps
		List<WebElement> rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		new Select(driver.findElement(By.name("activityId"))).selectByValue("0");
		wait.until(ExpectedConditions.stalenessOf(rows.get(0)));
		
		if (checkForThree) {
			//checks if there are exactly 3 timestamps
			rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
			assertEquals(3, rows.size()-1);
		}
		
		rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		while (rows.size() > 1) {
			rows.get(1).findElement(By.cssSelector("a[id^='delete']")).click();
			//modal window pop-up
			wait.until(ExpectedConditions.elementToBeClickable(By.name("btnOk"))).click();
			wait.until(ExpectedConditions.stalenessOf(rows.get(0)));
			rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		} 
		
		//chack if deleted
		assertEquals(1, driver.findElements(By.xpath("//fieldset/descendant::tr")).size());
		
	}
	
	/**
	 * Returns number of elements found by parameter 'by'.
	 * @param by
	 * @return
	 */
	protected Integer canFindElement(By by) {
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		int count = driver.findElements(by).size();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		return count;
	}
}
