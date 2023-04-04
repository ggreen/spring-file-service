package io.spring.file;

import io.spring.file.watch.event.domain.WatchEventMemento;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class GemFireConfig {

    @Bean
    Map<String, WatchEventMemento> region()
    {
        return new HashMap<>();
    }
}
