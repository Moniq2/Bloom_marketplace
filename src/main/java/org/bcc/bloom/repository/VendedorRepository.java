package org.bcc.bloom.repository;

import org.bcc.bloom.exception.ErroBancoDadosException;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Usuario;
import org.bcc.bloom.model.Vendedor;

import java.sql.*;

public class VendedorRepository {

    public Long salvar(Vendedor vendedor) {
        String sql = "INSERT INTO vendedor (nome, email, senha, cnpj) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, vendedor.getNome());
            pstmt.setString(2, vendedor.getEmail());
            pstmt.setString(3, vendedor.getSenha());
            pstmt.setString(4, vendedor.getCnpj());
            
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
               return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao salvar vendedor na base de dados: " + e.getMessage());
        }
        return null;
    }

    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM vendedor WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    String senha = rs.getString("senha");
                    long id = rs.getLong("id");
                    String cnpj = rs.getString("cnpj");

                    Vendedor vendedor = new Vendedor(nome, email, senha, cnpj);
                    vendedor.setId(id);
                    return vendedor;
                }
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao buscar utilizador por e-mail: " + e.getMessage());
        }
        return null;
    }

    public Usuario buscarUsuario(String email, String senha) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            return usuario;
        }
        return null;
    }
}