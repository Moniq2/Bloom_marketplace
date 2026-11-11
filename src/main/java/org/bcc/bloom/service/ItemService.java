
package org.bcc.bloom.service;

import org.bcc.bloom.dao.ItemDAO;
import org.bcc.bloom.dao.ProdutoDAO;
import org.bcc.bloom.model.ItemRegistro;
import org.bcc.bloom.model.Produto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ItemService {

    private final ItemDAO itemDAO = new ItemDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    public long criar(long carrinhoId, long produtoId, int quantidade) {

        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        Produto produto = produtoDAO.buscarPorId(produtoId);

        if (produto == null) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }

        if (quantidade > produto.getEstoque()) {
            throw new IllegalArgumentException("Estoque insuficiente.");
        }

        BigDecimal valor = BigDecimal.valueOf(produto.getPreco());

        try {
            return itemDAO.adicionarItem(
                    carrinhoId,
                    produtoId,
                    quantidade,
                    valor
            );
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar item ao carrinho.", e);
        }
    }

    public ItemRegistro buscarPorId(long itemId) {
        try {
            return itemDAO.buscarPorId(itemId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item.", e);
        }
    }

    public List<ItemRegistro> listarPorCarrinho(long carrinhoId) {
        try {
            return itemDAO.listarPorCarrinho(carrinhoId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens do carrinho.", e);
        }
    }

    public void atualizar(long itemId, int quantidade) {

        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        try {
            itemDAO.atualizarQuantidade(itemId, quantidade);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item.", e);
        }
    }

    public void remover(long itemId) {
        try {
            itemDAO.removerItem(itemId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover item.", e);
        }
    }
}
