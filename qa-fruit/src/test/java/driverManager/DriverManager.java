package driverManager;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class DriverManager {
    private static WebDriver driver;

    public static WebDriver getDriver() {
        int a = 2;
        if (a == 1) {
            if (driver == null) {
                System.setProperty("webdriver.chrome.driver", "C:\\Users\\Ирина\\Documents\\JAVA projects\\practical_task3\\qa-fruit\\src\\test\\resources\\chromedriver.exe");
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");

                driver = new ChromeDriver(options);
                driver.manage().window().maximize();
                driver.get("http://localhost:8080/");
            }
        } else if (a == 2) {
            initRemoteDriver();
            driver.manage().window().maximize();
            driver.get("https://qualit.appline.ru");
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private static void initRemoteDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserVersion", "109.0");
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", false);
        capabilities.setBrowserName("chrome");
        capabilities.setVersion("109.0");
        try {
            driver = new RemoteWebDriver(
                    URI.create("http://jenkins.applineselenoid.fvds.ru:4444/wd/hub/").toURL(),
                    capabilities
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        DesiredCapabilities capabilities = new DesiredCapabilities();
//        Map<String, Object> selenoidOptions = new HashMap<>();
//        selenoidOptions.put("browserName", "chrome");
//        selenoidOptions.put("browserVersion", "109.0");
//        selenoidOptions.put("enableVNC", true);
//        selenoidOptions.put("enableVideo", false);
//        capabilities.setCapability("selenoid:options", selenoidOptions);
//        try {
//            driver = new RemoteWebDriver(
//                    URI.create("http://jenkins.applineselenoid.fvds.ru:4444/wd/hub/").toURL(),
//                    capabilities
//            );
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
    }
}