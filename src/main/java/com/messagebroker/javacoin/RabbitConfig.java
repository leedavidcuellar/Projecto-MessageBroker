package com.messagebroker.javacoin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.*;

@Configuration
public class RabbitConfig {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue queue1(){
        return new Queue("Queue1");
    }
    @Bean
    public Queue errorBankWallet(){
        return new Queue("ErrorBankWallet");
    }

    @Bean
    public TopicExchange topicSell(){
        return new TopicExchange("TopicSell");
    }

    @Bean
    public Binding topicSellBindingQueue1(){
        return BindingBuilder
                .bind(queue1())
                .to(topicSell())
                .with("BUY");
    }
}
