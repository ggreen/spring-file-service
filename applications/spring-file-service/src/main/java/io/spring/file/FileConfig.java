//package io.spring.file;
//
//import io.spring.file.watch.event.service.FileSourceService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//import java.io.IOException;
//import java.nio.file.*;
//
//import static java.nio.file.StandardWatchEventKinds.*;
//
//@Slf4j
//@Configuration
////@Profile("watcher")
//public class FileConfig {
//    @Value("${file.watch.path}")
//    private String watchPath;
//
//    @Bean
//    WatchService watchService() throws IOException {
//        return  FileSystems.getDefault().newWatchService();
//    }
//
//    @Bean
//    Path watchPath()
//    {
//        return Paths.get(watchPath);
//    }
//
//    @Bean
//    WatchKey registerWatchKey(WatchService watchService, Path dir) throws IOException {
//
//
//
//            return dir.register(watchService,
//                    ENTRY_CREATE,
//                    ENTRY_DELETE,
//                    ENTRY_MODIFY);
//    }
//
//    @Bean
//    ApplicationRunner runner(WatchService watcher, WatchKey watchKey, FileSourceService fileSourceService)
//    {
//        log.info("Setting up watch service");
//
//        ApplicationRunner runner = args -> {
//
//            log.info("Running application");
//
//            fileSourceService.process(watchKey);
//
//            while (true) {
//
//                // wait for key to be signaled
//                WatchKey key;
//                try {
//                    key = watcher.take();
//                } catch (InterruptedException x) {
//                    log.error(" error {}",x);
//                    return;
//                }
//
//                fileSourceService.process(key);
//
//            }
//        };
//
//        return runner;
//    }
//
//}
