package org.bcc.bloom.dao;

import org.bcc.bloom.model.Pedido;
import java.sql.SQLException;
import java.util.List;

public interface IPedidoDAO {

    long criar(long compradorId, double valorTotal) throws SQLException;

    Pedido buscarPorId(long pedidoId) throws SQLException;

    List<Pedido> listarPorComprador(long compradorId) throws SQLException;

    void remover(long pedidoId) throws SQLException;
}