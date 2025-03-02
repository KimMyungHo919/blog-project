package com.project.blog.domain.rabbitmq.producer;

import com.project.blog.global.base.QueueBindings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitUserSignupProducer {

    private final RabbitTemplate rabbitTemplate;

    public void userSignupEvent(Long userId, LocalDateTime expiryTime) {
        long tokenExpiryInMillis = Duration.between(LocalDateTime.now(), expiryTime).toMillis();

        rabbitTemplate.convertAndSend(
                QueueBindings.USER_SIGNUP_EXCHANGE,
                QueueBindings.USER_SIGNUP_KEY,
                userId,
                message -> {
                    message.getMessageProperties().setHeader("x-delay", tokenExpiryInMillis);
                    return message;
                }
        );
    }
}
