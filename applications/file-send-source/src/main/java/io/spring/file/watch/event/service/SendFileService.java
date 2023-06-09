package io.spring.file.watch.event.service;

import io.spring.file.watch.event.domain.FileRecord;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.IO;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Send Files to RabbitMQ
 * @author gregory Green
 */
@Service
@Slf4j
public class SendFileService {

    private final RabbitTemplate rabbitTemplate;
    private final Map<String, FileRecord> region;

    private final String exchangeFile;

    private final Path rootDirectory;
    private final String filePattern;

    /**
     *
     * @param rabbitTemplate the rabbit template
     * @param region the gemfire region map
     * @param exchangeFile the RabbitMQ exchange
     * @param rootDirectory the root directory to watch for files
     * @param filePattern the filter pattern for files
     */
    public SendFileService(RabbitTemplate rabbitTemplate,
                           Map<String, FileRecord> region,
                           @Value("${spring.cloud.stream.bindings.output.destination}")
                           String exchangeFile,
                           @Value ("${file.source.rootDirectory}")
                           String rootDirectory,
                           @Value("${file.source.pattern}")
                           String filePattern

                           ) {

        this.rabbitTemplate = rabbitTemplate;
        this.region = region;
        this.exchangeFile = exchangeFile;
        this.filePattern = filePattern;
        this.rootDirectory = Paths.get(rootDirectory);
    }

    /**
     * Process a list of files
     * @param listFiles list of files to process
     */
    public void processFiles(File[] listFiles) {
        for (File file: listFiles) {
            process(file);
        }
    }

    /**
     * Send the given file
     * @param file the file to send
     */
    @SneakyThrows
    public void process(File file) {

        if(file.isDirectory())
        {
            processFiles(IO.listFiles(file,filePattern));
            return;
        }
        var absolutePath = file.getAbsolutePath();
        var routingKey = absolutePath;

        var fileRecord = region.get(absolutePath);

        log.info("Previous File info: {} for path ",fileRecord,absolutePath);

        if(fileRecord != null)
        {
            var newFileRecord = new FileRecord(
                    absolutePath,
                    file.lastModified());

            if(fileRecord.equals(newFileRecord))
            {
                log.info("Already sent!. Not sending file: {} event: {} ",file);
                return;
            }

            fileRecord = newFileRecord;
        }
        else {

            fileRecord = new FileRecord(absolutePath,
                    file.lastModified());
        }


        log.info("Sending file: {} event: {} ",file);

        var fileBytes = IO.readBinaryFile(file);

        if(fileBytes == null)
            fileBytes = new byte[0]; //empty

        rabbitTemplate.send(exchangeFile,
                routingKey,
                MessageBuilder
                        .withBody(fileBytes)
                        .setHeader("absolutePath",fileRecord.absolutePath())
                        .setHeader("lastModified",fileRecord.lastModified())
                        .setHeader("name",file.getName())
                        .setHeader("path",toPath(file))
                        .setContentType(Files.probeContentType(file.toPath()))
                        .build()
        );

        //save memento
        region.put(absolutePath,fileRecord);
    }

    /**
     *
     * @param file the process file
     * @return the relative path based on root directory
     */
    protected String toPath(File file) {
        return rootDirectory.relativize(file.toPath()).toString();
    }


}
