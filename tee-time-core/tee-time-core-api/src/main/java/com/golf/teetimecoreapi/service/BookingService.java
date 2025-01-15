package com.golf.teetimecoreapi.service;

import com.golf.teetimecoreapi.model.BookingRequest;
import com.golf.teetimecoreapi.model.BookingResponse;
import com.golf.teetimecoreapi.exception.BookingException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

@Service
@Slf4j
public class BookingService {

    private final WebDriver webDriver;

    @Value("${golf.booking.base-url}")
    private String baseUrl;

    @Autowired
    public BookingService(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public BookingResponse makeBooking(BookingRequest request) {
        try {
            String courseUrl = baseUrl + "/course/" + request.getCourse();
            webDriver.get(courseUrl);

            // Execute booking logic
            boolean success = executeBookingFlow(request);

            if (success) {
                String confirmationNumber = extractConfirmationNumber();
                return BookingResponse.builder()
                        .status("CONFIRMED")
                        .confirmationNumber(confirmationNumber)
                        .build();
            } else {
                return BookingResponse.builder()
                        .status("FAILED")
                        .message("Unable to complete booking")
                        .build();
            }
        } catch (Exception e) {
            log.error("Booking failed", e);
            throw new BookingException("Failed to make booking: " + e.getMessage());
        }
    }

    private boolean executeBookingFlow(BookingRequest request) {
        try {
            // Select date
            WebElement dateInput = findElement(By.id("date"));
            dateInput.sendKeys(request.getDate());

            // Select time
            WebElement timeSlot = findElement(By.xpath("//div[@data-time='" + request.getTime() + "']"));
            timeSlot.click();

            // Set players
            WebElement playerSelect = findElement(By.id("players"));
            Select select = new Select(playerSelect);
            select.selectByValue(String.valueOf(request.getPlayers()));

            // Confirm booking
            WebElement confirmButton = findElement(By.id("confirm-booking"));
            confirmButton.click();

            // Wait for confirmation
            return waitForConfirmation();
        } catch (Exception e) {
            log.error("Error during booking flow", e);
            return false;
        }
    }

    private WebElement findElement(By by) {
        return new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(by));
    }

    private boolean waitForConfirmation() {
        try {
            WebElement confirmation = new WebDriverWait(webDriver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.presenceOfElementLocated(By.className("booking-confirmation")));
            return confirmation.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    private String extractConfirmationNumber() {
        WebElement confirmationElement = findElement(By.className("confirmation-number"));
        return confirmationElement.getText();
    }
}