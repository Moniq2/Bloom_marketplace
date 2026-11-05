package org.bcc.bloom.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CarrinhoRepository {

    // CREATE: cria um carrinho para um comprador
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

    // READ: busca o carrinho pelo ID
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

    // READ: busca o carrinho de um comprador
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

    // DELETE: exclui um carrinho e seus itens
    public void excluirCarrinho(long carrinhoId) throws SQLException {
        String sqlItens = "DELETE FROM item WHERE container_id = ? AND tipo_container = 'CARRINHO'";
        String sqlCarrinho = "DELETE FROM carrinho WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(sqlItens)) {
                    ps.setLong(1, carrinhoId);
                    ps.executeUpdate();
                }

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