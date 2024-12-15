package steps;

import database.DatabaseManager;
import driverManager.DriverManager;
import io.cucumber.java.ru.И;
import org.openqa.selenium.WebDriver;
import pages.MainPage;
import pages.ProductListPage;

import static org.junit.jupiter.api.Assertions.*;

public class CommonSteps {
    private final DatabaseManager databaseSteps = new DatabaseManager();
    private final WebDriver driver = DriverManager.getDriver();
    private ProductListPage productListPage;
    private int productsCount;

    @И("Переходим на страницу 'Товары'")
    public void goProductListPage() {
        MainPage mainPage = new MainPage(driver);
        mainPage.selectSandboxItem("Товары");
    }

    @И("Проверяем URL страницы 'Товары'")
    public void checkUrl() {
        productListPage = new ProductListPage(driver);
        assertEquals("http://localhost:8080/food", driver.getCurrentUrl(), "Текущий url не совпадает с url страницы списка товаров");
    }

    @И("Проверяем URL страницы 'Товары' через Selenoid")
    public void checkUrlRemote() {
        productListPage = new ProductListPage(driver);
        assertEquals("https://qualit.appline.ru/food", driver.getCurrentUrl(), "Текущий url не совпадает с url страницы списка товаров");
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

        assertTrue(productListPage.assertNewElement(foodName, foodType, Boolean.parseBoolean(exotic)), "Последний продукт не совпадает с переданными данными");
    }


    @И("Сбрасываем данные")
    public void reset() {
        productListPage.selectSandboxItem("Сброс данных");
    }


    @И("Проверяем, что количество продуктов ДО добавления равно количеству ПОСЛЕ сброса данных")
    public void checkCountProduct() {
        assertEquals(productsCount, productListPage.getProducts().size());
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
