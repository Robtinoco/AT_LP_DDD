package com.petfriends.almoxarifado.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.petfriends.almoxarifado.domain.*;

import java.util.stream.Collectors;

@Component
public class PedidoEmPreparacaoConsumer {

    private final OrdemSeparacaoRepository repository;
    private final ObjectMapper objectMapper;

    public PedidoEmPreparacaoConsumer(OrdemSeparacaoRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    @Transactional
    public void handle(String json) {
        try {
            PedidoEmPreparacaoEvent event = objectMapper.readValue(json, PedidoEmPreparacaoEvent.class);
            var itens = event.getItens().stream()
                .map(item -> new ItemSeparacao(
                    item.getProdutoId(),
                    item.getSku(),
                    item.getNomeProduto(),
                    item.getQuantidade()
                ))
                .collect(Collectors.toList());
                
            OrdemSeparacao ordem = new OrdemSeparacao(event.getPedidoId(), event.getClienteId(), itens);
            repository.save(ordem);
            System.out.println("Ordem de separação criada para o pedido: " + event.getPedidoId());
        } catch (Exception e) {
            System.err.println("Erro ao processar evento de almoxarifado: " + e.getMessage());
        }
    }
}
