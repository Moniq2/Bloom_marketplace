
package org.bcc.bloom.repository;

import org.bcc.bloom.exception.ErroBancoDadosException;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Loja;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LojaRepository {

    public LojaRepository() {}

    // CREATE
    public void salvar(Loja loja, long vendedorId) {
        String sqlLoja = "INSERT INTO loja (nome, descricao, capa) VALUES (?, ?, ?)";
        String sqlVendedor = "UPDATE vendedor SET loja_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                long lojaId;

                try (PreparedStatement pstmt = conn.prepareStatement(
                        sqlLoja, Statement.RETURN_GENERATED_KEYS)) {

                    pstmt.setString(1, loja.getNome());
                    pstmt.setString(2, loja.getDescricao());
                    pstmt.setBytes(3, loja.getCapa());
                    pstmt.executeUpdate();

                    try (ResultSet chaves = pstmt.getGeneratedKeys()) {
                        if (!chaves.next()) {
                            throw new SQLException("O banco não retornou o ID da loja criada.");
                        }
                        lojaId = chaves.getLong(1);
                    }
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sqlVendedor)) {
                    pstmt.setLong(1, lojaId);
                    pstmt.setLong(2, vendedorId);

                    if (pstmt.executeUpdate() != 1) {
                        throw new RecursoNaoEncontradoException(
                                "Vendedor não encontrado (id " + vendedorId + ").");
                    }
                }

                conn.commit();
                loja.setId(lojaId);

            } catch (SQLException | RuntimeException e) {
                try {
                    conn.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }
                throw e;
            }

        } catch (SQLException e) {
            throw new ErroBancoDadosException(
                    "Erro ao salvar loja e vinculá-la ao vendedor: " + e.getMessage());
        }
    }

    // READ
    public Loja buscarPorId(long id) {
        String sql = "SELECT * FROM loja WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? converter(rs) : null;
            }

        } catch (SQLException e) {
            throw new ErroBancoDadosException(
                    "Erro ao buscar loja por id: " + e.getMessage());
        }
    }

    public Loja buscarPorVendedor(long vendedorId) {
        String sql = "SELECT l.* FROM loja l JOIN vendedor v ON v.loja_id = l.id WHERE v.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, vendedorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? converter(rs) : null;
            }

        } catch (SQLException e) {
            throw new ErroBancoDadosException(
                    "Erro ao buscar a loja do vendedor: " + e.getMessage());
        }
    }

    public List<Loja> listarPaginado(int pagina, int tamanho) {
        if (pagina < 0 || tamanho <= 0) {
            throw new ValorInvalidoException(
                    "A página deve ser 0 ou maior e o tamanho deve ser maior que zero.");
        }

        String sql = "SELECT * FROM loja ORDER BY id LIMIT ? OFFSET ?";
        List<Loja> lojas = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tamanho);
            pstmt.setInt(2, pagina * tamanho);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lojas.add(converter(rs));
                }
            }

        } catch (SQLException e) {
            throw new ErroBancoDadosException(
                    "Erro ao buscar lojas paginadas: " + e.getMessage());
        }

        return lojas;
    }

    public int contarTotalLojas() {
        String sql = "SELECT COUNT(*) AS total FROM loja";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt("total") : 0;

        } catch (SQLException e) {
            throw new ErroBancoDadosException(
                    "Erro ao contar lojas: " + e.getMessage());
        }
    }

    // UPDATE
    public void atualizar(Loja loja) {
        String sql = "UPDATE loja SET nome = ?, descricao = ?, capa = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loja.getNome());
            pstmt.setString(2, loja.getDescricao());
            pstmt.setBytes(3, loja.getCapa());
            pstmt.setLong(4, loja.getId());

            if (pstmt.executeUpdate() == 0) {
                throw new RecursoNaoEncontradoException(
                        "Loja não encontrada (id " + loja.getId() + ").");
            }

        } catch (SQLException e) {
            throw new ErroBancoDadosException(
                    "Erro ao atualizar loja: " + e.getMessage());
        }
    }

    // DELETE
    public void remover(long id) {
        String sqlVendedor = "UPDATE vendedor SET loja_id = NULL WHERE loja_id = ?";
        String sqlProdutos = "DELETE FROM produto WHERE loja_id = ?";
        String sqlLoja = "DELETE FROM loja WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                executarComId(conn, sqlVendedor, id);
                executarComId(conn, sqlProdutos, id);

                if (executarComId(conn, sqlLoja, id) == 0) {
                    throw new RecursoNaoEncontradoException(
                            "Loja não encontrada (id " + id + ").");
                }

                conn.commit();

            } catch (SQLException | RuntimeException e) {
                try {
                    conn.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }
                throw e;
            }

        } catch (SQLException e) {
            throw new ErroBancoDadosException(
                    "Erro ao remover loja: " + e.getMessage());
        }
    }

    private int executarComId(Connection conn, String sql, long id)
            throws SQLException {

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate();
        }
    }

    private Loja converter(ResultSet rs) throws SQLException {
        Loja loja = new Loja(
                rs.getString("nome"),
                rs.getString("descricao"),
                rs.getBytes("capa")
        );

        loja.setId(rs.getLong("id"));
        return loja;
    }
}
