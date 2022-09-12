package sk.qbsw.sed.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.LoggerFactory;

/**
 * Class for testing generating tamestamps.
 * @author moravcik
 *
 */
public class CTestTimestampGeneratePage extends ASeleniumTest {

	@Before
	public void setUp() {
		super.setUp();
		LOGGER = LoggerFactory.getLogger(CTestTimestampGeneratePage.class);
		LOGGER.info("Setting up TimestampGeneratePageTest.");
	}
	
	@After
	public void tearDown() {
		super.tearDown();
	}
	
	/**
	 * Tests generating timestamps with percentage timestamps division.
	 */
	@Test
	public void testGenerateTimestampsPercent() {
		LOGGER.info("Generating timestamps test start.");
		super.logInAsTestUser();
		wait.until(ExpectedConditions.urlContains(HOME_PAGE_URL));
		
		//go to timesheet page, delete all timestamps
		deleteAllTimestamps(false);
		
		//add 8 hour timestamp
		int rowCount = driver.findElements(By.xpath("//fieldset/descendant::tr")).size();
		driver.findElement(By.cssSelector("a[href='#timesheet_tab']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheet_tab'][class*='active in']")));
		driver.findElement(By.name("timeFrom")).clear();
		driver.findElement(By.name("timeFrom")).sendKeys("08:00");
		driver.findElement(By.name("timeTo")).clear();
		driver.findElement(By.name("timeTo")).sendKeys("16:00");
		driver.findElement(By.name("submitBtn")).click();
		//is back on table. chack if added
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheetTable_tab'][class*='active in']")));
		int rowCount2 = driver.findElements(By.xpath("//fieldset/descendant::tr")).size();
		assertTrue(rowCount2 == rowCount+1);
		
		//go on timestamp generating page
		driver.findElement(By.name("CTimestampGeneratePage")).click();
		wait.until(ExpectedConditions.urlContains(TIMESTAMP_GENERATE_PAGE_URL));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheetTable_tab'][class*='active in']")));
		
		//percentage
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@id,'dataInputType')]/label[1][contains(@class,'active')]")));
		
		//add timestamps
		addTimestamp("60", "17", null);
		addTimestamp("40", "23", null);
		
		//generate. must do modal and success alert
		driver.findElement(By.cssSelector("a[id^='confirm']")).click();
		
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='modal-body']/descendant::a[@name='btnOk']"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='alert alert-success']")));
		
		//go to timesheet page, check if 2 timestamps are generated, delete generated timestamps
		driver.findElement(By.name("CTimesheetPage")).click();
		wait.until(ExpectedConditions.urlContains(TIMESHEET_PAGE_URL));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheetTable_tab'][class*='active in']")));
		
		//list all timestamps
		List<WebElement> rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		new Select(driver.findElement(By.name("activityId"))).selectByValue("0"); //all timestamps
		wait.until(ExpectedConditions.stalenessOf(rows.get(0)));
		
		rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		assertEquals(2, rows.size()-1); //minus first empty row
		
		//list all working timestamps
		rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		new Select(driver.findElement(By.name("activityId"))).selectByValue("1"); //all working timestamps
		wait.until(ExpectedConditions.stalenessOf(rows.get(0)));
		
		rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		assertEquals(2, rows.size()-1); //minus first empty row
		
		deleteAllTimestamps(false);
		
	}
	
	private void addTimestamp(String duration, String activity, String project) {
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheetTable_tab'][class*='active in']")));
		int rowCount = driver.findElements(By.xpath("//fieldset/descendant::tr")).size();
		//add timestamp
		driver.findElement(By.cssSelector("a[href='#timesheet_tab']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheet_tab'][class*='active in']")));
		
		driver.findElement(By.cssSelector("input[name^='durationIn']")).clear();
		driver.findElement(By.cssSelector("input[name^='durationIn']")).sendKeys(duration);
		if (activity != null) {
			new Select(driver.findElement(By.name("activityContainer:activity"))).selectByValue(activity);
		}
		if (project != null) {
			new Select(driver.findElement(By.name("projectContainer:project"))).selectByValue(project);
		}
		driver.findElement(By.name("submitBtn")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[id='timesheetTable_tab'][class*='active in']")));
		int rowCount2 = driver.findElements(By.xpath("//fieldset/descendant::tr")).size();
		assertTrue(rowCount2 == rowCount+1);
	}
}
