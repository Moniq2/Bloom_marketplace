
package org.bcc.bloom.dao;

import org.bcc.bloom.model.ItemRegistro;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO implements IItemDAO {

    @Override
    public long adicionarItem(
            long carrinhoId,
            long produtoId,
            int quantidade,
            BigDecimal valorUnitario
    ) throws SQLException {

        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero.");
        }

        if (valorUnitario == null || valorUnitario.signum() < 0) {
            throw new IllegalArgumentException(
                    "O valor unitário é inválido.");
        }

        String sql = "INSERT INTO item "
                + "(produto_id, quantidade, container_id, tipo_container, valor_unitario) "
                + "VALUES (?, ?, ?, 'CARRINHO', ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, produtoId);
            ps.setInt(2, quantidade);
            ps.setLong(3, carrinhoId);
            ps.setBigDecimal(4, valorUnitario);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        throw new SQLException("Não foi possível adicionar o item.");
    }

    @Override
    public List<ItemRegistro> listarPorCarrinho(
            long carrinhoId
    ) throws SQLException {

        String sql = "SELECT id, produto_id, quantidade, valor_unitario "
                + "FROM item WHERE container_id = ? "
                + "AND tipo_container = 'CARRINHO' ORDER BY id";

        List<ItemRegistro> itens = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, carrinhoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    itens.add(converter(rs));
                }
            }
        }

        return itens;
    }

    @Override
    public ItemRegistro buscarPorId(long itemId) throws SQLException {

        String sql = "SELECT id, produto_id, quantidade, valor_unitario "
                + "FROM item WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, itemId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return converter(rs);
                }
            }
        }

        return null;
    }

    @Override
    public void atualizarQuantidade(
            long itemId,
            int novaQuantidade
    ) throws SQLException {

        if (novaQuantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero.");
        }

        String sql = "UPDATE item SET quantidade = ? WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, novaQuantidade);
            ps.setLong(2, itemId);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Item não encontrado.");
            }
        }
    }

    @Override
    public void removerItem(long itemId) throws SQLException {

        String sql = "DELETE FROM item WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, itemId);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Item não encontrado.");
            }
        }
    }

    @Override
    public void esvaziarCarrinho(long carrinhoId) throws SQLException {

        String sql = "DELETE FROM item "
                + "WHERE container_id = ? AND tipo_container = 'CARRINHO'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, carrinhoId);
            ps.executeUpdate();
        }
    }

    private ItemRegistro converter(ResultSet rs) throws SQLException {
        return new ItemRegistro(
                rs.getLong("id"),
                rs.getLong("produto_id"),
                rs.getInt("quantidade"),
                rs.getBigDecimal("valor_unitario")
        );
    }
}
