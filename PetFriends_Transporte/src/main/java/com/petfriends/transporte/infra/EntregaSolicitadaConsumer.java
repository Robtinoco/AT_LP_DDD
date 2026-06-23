package com.petfriends.transporte.infra;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.petfriends.transporte.domain.*;

@Component
public class EntregaSolicitadaConsumer {

    private final EntregaRepository repository;

    public EntregaSolicitadaConsumer(EntregaRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    @Transactional
    public void handle(EntregaSolicitadaEvent event) {
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
    }
}
