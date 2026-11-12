
package org.bcc.bloom.service;

import org.bcc.bloom.dao.DAOFactory;
import org.bcc.bloom.dao.IPedidoDAO;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Pedido;

import java.sql.SQLException;
import java.util.List;

public class PedidoService {

    private final IPedidoDAO pedidoDAO;

    public PedidoService() {
        this(DAOFactory.getPedidoDAO());
    }

    public PedidoService(IPedidoDAO pedidoDAO) {
        if (pedidoDAO == null) {
            throw new IllegalArgumentException(
                    "O DAO do pedido não pode ser nulo."
            );
        }

        this.pedidoDAO = pedidoDAO;
    }

    public Pedido criar(Pedido pedido) {

        if (pedido == null) {
            throw new RecursoNaoEncontradoException(
                    "O pedido informado é inválido."
            );
        }

        if (!(pedido.getUsuario() instanceof Comprador comprador)) {
            throw new ValorInvalidoException(
                    "O pedido precisa estar associado a um comprador."
            );
        }

        if (comprador.getId() == null || comprador.getId() <= 0) {
            throw new ValorInvalidoException(
                    "O ID do comprador é obrigatório."
            );
        }

        if (!Double.isFinite(pedido.getValorTotal())
                || pedido.getValorTotal() < 0) {
            throw new ValorInvalidoException(
                    "O valor total do pedido é inválido."
            );
        }

        try {
            long id = pedidoDAO.criar(
                    comprador.getId(),
                    pedido.getValorTotal()
            );

            pedido.setId(id);
            return pedido;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Não foi possível criar o pedido.",
                    e
            );
        }
    }

    public Pedido buscarPorId(long id) {

        if (id <= 0) {
            throw new ValorInvalidoException(
                    "O ID do pedido deve ser maior que zero."
            );
        }

        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);

            if (pedido == null) {
                throw new RecursoNaoEncontradoException(
                        "Pedido não encontrado."
                );
            }

            return pedido;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Não foi possível buscar o pedido.",
                    e
            );
        }
    }

    public List<Pedido> listarPorComprador(long compradorId) {

        if (compradorId <= 0) {
            throw new ValorInvalidoException(
                    "O ID do comprador é inválido."
            );
        }

        try {
            return pedidoDAO.listarPorComprador(compradorId);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Não foi possível listar os pedidos.",
                    e
            );
        }
    }

    public void remover(long id) {

        if (id <= 0) {
            throw new ValorInvalidoException(
                    "O ID do pedido deve ser maior que zero."
            );
        }

        try {
            pedidoDAO.remover(id);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Não foi possível remover o pedido.",
                    e
            );
        }
    }
}
