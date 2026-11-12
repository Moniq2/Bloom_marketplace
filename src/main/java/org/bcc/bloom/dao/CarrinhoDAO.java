
package org.bcc.bloom.dao;

import org.bcc.bloom.dto.CarrinhoDTO;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.model.Item;
import org.bcc.bloom.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarrinhoDAO implements ICarrinhoDAO {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @Override
    public long criarCarrinho(long compradorId) throws SQLException {

        String sql = "INSERT INTO carrinho (comprador_id) VALUES (?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, compradorId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        throw new SQLException("Não foi possível criar o carrinho.");
    }

    @Override
    public Long buscarPorId(long id) throws SQLException {

        String sql = "SELECT id FROM carrinho WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }

        return null;
    }

    @Override
    public Long buscarPorComprador(long compradorId) throws SQLException {

        String sql = "SELECT id FROM carrinho WHERE comprador_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, compradorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }

        return null;
    }

    @Override
    public CarrinhoDTO buscarItens(Long carrinhoId) throws SQLException {

        if (carrinhoId == null) {
            throw new IllegalArgumentException(
                    "O ID do carrinho não pode ser nulo."
            );
        }

        List<Item> itens = new ArrayList<>();
        List<Long> produtosIds = new ArrayList<>();

        String sqlCarrinho =
                "SELECT id FROM carrinho WHERE id = ?";

        String sqlItens =
                "SELECT id, produto_id, quantidade, valor_unitario " +
                "FROM item " +
                "WHERE container_id = ? AND tipo_container = 'CARRINHO' " +
                "ORDER BY id";

        try (Connection con = DatabaseConnection.getConnection()) {

            // Verifica se o carrinho existe.
            try (PreparedStatement ps = con.prepareStatement(sqlCarrinho)) {

                ps.setLong(1, carrinhoId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new RecursoNaoEncontradoException(
                                "Não existe carrinho de ID " + carrinhoId
                        );
                    }
                }
            }

            // Busca os itens pertencentes ao carrinho.
            try (PreparedStatement ps = con.prepareStatement(sqlItens)) {

                ps.setLong(1, carrinhoId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        Item item = new Item();

                        item.setId(rs.getLong("id"));
                        item.setQuantidade(rs.getInt("quantidade"));
                        item.setValorUnitario(
                                rs.getDouble("valor_unitario")
                        );

                        itens.add(item);

                        produtosIds.add(rs.getLong("produto_id"));
                    }
                }
            }
        }

        // Busca os produtos relacionados aos itens.
        List<Produto> produtos = produtoDAO.listarPorIds(produtosIds);

        // Organiza os produtos pelo ID para associá-los aos itens.
        Map<Long, Produto> produtosPorId = new HashMap<>();

        for (Produto produto : produtos) {
            produtosPorId.put(produto.getId(), produto);
        }

        // Associa cada produto ao seu respectivo item.
        for (int i = 0; i < itens.size(); i++) {

            Long produtoId = produtosIds.get(i);

            Produto produto = produtosPorId.get(produtoId);

            itens.get(i).setProduto(produto);
        }

        return new CarrinhoDTO(produtos, itens);
    }

    @Override
    public void excluirCarrinho(long carrinhoId) throws SQLException {

        String sqlItens =
                "DELETE FROM item " +
                "WHERE container_id = ? AND tipo_container = 'CARRINHO'";

        String sqlCarrinho =
                "DELETE FROM carrinho WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                // Remove primeiro os itens do carrinho.
                try (PreparedStatement ps = con.prepareStatement(sqlItens)) {

                    ps.setLong(1, carrinhoId);
                    ps.executeUpdate();
                }

                // Depois remove o carrinho.
                try (PreparedStatement ps = con.prepareStatement(sqlCarrinho)) {

                    ps.setLong(1, carrinhoId);

                    int linhas = ps.executeUpdate();

                    if (linhas == 0) {
                        throw new SQLException("Carrinho não encontrado.");
                    }
                }

                con.commit();

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }
        }
    }
}
