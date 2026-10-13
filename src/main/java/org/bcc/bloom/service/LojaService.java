package org.bcc.bloom.service;

import org.bcc.bloom.exception.RegraNegocioException;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.repository.LojaRepository;

public class LojaService {

    private final LojaRepository lojaRepository = new LojaRepository();

    public Loja cadastrarLoja(Vendedor vendedor, String nome, String descricao, byte[] capa) {
        if (vendedor == null) {
            throw new RegraNegocioException("Erro: Vendedor não pode ser nulo.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("Erro: O nome da loja é obrigatório.");
        }

        Loja novaLoja = new Loja(nome, descricao, capa);
        vendedor.getLojas().add(novaLoja);
        lojaRepository.salvar(novaLoja, vendedor.getEmail());
        return novaLoja;
    }
}