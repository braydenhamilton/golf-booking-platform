package com.golf.teetimecoreapi.service;

import com.golf.model.User;
import com.golf.teetimecoreapi.model.UserEntity;
import com.golf.teetimecoreapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BookingService {

    public static final Log LOGGER = LogFactory.getLog(BookingService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToUser)
                .orElse(null);
    }

    private User convertToUser(UserEntity entity) {
        User user = new User();
        user.setUsername(entity.getUsername());
        user.setGolfNZMemberId(entity.getGolfNZMemberId());
        user.setGolfNZPassword(entity.getGolfNZPassword());
        return user;
    }

    public WebElement findFinaliseButton(WebDriver driver, WebDriverWait wait) {
        List<By> locators = Arrays.asList(
                By.id("MainContent_FinishButton"),
                By.xpath("//input[@value='FINALISE BOOKING']"),
                By.cssSelector(".module-button-cta-nzg.btn.btn-primary"),
                By.name("ctl00$MainContent$FinishButton"),
                By.xpath("//div[contains(@class, 'alert-info')]//input[@type='submit']")
        );

        for (By locator : locators) {
            try {
                return wait.until(ExpectedConditions.elementToBeClickable(locator));
            } catch (TimeoutException e) {
                LOGGER.debug("Could not find finalise button with locator: " + locator);
                // Continue to next locator
            }
        }
        // Return null instead of throwing an exception when button is not found
        LOGGER.info("No finalise button found after trying all locators");
        return null;
    }
    public WebElement findSubmitButton(WebDriver driver, WebDriverWait wait) {
        List<By> locators = Arrays.asList(
                By.id("MainContent_ContinueButton"),
                By.xpath("//input[@type='submit' and @value='SUBMIT']"),
                By.cssSelector("input.module-button-cta.btn.btn-primary"),
                By.xpath("//input[contains(@class, 'module-button-cta') and contains(@class, 'btn-primary')]"),
                By.name("ctl00$MainContent$ContinueButton")
        );

        for (By locator : locators) {
            try {
                return wait.until(ExpectedConditions.elementToBeClickable(locator));
            } catch (TimeoutException e) {
                LOGGER.error("Could not find submit button with locator: " + locator);
                continue;
            }
        }
        throw new RuntimeException("Could not find submit button");
    }

    public WebElement findTimeSlot(WebDriver driver, WebDriverWait wait, String time) {
        List<By> locators = Arrays.asList(
                // Find by exact time and "Book Here" link
                By.xpath(String.format("//td[contains(@class, 'xtime') and text()='%s']/..//a[contains(@class, 'book_here_link')]", time)),
                // Alternative approach using ancestor
                By.xpath(String.format("//td[@class='xtime'][text()='%s']/following-sibling::td//a[contains(@class, 'book_here_link')]", time))
        );

        for (By locator : locators) {
            try {
                return wait.until(ExpectedConditions.elementToBeClickable(locator));
            } catch (TimeoutException e) {
                LOGGER.error("Could not find time slot with locator: " + locator);
                continue;
            }
        }
        throw new RuntimeException("Could not find booking link for time: " + time);
    }

    public WebElement findDateCell(WebDriver driver, WebDriverWait wait, WebElement courseRow, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(date);

        try {
            // Look for available date cells only within the course row
            return courseRow.findElement(
                    By.xpath(String.format(".//td[contains(@class, 'available') and contains(@onmouseover, '%s')]", formattedDate))
            );
        } catch (Exception e) {
            LOGGER.error("Could not find date cell for " + formattedDate + " in course row");
            throw new RuntimeException("Could not find available date cell for: " + formattedDate);
        }
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

    public void nzGolfLogin(WebDriver driver, WebDriverWait wait, User user) {
        // Decrypt the GolfNZ password
        String password = userService.decrypt(user.getGolfNZPassword());
        
        // Login procedure
        WebElement membershipNumberField = findMembershipNumberField(driver, wait);
        WebElement passwordField = findPasswordField(driver, wait);
        WebElement loginButton = findLoginButton(driver, wait);

        // Enter login details
        membershipNumberField.sendKeys(user.getGolfNZMemberId());
        passwordField.sendKeys(password);
        loginButton.click();
    }

}
