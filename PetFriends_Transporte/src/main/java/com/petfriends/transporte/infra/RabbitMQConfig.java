package com.petfriends.transporte.infra;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "petfriends.pedidos.exchange";
    public static final String QUEUE_NAME = "petfriends.transporte.entrega-solicitada.queue";
    public static final String ROUTING_KEY = "pedido.entrega-solicitada";

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue entregaSolicitadaQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingEntregaSolicitada(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
