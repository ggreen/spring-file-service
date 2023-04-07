package io.spring.file.watch.event.service;

import io.spring.file.watch.event.domain.FileRecord;
import nyla.solutions.core.patterns.memento.FileMemento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.File;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SendFileServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Map<String, FileRecord> region;
    private SendFileService subject;
    private File file = new File("src/test/resources/files/test.txt");
    private String exchange = "test";
    private String filePattern = "*.txt";


    @BeforeEach
    void setUp() {
        subject = new SendFileService(rabbitTemplate, region,exchange,filePattern);
    }

    @Test
    void send() {

        subject.process(file);
        rabbitTemplate.send(any(Message.class));
    }

    @DisplayName("Given directory WHEN process THEN process all nested files")
    @Test
    void sendDir() {
        File dir = new File("src/test/resources/files");

        subject.process(dir);
        rabbitTemplate.send(any(Message.class));
    }
}