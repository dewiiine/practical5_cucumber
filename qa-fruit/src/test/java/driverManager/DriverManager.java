package driverManager;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Properties;

import static Consts.UrlConsts.LOCAL_URL;
import static Consts.UrlConsts.REMOTE_URL;

public class DriverManager {
    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            // Загружаем свойства из файла
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream("src/main/resources/app.properties")) {
                properties.load(fis);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при загрузке файла app.properties", e);
            }

            String browserType = properties.getProperty("type.browser", "chrome");
            String driverType = properties.getProperty("type.driver", "local");

            if ("local".equalsIgnoreCase(driverType)) {
                // Локальный запуск
                driver = initLocalDriver(browserType);
            } else if ("remote".equalsIgnoreCase(driverType)) {
                // Удалённый запуск через Selenoid
                initRemoteDriver(browserType);
            } else {
                throw new IllegalArgumentException("Неверный тип драйвера: " + driverType);
            }

            // Общие настройки
            driver.manage().window().maximize();

            // Открываем URL в зависимости от типа запуска
            String baseUrl = "local".equalsIgnoreCase(driverType) ? LOCAL_URL : REMOTE_URL;
            driver.get(baseUrl);
        }

        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private static WebDriver initLocalDriver(String browserType) {
        WebDriver localDriver;

        switch (browserType.toLowerCase()) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", getChromeDriverPath());
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--remote-allow-origins=*");
                localDriver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
                System.setProperty("webdriver.gecko.driver", getGeckoDriverPath());
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                localDriver = new FirefoxDriver(firefoxOptions);
                break;
            default:
                throw new IllegalArgumentException("Не поддерживаемый браузер: " + browserType);
        }

        return localDriver;
    }

    private static void initRemoteDriver(String browserType) {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        switch (browserType.toLowerCase()) {
            case "chrome":
                capabilities.setBrowserName("chrome");
                break;
            case "firefox":
                capabilities.setBrowserName("firefox");
                break;
            default:
                throw new IllegalArgumentException("Не поддерживаемый браузер: " + browserType);
        }

        capabilities.setCapability("browserVersion", "109.0");
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", false);

        try {
            driver = new RemoteWebDriver(
                    URI.create("http://jenkins.applineselenoid.fvds.ru:4444/wd/hub/").toURL(),
                    capabilities
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static String getChromeDriverPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String driverName;

        if (os.contains("win")) {
            driverName = "chromedriver.exe";
        } else if (os.contains("mac")) {
            driverName = "chromedriver_mac";
        } else if (os.contains("nix") || os.contains("nux")) {
            driverName = "chromedriver_linux";
        } else {
            throw new RuntimeException("ОС не поддерживается: " + os);
        }

        return Paths.get("src", "test", "resources", driverName).toString();
    }

    private static String getGeckoDriverPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String driverName;

        if (os.contains("win")) {
            driverName = "geckodriver.exe";
        } else if (os.contains("mac")) {
            driverName = "geckodriver_mac";
        } else if (os.contains("nix") || os.contains("nux")) {
            driverName = "geckodriver_linux";
        } else {
            throw new RuntimeException("ОС не поддерживается: " + os);
        }

        return Paths.get("src", "test", "resources", driverName).toString();
    }


    /**
     * Метод для инициализации удалённого WebDriver для взаимодействия с Selenoid
     */
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