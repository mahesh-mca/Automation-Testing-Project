package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class MultiBrowserParallelTest {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<WebDriverWait> wait = new ThreadLocal<>();
    private static ThreadLocal<String> browserName = new ThreadLocal<>();

    public WebDriver getDriver() {
        return driver.get();
    }

    @Parameters("browser")
    @BeforeMethod
    public void setupBrowser(@Optional("chrome") String browser) {

        WebDriver wd;

        switch (browser.toLowerCase()) {

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                wd = new FirefoxDriver();
                browserName.set("Firefox");

                // LEFT SCREEN
                wd.manage().window().setPosition(new Point(0, 0));
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                wd = new EdgeDriver();
                browserName.set("Edge");

                // RIGHT SCREEN
                wd.manage().window().setPosition(new Point(1280, 0));
                break;

            default:
                WebDriverManager.chromedriver().setup();
                wd = new ChromeDriver();
                browserName.set("Chrome");

                // CENTER SCREEN
                wd.manage().window().setPosition(new Point(640, 0));
        }

        // SAME SIZE FOR ALL
        wd.manage().window().setSize(new Dimension(640, 800));

        driver.set(wd);
        wait.set(new WebDriverWait(wd, Duration.ofSeconds(15)));
    }

    // 🔥 COMMON FLOW
    public void runFlow(String testName) throws Exception {

        System.out.println(browserName.get() + " running " + testName);

        getDriver().get("https://www.saucedemo.com/");
        Thread.sleep(2000);

        // LOGIN
        getDriver().findElement(By.id("user-name")).sendKeys("standard_user");
        Thread.sleep(1000);

        getDriver().findElement(By.id("password")).sendKeys("secret_sauce");
        Thread.sleep(1000);

        getDriver().findElement(By.id("login-button")).click();
        Thread.sleep(2000);

        // ADD PRODUCT
        WebElement addBtn = wait.get().until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".btn_inventory")));
        addBtn.click();

        Thread.sleep(2000);
    }

    // ✅ TEST CASES (4)
    @Test
    public void testExecution1() throws Exception {
        runFlow("Test 1");
    }

    @Test
    public void testExecution2() throws Exception {
        runFlow("Test 2");
    }

    @Test
    public void testExecution3() throws Exception {
        runFlow("Test 3");
    }

    @Test
    public void testExecution4() throws Exception {
        runFlow("Test 4");
    }

    @AfterMethod
    public void tearDown() {

        try {
            Thread.sleep(2000); // keep visible before closing
        } catch (Exception e) {}

        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }
}