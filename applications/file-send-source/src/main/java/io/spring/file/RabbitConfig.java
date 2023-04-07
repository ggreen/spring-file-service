package io.spring.file;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.connection.ThreadChannelConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.support.converter.DefaultStreamMessageConverter;

@Configuration
public class RabbitConfig {

    @Value("${spring.rabbitmq.username:guest}")
    private String username = "guest";

    @Value("${spring.rabbitmq.password:guest}")
    private String password  = "guest";

    @Value("${spring.rabbitmq.host:localhost}")
    private String hostname = "localhost";

    @Value("${spring.application.name}")
    private String applicationName;


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

        return new ThreadChannelConnectionFactory(factory.getRabbitConnectionFactory())
                .getRabbitConnectionFactory();
    }

    @Bean
    MessageConverter convert()
    {
        return new DefaultStreamMessageConverter();
        //return new ContentTypeDelegatingMessageConverter();
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(MessageConverter converter) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(converter);
        return factory;
    }


    @Bean
    Exchange exchange(@Value("${spring.cloud.stream.bindings.output.destination}")
                      String exchangeName)
    {
        return new TopicExchange(exchangeName);
    }
}
