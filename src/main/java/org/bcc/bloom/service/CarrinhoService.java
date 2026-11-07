package org.bcc.bloom.service;

import org.bcc.bloom.dto.CarrinhoDTO;
import org.bcc.bloom.model.Item;
import org.bcc.bloom.dao.CarrinhoDAO;
import java.util.List;

public class CarrinhoService {
    CarrinhoDAO carrinhoDAO = new CarrinhoDAO();

    public Long criar(long compradorId) { return null; }

    public Long buscarPorId(long id) { return null; }

    public Long buscarPorComprador(long compradorId) { return null; }

    public void atualizar(long carrinhoId) {}

    public void remover(long carrinhoId) {}

    public CarrinhoDTO buscarItens(Long carrinhoId) {
        return carrinhoDAO.buscarItens(carrinhoId);
    }
}
