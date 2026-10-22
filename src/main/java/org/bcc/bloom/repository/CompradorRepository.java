package org.bcc.bloom.repository;

import org.bcc.bloom.exception.ErroBancoDadosException;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompradorRepository {

    public void salvar(Comprador comprador) {
        String sql = "INSERT INTO comprador (nome, email, senha, cpf, endereco) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, comprador.getNome());
            pstmt.setString(2, comprador.getEmail());
            pstmt.setString(3, comprador.getSenha());
            pstmt.setString(4, comprador.getCpf());
            pstmt.setString(5, comprador.getEndereco());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao salvar comprador na base de dados: " + e.getMessage());
        }
    }

    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM comprador WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    String senha = rs.getString("senha");
                    long id = rs.getLong("id");

                    String cpf = rs.getString("cpf");
                    Comprador comprador = new Comprador(nome ,email, senha, cpf);
                    comprador.setId(id);
                    return comprador;
                }
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao buscar utilizador por e-mail: " + e.getMessage());
        }
        return null;
    }

    public Usuario buscar(String email, String senha) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            return usuario;
        }
        return null;
    }
}
