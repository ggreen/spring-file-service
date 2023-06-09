package io.spring.file.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.IO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.function.Consumer;

import static java.lang.String.valueOf;

/**
 * Consumer to save files to disk
 * @author gregory green
 */
@Component
@Slf4j
public class FileConsumer implements Consumer<Message> {
    private static final String NAME_HEADER = "name";
    private final static String PATH_HEADER = "path";

    private final String rootDirectory;

    /**
     * Constructor
     * @param rootDirectory the root directory to save files
     */
    public FileConsumer(@Value("${file.sink.rootDirectory}") String rootDirectory) {
        this.rootDirectory = rootDirectory;

        log.info("*****Files will be written to value of propertu \"file.sink.rootDirectory\": {}",rootDirectory);
    }

    /**
     * Save recieve message payload to disk
     * @param message the input argument
     */
    @SneakyThrows
    @Transactional
    @Override
    public void accept(Message message) {

        log.info("Message: {}",message);

        var headers = message.getHeaders();

        String path = valueOf(headers.get(PATH_HEADER));
        var name = valueOf(headers.get(NAME_HEADER));
        Object rawPayload = message.getPayload();
        byte[] payload;

        if(rawPayload instanceof String textPayload)
            payload = textPayload.getBytes(StandardCharsets.UTF_8);
        else if(rawPayload instanceof byte[] bytesPayload)
            payload = bytesPayload;
        else
            throw new IllegalArgumentException("Payload is not a byte[] or String, Provided: contentType:"+headers.get("content_type")+" msg:"+message);

        File outFile = Paths.get(rootDirectory,path).toFile();

        var parentDir = outFile.getParentFile();
        log.info("Making parent directory: {}",parentDir);
        IO.mkdir(parentDir);

        log.info("Writing file out File: {}",outFile.getAbsolutePath());
        IO.writeFile(outFile,payload);

        log.info("Write file out File: {}",outFile.getAbsolutePath());
    }
}
