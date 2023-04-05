package io.spring.file.watch.event.consumer;

import io.spring.file.watch.event.domain.WatchEventMemento;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.IO;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Map;
import java.util.function.Consumer;

//@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitSenderWatchEventConsumer implements Consumer<WatchEvent<Path>> {
    private final RabbitTemplate rabbitmqTemplate;
    private final Map<String, WatchEventMemento> region;
    private final Path watchPath;

    @Value("${spring.cloud.stream.bindings.output.destination:amq.topic}")
    private String exchangeFile;

    @SneakyThrows
    @Override
    public void accept(WatchEvent<Path> event) {

        var path = watchPath.resolve(event.context());
        var file = path.toFile();
        var absolutePath = file.getAbsolutePath();
        var routingKey = absolutePath;


        var memento = region.get(absolutePath);

        log.info("Previous File info: {} for path ",memento,absolutePath);


        if(memento != null)
        {
            var newMemento = new WatchEventMemento(absolutePath,
                    file.lastModified(),
                    event.kind().name());

            if(memento.equals(newMemento))
            {
                log.info("Already sent!. Not sending file: {} event: {} ",file,event);
                return;
            }

            memento = newMemento;
        }
        else {

            memento = new WatchEventMemento(absolutePath,
                    file.lastModified(),
                    event.kind().name());
        }


        log.info("Sending file: {} event: {} ",file,event);

        var fileBytes = IO.readBinaryFile(file);


        rabbitmqTemplate.send(exchangeFile,
                routingKey,
                MessageBuilder
                .withBody(fileBytes)
                .setHeader("absolutePath",memento.absolutePath())
                .setHeader("lastModified",memento.lastModified())
                .setHeader("event.kind.name",memento.kindName())
                .build()
        );

        //save memento
        region.put(absolutePath,memento);
    }
}
