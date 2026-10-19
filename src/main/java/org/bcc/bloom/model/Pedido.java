package org.bcc.bloom.model;

import java.util.ArrayList;

public class Pedido {
    private final ArrayList<Produto> produtos;
    Usuario usuario;
    long id;
    double valorTotal;

    public Pedido(ArrayList<Produto> produtos) {
        this.produtos = produtos;
    }
}
