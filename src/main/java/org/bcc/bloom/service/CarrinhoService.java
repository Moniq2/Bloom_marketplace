
package org.bcc.bloom.service;

import org.bcc.bloom.dao.DAOFactory;
import org.bcc.bloom.dao.ICarrinhoDAO;
import org.bcc.bloom.dto.CarrinhoDTO;

import java.sql.SQLException;

public class CarrinhoService {

    private final ICarrinhoDAO carrinhoDAO;

    public CarrinhoService() {
        this(DAOFactory.getCarrinhoDAO());
    }

    public CarrinhoService(ICarrinhoDAO carrinhoDAO) {
        if (carrinhoDAO == null) {
            throw new IllegalArgumentException(
                    "O DAO do carrinho não pode ser nulo."
            );
        }

        this.carrinhoDAO = carrinhoDAO;
    }

    public Long criar(long compradorId) {
        try {
            return carrinhoDAO.criarCarrinho(compradorId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar carrinho.", e);
        }
    }

    public Long buscarPorId(long id) {
        try {
            return carrinhoDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar carrinho.", e);
        }
    }

    public Long buscarPorComprador(long compradorId) {
        try {
            return carrinhoDAO.buscarPorComprador(compradorId);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao buscar carrinho do comprador.", e
            );
        }
    }

    public void remover(long carrinhoId) {
        try {
            carrinhoDAO.excluirCarrinho(carrinhoId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover carrinho.", e);
        }
    }

    public CarrinhoDTO buscarItens(Long carrinhoId) {
        if (carrinhoId == null) {
            throw new IllegalArgumentException(
                    "O ID do carrinho não pode ser nulo."
            );
        }

        try {
            return carrinhoDAO.buscarItens(carrinhoId);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao buscar itens do carrinho.", e
            );
        }
    }
}
