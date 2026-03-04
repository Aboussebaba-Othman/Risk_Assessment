package com.riskassessment.alertservice.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String SCORE_EXCHANGE = "score.exchange";
    public static final String ALERT_QUEUE = "alerts.score.queue";
    public static final String ROUTING_KEY = "score.calculated";

    @Bean
    public Queue alertQueue() {
        return QueueBuilder.durable(ALERT_QUEUE).build();
    }

    @Bean
    public TopicExchange scoreExchange() {
        return ExchangeBuilder.topicExchange(SCORE_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding alertBinding(Queue alertQueue, TopicExchange scoreExchange) {
        return BindingBuilder.bind(alertQueue).to(scoreExchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
