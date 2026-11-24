
package org.bcc.bloom.service;

import org.bcc.bloom.dao.CarrinhoDAO;
import org.bcc.bloom.dao.DAOFactory;
import org.bcc.bloom.dto.CarrinhoDTO;
import org.bcc.bloom.model.Comprador;

import java.sql.SQLException;
import java.util.Optional;

public class CarrinhoService {

    private final CarrinhoDAO carrinhoDAO;

    public CarrinhoService() {
        this(DAOFactory.getCarrinhoDAO());
    }

    public CarrinhoService(CarrinhoDAO carrinhoDAO) {
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

    public Long buscarOuCriarParaComprador(Comprador comprador) {
        if (comprador == null || comprador.getId() == null || comprador.getId() <= 0) {
            throw new IllegalArgumentException("O comprador precisa estar conectado para usar o carrinho.");
        }

        Long carrinhoId = comprador.getCarrinho_id();
        if (carrinhoId != null && buscarPorId(carrinhoId) != null) {
            return carrinhoId;
        }

        carrinhoId = buscarPorComprador(comprador.getId());
        if (carrinhoId == null) {
            carrinhoId = criar(comprador.getId());
        }
        comprador.SetCarrinho_id(carrinhoId);
        return carrinhoId;
    }

    public void remover(long carrinhoId) {
        try {
            carrinhoDAO.excluirCarrinho(carrinhoId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover carrinho.", e);
        }
    }

    public Optional<CarrinhoDTO> buscarItens(Long carrinhoId) {
        if (carrinhoId == null) {
            throw new IllegalArgumentException(
                    "O ID do carrinho não pode ser nulo."
            );
        }
        CarrinhoDTO carrinhoInfo;
        try {
            carrinhoInfo = carrinhoDAO.buscarItens(carrinhoId);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao buscar itens do carrinho: " + e.getMessage(), e
            );
        }
        return Optional.ofNullable(carrinhoInfo);
    }
}
