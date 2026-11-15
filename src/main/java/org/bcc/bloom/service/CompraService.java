
package org.bcc.bloom.service;

import org.bcc.bloom.dao.DAOFactory;
import org.bcc.bloom.dao.ICompraDAO;
import org.bcc.bloom.exception.ErroBancoDadosException;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.model.Vendedor;

import java.sql.SQLException;

public class CompraService {

    private final ICompraDAO compraDAO;

    public CompraService() {
        this(DAOFactory.getCompraDAO());
    }

    public CompraService(ICompraDAO compraDAO) {
        if (compraDAO == null) {
            throw new IllegalArgumentException(
                    "O DAO da compra nÃ£o pode ser nulo."
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
                    "Comprador, produto ou vendedor nÃ£o encontrado."
            );
        }

        if (comprador.getId() == null || comprador.getId() <= 0
                || produto.getId() == null || produto.getId() <= 0
                || vendedorDono.getId() == null || vendedorDono.getId() <= 0) {
            throw new ValorInvalidoException(
                    "Os IDs do comprador, produto e vendedor sÃ£o obrigatÃ³rios."
            );
        }

        if (quantidade <= 0) {
            throw new ValorInvalidoException(
                    "A quantidade deve ser maior que zero."
            );
        }

        try {

            compraDAO.realizarCompra(
                    comprador.getId(),
                    produto.getId(),
                    vendedorDono.getId(),
                    quantidade
            );

        } catch (SQLException e) {
            throw new ErroBancoDadosException(
                    "NÃ£o foi possÃ­vel realizar a compra: " + e.getMessage()
            );
        }
    }
}
