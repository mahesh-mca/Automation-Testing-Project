package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.Duration;
import java.util.List;

/* EXTENT REPORT IMPORTS */

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class EcommerceTest {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    /* EXTENT REPORT OBJECTS */

    ExtentReports extent;
    ExtentTest test;
    ExtentSparkReporter spark;

    @BeforeClass
    public void setUp() {

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);

        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;

        /* EXTENT REPORT SETUP */

        spark = new ExtentSparkReporter("ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    private void highlight(WebElement element, String color) throws InterruptedException {
        js.executeScript("arguments[0].style.border='3px solid " + color + "'", element);
        Thread.sleep(1200);
    }

    private void flashAndClick(WebElement element) throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            js.executeScript("arguments[0].style.background='orange'", element);
            Thread.sleep(300);
            js.executeScript("arguments[0].style.background=''", element);
            Thread.sleep(300);
        }
        element.click();
        Thread.sleep(1500);
    }

    private void showBanner(String message) {
        js.executeScript(
                "let msg=document.createElement('div');" +
                        "msg.innerHTML='" + message + "';" +
                        "msg.style.position='fixed';" +
                        "msg.style.top='10px'; msg.style.left='50%';" +
                        "msg.style.transform='translateX(-50%)';" +
                        "msg.style.background='yellow';" +
                        "msg.style.color='black';" +
                        "msg.style.padding='10px 20px';" +
                        "msg.style.fontSize='16px';" +
                        "msg.style.zIndex='9999';" +
                        "msg.style.borderRadius='8px';" +
                        "document.body.appendChild(msg);" +
                        "setTimeout(()=>{msg.remove();},2000);"
        );
    }

    private void addProductsFromCategory(String categoryName) throws InterruptedException {

        showBanner("Category: " + categoryName);
        Thread.sleep(1500);

        List<WebElement> addButtons = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("button.btn_inventory"))
        );

        int count = 0;

        for (WebElement btn : addButtons) {

            js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'})", btn);

            highlight(btn, "red");

            flashAndClick(btn);

            count++;
        }

        test.pass("Products added in category: " + categoryName + " Count: " + count);
    }

    @Test
    public void fullDemoLoginAddCheckoutLogout() throws Exception {

        test = extent.createTest("SwagLabs Ecommerce Automation Test");

        driver.get("https://www.saucedemo.com/");

        test.info("Application launched");

        String path1 = takeScreenshot("01_Login_Page");
        test.addScreenCaptureFromPath(path1);

        /* LOGIN */

        showBanner("Step 1: Login");

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));

        highlight(username, "blue");

        username.sendKeys("standard_user");

        WebElement password = driver.findElement(By.id("password"));

        highlight(password, "blue");

        password.sendKeys("secret_sauce");

        WebElement loginBtn = driver.findElement(By.id("login-button"));

        highlight(loginBtn, "green");

        flashAndClick(loginBtn);

        test.pass("Login successful");

        String path2 = takeScreenshot("02_After_Login");
        test.addScreenCaptureFromPath(path2);

        /* ADD PRODUCTS */

        addProductsFromCategory("All Products");

        String path3 = takeScreenshot("03_Products_Added");
        test.addScreenCaptureFromPath(path3);

        /* CART */

        showBanner("Step 2: Go to Cart");

        WebElement cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.className("shopping_cart_link")));

        highlight(cartLink, "red");

        flashAndClick(cartLink);

        test.pass("Navigated to cart page");

        String path4 = takeScreenshot("04_Cart_Page");
        test.addScreenCaptureFromPath(path4);

        /* CHECKOUT */

        showBanner("Step 3: Checkout");

        WebElement checkoutBtn = driver.findElement(By.id("checkout"));

        highlight(checkoutBtn, "green");

        flashAndClick(checkoutBtn);

        WebElement firstName = driver.findElement(By.id("first-name"));
        firstName.sendKeys("Demo");

        WebElement lastName = driver.findElement(By.id("last-name"));
        lastName.sendKeys("User");

        WebElement postalCode = driver.findElement(By.id("postal-code"));
        postalCode.sendKeys("12345");

        WebElement continueBtn = driver.findElement(By.id("continue"));

        flashAndClick(continueBtn);

        String path5 = takeScreenshot("05_Checkout_Overview");
        test.addScreenCaptureFromPath(path5);

        WebElement finishBtn = driver.findElement(By.id("finish"));

        flashAndClick(finishBtn);

        test.pass("Order placed successfully");

        String path6 = takeScreenshot("06_Order_Completed");
        test.addScreenCaptureFromPath(path6);

        /* LOGOUT */

        showBanner("Step 4: Logout");

        WebElement menuBtn = driver.findElement(By.id("react-burger-menu-btn"));

        flashAndClick(menuBtn);

        WebElement logoutBtn = driver.findElement(By.id("logout_sidebar_link"));

        flashAndClick(logoutBtn);

        test.pass("Logout successful");

        String path7 = takeScreenshot("07_Logout_Page");
        test.addScreenCaptureFromPath(path7);
    }

    public String takeScreenshot(String fileName) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        File folder = new File("Screenshots");

        if (!folder.exists()) {
            folder.mkdir();
        }

        String path = "Screenshots/" + fileName + "_" + timeStamp + ".png";

        File dest = new File(path);

        FileUtils.copyFile(src, dest);

        return path;
    }

    @AfterClass
    public void tearDown() {

        extent.flush();

        if (driver != null)
            driver.quit();
    }
}