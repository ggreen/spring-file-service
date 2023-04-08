package io.spring.file;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.config.annotation.EnableLocator;
import org.springframework.data.gemfire.config.annotation.EnableManager;

@EnableLocator
@EnableManager
@Configuration
@Profile("embedded-locator")
public class LocatorConfig {
}
