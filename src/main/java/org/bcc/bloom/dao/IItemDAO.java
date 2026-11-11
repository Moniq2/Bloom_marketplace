
package org.bcc.bloom.dao;

import org.bcc.bloom.model.ItemRegistro;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface IItemDAO {

    long adicionarItem(
            long carrinhoId,
            long produtoId,
            int quantidade,
            BigDecimal valorUnitario
    ) throws SQLException;

    List<ItemRegistro> listarPorCarrinho(
            long carrinhoId
    ) throws SQLException;

    ItemRegistro buscarPorId(long itemId) throws SQLException;

    void atualizarQuantidade(
            long itemId,
            int novaQuantidade
    ) throws SQLException;

    void removerItem(long itemId) throws SQLException;

    void esvaziarCarrinho(long carrinhoId) throws SQLException;
}
