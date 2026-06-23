package com.petfriends.transporte.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Entrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long pedidoId;
    private Long clienteId;
    private String codigoRastreio;
    
    @Enumerated(EnumType.STRING)
    private StatusEntrega status;
    
    @Embedded
    private EnderecoEntrega enderecoEntrega;
    
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEnvio;
    private LocalDateTime dataEntrega;
    
    protected Entrega() {}
    
    public Entrega(Long pedidoId, Long clienteId, EnderecoEntrega enderecoEntrega) {
        this.pedidoId = pedidoId;
        this.clienteId = clienteId;
        this.enderecoEntrega = enderecoEntrega;
        this.status = StatusEntrega.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
    }
    
    public void iniciarTransporte(String codigoRastreio) {
        if(this.status != StatusEntrega.PENDENTE) {
            throw new IllegalStateException("A entrega deve estar pendente para iniciar o transporte.");
        }
        this.status = StatusEntrega.EM_TRANSITO;
        this.codigoRastreio = codigoRastreio;
        this.dataEnvio = LocalDateTime.now();
    }
    
    public void marcarComoEntregue() {
        if(this.status != StatusEntrega.EM_TRANSITO) {
            throw new IllegalStateException("A entrega deve estar em trânsito para ser entregue.");
        }
        this.status = StatusEntrega.ENTREGUE;
        this.dataEntrega = LocalDateTime.now();
    }
    
    public void marcarComoExtraviada() {
        if(this.status != StatusEntrega.EM_TRANSITO) {
            throw new IllegalStateException("A entrega deve estar em trânsito para ser extraviada.");
        }
        this.status = StatusEntrega.EXTRAVIADA;
    }
    
    public void marcarComoDevolvida() {
        if(this.status != StatusEntrega.EM_TRANSITO) {
            throw new IllegalStateException("A entrega deve estar em trânsito para ser devolvida.");
        }
        this.status = StatusEntrega.DEVOLVIDA;
    }
    
    public void cancelar() {
        this.status = StatusEntrega.CANCELADA;
    }

    public Long getId() { return id; }
    public Long getPedidoId() { return pedidoId; }
    public Long getClienteId() { return clienteId; }
    public String getCodigoRastreio() { return codigoRastreio; }
    public StatusEntrega getStatus() { return status; }
    public EnderecoEntrega getEnderecoEntrega() { return enderecoEntrega; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public LocalDateTime getDataEntrega() { return dataEntrega; }
}
