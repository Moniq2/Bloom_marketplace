
package org.bcc.bloom.dao;

import org.bcc.bloom.dto.CarrinhoDTO;

import java.sql.SQLException;

public interface ICarrinhoDAO {

    long criarCarrinho(long compradorId) throws SQLException;

    Long buscarPorId(long id) throws SQLException;

    Long buscarPorComprador(long compradorId) throws SQLException;

    CarrinhoDTO buscarItens(Long carrinhoId) throws SQLException;

    void excluirCarrinho(long carrinhoId) throws SQLException;
}
