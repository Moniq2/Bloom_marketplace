
package org.bcc.bloom.service;

import org.bcc.bloom.dao.DAOFactory;
import org.bcc.bloom.dao.ICompraDAO;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.model.Vendedor;

import java.math.BigDecimal;
import java.sql.SQLException;

public class CompraService {

    private final ICompraDAO compraDAO;

    public CompraService() {
        this(DAOFactory.getCompraDAO());
    }

    public CompraService(ICompraDAO compraDAO) {
        if (compraDAO == null) {
            throw new IllegalArgumentException(
                    "O DAO da compra não pode ser nulo."
            );
        }

        this.compraDAO = compraDAO;
    }

    public void realizarCompra(
            Comprador comprador,
            Produto produto,
            Vendedor vendedorDono,
            int quantidade
    ) {

        if (comprador == null || produto == null || vendedorDono == null) {
            throw new RecursoNaoEncontradoException(
                    "Comprador, produto ou vendedor inválido."
            );
        }

        if (comprador.getId() == null
                || produto.getId() == null
                || vendedorDono.getId() == null) {
            throw new ValorInvalidoException(
                    "Os IDs do comprador, produto e vendedor são obrigatórios."
            );
        }

        if (quantidade <= 0) {
            throw new ValorInvalidoException(
                    "A quantidade deve ser maior que zero."
            );
        }

        if (!Double.isFinite(produto.getPreco())
                || produto.getPreco() < 0) {
            throw new ValorInvalidoException(
                    "O preço do produto é inválido."
            );
        }

        BigDecimal valorTotal = BigDecimal.valueOf(produto.getPreco())
                .multiply(BigDecimal.valueOf(quantidade))
                .setScale(2, java.math.RoundingMode.HALF_UP);

        long custoTotal;

        try {
            custoTotal = valorTotal.movePointRight(2).longValueExact();
        } catch (ArithmeticException e) {
            throw new ValorInvalidoException(
                    "O valor total da compra é inválido."
            );
        }

        try {
            // A compra é validada e persistida em uma única transação.
            compraDAO.realizarCompra(
                    comprador.getId(),
                    produto.getId(),
                    vendedorDono.getId(),
                    quantidade
            );

            // Atualiza os objetos em memória após a confirmação do banco.
            comprador.setSaldo(comprador.getSaldo() - custoTotal);
            vendedorDono.setSaldo(vendedorDono.getSaldo() + custoTotal);
            produto.setEstoque(produto.getEstoque() - quantidade);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Não foi possível realizar a compra: " + e.getMessage(),
                    e
            );
        }
    }
}
