package org.bcc.bloom.repository;

import org.bcc.bloom.exception.ErroBancoDadosException;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Usuario;
import org.bcc.bloom.model.Vendedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VendedorRepository {

    // ---------- CREATE ----------

    public void salvarVendedor(Vendedor vendedor) {
        String sql = "INSERT INTO vendedor (nome, email, senha, cnpj, perfil) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, vendedor.getNome());
            pstmt.setString(2, vendedor.getEmail());
            pstmt.setString(3, vendedor.getSenha());
            pstmt.setString(4, vendedor.getCnpj());
            pstmt.setBytes(5, vendedor.getPerfil());

            pstmt.executeUpdate();

            try (ResultSet chaves = pstmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    vendedor.setId(chaves.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao salvar vendedor na base de dados: " + e.getMessage());
        }
    }

    // ---------- READ ----------

    public Vendedor buscarPorId(long id) {
        return buscarUm("SELECT * FROM vendedor WHERE id = ?", id, "Erro ao buscar vendedor por id: ");
    }

    public Vendedor buscarPorEmail(String email) {
        return buscarUm("SELECT * FROM vendedor WHERE email = ?", email, "Erro ao buscar vendedor por e-mail: ");
    }

    public List<Vendedor> listarPaginado(int pagina, int tamanho) {
        if (pagina < 0 || tamanho <= 0) {
            throw new ValorInvalidoException("Erro: A página deve ser 0 ou maior e o tamanho deve ser maior que zero.");
        }

        String sql = "SELECT * FROM vendedor ORDER BY id LIMIT ? OFFSET ?";
        List<Vendedor> vendedores = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tamanho);
            pstmt.setInt(2, pagina * tamanho);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    vendedores.add(converter(rs));
                }
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao listar vendedores: " + e.getMessage());
        }
        return vendedores;
    }

    public int contarTotal() {
        String sql = "SELECT COUNT(*) AS total FROM vendedor";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao contar vendedores: " + e.getMessage());
        }
    }

    // ---------- UPDATE ----------

    public void atualizar(Vendedor vendedor) {
        String sql = "UPDATE vendedor SET nome = ?, email = ?, cnpj = ?, perfil = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vendedor.getNome());
            pstmt.setString(2, vendedor.getEmail());
            pstmt.setString(3, vendedor.getCnpj());
            pstmt.setBytes(4, vendedor.getPerfil());
            pstmt.setLong(5, vendedor.getId());

            exigirAlgumaLinha(pstmt.executeUpdate(), vendedor.getId());
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao atualizar vendedor: " + e.getMessage());
        }
    }

    public void atualizarSenha(long id, String senhaCriptografada) {
        String sql = "UPDATE vendedor SET senha = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, senhaCriptografada);
            pstmt.setLong(2, id);

            exigirAlgumaLinha(pstmt.executeUpdate(), id);
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao atualizar a senha do vendedor: " + e.getMessage());
        }
        return null;
    }

    public void vincularLoja(long vendedorId, long lojaId) {
        String sql = "UPDATE vendedor SET loja_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, lojaId);
            pstmt.setLong(2, vendedorId);

            exigirAlgumaLinha(pstmt.executeUpdate(), vendedorId);
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao vincular a loja ao vendedor: " + e.getMessage());
        }
    }

    // ---------- DELETE ----------

    public void remover(long id) {
        String sql = "DELETE FROM vendedor WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            exigirAlgumaLinha(pstmt.executeUpdate(), id);
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao remover vendedor: " + e.getMessage());
        }
    }

    // ---------- antigo (mantido para nao quebrar quem chama) ----------

    public Usuario buscarUsuario(String email, String senha) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            return usuario;
        }
        return null;
    }

    // ---------- apoio ----------

    private Vendedor buscarUm(String sql, Object parametro, String mensagemErro) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, parametro);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? converter(rs) : null;
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException(mensagemErro + e.getMessage());
        }
    }

    private void exigirAlgumaLinha(int linhasAfetadas, long id) {
        if (linhasAfetadas == 0) {
            throw new RecursoNaoEncontradoException("Erro: Vendedor não encontrado (id " + id + ").");
        }
    }

    private Vendedor converter(ResultSet rs) throws SQLException {
        Vendedor vendedor = new Vendedor(
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha"),
                rs.getString("cnpj"));
        vendedor.setId(rs.getLong("id"));
        vendedor.setPerfil(rs.getBytes("perfil"));
        return vendedor;
    }
}