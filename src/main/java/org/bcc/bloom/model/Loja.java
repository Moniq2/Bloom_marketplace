package org.bcc.bloom.model;

import java.util.ArrayList;

public class Loja {
    private String nome;
    private String descricao;
    private ArrayList<Produto> produtos;
    private byte[] capa;

    public Loja() {
        this.produtos = new ArrayList<>();
    }

    public Loja(String nome, String descricao, byte[] capa) {
        this.nome = nome;
        this.descricao = descricao;
        this.produtos = new ArrayList<>();
        this.capa = capa;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public ArrayList<Produto> getProdutos() { return produtos; }
    public void setProdutos(ArrayList<Produto> produtos) { this.produtos = produtos; }

    public byte[] getCapa() { return capa; }
    public void setCapa(byte[] capa) { this.capa = capa; }
}