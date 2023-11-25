package eus.ibai.jobs.alerts.infrastructure.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class WebDriverFactory {

    private final String remoteServerUrl;

    public WebDriverFactory(@Value("${selenium.server.url:http://localhost:4444}") String remoteServerUrl) {
        this.remoteServerUrl = remoteServerUrl;
    }

    public WebDriver firefoxRemoteWebDriver() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.addArguments("--headless");
        firefoxOptions.addArguments("--no-sandbox");
        firefoxOptions.addArguments("disable-gpu");

        ClientConfig config = ClientConfig.defaultConfig()
                .connectionTimeout(Duration.ofMinutes(20))
                .readTimeout(Duration.ofMinutes(20));

        return RemoteWebDriver.builder()
                .oneOf(firefoxOptions)
                .address(remoteServerUrl)
                .config(config)
                .build();
    }
}
