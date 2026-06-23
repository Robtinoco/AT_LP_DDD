package com.petfriends.pedidos.events;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Evento de domínio publicado pelo PetFriends_Pedidos quando um pedido entra
 * no estado EM_PREPARACAO. É consumido pelo PetFriends_Almoxarifado.
 *
 * Exchange : petfriends.pedidos.exchange  (Topic)
 * Routing Key: pedido.em-preparacao
 */
public class PedidoEmPreparacaoEvent {

    private String eventId;
    private String eventType = "PedidoEmPreparacao";
    private String eventVersion = "1.0";
    private String correlationId;
    private LocalDateTime occurredAt;
    private Long pedidoId;
    private Long clienteId;
    private List<ItemPedido> itens;

    public static class ItemPedido {
        private Long produtoId;
        private String sku;
        private String nomeProduto;
        private Integer quantidade;

        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public String getNomeProduto() { return nomeProduto; }
        public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }
        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getEventVersion() { return eventVersion; }
    public void setEventVersion(String eventVersion) { this.eventVersion = eventVersion; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
}
