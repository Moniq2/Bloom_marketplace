package org.bcc.bloom.repository;

import org.bcc.bloom.exception.RegraNegocioException;
import org.bcc.bloom.model.Loja;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LojaRepository {

    public LojaRepository() {
        criarTabelaSeNaoExistir();
    }

    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS lojas (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "nome VARCHAR(100) NOT NULL, " +
                "descricao VARCHAR(255), " +
                "vendedor_email VARCHAR(150)" +
                ")";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao criar tabela de lojas: " + e.getMessage());
        }
    }

    public void salvar(Loja loja, String vendedorEmail) {
        String sql = "INSERT INTO lojas (nome, descricao, vendedor_email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loja.getNome());
            pstmt.setString(2, loja.getDescricao());
            pstmt.setString(3, vendedorEmail);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao salvar loja na base de dados: " + e.getMessage());
        }
    }

    public List<Loja> listarTodas() {
        String sql = "SELECT * FROM lojas";
        List<Loja> lojas = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                lojas.add(new Loja(nome, descricao, null));
            }
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao buscar lojas: " + e.getMessage());
        }
        return lojas;
    }

    public List<Loja> listarPaginado(int pagina, int tamanho) {
        if (pagina < 0 || tamanho <= 0) {
            throw new RegraNegocioException("Erro: A página deve ser 0 ou maior e o tamanho deve ser maior que zero.");
        }

        String sql = "SELECT * FROM lojas ORDER BY id LIMIT ? OFFSET ?";
        List<Loja> lojas = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tamanho);
            pstmt.setInt(2, pagina * tamanho);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    String descricao = rs.getString("descricao");
                    lojas.add(new Loja(nome, descricao, null));
                }
            }
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao buscar lojas paginadas: " + e.getMessage());
        }
        return lojas;
    }

    public int contarTotalLojas() {
        String sql = "SELECT COUNT(*) AS total FROM lojas";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao contar lojas: " + e.getMessage());
        }
    }
}