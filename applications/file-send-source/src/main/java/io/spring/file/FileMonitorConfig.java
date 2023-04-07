package io.spring.file;

import io.spring.file.watch.event.service.SendFileService;
import nyla.solutions.core.io.FileEvent;
import nyla.solutions.core.io.FileMonitor;
import nyla.solutions.core.patterns.observer.SubjectObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Configuration settings for file monitoring
 *
 * See properties
 * file.source.pollingIntervalMs = schedule monitoring intervale
 * file.source.delayMs = time in milliseconds between successive task executions.
 * file.source.rootDirectory =  the source directory to watch
 * file.source.fileNameFilter = Expression to filter files (ex. *.*) (default *)
 * file.source.processCurrentFiles = boolean to process existing files (default true)
 *
 * @author gregory green
 */
@Configuration
public class FileMonitorConfig {


    @Value ("${file.source.pollingIntervalMs:1000}")
    private long pollingIntervalMs = 1000;

    @Value ("${file.source.delayMs:1000}")
    private long delayMs = 1000;

    @Value ("${file.source.rootDirectory}")
    private String rootDirectory;

    @Value ("${file.source.fileNameFilter:*}")
    private String fileNameFilter;

    @Value ("${file.source.processCurrentFiles:true}")
    private boolean processCurrentFiles;

    @Bean
    FileMonitor fileMonitor(SubjectObserver<FileEvent> observer)
    {
        FileMonitor monitor = new FileMonitor(pollingIntervalMs,delayMs);

        monitor.add(observer);
        monitor.monitor(rootDirectory,
                fileNameFilter,
                processCurrentFiles);

        return monitor;
    }

    @Bean
    SubjectObserver<FileEvent> observer(SendFileService sendFileService)
    {
        return (subjectName, event) -> {
            sendFileService.process(event.getFile());
        };
    }
}
