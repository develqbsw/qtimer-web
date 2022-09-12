package sk.qbsw.sed.test.dao;

import static org.junit.Assert.*;

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
 * Class for testing user projects page.
 * @author moravcik
 *
 */
public class CTestUserProjectPage extends ASeleniumTest {

	@Before
	public void setUp() {
		super.setUp();
		LOGGER = LoggerFactory.getLogger(CTestUserProjectPage.class);
		LOGGER.info("Setting up CUserProjectPageTest.");
	}
	
	@After
	public void tearDown() {
		super.tearDown();
	}

	/**
	 * Test makes project a favorite project.
	 * @throws InterruptedException 
	 */
	@Test
	public void testMakeFavorite() {
		LOGGER.info("Make project favorite test.");	
		//log in, get users projects page
		super.logInAsTestUser();
		assertTrue(driver.getCurrentUrl().contains(HOME_PAGE_URL));
		driver.findElement(By.name("CUserProjectPage")).click();
		wait.until(ExpectedConditions.urlContains(USERPROJECTS_PAGE_URL));
		
		List<WebElement> rows = null;
		//list only users favorite projects and save count
		rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		new Select(driver.findElement(By.name("projectId"))).selectByValue("1");
		wait.until(ExpectedConditions.stalenessOf(rows.get(0)));
		
		rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		int favoriteCount = rows.size();
		
		//list all projects
		new Select(driver.findElement(By.name("projectId"))).selectByValue("0");
		wait.until(ExpectedConditions.stalenessOf(rows.get(0)));
		
		//find unfavorited project and make it favorite
		String usedProject = null;
		rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		for (int i = 1; i < rows.size(); i++) {
			if (rows.get(i).findElement(By.tagName("i")).getAttribute("class").equals("fa fa-star-o")) { //if not favorite
				usedProject = rows.get(i).findElements(By.cssSelector("div[class='imxt-a imxt-nowrap']")).get(1).getText();
				rows.get(i).findElement(By.tagName("a")).click();
				wait.until(ExpectedConditions.stalenessOf(rows.get(i)));
				break;
			}
		}
		
		//list only users favorite project
		rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		new Select(driver.findElement(By.name("projectId"))).selectByValue("1");
		wait.until(ExpectedConditions.stalenessOf(rows.get(0)));
		
		rows = driver.findElements(By.xpath("//fieldset/descendant::tr"));
		assertTrue(rows.size() == favoriteCount+1);
		
		//make just favorited project unfavorited
		for (int i = 1; i < rows.size(); i++) {
			if (rows.get(i).findElements(By.cssSelector("div[class='imxt-a imxt-nowrap']")).get(1)
					.getText().equals(usedProject)) {
				rows.get(i).findElement(By.tagName("a")).click();
				wait.until(ExpectedConditions.stalenessOf(rows.get(i)));
				break;
			}
		}
		//number of favorite projects must be the same
		assertEquals(favoriteCount, driver.findElements(By.xpath("//fieldset/descendant::tr")).size());
	}
	
}
