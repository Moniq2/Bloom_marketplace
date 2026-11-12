
package org.bcc.bloom.dao;

import org.bcc.bloom.model.Categoria;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Pedido;
import org.bcc.bloom.model.Produto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PedidoDAO implements IPedidoDAO {

    @Override
    public long criar(long compradorId, double valorTotal)
            throws SQLException {

        if (compradorId <= 0 || !Double.isFinite(valorTotal)
                || valorTotal < 0) {
            throw new IllegalArgumentException("Dados do pedido inválidos.");
        }

        String sql = "INSERT INTO pedido (comprador_id, valor_total) VALUES (?, ?)";

        try (Connection conexao = DatabaseConnection.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, compradorId);
            stmt.setBigDecimal(2, BigDecimal.valueOf(valorTotal));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

            throw new SQLException("Não foi possível obter o ID do pedido.");
        }
    }

    @Override
    public Pedido buscarPorId(long pedidoId) throws SQLException {

        String sql = "SELECT id, comprador_id, valor_total FROM pedido WHERE id = ?";

        try (Connection conexao = DatabaseConnection.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, pedidoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                long id = rs.getLong("id");
                long compradorId = rs.getLong("comprador_id");
                double valorTotal = rs.getBigDecimal("valor_total").doubleValue();

                Comprador comprador = new Comprador();
                comprador.setId(compradorId);

                ArrayList<Produto> produtos = carregarProdutos(conexao, id);

                return new Pedido(id, comprador, valorTotal, produtos);
            }
        }
    }

    @Override
    public List<Pedido> listarPorComprador(long compradorId)
            throws SQLException {

        if (compradorId <= 0) {
            throw new IllegalArgumentException("ID do comprador inválido.");
        }

        List<Long> ids = new ArrayList<>();

        String sql = "SELECT id FROM pedido WHERE comprador_id = ? ORDER BY id DESC";

        try (Connection conexao = DatabaseConnection.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, compradorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getLong("id"));
                }
            }
        }

        List<Pedido> pedidos = new ArrayList<>();

        for (Long id : ids) {
            Pedido pedido = buscarPorId(id);

            if (pedido != null) {
                pedidos.add(pedido);
            }
        }

        return pedidos;
    }

    @Override
    public void remover(long pedidoId) throws SQLException {

        if (pedidoId <= 0) {
            throw new IllegalArgumentException("ID do pedido inválido.");
        }

        try (Connection conexao = DatabaseConnection.getConnection()) {

            conexao.setAutoCommit(false);

            try {
                String excluirItens =
                        "DELETE FROM item WHERE container_id = ? "
                        + "AND tipo_container = 'PEDIDO'";

                try (PreparedStatement stmt = conexao.prepareStatement(excluirItens)) {
                    stmt.setLong(1, pedidoId);
                    stmt.executeUpdate();
                }

                String excluirPedido = "DELETE FROM pedido WHERE id = ?";

                try (PreparedStatement stmt = conexao.prepareStatement(excluirPedido)) {
                    stmt.setLong(1, pedidoId);

                    if (stmt.executeUpdate() == 0) {
                        throw new SQLException("Pedido não encontrado.");
                    }
                }

                conexao.commit();

            } catch (SQLException | RuntimeException e) {
                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }

                throw e;
            }
        }
    }

    private ArrayList<Produto> carregarProdutos(
            Connection conexao,
            long pedidoId
    ) throws SQLException {

        ArrayList<Produto> produtos = new ArrayList<>();

        String sql = "SELECT p.id, p.nome, p.descricao, p.estoque, "
                + "p.preco, p.categoria, p.imagem "
                + "FROM item i "
                + "INNER JOIN produto p ON p.id = i.produto_id "
                + "WHERE i.container_id = ? "
                + "AND i.tipo_container = 'PEDIDO' "
                + "ORDER BY i.id";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, pedidoId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Produto produto = new Produto();

                    produto.setId(rs.getLong("id"));
                    produto.setNome(rs.getString("nome"));
                    produto.setDescricao(rs.getString("descricao"));
                    produto.setEstoque(rs.getInt("estoque"));
                    produto.setPreco(rs.getDouble("preco"));
                    produto.setImagem(rs.getBytes("imagem"));

                    String categoriaBanco = rs.getString("categoria");

                    if (categoriaBanco != null) {
                        if ("grãos".equalsIgnoreCase(categoriaBanco)) {
                            produto.setCategoria(Categoria.GRAOS);
                        } else {
                            produto.setCategoria(
                                    Categoria.valueOf(
                                            categoriaBanco.toUpperCase(Locale.ROOT)
                                    )
                            );
                        }
                    }

                    produtos.add(produto);
                }
            }
        }

        return produtos;
    }
}
