package io.spring.file.consumer;

import nyla.solutions.core.io.IO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.support.MessageBuilder;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileConsumerTest {

    private String rootDirectory = "src/test/resources/ignore";
    private FileConsumer subject;
    private String payload = "hello";
    private File inFile = new File("src/test/resources/files/empty.txt");

    @DisplayName("GIVEN: message with string payload, when accept THEN: save to root directory")
    @Test
    void accept() throws IOException {
        var message = MessageBuilder.withPayload(payload)
                .setHeader("absolutePath",inFile.getAbsolutePath())
                .setHeader("lastModified",inFile.lastModified())
                .setHeader("name",inFile.getName())
                .setHeader("path",inFile.getPath())
                .build();

        subject = new FileConsumer(rootDirectory);


        var outFile = new File("src/test/resources/ignore/"+inFile.getPath()+"/out.txt");

        IO.delete(new File("src/test/resources/ignore/src/test/resources/files/empty.txt"));
        assertFalse(IO.exists("src/test/resources/ignore/src/test/resources/files/empty.txt"));

        subject.accept(message);

        assertTrue(IO.exists("src/test/resources/ignore/src/test/resources/files/empty.txt"));
    }
}