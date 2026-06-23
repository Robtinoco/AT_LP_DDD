package com.petfriends.almoxarifado.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class OrdemSeparacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long pedidoId;
    private Long clienteId;
    
    @Enumerated(EnumType.STRING)
    private StatusOrdemSeparacao status;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<ItemSeparacao> itens = new ArrayList<>();
    
    private LocalDateTime dataCriacao;
    private LocalDateTime dataSeparacao;
    private LocalDateTime dataDespacho;
    
    protected OrdemSeparacao() {}
    
    public OrdemSeparacao(Long pedidoId, Long clienteId, List<ItemSeparacao> itens) {
        this.pedidoId = pedidoId;
        this.clienteId = clienteId;
        this.itens = itens;
        this.status = StatusOrdemSeparacao.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
    }
    
    public void iniciarSeparacao() {
        if(this.status != StatusOrdemSeparacao.PENDENTE) {
            throw new IllegalStateException("Só é possível iniciar separação de ordens pendentes");
        }
        this.status = StatusOrdemSeparacao.EM_SEPARACAO;
    }
    
    public void marcarComoSeparada() {
        if(this.status != StatusOrdemSeparacao.EM_SEPARACAO) {
            throw new IllegalStateException("A ordem precisa estar em separação");
        }
        this.status = StatusOrdemSeparacao.SEPARADA;
        this.dataSeparacao = LocalDateTime.now();
    }
    
    public void marcarComoDespachada() {
        if(this.status != StatusOrdemSeparacao.SEPARADA) {
            throw new IllegalStateException("A ordem precisa estar separada para ser despachada");
        }
        this.status = StatusOrdemSeparacao.DESPACHADA;
        this.dataDespacho = LocalDateTime.now();
    }
    
    public void cancelar() {
        this.status = StatusOrdemSeparacao.CANCELADA;
    }

    public Long getId() { return id; }
    public Long getPedidoId() { return pedidoId; }
    public Long getClienteId() { return clienteId; }
    public StatusOrdemSeparacao getStatus() { return status; }
    public List<ItemSeparacao> getItens() { return itens; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataSeparacao() { return dataSeparacao; }
    public LocalDateTime getDataDespacho() { return dataDespacho; }
}
