package org.bcc.bloom.repository;

import org.bcc.bloom.exception.ErroBancoDadosException;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CompradorRepository {

  

    public void salvar(Comprador comprador) {
        String sql = "INSERT INTO comprador (nome, email, senha, saldo, cpf, endereco) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, comprador.getNome());
            pstmt.setString(2, comprador.getEmail());
            pstmt.setString(3, comprador.getSenha());
            pstmt.setLong(4, comprador.getSaldo());
            pstmt.setString(5, comprador.getCpf());
            pstmt.setString(6, comprador.getEndereco());

            pstmt.executeUpdate();

            try (ResultSet chaves = pstmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    comprador.setId(chaves.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao salvar comprador na base de dados: " + e.getMessage());
        }
    }

    // ---------- READ ----------

    public Comprador buscarPorId(long id) {
        return buscarUm("SELECT * FROM comprador WHERE id = ?", id, "Erro ao buscar comprador por id: ");
    }

    public Comprador buscarPorEmail(String email) {
        return buscarUm("SELECT * FROM comprador WHERE email = ?", email, "Erro ao buscar comprador por e-mail: ");
    }

    public List<Comprador> listarPaginado(int pagina, int tamanho) {
        if (pagina < 0 || tamanho <= 0) {
            throw new ValorInvalidoException("Erro: A página deve ser 0 ou maior e o tamanho deve ser maior que zero.");
        }

        String sql = "SELECT * FROM comprador ORDER BY id LIMIT ? OFFSET ?";
        List<Comprador> compradores = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tamanho);
            pstmt.setInt(2, pagina * tamanho);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    compradores.add(converter(rs));
                }
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao listar compradores: " + e.getMessage());
        }
        return compradores;
    }

    public int contarTotal() {
        String sql = "SELECT COUNT(*) AS total FROM comprador";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao contar compradores: " + e.getMessage());
        }
    }

    
    public void atualizar(Comprador comprador) {
        String sql = "UPDATE comprador SET nome = ?, email = ?, cpf = ?, endereco = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, comprador.getNome());
            pstmt.setString(2, comprador.getEmail());
            pstmt.setString(3, comprador.getCpf());
            pstmt.setString(4, comprador.getEndereco());
            pstmt.setLong(5, comprador.getId());

            exigirAlgumaLinha(pstmt.executeUpdate(), comprador.getId());
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao atualizar comprador: " + e.getMessage());
        }
    }

    public void atualizarSenha(long id, String senhaCriptografada) {
        String sql = "UPDATE comprador SET senha = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, senhaCriptografada);
            pstmt.setLong(2, id);

            exigirAlgumaLinha(pstmt.executeUpdate(), id);
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao atualizar a senha do comprador: " + e.getMessage());
        }
    }

    public void atualizarSaldo(long id, long novoSaldo) {
        String sql = "UPDATE comprador SET saldo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, novoSaldo);
            pstmt.setLong(2, id);

            exigirAlgumaLinha(pstmt.executeUpdate(), id);
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao atualizar o saldo do comprador: " + e.getMessage());
        }
    }



    public void remover(long id) {
        String sql = "DELETE FROM comprador WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            exigirAlgumaLinha(pstmt.executeUpdate(), id);
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao remover comprador: " + e.getMessage());
        }
    }

    public Usuario buscar(String email, String senha) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            return usuario;
        }
        return null;
    }

    // ---------- apoio ----------

    private Comprador buscarUm(String sql, Object parametro, String mensagemErro) {
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
            throw new RecursoNaoEncontradoException("Erro: Comprador não encontrado (id " + id + ").");
        }
    }

    private Comprador converter(ResultSet rs) throws SQLException {
        Comprador comprador = new Comprador(
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha"),
                rs.getString("cpf"));
        comprador.setId(rs.getLong("id"));
        comprador.setSaldo(rs.getLong("saldo"));
        comprador.setEndereco(rs.getString("endereco"));
        return comprador;
    }
}