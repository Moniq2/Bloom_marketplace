package org.bcc.bloom.repository;

import org.bcc.bloom.exception.ErroBancoDadosException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Loja;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LojaRepository {

    public LojaRepository() {}

    // MELHORE ESSE MÉTODO. ADICIONAR ID DA LOJA CRIADA PELO BD NA COLUNA loja_id DO VENDEDOR, USAR TRANSAÇÃO!!
    public void salvar(Loja loja, long vendedor_id) {
        String sql = "INSERT INTO loja (nome, descricao) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, loja.getNome());
            pstmt.setString(2, loja.getDescricao());

            pstmt.executeUpdate();

            try (ResultSet chaves = pstmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    loja.setId(chaves.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao salvar loja na base de dados: " + e.getMessage());
        }
    }

    public List<Loja> listarTodas() {
        String sql = "SELECT * FROM loja";
        List<Loja> lojas = new ArrayList<>(); //FAZER USANDO PAGINAÇÃO!!!

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lojas.add(converter(rs));
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                byte[] capa = rs.getBytes("capa");
                lojas.add(new Loja(nome, descricao, capa));
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao buscar lojas: " + e.getMessage());
        }
        return lojas;
    }

    public List<Loja> listarPaginado(int pagina, int tamanho) {
        if (pagina < 0 || tamanho <= 0) {
            throw new ValorInvalidoException("Erro: A página deve ser 0 ou maior e o tamanho deve ser maior que zero.");
        }

        String sql = "SELECT * FROM loja ORDER BY id LIMIT ? OFFSET ?";
        List<Loja> lojas = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tamanho);
            pstmt.setInt(2, pagina * tamanho);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lojas.add(converter(rs));
                }
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao buscar lojas paginadas: " + e.getMessage());
        }
        return lojas;
    }

    public int contarTotalLojas() {
        String sql = "SELECT COUNT(*) AS total FROM loja";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao contar lojas: " + e.getMessage());
        }
    }

    private Loja converter(ResultSet rs) throws SQLException {
        Loja loja = new Loja(rs.getString("nome"), rs.getString("descricao"), null);
        loja.setId(rs.getLong("id"));
        return loja;
    }
}