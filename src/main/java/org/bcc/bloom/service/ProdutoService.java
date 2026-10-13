package org.bcc.bloom.service;

import org.bcc.bloom.exception.RegraNegocioException;
import org.bcc.bloom.model.Categoria;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.repository.ProdutoRepository;

public class ProdutoService {

    private final ProdutoRepository produtoRepository = new ProdutoRepository();

    public Produto cadastrarProduto(Loja loja, String nome, String descricao, int estoque, double preco, Categoria categoria, byte[] imagem) {
        if (loja == null) {
            throw new RegraNegocioException("Erro: A loja de destino não pode ser nula.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("Erro: O nome do produto é obrigatório.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new RegraNegocioException("Erro: A descrição do produto é obrigatória.");
        }
        if (categoria == null) {
            throw new RegraNegocioException("Erro: A categoria do produto é obrigatória.");
        }

        Produto produto = new Produto(nome, descricao, estoque, preco, categoria, imagem);
        loja.getProdutos().add(produto);
        produtoRepository.salvar(produto);
        return produto;
    }
}