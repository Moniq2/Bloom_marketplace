package org.bcc.bloom.repository;

import org.bcc.bloom.exception.RegraNegocioException;
import org.bcc.bloom.model.Categoria;
import org.bcc.bloom.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepository {

    private static final String COLUNAS = "nome, descricao, estoque, preco, categoria";

    public ProdutoRepository() {
        criarTabelaSeNaoExistir();
    }

    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS produtos (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "nome VARCHAR(100) NOT NULL, " +
                "descricao VARCHAR(255), " +
                "estoque INT, " +
                "preco DECIMAL(10,2), " +
                "categoria VARCHAR(20), " +
                "loja_id BIGINT" +
                ")";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao criar tabela de produtos: " + e.getMessage());
        }
    }

    public void salvar(Produto produto) {
        salvar(produto, null);
    }

    public void salvar(Produto produto, Long lojaId) {
        String sql = "INSERT INTO produtos (nome, descricao, estoque, preco, categoria, loja_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setInt(3, produto.getEstoque());
            pstmt.setDouble(4, produto.getPreco());
            pstmt.setString(5, produto.getCategoria() == null ? null : produto.getCategoria().name());
            if (lojaId == null) {
                pstmt.setNull(6, Types.BIGINT);
            } else {
                pstmt.setLong(6, lojaId);
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao salvar o produto na base de dados: " + e.getMessage());
        }
    }

    //USAR PAGINAÇÃO SE FOR LISTAR TODOS DE TODAS AS LOJAS
    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT " + COLUNAS + " FROM produtos";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                produtos.add(converter(rs));
            }
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao listar os produtos da base de dados: " + e.getMessage());
        }

        return produtos;
    }

    public List<Produto> listarPaginado(int pagina, int tamanho) {
        validarPagina(pagina, tamanho);

        String sql = "SELECT " + COLUNAS + " FROM produtos ORDER BY id LIMIT ? OFFSET ?";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tamanho);
            pstmt.setInt(2, pagina * tamanho);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(converter(rs));
                }
            }
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao listar os produtos paginados: " + e.getMessage());
        }
        return produtos;
    }

    public List<Produto> listarPorLoja(Long lojaId, int pagina, int tamanho) {
        validarPagina(pagina, tamanho);

        String sql = "SELECT " + COLUNAS + " FROM produtos "
                + "WHERE loja_id = ? ORDER BY id LIMIT ? OFFSET ?";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, lojaId);
            pstmt.setInt(2, tamanho);
            pstmt.setInt(3, pagina * tamanho);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(converter(rs));
                }
            }
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao listar os produtos da loja: " + e.getMessage());
        }
        return produtos;
    }

    public int contarTotal() {
        String sql = "SELECT COUNT(*) AS total FROM produtos";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao contar os produtos: " + e.getMessage());
        }
    }

    public int contarPorLoja(Long lojaId) {
        String sql = "SELECT COUNT(*) AS total FROM produtos WHERE loja_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, lojaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao contar os produtos da loja: " + e.getMessage());
        }
    }

    private void validarPagina(int pagina, int tamanho) {
        if (pagina < 0 || tamanho <= 0) {
            throw new RegraNegocioException("Erro: A página deve ser 0 ou maior e o tamanho deve ser maior que zero.");
        }
    }

    private Produto converter(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setNome(rs.getString("nome"));
        p.setDescricao(rs.getString("descricao"));
        p.setEstoque(rs.getInt("estoque"));
        p.setPreco(rs.getDouble("preco"));

        String categoria = rs.getString("categoria");
        if (categoria != null) {
            p.setCategoria(Categoria.valueOf(categoria));
        }
        return p;
    }
}