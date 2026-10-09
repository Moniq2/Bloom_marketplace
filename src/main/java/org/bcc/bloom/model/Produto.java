package org.bcc.bloom.model;

public class Produto {
    private String nome;
    private String descricao;
    private int estoque;
    private double preco;
    private Categoria categoria;
    private byte[] imagem;

    public Produto() {}

    public Produto(String nome, String descricao, int estoque, double preco, Categoria categoria, byte[] imagem) {
        this.nome = nome;
        this.descricao = descricao;
        this.estoque = estoque;
        this.preco = preco;
        this.categoria = categoria;
        this.imagem = imagem;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescdescricao() { return descricao; } // Nota: manter getter padrão getDescricao
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getEstoque() { return estoque; }
    public void setEstoque(int estoque) { this.estoque = estoque; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public byte[] getImagem() { return imagem; }
    public void setImagem(byte[] imagem) { this.imagem = imagem; }
}