package com.petfriends.pedidos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petfriends.pedidos.events.EntregaSolicitadaEvent;
import com.petfriends.pedidos.events.PedidoEmPreparacaoEvent;
import com.petfriends.pedidos.infra.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
public class PedidosController {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public PedidosController(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/publicar-em-preparacao")
    public String publicarPedidoEmPreparacao() throws Exception {
        PedidoEmPreparacaoEvent event = new PedidoEmPreparacaoEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setCorrelationId(UUID.randomUUID().toString());
        event.setOccurredAt(LocalDateTime.now());
        event.setPedidoId((long) (Math.random() * 1000));
        event.setClienteId((long) (Math.random() * 1000));
        
        PedidoEmPreparacaoEvent.ItemPedido item = new PedidoEmPreparacaoEvent.ItemPedido();
        item.setProdutoId(99L);
        item.setSku("PROD-DEMO-01");
        item.setNomeProduto("Produto de Teste");
        item.setQuantidade(1);
        event.setItens(List.of(item));

        String json = objectMapper.writeValueAsString(event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "pedido.em-preparacao", json);

        return "Evento PedidoEmPreparacaoEvent publicado no exchange petfriends.pedidos.exchange com routing key pedido.em-preparacao.\nJSON enviado: " + json;
    }

    @PostMapping("/publicar-entrega-solicitada")
    public String publicarEntregaSolicitada() throws Exception {
        EntregaSolicitadaEvent event = new EntregaSolicitadaEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setCorrelationId(UUID.randomUUID().toString());
        event.setOccurredAt(LocalDateTime.now());
        event.setPedidoId((long) (Math.random() * 1000));
        event.setClienteId((long) (Math.random() * 1000));
        event.setDestinatario("João da Silva");
        event.setVolumes(2);

        EntregaSolicitadaEvent.EnderecoEvento endereco = new EntregaSolicitadaEvent.EnderecoEvento();
        endereco.setLogradouro("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("01000-000");
        event.setEnderecoEntrega(endereco);

        String json = objectMapper.writeValueAsString(event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "pedido.entrega-solicitada", json);

        return "Evento EntregaSolicitadaEvent publicado no exchange petfriends.pedidos.exchange com routing key pedido.entrega-solicitada.\nJSON enviado: " + json;
    }
}
