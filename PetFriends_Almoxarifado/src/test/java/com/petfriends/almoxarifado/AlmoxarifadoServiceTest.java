package com.petfriends.almoxarifado;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petfriends.almoxarifado.domain.*;
import com.petfriends.almoxarifado.infra.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AlmoxarifadoServiceTest {

    @Autowired
    private PedidoEmPreparacaoConsumer consumer;

    @Autowired
    private OrdemSeparacaoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void processarEventoPedidoEmPreparacaoTest() throws Exception {
        PedidoEmPreparacaoEvent event = new PedidoEmPreparacaoEvent();
        event.setEventId("evt-123");
        event.setCorrelationId("corr-123");
        event.setPedidoId(1L);
        event.setClienteId(100L);
        event.setOccurredAt(LocalDateTime.now());
        
        PedidoEmPreparacaoEvent.Item item = new PedidoEmPreparacaoEvent.Item();
        item.setProdutoId(10L);
        item.setSku("SKU-X");
        item.setNomeProduto("Produto X");
        item.setQuantidade(2);
        event.setItens(List.of(item));

        String json = objectMapper.writeValueAsString(event);
        consumer.handle(json);

        List<OrdemSeparacao> ordens = repository.findAll();
        assertFalse(ordens.isEmpty());
        
        OrdemSeparacao ordem = ordens.get(0);
        assertEquals(1L, ordem.getPedidoId());
        assertEquals(100L, ordem.getClienteId());
        assertEquals(StatusOrdemSeparacao.PENDENTE, ordem.getStatus());
        assertEquals(1, ordem.getItens().size());
        assertEquals("SKU-X", ordem.getItens().get(0).getSku());
    }
}
