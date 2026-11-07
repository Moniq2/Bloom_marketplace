package org.bcc.bloom.dao;

import org.bcc.bloom.exception.ErroBancoDadosException;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Categoria;
import org.bcc.bloom.model.Produto;

import java.sql.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ProdutoDAO {

    private static final String COLUNAS = "id, nome, descricao, estoque, preco, categoria, imagem";

    public void salvar(Produto produto, long lojaId) {
        String sql = "INSERT INTO produto (nome, descricao, estoque, preco, categoria, imagem, loja_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setInt(3, produto.getEstoque());
            pstmt.setDouble(4, produto.getPreco());
            pstmt.setString(5, categoriaParaBanco(produto.getCategoria()));
            pstmt.setBytes(6, produto.getImagemBytes());
            pstmt.setLong(7, lojaId);

            pstmt.executeUpdate();

            try (ResultSet chaves = pstmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    produto.setId(chaves.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao salvar o produto na base de dados: " + e.getMessage());
        }
    }



    public Produto buscarPorId(long id) {
        String sql = "SELECT " + COLUNAS + " FROM produto WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? converter(rs) : null;
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao buscar produto por id: " + e.getMessage());
        }
    }

    public List<Produto> listarPaginado(int pagina, int tamanho) {
        validarPagina(pagina, tamanho);

        String sql = "SELECT " + COLUNAS + " FROM produto ORDER BY id LIMIT ? OFFSET ?";
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
            throw new ErroBancoDadosException("Erro ao listar os produtos paginados: " + e.getMessage());
        }
        return produtos;
    }

    public List<Produto> listarPorLoja(Long lojaId, int pagina, int tamanho) {
        validarPagina(pagina, tamanho);

        String sql = "SELECT " + COLUNAS + " FROM produto WHERE loja_id = ? ORDER BY id LIMIT ? OFFSET ?";
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
            throw new ErroBancoDadosException("Erro ao listar os produtos da loja: " + e.getMessage());
        }
        return produtos;
    }

    public List<Produto> listarPorCategoria(Categoria categoria, int pagina, int tamanho) {
        validarPagina(pagina, tamanho);

        String sql = "SELECT " + COLUNAS + " FROM produto WHERE categoria = ? ORDER BY id LIMIT ? OFFSET ?";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoriaParaBanco(categoria));
            pstmt.setInt(2, tamanho);
            pstmt.setInt(3, pagina * tamanho);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(converter(rs));
                }
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao listar os produtos por categoria: " + e.getMessage());
        }
        return produtos;
    }

    public int contarTotal() {
        String sql = "SELECT COUNT(*) AS total FROM produto";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao contar os produtos: " + e.getMessage());
        }
    }

    public int contarPorLoja(Long lojaId) {
        String sql = "SELECT COUNT(*) AS total FROM produto WHERE loja_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, lojaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao contar os produtos da loja: " + e.getMessage());
        }
    }

    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, descricao = ?, estoque = ?, preco = ?, categoria = ?, imagem = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setInt(3, produto.getEstoque());
            pstmt.setDouble(4, produto.getPreco());
            pstmt.setString(5, categoriaParaBanco(produto.getCategoria()));
            pstmt.setBytes(6, produto.getImagemBytes());
            pstmt.setLong(7, produto.getId());

            exigirAlgumaLinha(pstmt.executeUpdate(), produto.getId());
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    public void atualizarEstoque(long id, int novoEstoque) {
        if (novoEstoque < 0) {
            throw new ValorInvalidoException("Erro: O estoque não pode ser negativo.");
        }

        String sql = "UPDATE produto SET estoque = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, novoEstoque);
            pstmt.setLong(2, id);

            exigirAlgumaLinha(pstmt.executeUpdate(), id);
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao atualizar o estoque do produto: " + e.getMessage());
        }
    }

    

    public void remover(long id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            exigirAlgumaLinha(pstmt.executeUpdate(), id);
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao remover produto: " + e.getMessage());
        }
    }



    private void validarPagina(int pagina, int tamanho) {
        if (pagina < 0 || tamanho <= 0) {
            throw new ValorInvalidoException("Erro: A página deve ser 0 ou maior e o tamanho deve ser maior que zero.");
        }
    }

    private void exigirAlgumaLinha(int linhasAfetadas, long id) {
        if (linhasAfetadas == 0) {
            throw new RecursoNaoEncontradoException("Erro: Produto não encontrado (id " + id + ").");
        }
    }


    private String categoriaParaBanco(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return categoria == Categoria.GRAOS ? "grãos" : categoria.name().toLowerCase(Locale.ROOT);
    }


    private Categoria categoriaDoBanco(String valor) {
        if (valor == null) {
            return null;
        }
        String semAcento = Normalizer.normalize(valor, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return Categoria.valueOf(semAcento.toUpperCase(Locale.ROOT));
    }

    private Produto converter(ResultSet rs) throws SQLException {
        Produto produto = new Produto(
                rs.getString("nome"),
                rs.getString("descricao"),
                rs.getInt("estoque"),
                rs.getDouble("preco"),
                categoriaDoBanco(rs.getString("categoria")),
                rs.getBytes("imagem"));
        produto.setId(rs.getLong("id"));
        return produto;
    }

    //Busca todos os produtos que contêm um dos IDs da lista
    public List<Produto> listarPorIds(List<Long> ids){
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM produto WHERE id in( " + placeholders + ")";

        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i=0; i<ids.size(); i++) {
                ps.setLong(i+1, ids.get(i));
            }
            try (ResultSet rs = ps.executeQuery()){
                if (!rs.next()) {
                    throw new RecursoNaoEncontradoException("Não foi possível encontrar produtos com os IDs enviados.");
                }
                while (rs.next()) {
                    produtos.add(converter(rs));
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return produtos;
    }
}
