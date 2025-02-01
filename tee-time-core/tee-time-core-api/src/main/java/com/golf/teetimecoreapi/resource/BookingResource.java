package com.golf.teetimecoreapi.resource;

import com.golf.api.BookingApi;
import com.golf.model.BookingConfiguration;
import com.golf.model.NewUserConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class BookingResource implements BookingApi {

    private static final Log LOGGER = LogFactory.getLog(BookingResource.class);

    @Override
    public ResponseEntity<BookingConfiguration> makeBooking(@Valid @RequestBody BookingConfiguration bookingConfiguration) {
        LOGGER.info("Received booking request: " + bookingConfiguration);

        // Get report parameters
        Date date = bookingConfiguration.getDate();
        String time = bookingConfiguration.getTime();
        String course = bookingConfiguration.getCourse();
        Integer players = bookingConfiguration.getPlayers(); // TODO: implement adding players to booking

        // Execute booking logic
        WebDriver driver = initialiseWebDriver();

        try {
            driver.get("https://www.golf.co.nz/my-golf-login");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));  // WebDriverWait for element visibility
            System.out.println("Navigated to login page");
            
            // Login procedure
            WebElement membershipNumberField = findMembershipNumberField(driver, wait);
            WebElement passwordField = findPasswordField(driver, wait);
            WebElement loginButton = findLoginButton(driver, wait);

            // Enter login details

            membershipNumberField.sendKeys("4676144"); // NewUserConfiguration.getMembershipNumber());
            passwordField.sendKeys("Sard3nia");  //NewUserConfiguration.getGolfPassword()); // *******
            loginButton.click();
            Thread.sleep(2000);  // Wait for login to complete

            // Navigate to booking page
            driver.get("https://www.golf.co.nz/Teebooking/SearchClubDay.aspx");
            Thread.sleep(2000);  // Wait for the page to load

            // Find the course by name
            WebElement selectedCourse = findCourseElement(driver, course);


            if (selectedCourse == null) {
                System.out.println("Course not found: " + course);
                return ResponseEntity.badRequest().build();
            }

            // Locate the row containing the selected course
            WebElement courseRow = selectedCourse.findElement(By.xpath("./ancestor::tr"));

            // Date finding
            WebElement dateCell = findDateCell(driver, wait, date);
            if (dateCell != null) {
                dateCell.click();
            } else {
                LOGGER.error("Could not find available booking slot for date: " + date);
                return ResponseEntity.badRequest().build();
            }

            Thread.sleep(2000);  // Wait for navigation to booking sheet

            // Locate and book the desired time slot
            WebElement timeSlot = driver.findElement(By.xpath("//button[@data-time='" + time + "']"));
            timeSlot.click();

            LOGGER.info("Successfully booked time slot: " + time);

            // Handle post-booking logic
            Thread.sleep(2000);  // Wait for booking to complete

            return ResponseEntity.ok(bookingConfiguration);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } finally {
            driver.quit();
        }
    }

    public WebElement findDateCell(WebDriver driver, WebDriverWait wait, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(date);

        List<By> locators = Arrays.asList(
                By.xpath(String.format("//td[contains(@onmouseover, '%s') and contains(@class, 'available')]", formattedDate)),
                By.cssSelector(String.format("td.available[onmouseover*='%s']", formattedDate)),
                By.xpath(String.format("//td[contains(@onclick, '%s')]", formattedDate))
        );

        for (By locator : locators) {
            try {
                return wait.until(ExpectedConditions.elementToBeClickable(locator));
            } catch (TimeoutException e) {
                LOGGER.error("Could not find date cell with locator: " + locator);
                continue;
            }
        }
        throw new RuntimeException("Could not find available date cell for: " + formattedDate);
    }

    public WebElement findCourseElement(WebDriver driver, String course) {
        List<WebElement> courseElements = driver.findElements(By.cssSelector(".club_info.course-name span"));
        for (WebElement courseElement : courseElements) {
            if (courseElement.getText().equalsIgnoreCase(course)) {
                return courseElement;
            }
        }
        return null;
    }

    public WebElement findMembershipNumberField(WebDriver driver, WebDriverWait wait) {
        List<By> locators = Arrays.asList(
                By.xpath("//input[contains(@class, 'form-control') and contains(@id, 'tbMembershipNumber')]"),
                By.cssSelector("input[placeholder='e.g. 12343243']"),
                By.name("ctl*$tbMembershipNumber")  // Partial name matching
        );

        for (By locator : locators) {
            try {
                return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            } catch (TimeoutException e) {
                // Log error finding element and continue to next locator
                LOGGER.error("Could not find membership number input field with locator: " + locator);
                return null;
            }
        }
        throw new RuntimeException("Could not find membership number input field");
    }

    public WebElement findPasswordField(WebDriver driver, WebDriverWait wait) {
        List<By> locators = Arrays.asList(
                By.xpath("//input[contains(@class, 'form-control') and contains(@type, 'password')]"),
                By.cssSelector("input[type='password']"),
                By.name("ctl*$tbPassword"),
                By.xpath("//label[text()='Password']/following-sibling::input[@type='password']")
        );

        for (By locator : locators) {
            try {
                return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            } catch (TimeoutException e) {
                LOGGER.error("Could not find password input field with locator: " + locator);
                return null;
            }
        }
        throw new RuntimeException("Could not find password input field");
    }

    public WebElement findLoginButton(WebDriver driver, WebDriverWait wait) {
        List<By> locators = Arrays.asList(
                By.xpath("//input[@type='submit' and @value='Login']"),
                By.cssSelector("input.btn-primary.btn-block[value='Login']"),
                By.name("ctl*$btnLogin"),
                By.id("ctl61_btnLogin"),
                By.xpath("//input[contains(@class, 'btn-primary') and contains(@class, 'btn-block')]")
        );

        for (By locator : locators) {
            try {
                return wait.until(ExpectedConditions.elementToBeClickable(locator));
            } catch (TimeoutException e) {
                LOGGER.error("Could not find login button with locator: " + locator);
                return null;
            }
        }
        throw new RuntimeException("Could not find login button");
    }

    private WebDriver initialiseWebDriver() {
        System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver-win64/chromedriver.exe"); // Update the path
        return new ChromeDriver();
    }
}
