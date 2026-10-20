package org.bcc.bloom.service;

import org.bcc.bloom.exception.CampoObrigatorioException;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Categoria;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.repository.ProdutoRepository;

import java.util.List;

public class ProdutoService {

    private final ProdutoRepository produtoRepository = new ProdutoRepository();

    public Produto cadastrarProduto(Loja loja, String nome, String descricao, int estoque, double preco,
            Categoria categoria, byte[] imagem) {
        if (loja == null) {
            throw new RecursoNaoEncontradoException("Erro: A loja de destino não foi encontrada.");
        }
        if (loja.getId() <= 0) {
            throw new RecursoNaoEncontradoException("Erro: A loja precisa estar cadastrada antes de receber produtos.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new CampoObrigatorioException("Erro: O nome do produto é obrigatório.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new CampoObrigatorioException("Erro: A descrição do produto é obrigatória.");
        }
        if (categoria == null) {
            throw new CampoObrigatorioException("Erro: A categoria do produto é obrigatória.");
        }

        Produto produto = new Produto(nome, descricao, estoque, preco, categoria, imagem);
        produtoRepository.salvar(produto, loja.getId());
        loja.getProdutos().add(produto);
        return produto;
    }

    public List<Produto> listarProdutos(int pagina, int tamanho) {
        return produtoRepository.listarPaginado(pagina, tamanho);
    }

    public List<Produto> listarProdutosDaLoja(Long lojaId, int pagina, int tamanho) {
        if (lojaId == null) {
            throw new CampoObrigatorioException("Erro: O id da loja é obrigatório.");
        }
        return produtoRepository.listarPorLoja(lojaId, pagina, tamanho);
    }

    public int contarProdutos() {
        return produtoRepository.contarTotal();
    }

    public int contarProdutosDaLoja(Long lojaId) {
        if (lojaId == null) {
            throw new CampoObrigatorioException("Erro: O id da loja é obrigatório.");
        }
        return produtoRepository.contarPorLoja(lojaId);
    }

    public int totalDePaginas(int tamanho) {
        if (tamanho <= 0) {
            throw new ValorInvalidoException("Erro: O tamanho da página deve ser maior que zero.");
        }
        return (contarProdutos() + tamanho - 1) / tamanho;
    }

    public int totalDePaginasDaLoja(Long lojaId, int tamanho) {
        if (tamanho <= 0) {
            throw new ValorInvalidoException("Erro: O tamanho da página deve ser maior que zero.");
        }
        return (contarProdutosDaLoja(lojaId) + tamanho - 1) / tamanho;
    }
}