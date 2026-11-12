
package org.bcc.bloom.service;

import org.bcc.bloom.dao.DAOFactory;
import org.bcc.bloom.dao.IProdutoDAO;
import org.bcc.bloom.exception.CampoObrigatorioException;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Categoria;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Produto;

import java.util.List;

public class ProdutoService {

    private final IProdutoDAO produtoDAO;

    public ProdutoService() {
        this(DAOFactory.getProdutoDAO());
    }

    public ProdutoService(IProdutoDAO produtoDAO) {
        if (produtoDAO == null) {
            throw new IllegalArgumentException(
                    "O DAO do produto não pode ser nulo."
            );
        }

        this.produtoDAO = produtoDAO;
    }

    public Produto cadastrarProduto(
            Loja loja,
            String nome,
            String descricao,
            int estoque,
            double preco,
            Categoria categoria,
            byte[] imagem) {

        if (loja == null) {
            throw new RecursoNaoEncontradoException(
                    "Erro: A loja de destino não foi encontrada."
            );
        }

        if (loja.getId() == null || loja.getId() <= 0) {
            throw new RecursoNaoEncontradoException(
                    "Erro: A loja precisa estar cadastrada antes de receber produtos."
            );
        }

        if (nome == null || nome.trim().isEmpty()) {
            throw new CampoObrigatorioException(
                    "Erro: O nome do produto é obrigatório."
            );
        }

        if (descricao == null || descricao.trim().isEmpty()) {
            throw new CampoObrigatorioException(
                    "Erro: A descrição do produto é obrigatória."
            );
        }

        if (categoria == null) {
            throw new CampoObrigatorioException(
                    "Erro: A categoria do produto é obrigatória."
            );
        }

        if (!Double.isFinite(preco) || preco <= 0) {
            throw new ValorInvalidoException(
                    "Erro: O preço do produto deve ser maior que zero."
            );
        }

        if (estoque < 0) {
            throw new ValorInvalidoException(
                    "Erro: O estoque não pode ser negativo."
            );
        }

        Produto produto = new Produto(
                nome.trim(),
                descricao.trim(),
                estoque,
                preco,
                categoria,
                imagem
        );

        produtoDAO.salvar(produto, loja.getId());

        loja.getProdutos().add(produto);

        return produto;
    }

    public List<Produto> listarProdutos(int pagina, int tamanho) {
        validarPaginacao(pagina, tamanho);
        return produtoDAO.listarPaginado(pagina, tamanho);
    }

    public Produto buscarPorId(long id) {
        validarId(id);

        Produto produto = produtoDAO.buscarPorId(id);

        if (produto == null) {
            throw new RecursoNaoEncontradoException(
                    "Erro: Produto não encontrado."
            );
        }

        return produto;
    }

    public void atualizar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException(
                    "O produto não pode ser nulo."
            );
        }

        if (produto.getId() == null || produto.getId() <= 0) {
            throw new ValorInvalidoException(
                    "Erro: O produto precisa possuir um ID válido."
            );
        }

        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new CampoObrigatorioException(
                    "Erro: O nome do produto é obrigatório."
            );
        }

        if (produto.getDescricao() == null
                || produto.getDescricao().trim().isEmpty()) {
            throw new CampoObrigatorioException(
                    "Erro: A descrição do produto é obrigatória."
            );
        }

        if (produto.getCategoria() == null) {
            throw new CampoObrigatorioException(
                    "Erro: A categoria do produto é obrigatória."
            );
        }

        if (!Double.isFinite(produto.getPreco()) || produto.getPreco() <= 0) {
            throw new ValorInvalidoException(
                    "Erro: O preço do produto deve ser maior que zero."
            );
        }

        if (produto.getEstoque() < 0) {
            throw new ValorInvalidoException(
                    "Erro: O estoque não pode ser negativo."
            );
        }

        produto.setNome(produto.getNome().trim());
        produto.setDescricao(produto.getDescricao().trim());

        produtoDAO.atualizar(produto);
    }

    public void atualizarEstoque(long id, int novoEstoque) {
        validarId(id);

        if (novoEstoque < 0) {
            throw new ValorInvalidoException(
                    "Erro: O estoque não pode ser negativo."
            );
        }

        produtoDAO.atualizarEstoque(id, novoEstoque);
    }

    public void remover(long id) {
        validarId(id);
        produtoDAO.remover(id);
    }

    public List<Produto> listarProdutosDaLoja(
            Long lojaId,
            int pagina,
            int tamanho) {

        if (lojaId == null || lojaId <= 0) {
            throw new CampoObrigatorioException(
                    "Erro: O ID da loja é obrigatório e deve ser válido."
            );
        }

        validarPaginacao(pagina, tamanho);

        return produtoDAO.listarPorLoja(lojaId, pagina, tamanho);
    }

    public List<Produto> listarPorCategoria(
            Categoria categoria,
            int pagina,
            int tamanho) {

        if (categoria == null) {
            throw new CampoObrigatorioException(
                    "Erro: A categoria é obrigatória."
            );
        }

        validarPaginacao(pagina, tamanho);

        return produtoDAO.listarPorCategoria(
                categoria,
                pagina,
                tamanho
        );
    }

    public int contarProdutos() {
        return produtoDAO.contarTotal();
    }

    public int contarProdutosDaLoja(Long lojaId) {
        if (lojaId == null || lojaId <= 0) {
            throw new CampoObrigatorioException(
                    "Erro: O ID da loja é obrigatório e deve ser válido."
            );
        }

        return produtoDAO.contarPorLoja(lojaId);
    }

    public int totalDePaginas(int tamanho) {
        if (tamanho <= 0) {
            throw new ValorInvalidoException(
                    "Erro: O tamanho da página deve ser maior que zero."
            );
        }

        return (contarProdutos() + tamanho - 1) / tamanho;
    }

    public int totalDePaginasDaLoja(Long lojaId, int tamanho) {
        if (tamanho <= 0) {
            throw new ValorInvalidoException(
                    "Erro: O tamanho da página deve ser maior que zero."
            );
        }

        return (contarProdutosDaLoja(lojaId) + tamanho - 1) / tamanho;
    }

    private void validarId(long id) {
        if (id <= 0) {
            throw new ValorInvalidoException(
                    "Erro: O ID deve ser maior que zero."
            );
        }
    }

    private void validarPaginacao(int pagina, int tamanho) {
        if (pagina < 0) {
            throw new ValorInvalidoException(
                    "Erro: A página não pode ser negativa."
            );
        }

        if (tamanho <= 0) {
            throw new ValorInvalidoException(
                    "Erro: O tamanho da página deve ser maior que zero."
            );
        }
    }
}
