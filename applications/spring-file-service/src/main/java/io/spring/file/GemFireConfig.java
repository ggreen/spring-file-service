package io.spring.file;

import io.spring.file.watch.event.domain.FileRecord;
import org.apache.geode.cache.GemFireCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.CacheServerApplication;
import org.springframework.data.gemfire.config.annotation.EnablePdx;

import java.util.Map;

@Configuration
@CacheServerApplication
@EnablePdx(persistent = true)
public class GemFireConfig {

    @Bean("File")
    PartitionedRegionFactoryBean exampleRegion(GemFireCache gemfireCache) {

        PartitionedRegionFactoryBean<Long, FileRecord> fileRegion =
                new PartitionedRegionFactoryBean<>();
        fileRegion.setName("File");
        fileRegion.setCache(gemfireCache);
        fileRegion.setPersistent(true);
        return fileRegion;
    }

    @Bean
    Map<String,FileRecord> map(PartitionedRegionFactoryBean factory)
    {
        return factory.getRegion();
    }
}
