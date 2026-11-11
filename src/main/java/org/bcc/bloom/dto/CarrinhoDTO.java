
package org.bcc.bloom.dto;

import org.bcc.bloom.model.Item;
import org.bcc.bloom.model.Produto;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CarrinhoDTO implements Iterable<Item> {

    private final List<Produto> produtos;
    private final List<Item> itens;

    public CarrinhoDTO(List<Produto> produtos, List<Item> itens) {
        this.produtos = produtos != null
                ? produtos
                : Collections.emptyList();

        this.itens = itens != null
                ? itens
                : Collections.emptyList();
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public List<Item> getItens() {
        return itens;
    }

    public boolean isEmpty() {
        return itens.isEmpty();
    }

    @Override
    public Iterator<Item> iterator() {
        return itens.iterator();
    }
}
