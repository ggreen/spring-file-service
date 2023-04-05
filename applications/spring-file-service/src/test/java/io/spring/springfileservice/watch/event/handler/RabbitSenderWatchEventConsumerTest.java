package io.spring.springfileservice.watch.event.handler;

import io.spring.file.watch.event.domain.WatchEventMemento;
import io.spring.file.watch.event.consumer.RabbitSenderWatchEventConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitSenderWatchEventConsumerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private WatchEvent<Path> event;

    @Mock
    private Map<String, WatchEventMemento> region;


    @Mock
    private WatchEvent.Kind<Path> kind;

    private File file = new File("src/test/resources/files/test.txt");

    private Path path = Paths.get("/Users/Projects/solutions/integration/files/dev/spring-file-service/applications/spring-file-service");


    private RabbitSenderWatchEventConsumer subject;


    @BeforeEach
    void setUp() {
        subject = new RabbitSenderWatchEventConsumer(rabbitTemplate,region, path);
    }

    @DisplayName("Given: event did not previous exist WHEN:accept THEN: send to RabbitMQ")
    @Test
    void accept() {

        when(event.context()).thenReturn(path);
//        when(path.toFile()).thenReturn(file);
        when(event.kind()).thenReturn(kind);

        subject.accept(event);

        verify(rabbitTemplate).send(any(Message.class));
    }
}