package com.petfriends.transporte;

import com.petfriends.transporte.domain.*;
import com.petfriends.transporte.infra.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransporteServiceTest {

    @Autowired
    private EntregaSolicitadaConsumer consumer;

    @Autowired
    private EntregaRepository repository;

    @Test
    void processarEventoEntregaSolicitadaTest() {
        EntregaSolicitadaEvent event = new EntregaSolicitadaEvent();
        event.setEventId("evt-456");
        event.setCorrelationId("corr-456");
        event.setPedidoId(2L);
        event.setClienteId(200L);
        event.setOccurredAt(LocalDateTime.now());
        
        EntregaSolicitadaEvent.Endereco endereco = new EntregaSolicitadaEvent.Endereco();
        endereco.setLogradouro("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("01000-000");
        event.setEnderecoEntrega(endereco);

        // Chama o consumer diretamente (bypass RabbitMQ)
        consumer.handle(event);

        List<Entrega> entregas = repository.findAll();
        assertFalse(entregas.isEmpty());
        
        Entrega entrega = entregas.get(0);
        assertEquals(2L, entrega.getPedidoId());
        assertEquals(200L, entrega.getClienteId());
        assertEquals(StatusEntrega.PENDENTE, entrega.getStatus());
        assertEquals("Rua das Flores", entrega.getEnderecoEntrega().getLogradouro());
    }
}
