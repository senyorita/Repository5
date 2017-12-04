package Test.QATestLab5;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;

public class Script {

	final static String urlMain = "http://prestashop-automation.qatestlab.com.ua/";
	RemoteWebDriver remoteDriver;
	WebDriverWait waitElement;
	int randomItem;
	int quantity;
	String nameItem;
	String price;
	String nameOriginalItem;

	String firstNameCustomer = "Tom";
	String lastNameCustomer = "Ford";
	String emailCustomer = "bigTom@gmail.com";
	String addressCustomer = "bou. Shevchenko 25";
	String postcodeCustomer = "86456";
	String cityCustomer = "Kiev";

	@Test
	public void checkSite() {

		remoteDriver.get(urlMain);
		WebElement titleSite = remoteDriver.findElement(By.xpath("//div[@class='hidden-md-up text-xs-center mobile']"));
		if (titleSite.isDisplayed())
			System.out.println("Мобильная версия сайта");
		else
			System.out.println("Десктопная версия сайта");

	}

	@Test(dependsOnMethods = "checkSite")
	public void allProducts() {

		WebElement buttonAllProducts = remoteDriver.findElement(By.xpath("//section[@id='content']/section/a"));
		buttonAllProducts.click();

	}

	@Test(dependsOnMethods = "allProducts")
	public void randomItems() {

		List<WebElement> listItems = remoteDriver.findElements(By.xpath("//div[@class='products row']//h1/a"));
		randomItem = (int) (Math.random() * listItems.size());
		listItems.get(randomItem).click();
		remoteDriver.findElement(By.xpath("//a[@href = '#product-details']")).click();

		WebElement quantityItem = waitElement.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='product-quantities']/span")));
		String quant = quantityItem.getText();
		quantity = Integer.parseInt((quant.substring(0, quant.length() - 7)));
		nameItem = remoteDriver.findElement(By.xpath("//h1[@itemprop='name']")).getText();

		WebElement elementPrice = remoteDriver.findElement(By.xpath("//span[@itemprop='price']"));
		price = elementPrice.getText().substring(0, elementPrice.getText().length() - 2);

		WebElement chipBasket = remoteDriver.findElement(By.xpath("//button[@class='btn btn-primary add-to-cart']"));
		chipBasket.click();

	}

	@Test(dependsOnMethods = "randomItems")
	public void buttonOrderItems() {

		WebElement buttonOrder = waitElement
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-primary']")));
		buttonOrder.click();
		WebElement nameItem = remoteDriver.findElement(By.xpath("//div[@class='product-line-info']/a[@class='label']"));
		nameOriginalItem = nameItem.getText();
		Assert.assertEquals(nameOriginalItem.toUpperCase(), this.nameItem.toUpperCase(),
				"Название добавленного товара не соответствует названию исходного товара");

		WebElement priceItem = remoteDriver
				.findElement(By.xpath("//div[@class='product-line-grid-body col-md-4 col-xs-8']/div[2]/span"));
		String price = priceItem.getText().substring(0, priceItem.getText().length() - 2);
		Assert.assertEquals(price, this.price, "Цена добавленного товара не соответствует цене исходного товара");

		WebElement quantityItem = remoteDriver.findElement(By.xpath("//span[@class='label js-subtotal']"));
		String quantity = quantityItem.getText().substring(0, quantityItem.getText().length() - 4);

		Assert.assertEquals(quantity, "1",
				"Количество добавленного товара не соответствует количеству исходного товара");

		WebElement buttonOrderItem = remoteDriver.findElement(By.xpath("//a[@class='btn btn-primary']"));
		buttonOrderItem.click();

	}

	@Test(dependsOnMethods = "buttonOrderItems")
	public void registrationOfPurchase() {

		WebElement fieldFirstNameCustomer = remoteDriver.findElement(By.xpath("//input[@name='firstname']"));
		fieldFirstNameCustomer.sendKeys(this.firstNameCustomer);

		WebElement fieldLastNameCustomer = remoteDriver.findElement(By.xpath("//input[@name='lastname']"));
		fieldLastNameCustomer.sendKeys(this.lastNameCustomer);

		WebElement fieldEmailCustomer = remoteDriver
				.findElement(By.xpath("//div[@id='checkout-guest-form']//input[@name='email']"));
		fieldEmailCustomer.sendKeys(this.emailCustomer);

		WebElement buttonContinue = remoteDriver.findElement(By.xpath("//form[@id='customer-form']/footer/button"));
		buttonContinue.click();

		WebElement fieldAddressCustomer = remoteDriver.findElement(By.xpath("//input[@name='address1']"));
		fieldAddressCustomer.sendKeys(this.addressCustomer);

		WebElement fieldPostcodeCustomer = remoteDriver.findElement(By.xpath("//input[@name='postcode']"));
		fieldPostcodeCustomer.sendKeys(this.postcodeCustomer);

		WebElement fieldCityCustomer = remoteDriver.findElement(By.xpath("//input[@name='city']"));
		fieldCityCustomer.sendKeys(this.cityCustomer);

		WebElement buttonContinue2 = remoteDriver.findElement(By.xpath("//button[@name='confirm-addresses']"));
		buttonContinue2.click();

		WebElement buttonContinue3 = remoteDriver.findElement(By.xpath("//button[@name='confirmDeliveryOption']"));
		buttonContinue3.click();

		WebElement buttonPaymentCheck = remoteDriver.findElement(By.xpath("//input[@id='payment-option-1']"));
		buttonPaymentCheck.click();

		WebElement buttonOK = remoteDriver
				.findElement(By.xpath("//input[@id='conditions_to_approve[terms-and-conditions]']"));
		buttonOK.click();

		WebElement buttonOrderPayment = waitElement.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='btn btn-primary center-block']")));
		buttonOrderPayment.click();

	}

	@Test(dependsOnMethods = "registrationOfPurchase")
	public void confirmationOrder() {

		WebElement titleOrder = remoteDriver.findElement(By.xpath("//h3[@class='h1 card-title']"));

		Assert.assertEquals(titleOrder.getText().substring(1, titleOrder.getText().length()), "ВАШ ЗАКАЗ ПОДТВЕРЖДЁН",
				"Пользователю отображается некорректное сообщение");

		WebElement titleBlouse = remoteDriver.findElement(By.xpath("//div[@class='col-sm-4 col-xs-9 details']/span"));

		Assert.assertEquals(titleBlouse.getText().substring(0, nameOriginalItem.length()), nameOriginalItem,
				"Пользователю отображается некорректное название товара");

		WebElement titlePrice = remoteDriver
				.findElement(By.xpath("//div[@class='col-xs-5 text-sm-right text-xs-left']"));

		Assert.assertEquals(titlePrice.getText().substring(0, titlePrice.getText().length() - 2), price,
				"Пользователю отображается некорректная цена товара");

		WebElement titleAmount = remoteDriver.findElement(By.xpath("//div[@class='col-xs-2']"));

		Assert.assertEquals(titleAmount.getText(), "1", "Пользователю отображается некорректная позиция товара");
	}

	@Test(dependsOnMethods = "confirmationOrder")
	public void BackToProduct() {

		WebElement buttonAllProduct = remoteDriver.findElement(By.xpath("//section/a"));
		buttonAllProduct.click();

		WebElement titleSameProduct = remoteDriver.findElement(By.linkText(nameOriginalItem));

		titleSameProduct.click();

		remoteDriver.findElement(By.xpath("//a[@href = '#product-details']")).click();

		WebElement titleStockProduct = waitElement.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='product-quantities']/span")));

		Assert.assertEquals(
				Integer.parseInt(titleStockProduct.getText().substring(0, titleStockProduct.getText().length() - 7)),
				quantity - 1, "Количество товара отображается неверно");

	}

	@BeforeTest
	@Parameters("browser")
	public void getBrowser(String browser) throws MalformedURLException {

		switch (browser) {
		case "Chrome": {
			DesiredCapabilities cap = DesiredCapabilities.chrome();
			remoteDriver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
			remoteDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			remoteDriver.manage().window().maximize();
			waitElement = new WebDriverWait(remoteDriver, 10);
			break;

		}
		case "Firefox": {
			DesiredCapabilities cap = DesiredCapabilities.firefox();
			remoteDriver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
			remoteDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			remoteDriver.manage().window().maximize();
			waitElement = new WebDriverWait(remoteDriver, 10);
			break;

		}
		case "InternetExplorer": {
			DesiredCapabilities cap = DesiredCapabilities.internetExplorer();
			cap.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
			remoteDriver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
			remoteDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			remoteDriver.manage().window().maximize();
			waitElement = new WebDriverWait(remoteDriver, 10);
			break;

		}
		case "Android": {
			DesiredCapabilities cap = DesiredCapabilities.android();
			remoteDriver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
			remoteDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			waitElement = new WebDriverWait(remoteDriver, 10);
			break;
		}
		}
	}

	@AfterTest
	public void afterTest() {
		remoteDriver.quit();
	}

}
