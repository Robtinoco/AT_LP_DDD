package com.petfriends.transporte.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.petfriends.transporte.domain.*;

@Component
public class EntregaSolicitadaConsumer {

    private final EntregaRepository repository;
    private final ObjectMapper objectMapper;

    public EntregaSolicitadaConsumer(EntregaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    @Transactional
    public void handle(String json) {
        try {
            EntregaSolicitadaEvent event = objectMapper.readValue(json, EntregaSolicitadaEvent.class);
            EnderecoEntrega endereco = new EnderecoEntrega(
                event.getEnderecoEntrega().getLogradouro(),
                event.getEnderecoEntrega().getNumero(),
                event.getEnderecoEntrega().getComplemento(),
                event.getEnderecoEntrega().getBairro(),
                event.getEnderecoEntrega().getCidade(),
                event.getEnderecoEntrega().getEstado(),
                event.getEnderecoEntrega().getCep()
            );
                
            Entrega entrega = new Entrega(event.getPedidoId(), event.getClienteId(), endereco);
            repository.save(entrega);
            System.out.println("Entrega criada para o pedido: " + event.getPedidoId());
        } catch (Exception e) {
            System.err.println("Erro ao processar evento de transporte: " + e.getMessage());
        }
    }
}
