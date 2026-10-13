package org.bcc.bloom.repository;

import org.bcc.bloom.exception.RegraNegocioException;
import org.bcc.bloom.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepository {

    public ProdutoRepository() {
        criarTabelaSeNaoExistir();
    }

    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS produtos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "descricao TEXT, " +
                "estoque INTEGER, " +
                "preco REAL" +
                ")";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao criar tabela de produtos: " + e.getMessage());
        }
    }

    public void salvar(Produto produto) {
        String sql = "INSERT INTO produtos (nome, descricao, estoque, preco) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setInt(3, produto.getEstoque());
            pstmt.setDouble(4, produto.getPreco());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao salvar o produto na base de dados: " + e.getMessage());
        }
    }

    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT nome, descricao, estoque, preco FROM produtos";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Produto p = new Produto();
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                p.setEstoque(rs.getInt("estoque"));
                p.setPreco(rs.getDouble("preco"));
                produtos.add(p);
            }
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao listar os produtos da base de dados: " + e.getMessage());
        }
        
        return produtos;
    }
}