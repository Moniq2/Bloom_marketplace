package org.bcc.bloom.model;

public class Item {
    private Long id;
    private Produto produto;
    private Loja loja;
    private int quantidade;
    private double valorUnitario;

    public Item() {}

    public Item(Produto produto, Loja loja, int quantidade, double valorUnitario) {
        this.produto = produto;
        this.loja = loja;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Loja getLoja() { return loja; }
    public void setLoja(Loja loja) { this.loja = loja; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(double valorUnitario) { this.valorUnitario = valorUnitario; }

    public double getSubtotal() { return valorUnitario * quantidade; }
}