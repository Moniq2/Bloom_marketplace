package org.bcc.bloom.service;

import java.util.List;
import org.bcc.bloom.dao.ItemDAO.ItemRegistro;

public class ItemService {
    public long criar(long carrinhoId, long produtoId, int quantidade) { return 0L; }

    public ItemRegistro buscarPorId(long itemId) { return null; }

    public List<ItemRegistro> listarPorCarrinho(long carrinhoId) { return null; }

    public void atualizar(long itemId, int quantidade) {}

    public void remover(long itemId) {}
}
