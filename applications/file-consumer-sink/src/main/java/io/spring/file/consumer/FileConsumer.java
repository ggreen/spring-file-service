package io.spring.file.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Component
@Slf4j
public class FileConsumer implements Consumer<Message> {
    @Transactional
    @Override
    public void accept(Message message) {

        log.info("Message: {}",message);
    }
}
