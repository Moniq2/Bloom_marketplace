package org.bcc.bloom.service;

import org.bcc.bloom.model.Categoria;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.repository.ProdutoRepository;

public class ProdutoService {

    public Produto cadastrarProduto(Loja loja, String nome, String descricao, int estoque, double preco, Categoria categoria, byte[] imagem) {
        if (loja == null) {
            throw new IllegalArgumentException("Erro: A loja de destino não pode ser nula.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome do produto é obrigatório.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A descrição do produto é obrigatória.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("Erro: A categoria do produto é obrigatória.");
        }

        Produto produto = new Produto(nome, descricao, estoque, preco, categoria, imagem);
        loja.getProdutos().add(produto);
        ProdutoRepository.salvar(produto);
        return produto;
    }
}