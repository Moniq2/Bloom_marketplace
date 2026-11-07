package org.bcc.bloom.dto;

import org.bcc.bloom.model.Item;
import org.bcc.bloom.model.Produto;

import java.util.List;

public class CarrinhoDTO {
    public CarrinhoDTO(List<Produto> produtos, List<Item> itens){
        this.produtos = produtos;
        this.itens = itens;
    }

    List<Produto> produtos;
    List<Item> itens;
    public List<Produto> getProdutos() {
        return produtos;
    }
    public List<Item> getItens() {
        return itens;
    }
}
