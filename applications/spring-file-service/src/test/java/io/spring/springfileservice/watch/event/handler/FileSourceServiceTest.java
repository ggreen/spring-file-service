package io.spring.springfileservice.watch.event.handler;

import io.spring.file.watch.event.service.FileSourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileSourceServiceTest {

    @Mock
    private Consumer<WatchEvent<Path>> consumer;

    @Mock
    private WatchKey watchKey;

    @Mock
    private WatchEvent<Path> event;

    private FileSourceService subject;

    @BeforeEach
    void setUp() {
        subject = new FileSourceService(consumer);
    }

    @DisplayName("Given WatchKey when process then call consumer accept")
    @Test
    void process() {

        List<WatchEvent<?>> list = asList(event);
        when(watchKey.pollEvents()).thenReturn(list);
        when(watchKey.reset()).thenReturn(true);

        assertTrue(subject.process(watchKey));

        verify(consumer).accept(any());
    }
}