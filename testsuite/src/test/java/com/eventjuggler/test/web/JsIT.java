package com.eventjuggler.test.web;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;

import com.eventjuggler.test.Deployments;
import com.google.common.base.Predicate;

@RunWith(Arquillian.class)
public class JsIT {

    @Deployment(name = "eventjuggler-ear", testable = false)
    public static EnterpriseArchive getEventJugglerServer() throws Exception {
        return Deployments.getEventJugglerServer();
    }

    @Drone
    WebDriver driver;

    @Test
    public void registerAndLogin() throws Exception {
        System.out.println(IOUtils.toString(getClass().getResourceAsStream("/arquillian.xml")));

        driver.get("http://localhost:8080/eventjuggler-web-js/#/events");
        driver.findElement(By.linkText("Register")).click();

        waitForCurrentUrl("http://localhost:8080/eventjuggler-web-js/#/register");

        String userName = "user-" + System.currentTimeMillis();

        driver.findElement(By.id("userName")).sendKeys(userName);
        driver.findElement(By.id("password")).sendKeys("bar");
        driver.findElement(By.id("firstName")).sendKeys("Foo");
        driver.findElement(By.id("lastName")).sendKeys("Bar");
        driver.findElement(By.id("email")).sendKeys("foo@bar.com");

        driver.findElement(By.id("registerButton")).click();

        waitForText("User registered!");

        driver.findElement(By.cssSelector("a.btn")).click();

        waitForElement(By.id("loginModal"));
        Thread.sleep(500); // without this the only parts of the username is sent for some reason
        driver.findElement(By.id("username")).sendKeys(userName);
        driver.findElement(By.xpath("(//input[@id='password'])[2]")).sendKeys("bar");
        driver.findElement(
                By.cssSelector("form[name=\"loginForm\"] > div.control-group > div.controls > button.btn.btn-primary")).click();

        waitForCurrentUrl("http://localhost:8080/eventjuggler-web-js/#/events");

        Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().contains("Foo Bar"));
    }

    public void waitForCurrentUrl(String url) {
        FluentWait<String> fluentWait = new FluentWait<String>(url);
        fluentWait.pollingEvery(100, TimeUnit.MILLISECONDS);
        fluentWait.withTimeout(1000, TimeUnit.MILLISECONDS);
        fluentWait.until(new Predicate<String>() {
            @Override
            public boolean apply(String url) {
                return driver.getCurrentUrl().equals(url);
            }
        });
    }

    public void waitForText(String text) {
        FluentWait<String> fluentWait = new FluentWait<String>(text);
        fluentWait.pollingEvery(100, TimeUnit.MILLISECONDS);
        fluentWait.withTimeout(1000, TimeUnit.MILLISECONDS);
        fluentWait.until(new Predicate<String>() {
            @Override
            public boolean apply(String text) {
                return driver.findElement(By.cssSelector("BODY")).getText().contains(text);
            }
        });
    }

    public void waitForElement(By by) {
        FluentWait<By> fluentWait = new FluentWait<By>(by);
        fluentWait.pollingEvery(100, TimeUnit.MILLISECONDS);
        fluentWait.withTimeout(1000, TimeUnit.MILLISECONDS);
        fluentWait.until(new Predicate<By>() {
            @Override
            public boolean apply(By by) {
                try {
                    return driver.findElement(by).isDisplayed();
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
        });
    }
}
