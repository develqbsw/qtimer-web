package sk.qbsw.sed.test.dao;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.LoggerFactory;

/**
 * Class for testing request page.
 * @author moravcik
 *
 */
public class CTestRequestPage extends ASeleniumTest {
	
	private final static String CREATED = "Vytvorená";
	private final static String CANCELED = "Zrušená";
	private final static String HALFDAY = "0,5";
	
	@Before
	public void setUp() {
		super.setUp();
		LOGGER = LoggerFactory.getLogger(CTestRequestPage.class);
		LOGGER.info("Setting up CRequestPageTest.");
	}
	
	@After
	public void tearDown() {
		super.tearDown();
	}

	/**
	 * Test for adding, editing and canceling a request.
	 * @throws InterruptedException 
	 */
	@Test
	public void testAddEditCancel() {
		LOGGER.info("Add, edit, cancel request test.");	
		//log in, get request page
		super.logInAsTestUser();
		assertTrue(driver.getCurrentUrl().contains(HOME_PAGE_URL));
		driver.findElement(By.name("CRequestPage")).click();
		wait.until(ExpectedConditions.urlContains(REQUEST_PAGE_URL));
		
		//cancell all requests
		cancelAllRequests();
		
		//add new request
		addNewRequest(false);
		
		//edit
		assertTrue(driver.findElement(By.id("requestTable_tab")).getAttribute("class").contains("active in"));
		//order by status - created first
		List<WebElement> tableRows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		driver.findElement(By.cssSelector("a[id^='statusDescription'")).click();
		wait.until(ExpectedConditions.stalenessOf(tableRows.get(0))); //pokym neodpoji stary riadok (refreshne tabulku)
		tableRows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		tableRows.get(1).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='request_tab'][class*='active in']")));
		//click edit, input text in place, save
		driver.findElement(By.cssSelector("a[id^='edit'")).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.name("place")));
		driver.findElement(By.name("place")).clear();
		driver.findElement(By.name("place")).sendKeys("test");
		driver.findElement(By.name("submitBtn")).click();
		//is on table tab
		
		//cancel
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='requestTable_tab'][class*='active in']")));
		tableRows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		wait.until(ExpectedConditions.visibilityOf(tableRows.get(1))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='request_tab'][class*='active in']")));
		//click cancel
		driver.findElement(By.cssSelector("a[id^='cancel'")).click();
		//modal window pop-up, click ok
		wait.until(ExpectedConditions.elementToBeClickable(By.name("btnOk"))).click();
		//is back on table
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='requestTable_tab'][class*='active in']")));

	}
	
	/**
	 * Tests adding a request for half a day.
	 */
	@Test
	public void testHalfDay() {
		LOGGER.info("Half day request test.");	
		//log in, get request page
		super.logInAsTestUser();
		assertTrue(driver.getCurrentUrl().contains(HOME_PAGE_URL));
		driver.findElement(By.name("CRequestPage")).click();
		wait.until(ExpectedConditions.urlContains(REQUEST_PAGE_URL));
		
		cancelAllRequests();
		
		//add a half day request
		addNewRequest(true);
		
		//check if added request in table has 0.5 day count
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='requestTable_tab'][class*='active in']")));
		boolean isCorrect = false;
		List<WebElement> tableRows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		driver.findElement(By.cssSelector("a[id^='statusDescription']")).click();
		wait.until(ExpectedConditions.stalenessOf(tableRows.get(0)));
		String status = driver.findElements(By.xpath("//fieldset/descendant::tr/td[6]/div")).get(0).getText();
		String daycount = driver.findElements(By.xpath("//fieldset/descendant::tr/td[4]/div")).get(0).getText();
		isCorrect = CREATED.equals(status) && HALFDAY.equals(daycount);
		assertTrue(isCorrect);
		
		cancelAllRequests();
	}
	
	/**
	 * Tests date input format.
	 */
	@Test
	public void testDateFormat() {
		LOGGER.info("Half day request test.");	
		//log in, get request page
		super.logInAsTestUser();
		assertTrue(driver.getCurrentUrl().contains(HOME_PAGE_URL));
		driver.findElement(By.name("CRequestPage")).click();
		wait.until(ExpectedConditions.urlContains(REQUEST_PAGE_URL));
		
		cancelAllRequests();
		
		//add new request - try date format
		//is on table tab? save numbers of rows in table
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='requestTable_tab'][class*='active in']")));
		//go on "New" tab
		driver.findElement(By.cssSelector("a[href='#request_tab']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='request_tab'][class*='active in']")));
		
		//set dates 
		String dateStr =  new SimpleDateFormat("dd.MM.yyyy").format(new Date());
		driver.findElement(By.name("date")).clear();
		driver.findElement(By.name("date")).sendKeys(dateStr);
		driver.findElement(By.name("date")).click();
		//save
		driver.findElement(By.name("submitBtn")).click();
		//must do error
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='request_tab'][class*='active in']")));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span[class='input-error help-block']")));
		WebElement error = driver.findElement(By.cssSelector("span[class='input-error help-block']"));
		assertNotNull(error);
		
		//again. another invalid format
		driver.findElement(By.name("CRequestPage")).click();
		wait.until(ExpectedConditions.urlContains(REQUEST_PAGE_URL));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='requestTable_tab'][class*='active in']")));
		//go on "New" tab
		driver.findElement(By.cssSelector("a[href='#request_tab']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='request_tab'][class*='active in']")));
		
		//set dates 
		dateStr =  new SimpleDateFormat("dd.MM.yyyy").format(new Date());
		driver.findElement(By.name("date")).clear();
		driver.findElement(By.name("date")).sendKeys(dateStr);
		driver.findElement(By.name("date")).click();
		//save
		driver.findElement(By.name("submitBtn")).click();
		//must do error
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='request_tab'][class*='active in']")));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span[class='input-error help-block']")));
		WebElement error2 = driver.findElement(By.cssSelector("span[class='input-error help-block']"));
		assertNotNull(error2);
	}
	
	/**
	 * Cancels all users requests.
	 */
	private void cancelAllRequests() {
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='requestTable_tab'][class*='active in']")));
		List<WebElement> tableRows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		int rowCount = tableRows.size();
		for (int i = 1; i < rowCount; i++) {
			wait.until(ExpectedConditions.visibilityOf(tableRows.get(i)));
			if (!CANCELED.equals(tableRows.get(i).findElement(By.xpath("//td[6]/div")).getText())) {
				wait.until(ExpectedConditions.visibilityOf(tableRows.get(i))).click();
				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='request_tab'][class*='active in']")));
				driver.findElement(By.cssSelector("a[id^='cancel'")).click();
				wait.until(ExpectedConditions.elementToBeClickable(By.name("btnOk"))).click();
				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='requestTable_tab'][class*='active in']")));
			}
			tableRows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
	
	/**
	 * Adds a new request.
	 * @param halfDay
	 */
	private void addNewRequest(boolean halfDay) {
		//is on table tab? save numbers of rows in table
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='requestTable_tab'][class*='active in']")));
		int rowCount = driver.findElements(By.xpath("//fieldset/descendant::tr")).size();
		//go on "New" tab
		driver.findElement(By.cssSelector("a[href='#request_tab']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='request_tab'][class*='active in']")));
		
		//set dates 
		if (halfDay) {
			driver.findElement(By.cssSelector("input[id^='halfday']")).sendKeys(Keys.SPACE);
			driver.findElement(By.name("date")).click();
			driver.findElement(By.cssSelector("td[class='active day']")).click();
		} else {
			String dateStr = new SimpleDateFormat("dd.MM.yyyy - dd.MM.yyyy").format(new Date());
			driver.findElement(By.name("date")).clear();
			driver.findElement(By.name("date")).sendKeys(dateStr);
		}
		//save
		wait.until(ExpectedConditions.elementToBeClickable(By.name("submitBtn"))).click();
		//is back on table. chack if added
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='requestTable_tab'][class*='active in']")));
		int rowCount2 = driver.findElements(By.xpath("//fieldset/descendant::tr")).size();
		assertTrue(rowCount2 == rowCount+1);
	}
}

