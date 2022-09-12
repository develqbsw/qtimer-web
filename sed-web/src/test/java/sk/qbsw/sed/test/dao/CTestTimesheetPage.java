package sk.qbsw.sed.test.dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.LoggerFactory;

/**
 * Class for testing timesheet page.
 * @author moravcik
 *
 */
public class CTestTimesheetPage extends ASeleniumTest {

	
	@Before
	public void setUp() {
		super.setUp();
		LOGGER = LoggerFactory.getLogger(CTestTimesheetPage.class);
		LOGGER.info("Setting up CTimesheetPageTest.");
	}
	
	@After
	public void tearDown() {
		super.tearDown();
	}
	
	/**
	 * Test for adding, edit and delete a timestamp.
	 * @throws InterruptedException 
	 */
	@Test
	public void testAddEditDelete() {
		LOGGER.info("Add, edit and delete timestamp test.");	
		//log in, get timesheet page
		super.logInAsTestUser();
		assertTrue(driver.getCurrentUrl().contains(HOME_PAGE_URL));
		
		deleteAllTimestamps(false);
		
		driver.findElement(By.name("CTimesheetPage")).click();
		wait.until(ExpectedConditions.urlContains(TIMESHEET_PAGE_URL));
		// is on table tab. save number or rows in table
		assertTrue(driver.findElement(By.id("timesheetTable_tab")).getAttribute("class").contains("active in"));
		int rowCount = driver.findElements(By.xpath("//fieldset/descendant::tr")).size();
		
		this.addTimestamp(false);
		
		//edit by tab
		List<WebElement> tableRows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		rowCount = tableRows.size();
		wait.until(ExpectedConditions.visibilityOf(tableRows.get(rowCount-1))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheet_tab'][class*='active in']")));
		//.click edit. set end time the same as start time.save
		driver.findElement(By.name("edit")).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.name("timeFrom")));
		String time = driver.findElement(By.name("timeFrom")).getAttribute("value");
		driver.findElement(By.name("timeTo")).clear();
		driver.findElement(By.name("timeTo")).sendKeys(time);
		driver.findElement(By.name("submitBtn")).click();
		//is on table tab
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheetTable_tab'][class*='active in']")));
		
		//edit note in row
		tableRows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		rowCount = tableRows.size();
		wait.until(ExpectedConditions.visibilityOf(tableRows.get(rowCount-1).findElement(By.cssSelector("a[id*='edit']")))).click();
		wait.until(ExpectedConditions.stalenessOf(tableRows.get(rowCount-1).findElement(By.cssSelector("a[id*='edit']"))));
		tableRows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		rowCount = tableRows.size();
		tableRows.get(rowCount-1).findElement(By.xpath("//input[contains(@name,'note')]")).clear();
		tableRows.get(rowCount-1).findElement(By.xpath("//input[contains(@name,'note')]")).sendKeys("test");
		tableRows.get(rowCount-1).findElement(By.xpath("//a[contains(@id,'submit')]")).click();
		wait.until(ExpectedConditions.stalenessOf(tableRows.get(rowCount-1)));
		
		this.deleteLastTimestamp();
	}
	
	/**
	 * Test for confirming timestamps and canceling confirmation.
	 * @throws InterruptedException 
	 */
	@Test
	public void testConfirmCancelConfirm() {
		LOGGER.info("Confirm timestamps test.");	
		//log in, get timesheet page
		super.logInAsTestUser();
		assertTrue(driver.getCurrentUrl().contains(HOME_PAGE_URL));
		
		deleteAllTimestamps(false);
		
		driver.findElement(By.name("CTimesheetPage")).click();
		wait.until(ExpectedConditions.urlContains(TIMESHEET_PAGE_URL));
		// is on table tab. save number or rows in table
		assertTrue(driver.findElement(By.id("timesheetTable_tab")).getAttribute("class").contains("active in"));
		
		this.addTimestamp(true);
		
		//confirm. submit in modal. should give success alert
		assertTrue(driver.findElement(By.id("timesheetTable_tab")).getAttribute("class").contains("active in"));
		driver.findElement(By.xpath("//div[contains(@id,'confirmTimesheetRecords')]/div/a")).click();
		
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		if (driver.findElements(By.cssSelector("div[id^='manageEmployees']")).size() > 0) { //if user is superior to other users
			driver.findElement(By.cssSelector("a[href$='confirmTimesheetRecordsSuperior']")).click();
		} else { //if user is no superior
			driver.findElement(By.cssSelector("a[href$='confirmTimesheetRecordsEmployee']")).click();
		}		
		wait.until(ExpectedConditions.elementToBeClickable(By.name("btnOk"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='alert alert-success']")));
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		//cancel confirmation. click link in dropdown, ok in modal, get success alert
		assertTrue(driver.findElement(By.id("timesheetTable_tab")).getAttribute("class").contains("active in"));
		driver.findElement(By.xpath("//div[contains(@id,'confirmTimesheetRecords')]/div/a")).click();
		
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		if (driver.findElements(By.cssSelector("div[id^='manageEmployees']")).size() > 0) { //if user is superior to other users
			driver.findElement(By.cssSelector("a[href$='cancelTimesheetRecordsSuperior']")).click();
		} else { //if user is no superior
			driver.findElement(By.cssSelector("a[href$='cancelTimesheetRecordsEmployee']")).click();
		}	
		wait.until(ExpectedConditions.elementToBeClickable(By.name("btnOk"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='alert alert-success']")));
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		this.deleteLastTimestamp();	
	}
	
	private void addTimestamp(boolean end) {
		int rowCount = driver.findElements(By.xpath("//fieldset/descendant::tr")).size();
		//go on "New" tab - add timestamp
		driver.findElement(By.cssSelector("a[href='#timesheet_tab']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheet_tab'][class*='active in']")));
		if (end) {
			//set time to (to be able to confirm) and save
			String time = driver.findElement(By.name("timeFrom")).getAttribute("value");
			driver.findElement(By.name("timeTo")).sendKeys(time);
		}
		WebElement btn = driver.findElement(By.name("submitBtn"));
		btn.click();
		//is back on table. chack if added
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheetTable_tab'][class*='active in']")));
		wait.until(ExpectedConditions.stalenessOf(btn));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("fieldset")));
		int rowCount2 = driver.findElements(By.xpath("//fieldset/descendant::tr")).size();
		assertTrue(rowCount2 == rowCount+1);
	}
	
	private void deleteLastTimestamp() {
		//delete last timestamp
		assertTrue(driver.findElement(By.id("timesheetTable_tab")).getAttribute("class").contains("active in"));
		List<WebElement> tableRows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		int rowCount = tableRows.size();
		//delete last row with trash button
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//fieldset/descendant::tr/descendant::a[contains(@id,'delete')][last()]"))).click();
		//modal window pop-up
		wait.until(ExpectedConditions.elementToBeClickable(By.name("btnOk"))).click();
		wait.until(ExpectedConditions.stalenessOf(tableRows.get(0)));
		//is back on table. chack if deleted
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheetTable_tab'][class*='active in']")));
		int rowCount2 = driver.findElements(By.xpath("//fieldset/descendant::tr")).size();
		assertTrue(rowCount2 == rowCount-1);
	}
}
