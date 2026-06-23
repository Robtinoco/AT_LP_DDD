package com.petfriends.almoxarifado.infra;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.petfriends.almoxarifado.domain.*;

import java.util.stream.Collectors;

@Component
public class PedidoEmPreparacaoConsumer {

    private final OrdemSeparacaoRepository repository;

    public PedidoEmPreparacaoConsumer(OrdemSeparacaoRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    @Transactional
    public void handle(PedidoEmPreparacaoEvent event) {
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
    }
}
