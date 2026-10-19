package org.bcc.bloom.service;

import org.bcc.bloom.exception.RegraNegocioException;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.repository.LojaRepository;

import java.util.List;

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

    public List<Loja> listarLojas(int pagina, int tamanho) {
        return lojaRepository.listarPaginado(pagina, tamanho);
    }

    public int contarLojas() {
        return lojaRepository.contarTotalLojas();
    }

    public int totalDePaginas(int tamanho) {
        if (tamanho <= 0) {
            throw new RegraNegocioException("Erro: O tamanho da página deve ser maior que zero.");
        }
        return (contarLojas() + tamanho - 1) / tamanho;
    }
}