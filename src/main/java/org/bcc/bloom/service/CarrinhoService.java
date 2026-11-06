package org.bcc.bloom.service;

import org.bcc.bloom.model.Item;
import org.bcc.bloom.dao.CarrinhoDAO;
import java.util.List;

public class CarrinhoService {
    CarrinhoDAO carrinhoDAO = new CarrinhoDAO();

    public List<Item> buscarItens(Long carrinhoId) {
        return carrinhoDAO.buscarItens(carrinhoId);
    }
}
