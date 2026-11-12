
package org.bcc.bloom.service;

import org.bcc.bloom.dao.DAOFactory;
import org.bcc.bloom.dao.IItemDAO;
import org.bcc.bloom.dao.IProdutoDAO;
import org.bcc.bloom.model.ItemRegistro;
import org.bcc.bloom.model.Produto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ItemService {

    private final IItemDAO itemDAO;
    private final IProdutoDAO produtoDAO;

    public ItemService() {
        this(DAOFactory.getItemDAO(), DAOFactory.getProdutoDAO());
    }

    public ItemService(IItemDAO itemDAO, IProdutoDAO produtoDAO) {
        if (itemDAO == null || produtoDAO == null) {
            throw new IllegalArgumentException(
                    "Os DAOs não podem ser nulos."
            );
        }

        this.itemDAO = itemDAO;
        this.produtoDAO = produtoDAO;
    }

    public long criar(long carrinhoId, long produtoId, int quantidade) {

        if (carrinhoId <= 0 || produtoId <= 0) {
            throw new IllegalArgumentException("IDs inválidos.");
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        Produto produto = produtoDAO.buscarPorId(produtoId);

        if (produto == null) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }

        if (quantidade > produto.getEstoque()) {
            throw new IllegalArgumentException("Estoque insuficiente.");
        }

        double preco = produto.getPreco();

        if (!Double.isFinite(preco) || preco < 0) {
            throw new IllegalArgumentException("Preço do produto inválido.");
        }

        BigDecimal valor = BigDecimal.valueOf(preco);

        try {
            return itemDAO.adicionarItem(
                    carrinhoId,
                    produtoId,
                    quantidade,
                    valor
            );
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao adicionar item ao carrinho.", e
            );
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

        if (itemId <= 0 || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "ID e quantidade devem ser maiores que zero."
            );
        }

        try {
            itemDAO.atualizarQuantidade(itemId, quantidade);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item.", e);
        }
    }

    public void remover(long itemId) {

        if (itemId <= 0) {
            throw new IllegalArgumentException("ID do item inválido.");
        }

        try {
            itemDAO.removerItem(itemId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover item.", e);
        }
    }
}
