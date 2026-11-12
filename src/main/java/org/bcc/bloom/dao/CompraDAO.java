
package org.bcc.bloom.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CompraDAO implements ICompraDAO {

    @Override
    public long realizarCompra(
            long compradorId,
            long produtoId,
            long vendedorId,
            int quantidade
    ) throws SQLException {

        if (compradorId <= 0 || produtoId <= 0 || vendedorId <= 0) {
            throw new IllegalArgumentException("IDs inválidos.");
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        try (Connection conexao = DatabaseConnection.getConnection()) {

            conexao.setAutoCommit(false);

            try {
                // 1. Bloqueia e consulta o saldo do comprador.
                long saldoComprador;

                String sqlComprador =
                        "SELECT saldo FROM comprador WHERE id = ? FOR UPDATE";

                try (PreparedStatement stmt =
                             conexao.prepareStatement(sqlComprador)) {

                    stmt.setLong(1, compradorId);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Comprador não encontrado.");
                        }

                        saldoComprador = rs.getLong("saldo");
                    }
                }

                // 2. Bloqueia e consulta o saldo e a loja do vendedor.
                long saldoVendedor;
                long lojaVendedor;

                String sqlVendedor =
                        "SELECT saldo, loja_id FROM vendedor WHERE id = ? FOR UPDATE";

                try (PreparedStatement stmt =
                             conexao.prepareStatement(sqlVendedor)) {

                    stmt.setLong(1, vendedorId);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Vendedor não encontrado.");
                        }

                        saldoVendedor = rs.getLong("saldo");
                        lojaVendedor = rs.getLong("loja_id");
                    }
                }

                // 3. Bloqueia e consulta o produto.
                int estoque;
                BigDecimal preco;
                long lojaProduto;

                String sqlProduto =
                        "SELECT estoque, preco, loja_id FROM produto WHERE id = ? FOR UPDATE";

                try (PreparedStatement stmt =
                             conexao.prepareStatement(sqlProduto)) {

                    stmt.setLong(1, produtoId);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Produto não encontrado.");
                        }

                        estoque = rs.getInt("estoque");
                        preco = rs.getBigDecimal("preco");
                        lojaProduto = rs.getLong("loja_id");
                    }
                }

                // 4. Valida estoque e propriedade do produto.
                if (estoque < quantidade) {
                    throw new SQLException("Estoque insuficiente.");
                }

                if (lojaProduto != lojaVendedor) {
                    throw new SQLException(
                            "O produto não pertence à loja do vendedor informado."
                    );
                }

                if (preco == null || preco.signum() < 0) {
                    throw new SQLException("Preço do produto inválido.");
                }

                // 5. Calcula o valor da compra em reais e centavos.
                BigDecimal valorTotal = preco
                        .multiply(BigDecimal.valueOf(quantidade))
                        .setScale(2, RoundingMode.HALF_UP);

                long valorEmCentavos = valorTotal
                        .movePointRight(2)
                        .longValueExact();

                if (saldoComprador < valorEmCentavos) {
                    throw new SQLException("Saldo insuficiente.");
                }

                long novoSaldoVendedor = Math.addExact(
                        saldoVendedor,
                        valorEmCentavos
                );

                long novoSaldoComprador =
                        saldoComprador - valorEmCentavos;

                // 6. Atualiza o saldo do comprador.
                String atualizarComprador =
                        "UPDATE comprador SET saldo = ? WHERE id = ?";

                try (PreparedStatement stmt =
                             conexao.prepareStatement(atualizarComprador)) {

                    stmt.setLong(1, novoSaldoComprador);
                    stmt.setLong(2, compradorId);
                    stmt.executeUpdate();
                }

                // 7. Atualiza o saldo do vendedor.
                String atualizarVendedor =
                        "UPDATE vendedor SET saldo = ? WHERE id = ?";

                try (PreparedStatement stmt =
                             conexao.prepareStatement(atualizarVendedor)) {

                    stmt.setLong(1, novoSaldoVendedor);
                    stmt.setLong(2, vendedorId);
                    stmt.executeUpdate();
                }

                // 8. Atualiza o estoque do produto.
                String atualizarEstoque =
                        "UPDATE produto SET estoque = estoque - ? "
                        + "WHERE id = ? AND estoque >= ?";

                try (PreparedStatement stmt =
                             conexao.prepareStatement(atualizarEstoque)) {

                    stmt.setInt(1, quantidade);
                    stmt.setLong(2, produtoId);
                    stmt.setInt(3, quantidade);

                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException(
                                "Não foi possível atualizar o estoque."
                        );
                    }
                }

                // 9. Registra o pedido.
                long pedidoId;

                String inserirPedido =
                        "INSERT INTO pedido (comprador_id, valor_total) "
                        + "VALUES (?, ?)";

                try (PreparedStatement stmt = conexao.prepareStatement(
                        inserirPedido,
                        Statement.RETURN_GENERATED_KEYS
                )) {

                    stmt.setLong(1, compradorId);
                    stmt.setBigDecimal(2, valorTotal);
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Não foi possível obter o ID do pedido."
                            );
                        }

                        pedidoId = rs.getLong(1);
                    }
                }

                // 10. Registra o produto como item do pedido.
                String inserirItem =
                        "INSERT INTO item "
                        + "(produto_id, quantidade, container_id, "
                        + "tipo_container, valor_unitario) "
                        + "VALUES (?, ?, ?, 'PEDIDO', ?)";

                try (PreparedStatement stmt =
                             conexao.prepareStatement(inserirItem)) {

                    stmt.setLong(1, produtoId);
                    stmt.setInt(2, quantidade);
                    stmt.setLong(3, pedidoId);
                    stmt.setBigDecimal(4, preco);
                    stmt.executeUpdate();
                }

                // 11. Confirma todas as operações.
                conexao.commit();

                return pedidoId;

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
}
