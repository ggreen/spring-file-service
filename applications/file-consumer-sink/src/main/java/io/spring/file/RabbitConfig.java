package io.spring.file;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.stream.OffsetSpecification;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.connection.ThreadChannelConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.MessagingMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.support.converter.DefaultStreamMessageConverter;

@Configuration
public class RabbitConfig {

    @Value("${spring.rabbitmq.username:guest}")
    private String username = "guest";

    @Value("${spring.rabbitmq.password:guest}")
    private String password  = "guest";

    @Value("${spring.rabbitmq.host}")
    private String hostname = "localhost";

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.rabbitmq.port:5672}")
    private int port;


    @Bean
    ConnectionNameStrategy connectionNameStrategy(){
        return f -> applicationName;
    }

    @Bean
    ConnectionFactory connectionFactory( )
    {
        var factory = new RabbitConnectionFactoryBean();
        factory.setHost(hostname);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setPort(port);

        return new ThreadChannelConnectionFactory(factory.getRabbitConnectionFactory())
                .getRabbitConnectionFactory();
    }

    @Bean
    MessageConverter convert()
    {
        return new ContentTypeDelegatingMessageConverter();
    }


    /**
     * if you always want the former (starting at next), you should not name your consumer.
     * @return customizer
     */
    @Bean
    @ConditionalOnProperty(name = "spring.rabbitmq.listener.type",havingValue = "stream")
    ListenerContainerCustomizer<MessageListenerContainer> customizer() {
        return (cont, dest, group) -> {
            StreamListenerContainer container = (StreamListenerContainer) cont;
            container.setConsumerCustomizer((name, builder) -> {
//                builder.name(applicationName);
                builder.offset(OffsetSpecification.next())
                        .name(applicationName);
                builder.autoTrackingStrategy();
            });
            // ...
        };
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(MessageConverter converter) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(converter);
        return factory;
    }

}
