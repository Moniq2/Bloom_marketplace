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
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "descricao TEXT, " +
                "vendedor_email TEXT" +
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
}