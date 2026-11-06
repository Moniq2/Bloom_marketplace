package org.bcc.bloom.service;

import org.bcc.bloom.exception.CampoObrigatorioException;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Categoria;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.dao.ProdutoDAO;

import java.util.List;

public class ProdutoService {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    public Produto cadastrarProduto(Loja loja, String nome, String descricao, int estoque, double preco,
            Categoria categoria, byte[] imagem) {
        if (loja == null) {
            throw new RecursoNaoEncontradoException("Erro: A loja de destino não foi encontrada.");
        }
        if (loja.getId() == null || loja.getId() <= 0) {
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
        if (preco <= 0) {
            throw new ValorInvalidoException("Erro: O preço do produto deve ser maior que zero.");
        }
        if (estoque < 0) {
            throw new ValorInvalidoException("Erro: O estoque não pode ser negativo.");
        }

        Produto produto = new Produto(nome, descricao, estoque, preco, categoria, imagem);
        produtoDAO.salvar(produto, loja.getId());
        loja.getProdutos().add(produto);
        return produto;
    }

    public List<Produto> listarProdutos(int pagina, int tamanho) {
        return produtoDAO.listarPaginado(pagina, tamanho);
    }

    public List<Produto> listarProdutosDaLoja(Long lojaId, int pagina, int tamanho) {
        if (lojaId == null) {
            throw new CampoObrigatorioException("Erro: O id da loja é obrigatório.");
        }
        return produtoDAO.listarPorLoja(lojaId, pagina, tamanho);
    }

    public int contarProdutos() {
        return produtoDAO.contarTotal();
    }

    public int contarProdutosDaLoja(Long lojaId) {
        if (lojaId == null) {
            throw new CampoObrigatorioException("Erro: O id da loja é obrigatório.");
        }
        return produtoDAO.contarPorLoja(lojaId);
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
