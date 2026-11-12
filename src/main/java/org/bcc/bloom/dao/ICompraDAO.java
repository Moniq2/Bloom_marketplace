package org.bcc.bloom.dao;

import java.sql.SQLException;

public interface ICompraDAO {

    long realizarCompra(
            long compradorId,
            long produtoId,
            long vendedorId,
            int quantidade
    ) throws SQLException;
}