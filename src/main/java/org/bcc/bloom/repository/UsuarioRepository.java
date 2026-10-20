package org.bcc.bloom.repository;

import org.bcc.bloom.exception.ErroBancoDadosException;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Usuario;
import org.bcc.bloom.model.Vendedor;

import java.sql.*;

public class UsuarioRepository {

    public void salvarComprador(Comprador comprador) {
        String sql = "INSERT INTO usuarios (nome, email, senha, tipo, cpf, endereco) VALUES (?, ?, ?, 'COMPRADOR', ?, ?)";
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

    public void salvarVendedor(Vendedor vendedor) {
        String sql = "INSERT INTO usuarios (nome, email, senha, tipo, cpf) VALUES (?, ?, ?, 'VENDEDOR', ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, vendedor.getNome());
            pstmt.setString(2, vendedor.getEmail());
            pstmt.setString(3, vendedor.getSenha());
            pstmt.setString(4, vendedor.getCpf());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao salvar vendedor na base de dados: " + e.getMessage());
        }
    }

    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo");
                    String nome = rs.getString("nome");
                    String senha = rs.getString("senha");
                    long id = rs.getLong("id");

                    if ("COMPRADOR".equals(tipo)) {
                        String cpf = rs.getString("cpf");
                        String endereco = rs.getString("endereco");
                        return new Comprador(nome, email, senha, id, cpf, endereco);
                    } else {
                        String cnpj = rs.getString("cnpj");
                        return new Vendedor(nome, email, senha, id, cnpj);
                    }
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