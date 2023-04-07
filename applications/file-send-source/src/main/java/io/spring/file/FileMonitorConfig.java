package io.spring.file;

import io.spring.file.watch.event.service.SendFileService;
import nyla.solutions.core.io.FileEvent;
import nyla.solutions.core.io.FileMonitor;
import nyla.solutions.core.patterns.observer.SubjectObserver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileMonitorConfig {

    long pollingIntervalMs = 1000;
    long delayMs = 1000;
    private String directory = "/tmp/io/input/";
    private boolean processCurrentFiles = true;

    @Bean
    FileMonitor fileMonitor(SubjectObserver<FileEvent> observer)
    {
        FileMonitor monitor = new FileMonitor(pollingIntervalMs,delayMs);

        monitor.add(observer);
        monitor.monitor(directory,
                "*",
                processCurrentFiles);

        return monitor;
    }

    @Bean
    SubjectObserver<FileEvent> observer(SendFileService sendFileService)
    {
        return (subjectName, event) -> {sendFileService.process(event.getFile());};
    }
}
