package steps;

import database.DatabaseManager;
import driverManager.DriverManager;
import io.cucumber.java.ru.И;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.MainPage;
import pages.ProductListPage;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class CommonSteps {
    private final DatabaseManager databaseSteps = new DatabaseManager();
    private final WebDriver driver = DriverManager.getDriver();
    private String baseUrl;
    private ProductListPage productListPage;
    private int productsCount;

    @И("Переходим на страницу 'Товары'")
    public void goProductListPage() {
        baseUrl = driver.getCurrentUrl();
        MainPage mainPage = new MainPage(driver);
        mainPage.selectSandboxItem("Товары");
    }

    @И("Проверяем URL страницы 'Товары'")
    public void checkUrl() {
        productListPage = new ProductListPage(driver);
        assertEquals(baseUrl + "food", driver.getCurrentUrl(), "Текущий url не совпадает с url страницы списка товаров");
    }

    @И("Получаем количество продуктов на странице")
    public void getCountProduct() {
        productsCount = productListPage.getProducts().size();
    }

    @И("Добавляем новый товар {string} {string} {string} и проверяем, что он добавлен верно")
    public void addProduct(String foodName, String foodType, String exotic) {
        productListPage.buttonAddClick();
        assertFalse(productListPage.getExoticCheckbox().isSelected(), "Неверное состояние чекбокса");
        productListPage.fillProductNameField(foodName);
        productListPage.selectProductType(foodType);
        productListPage.selectExotic(Boolean.parseBoolean(exotic));
        productListPage.buttonSaveClick();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.invisibilityOf(productListPage.getExoticCheckbox()));

        assertTrue(productListPage.assertNewElement(foodName, foodType, Boolean.parseBoolean(exotic)), "Последний продукт не совпадает с переданными данными");
    }


    @И("Сбрасываем данные")
    public void reset() {
        productListPage.selectSandboxItem("Сброс данных");
    }


    @И("Проверяем, что количество продуктов ДО добавления равно количеству ПОСЛЕ сброса данных")
    public void checkCountProduct() {
        int maxAttempts = 5; // Максимальное количество попыток
        int attempt = 0;
        boolean isCountCorrect = false;

        while (attempt < maxAttempts) {
            try {
                // Попытка проверить количество продуктов
                assertEquals(productsCount, productListPage.getProducts().size());
                isCountCorrect = true;
                break;  // Если проверка прошла успешно, выходим из цикла
            } catch (AssertionError e) {
                attempt++;
                if (attempt >= maxAttempts) {
                    throw new AssertionError("Количество продуктов не совпадает после " + maxAttempts + " попыток");
                }
                // Задержка перед следующей попыткой
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

        if (!isCountCorrect) {
            // Если после всех попыток количество не совпало, можно выбросить ошибку
            throw new AssertionError("Количество продуктов не соответствует ожидаемому");
        }
    }

    @И("Проверяем, что количество строк с товаром {string} равно {int}")
    public void assertRowsCountByFoodName(String foodName, int expectedCount) {
        assertNotNull(databaseSteps, "Database manager is not initialized");

        assertEquals(expectedCount, databaseSteps.getFoodCount(foodName), "Количество товаров не соответствует");
    }

    @И("Удалить из БД последний добавленный товар с именем {string}")
    public void deleteLastAddedFood(String foodName) {
        databaseSteps.deleteLastAddedFood(foodName);
    }

}
