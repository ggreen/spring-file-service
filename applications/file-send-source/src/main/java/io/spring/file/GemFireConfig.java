package io.spring.file;

import io.spring.file.watch.event.domain.FileRecord;
import org.apache.geode.cache.GemFireCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.DiskStoreFactoryBean;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * Embedded GemFire service values
 *
 * Create GemFire connections.
 *
 * Create region: File
 *
 *
 *
 * Properties
 *
 * - gemfire.work.dir = the GemFire working directory for persistence
 *
 *  --add-exports  java.management/com.sun.jmx.remote.security=ALL-UNNAMED
 * java.management
 * @author gregory green
 */
@Configuration
@CacheServerApplication
@EnablePdx(persistent = true, diskStoreName = "PDX-DS")
@EnableDiskStores(
        diskStores = {
//                @EnableDiskStore(name = "DEFAULT", maxOplogSize = 212, diskDirectories = @EnableDiskStore.DiskDirectory(location = "${spring.data.gemfire.disk.store.directory.location}")),
                @EnableDiskStore(name = "PDX-DS", maxOplogSize = 212, diskDirectories = @EnableDiskStore.DiskDirectory(location = "${spring.data.gemfire.disk.store.directory.location}")),
                @EnableDiskStore(name = "FILE-DS", maxOplogSize = 512, diskDirectories = @EnableDiskStore.DiskDirectory(location = "${spring.data.gemfire.disk.store.directory.location}"))
        }
)
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
    DiskStoreConfigurer diskStoreConfigurer(
            @Value("${spring.data.gemfire.disk.store.directory.location}") String location) {

        return (beanName, diskStoreFactoryBean) -> {
                diskStoreFactoryBean.setDiskDirs(Collections.singletonList(new DiskStoreFactoryBean.DiskDir(location)));
        };
    }

    @Bean
    Map<String,FileRecord> map(PartitionedRegionFactoryBean factory)
    {
        return factory.getRegion();
    }
}
