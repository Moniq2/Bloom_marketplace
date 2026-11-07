package org.bcc.bloom.dao;

import org.bcc.bloom.dto.CarrinhoDTO;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.model.Item;
import org.bcc.bloom.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CarrinhoDAO {
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    public long criarCarrinho(long compradorId) throws SQLException {
        String sql = "INSERT INTO carrinho (comprador_id) VALUES (?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, compradorId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        throw new SQLException("Não foi possível criar o carrinho.");
    }


    public Long buscarPorId(long id) throws SQLException {
        String sql = "SELECT id FROM carrinho WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }

        return null;
    }


    public Long buscarPorComprador(long compradorId) throws SQLException {
        String sql = "SELECT id FROM carrinho WHERE comprador_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, compradorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }

        return null;
    }

    //Busca itens do carrinho e seus respectivos produtos
    public CarrinhoDTO buscarItens(Long carrinhoId) {

        String sql = "SELECT * FROM carrinho WHERE id = ?";
        List<Item> itens = new ArrayList<>();

        //Busca de itens
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1, carrinhoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new RecursoNaoEncontradoException("Não existe carrinho de id " + carrinhoId);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        sql = "SELECT * FROM item WHERE carrinho_id = ?";
        //Lista para armazenar os IDs dos itens
        List<Long> itensIds = new ArrayList<>();

        try (
            PreparedStatement ps = conn.prepareStatement(sql);
        )
        {
            ps.setLong(1, carrinhoId);
            try (ResultSet rs = ps.executeQuery(sql)) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getLong("id"));
                    item.setValorUnitario(rs.getDouble("valor_unitario"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    itens.add(item);

                    itensIds.add(item.getId());
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        //Buscar os produtos de cada item
        List<Produto> produtos = produtoDAO.listarPorIds(itensIds);

        return new CarrinhoDTO(produtos, itens);
    }


    public void excluirCarrinho(long carrinhoId) throws SQLException {
        String sqlItens = "DELETE FROM item WHERE container_id = ? AND tipo_container = 'CARRINHO'";
        String sqlCarrinho = "DELETE FROM carrinho WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(sqlItens)) {
                    ps.setLong(1, carrinhoId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(sqlCarrinho)) {
                    ps.setLong(1, carrinhoId);

                    int linhas = ps.executeUpdate();

                    if (linhas == 0) {
                        throw new SQLException("Carrinho não encontrado.");
                    }
                }

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
