package com.pack1;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class AddNewBiz {

    private WebDriver driver;
    private ScreenRecorder screenRecorder;

    @Test
    public void AddBiz() throws Exception {

        startRecording("Add_New_Business_Test");

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1280,1024");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        // DO NOT use --headless when recording

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));

        try {
            executeBusinessFlow(wait);
        } catch (Exception e) {
            takeScreenshot("Failure_Log");
            throw e;
        } finally {
            stopRecording();
            attachVideoToAllure();
            if (driver != null) driver.quit();
        }
    }

    @Step("Executing Add Business Flow")
    private void executeBusinessFlow(WebDriverWait wait) throws Exception {

        driver.get("https://yogi.web.cashbook.in/login");

        List<WebElement> checkHeading =
                driver.findElements(By.xpath("//h4[text()='Choose one option to continue']"));

        if (!checkHeading.isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[text()='Other ways to login']"))).click();

            new Select(driver.findElement(By.name("phoneNumberCountry")))
                    .selectByValue("IN");

            driver.findElement(By.id("phoneNumber")).sendKeys("1000113114");
        } else {
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.className("PhoneInputCountry"))).click();

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//option[text()='India']"))).click();

            driver.findElement(By.id("phoneNumber")).sendKeys("1000113184");
        }
        Thread.sleep(8000);
        driver.findElement(By.id("submitPhoneNumber")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("code")))
                .sendKeys("123456");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        try {
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[text()='Ok, Got it']"))).click();
        } catch (TimeoutException ignored) {}
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='box_position_relative_xs__1dl117711e'])[1]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Add New Business']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")))
                .sendKeys("AutomationBiz");

        driver.findElement(By.xpath("//p[text()='Construction']")).click();
        driver.findElement(By.xpath("//p[text()='Manufacturer']")).click();
        driver.findElement(By.xpath("//button[text()='Create Business']")).click();
    }

    // ================== SCREEN RECORDING ==================

    public void startRecording(String name) throws Exception {

        File dir = new File("recordings");
        if (!dir.exists()) dir.mkdirs();

        java.awt.Rectangle screenSize = new java.awt.Rectangle(0, 0, 1280, 1024);

        GraphicsConfiguration gc = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        screenRecorder = new SpecializedScreenRecorder(
                gc,
                screenSize,
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO,
                        EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        DepthKey, 24,
                        FrameRateKey, Rational.valueOf(15),
                        QualityKey, 1.0f),
                null,
                null,
                dir,
                name
        );

        screenRecorder.start();
    }

    public void stopRecording() throws Exception {
        if (screenRecorder != null) {
            screenRecorder.stop();
        }
    }

    // ================== SCREENSHOT ==================

    public void takeScreenshot(String name) throws IOException {

        File screenshotDir = new File("screenshots");
        if (!screenshotDir.exists()) screenshotDir.mkdirs();

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(
                src,
                new File(screenshotDir,
                        name + "_" +
                                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png")
        );
    }

    @Attachment(value = "Execution Video", type = "video/avi")
    public byte[] attachVideoToAllure() throws IOException {

        File dir = new File("recordings");
        File[] files = dir.listFiles();

        return (files != null && files.length > 0)
                ? Files.readAllBytes(files[files.length - 1].toPath())
                : new byte[0];
    }
}
