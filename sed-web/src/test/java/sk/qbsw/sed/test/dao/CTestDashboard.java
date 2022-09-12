package sk.qbsw.sed.test.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.LoggerFactory;

/**
 * Class for testing dashboard page.
 * @author moravcik
 *
 */
public class CTestDashboard extends ASeleniumTest {
	

	@Before
	public void setUp() {
		super.setUp();
		LOGGER = LoggerFactory.getLogger(CTestDashboard.class);
		LOGGER.info("Setting up DashboardTest.");
	}
	
	@After
	public void tearDown() {
		super.tearDown();
	}
	
	/**
	 * Tests timer buttons to start work, start break, stop break, stop work. Checks with timestamps.
	 * @throws InterruptedException
	 */
	@Test
	public void testTimerButtons() {
		LOGGER.info("Timer buttons test start.");
		super.logInAsTestUser();
		wait.until(ExpectedConditions.urlContains(HOME_PAGE_URL));
		
		//go to timesheet page, delete all timestamps
		deleteAllTimestamps(false);
		
		driver.findElement(By.name("CHomePage")).click();
		wait.until(ExpectedConditions.urlContains(HOME_PAGE_URL));
		
		//start work
		driver.findElement(By.cssSelector("a[id*='actionButton']")).click();
		//break
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[id*='breakButton']"))).click();
		
		driver.findElement(By.name("timesheetNote")).sendKeys("poznamka");
		
		//stop break
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[id*='breakButton']"))).click();
		//stop work
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[id*='actionButton']"))).click();
		
		//go to timesheet page, check if 3 timestamps are generated, delete generated timestamps
		deleteAllTimestamps(true);
	}
	
	/**
	 * Tests adding users into favorited group.
	 */
	@Test
	public void testFavoriteUsers() {
		LOGGER.info("Favorite users test start.");
		super.logInAsTestUser();
		wait.until(ExpectedConditions.urlContains(HOME_PAGE_URL));
		
		//should not have 'favorited users' tab
		assertTrue(0 == canFindElement(By.cssSelector("a[href='#users_tab_favorite']")));
		
		//click on 'all users' tab to make users visible
		driver.findElement(By.cssSelector("a[href='#users_tab_all']")).click();
		
		//make user favorited
		WebElement userRow = driver.findElement(By.xpath("//div[@id='users_tab_all']/descendant::tr[1]"));
		String userName = userRow.findElement(By.xpath("//descendant::td[2]/span")).getText();
		userRow.findElement(By.cssSelector("a[id^='favourite']")).click();
		
		//must have 'favorited users' tab. click
		assertTrue(1 == canFindElement(By.cssSelector("a[href='#users_tab_favorite']")));
		
		//must be in this tab
		driver.findElement(By.cssSelector("a[href='#users_tab_favorite']")).click();
		WebElement userRow2 = driver.findElement(By.xpath("//div[@id='users_tab_favorite']/descendant::tr[1]"));
		String userName2 = userRow2.findElement(By.xpath("//descendant::td[2]/span")).getText();
		assertEquals(userName, userName2);
		
		//click on 'all users' and unfavorite user
		driver.findElement(By.cssSelector("a[href='#users_tab_all']")).click();
		userRow = driver.findElement(By.xpath("//div[@id='users_tab_all']/descendant::tr[1]"));
		userRow.findElement(By.cssSelector("a[id^='favourite']")).click();
		
		//should not have 'favorited users' tab
		wait.until(ExpectedConditions.stalenessOf(userRow2));
		assertTrue(0 == canFindElement(By.cssSelector("a[href='#users_tab_favorite']")));
	}
	
	/**
	 * Tests funcionality of timer panel.
	 */
	@Test
	public void testTimerPanel() {
		LOGGER.info("Timer panel test start.");
		super.logInAsTestUser();
		wait.until(ExpectedConditions.urlContains(HOME_PAGE_URL));
		
		//start some timestamp
		driver.findElement(By.cssSelector("a[id*='actionButton']")).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[id*='breakButton']")));
		
		//timer panel should be green, buttons not visible
		assertTrue(driver.findElement(By.cssSelector("form[id^='timesheetForm']")).getAttribute("class").contains("panel-in-work"));
		List<WebElement> buttons = driver.findElements(By.xpath("//div[@id='timerPanel_div']/descendant::div[@class='row'][3]/descendant::button"));
		for (WebElement e : buttons) {
			assertEquals(ELEMENT_UNSEEN, e.getAttribute("style"));
		}
		
		//change note in timer panel
		driver.findElement(By.name("timesheetNote")).clear();
		driver.findElement(By.name("timesheetNote")).sendKeys("test");
		
		//timer panel should be red and offer 3 buttons, timer buttons gray
		wait.until(ExpectedConditions.elementToBeClickable(By.name("changeButton")));
		assertTrue(driver.findElement(By.cssSelector("form[id^='timesheetForm']")).getAttribute("class").contains("panel-change"));
		assertTrue(3 == canFindElement(By.cssSelector("span[class*='disabled']")));
		
		//click the change button, should be green
		driver.findElement(By.name("changeButton")).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[id*='breakButton']")));
		assertTrue(driver.findElement(By.cssSelector("form[id^='timesheetForm']")).getAttribute("class").contains("panel-in-work"));
		
		//stop the timestamp
		driver.findElement(By.cssSelector("a[id*='actionButton']")).click();
		
		//delete timestamps
		deleteAllTimestamps(false);
	}
}
