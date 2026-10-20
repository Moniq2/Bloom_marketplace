package org.bcc.bloom.service;

import org.bcc.bloom.exception.CampoObrigatorioException;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.repository.LojaRepository;

import java.util.List;

public class LojaService {

    private final LojaRepository lojaRepository = new LojaRepository();

    public Loja cadastrarLoja(Vendedor vendedor, String nome, String descricao, byte[] capa) {
        if (vendedor == null) {
            throw new RecursoNaoEncontradoException("Erro: Vendedor não encontrado.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new CampoObrigatorioException("Erro: O nome da loja é obrigatório.");
        }

        Loja novaLoja = new Loja(nome, descricao, capa);
        lojaRepository.salvar(novaLoja, vendedor.getEmail());
        vendedor.getLojas().add(novaLoja);
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
            throw new ValorInvalidoException("Erro: O tamanho da página deve ser maior que zero.");
        }
        return (contarLojas() + tamanho - 1) / tamanho;
    }
}