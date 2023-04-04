package io.spring.file.watch.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileSourceService {

    private final Consumer<WatchEvent<Path>> consumer;

    public boolean process(WatchKey watchKey) {

        List<WatchEvent<Path>> events = (List)watchKey.pollEvents();
        for (WatchEvent<Path> event: events) {
            try
            {
                WatchEvent.Kind<?> kind = event.kind();

                log.info("Process event:{}",event);

                // This key is registered only
                // for ENTRY_CREATE events,
                // but an OVERFLOW event can
                // occur regardless if events
                // are lost or discarded.
                if (kind == OVERFLOW) {
                    continue;
                }

                consumer.accept(event);
            }
            catch (Throwable exception)
            {
                log.error("ERROR: {}",exception);
            }
        }


        // Reset the key -- this step is critical if you want to
        // receive further watch events.  If the key is no longer valid,
        // the directory is inaccessible so exit the loop.
        return watchKey.reset();
    }
}
