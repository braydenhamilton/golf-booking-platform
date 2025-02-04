package com.golf.teetimecoreapi.resource;

import com.golf.api.BookingApi;
import com.golf.model.BookingConfiguration;
import com.golf.model.NewUserConfiguration;
import com.golf.model.User;
import com.golf.teetimecoreapi.service.BookingService;
import com.golf.teetimecoreapi.session.UserSessionStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final BookingService bookingService;

    @Autowired
    public BookingResource(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public ResponseEntity<BookingConfiguration> makeBooking(@Valid @RequestBody BookingConfiguration bookingConfiguration) {
        LOGGER.info("Received booking request: " + bookingConfiguration);

        User user = UserSessionStore.getUserSession();

        if (user == null) {
            LOGGER.error("User not logged in");
            //Unauthorized, invalid credentials
            return ResponseEntity.status(401).build();
        }

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

            bookingService.nzGolfLogin(driver, wait, user);
            Thread.sleep(2000);  // Wait for login to complete

            // Navigate to booking page
            driver.get("https://www.golf.co.nz/Teebooking/SearchClubDay.aspx");
            Thread.sleep(2000);  // Wait for the page to load

            // Find the course by name
            WebElement selectedCourse = bookingService.findCourseElement(driver, course);

            if (selectedCourse == null) {
                System.out.println("Course not found: " + course);
                return ResponseEntity.badRequest().build();
            }

            // Locate the row containing the selected course
            WebElement courseRow = selectedCourse.findElement(By.xpath("./parent::td/parent::tr"));

            // Date finding
            WebElement dateCell = bookingService.findDateCell(driver, wait, courseRow, date);
            if (dateCell != null) {
                dateCell.click();
            } else {
                LOGGER.error("Could not find available booking slot for date: " + date);
                return ResponseEntity.badRequest().build();
            }

            Thread.sleep(2000);  // Wait for navigation to booking sheet

            // Locate and book the desired time slot
            WebElement bookingLink = bookingService.findTimeSlot(driver, wait, time);
            if (bookingLink != null) {
                LOGGER.info("Booking link found for time slot: " + time);
                bookingLink.click();
            } else {
                LOGGER.error("Could not find booking link for time slot: " + time);
                return ResponseEntity.badRequest().build();
            }

            Thread.sleep(2000);  // Wait for booking to complete


            // Submit the booking
            WebElement submitButton = bookingService.findSubmitButton(driver, wait);
            if (submitButton != null) {
                submitButton.click();
            } else {
                LOGGER.error("Could not find submit button for booking");
                return ResponseEntity.badRequest().build();
            }

            LOGGER.info("Successfully booked time slot: " + time);

            // Handle post-booking logic
            Thread.sleep(2000);  // Wait for booking to complete

            // one more step finalising booking
            WebElement finaliseButton = bookingService.findFinaliseButton(driver, wait);
            if (finaliseButton != null) {
                finaliseButton.click();
                Thread.sleep(2000);  // Wait for finalisation to complete
                LOGGER.info("Successfully finalised booking");
            } else {
                LOGGER.error("Could not find finalise button");
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(bookingConfiguration);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } finally {
            driver.quit();
        }
    }

    private WebDriver initialiseWebDriver() {
        System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver-win64/chromedriver.exe"); // Update the path
        return new ChromeDriver();
    }
}
