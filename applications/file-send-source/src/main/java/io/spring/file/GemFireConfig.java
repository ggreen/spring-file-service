package io.spring.file;

import io.spring.file.watch.event.domain.FileRecord;
import org.apache.geode.cache.GemFireCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.CacheServerApplication;
import org.springframework.data.gemfire.config.annotation.EnableDiskStore;
import org.springframework.data.gemfire.config.annotation.EnableDiskStores;
import org.springframework.data.gemfire.config.annotation.EnablePdx;

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
 * @author gregory green
 */
@Configuration
@CacheServerApplication
@EnablePdx(persistent = true, diskStoreName = "PDX-DS")
@EnableDiskStores(
        diskStores = {
                @EnableDiskStore(name = "PDX-DS", maxOplogSize = 212, diskDirectories = @EnableDiskStore.DiskDirectory(location = "${gemfire.work.dir}")),
                @EnableDiskStore(name = "FILE-DS", maxOplogSize = 512, diskDirectories = @EnableDiskStore.DiskDirectory(location = "${gemfire.work.dir}"))
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
    Map<String,FileRecord> map(PartitionedRegionFactoryBean factory)
    {
        return factory.getRegion();
    }
}
