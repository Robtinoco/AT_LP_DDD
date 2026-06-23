package com.petfriends.almoxarifado.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class ItemSeparacao {
    private Long produtoId;
    private String sku;
    private String nomeProduto;
    private Integer quantidade;

    protected ItemSeparacao() {}

    public ItemSeparacao(Long produtoId, String sku, String nomeProduto, Integer quantidade) {
        this.produtoId = produtoId;
        this.sku = sku;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
    }

    public Long getProdutoId() { return produtoId; }
    public String getSku() { return sku; }
    public String getNomeProduto() { return nomeProduto; }
    public Integer getQuantidade() { return quantidade; }
}
