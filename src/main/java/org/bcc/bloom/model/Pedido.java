
package org.bcc.bloom.model;

import java.util.ArrayList;

public class Pedido {

    private ArrayList<Produto> produtos;
    private Usuario usuario;
    private long id;
    private double valorTotal;

    public Pedido() {
        this.produtos = new ArrayList<>();
    }

    public Pedido(ArrayList<Produto> produtos) {
        this.produtos = produtos != null
                ? produtos
                : new ArrayList<>();
    }

    public Pedido(
            long id,
            Usuario usuario,
            double valorTotal,
            ArrayList<Produto> produtos
    ) {
        this.id = id;
        this.usuario = usuario;
        this.valorTotal = valorTotal;
        this.produtos = produtos != null
                ? produtos
                : new ArrayList<>();
    }

    public ArrayList<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(ArrayList<Produto> produtos) {
        this.produtos = produtos != null
                ? produtos
                : new ArrayList<>();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
