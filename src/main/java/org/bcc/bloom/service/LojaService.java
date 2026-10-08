package org.bcc.bloom.service;

import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Vendedor;

public class LojaService {

    public Loja cadastrarLoja(Vendedor vendedor, String nome, String descricao, byte[] capa) {
        if (vendedor == null) {
            throw new IllegalArgumentException("Erro: Vendedor não pode ser nulo.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome da loja é obrigatório.");
        }

        Loja novaLoja = new Loja(nome, descricao, capa);
        vendedor.getLojas().add(novaLoja);
        return novaLoja;
    }
}