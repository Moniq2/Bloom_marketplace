package org.bcc.bloom.repository;

import java.util.ArrayList;

import org.bcc.bloom.model.Produto;

public class ProdutoRepository {
    private static final ArrayList<Produto> produtos = new ArrayList<>();

    public static void salvar(Produto produto) {
        if (produto != null) {
            produtos.add(produto);
        }
    }

    public static ArrayList<Produto> listarTodos() {
        return produtos;
    }
}