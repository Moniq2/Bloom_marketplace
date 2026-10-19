package org.bcc.bloom.service;

import org.bcc.bloom.exception.RegraNegocioException;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.repository.LojaRepository;

import java.util.List;

public class LojaService {

    private final LojaRepository lojaRepository = new LojaRepository();

    //COLOCAR LIMITE DE CARACTERES NO NOME DA LOJA E DESCRIÇÃO, NAO ESQUEÇA DE CRIAR AS EXCECOES PERSONALIZADAS PARA CADA ERRO
    public Loja cadastrarLoja(Vendedor vendedor, String nome, String descricao, byte[] capa) {
        if (vendedor == null) {
            throw new RegraNegocioException("Erro: Vendedor não pode ser nulo.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("Erro: O nome da loja é obrigatório.");
        }

        Loja novaLoja = new Loja(nome, descricao, capa);
        vendedor.getLojas().add(novaLoja);
        lojaRepository.salvar(novaLoja, vendedor.getId());
        return novaLoja;
    }
}