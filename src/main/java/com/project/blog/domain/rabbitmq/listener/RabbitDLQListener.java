package com.project.blog.domain.rabbitmq.listener;

import com.project.blog.global.base.QueueBindings;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitDLQListener {

    // USER DLQ
    @RabbitListener(queues = QueueBindings.USER_DLQ, ackMode = "MANUAL")
    public void userProcessDeadLetterQueue(Long userId, Channel channel, Message message) throws IOException {
        log.warn("DLQ 수신 : 처리 실패한 USER 아이디 : {}", userId);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
