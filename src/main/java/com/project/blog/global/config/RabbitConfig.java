package com.project.blog.global.config;

import com.project.blog.global.base.QueueBindings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;

    // 1. RabbitMQ 연결 및 템플릿 설정
    /**
     * RabbitMQ와 연결할 ConnectionFactory 설정
     * @return ConnectionFactory 객체
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    /**
     * RabbitTemplate 설정
     * @param connectionFactory RabbitMQ 연결 팩토리
     * @return RabbitTemplate 객체
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    // 2. 커스텀 Exchange 생성
    @Bean
    public CustomExchange userSignupExchange() {
        return new CustomExchange(
                QueueBindings.USER_SIGNUP_EXCHANGE,
                "x-delayed-message",
                true,
                false
        ) {
            {
                getArguments().put("x-delayed-type", "direct");
            }
        };
    }

    @Bean
    public CustomExchange userDeadLetterExchange() {
        return new CustomExchange(
                QueueBindings.USER_DLX,
                "direct",
                true,
                false
        );
    }

    // 3. 커스텀 Queue 생성
    @Bean
    public Queue userSignupQueue() {
        return QueueBuilder.durable(QueueBindings.USER_SIGNUP_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueBindings.USER_DLX)
                .withArgument("x-dead-letter-routing-key", QueueBindings.USER_DLQ_KEY)
                .build();
    }

    // DLQ 생성 - 실패한 메시지들 오는 큐
    @Bean
    public Queue userDeadLetterQueue() {
        return new Queue(QueueBindings.USER_DLQ, true);
    }

    // 4. Exchange - Queue 연결설정
    @Bean
    public Binding userSignupQueueBinding(Queue userSignupQueue, CustomExchange userSignupExchange) {
        return BindingBuilder
                .bind(userSignupQueue)
                .to(userSignupExchange)
                .with(QueueBindings.USER_SIGNUP_KEY)
                .noargs();
    }

    @Bean
    public Binding userDLQBinding(Queue userDeadLetterQueue, CustomExchange userDeadLetterExchange) {
        return BindingBuilder
                .bind(userDeadLetterQueue)
                .to(userDeadLetterExchange)
                .with(QueueBindings.USER_DLQ_KEY)
                .noargs();
    }

}
