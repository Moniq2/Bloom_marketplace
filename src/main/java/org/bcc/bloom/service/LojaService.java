package org.bcc.bloom.service;

import org.bcc.bloom.exception.CampoObrigatorioException;
import org.bcc.bloom.exception.VendedorNaoCadastradoException;
import org.bcc.bloom.exception.VendedorNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.dao.LojaDAO;
import java.util.List;

public class LojaService {

    private final LojaDAO lojaDAO = new LojaDAO();

    public Loja cadastrarLoja(Vendedor vendedor, String nome, String descricao, byte[] capa) {
        if (vendedor == null) {
            throw new VendedorNaoEncontradoException("Não há vendedor informado para cadastrar a loja.");
        }
        if (vendedor.getId() == null) {
            throw new VendedorNaoCadastradoException("O vendedor precisa estar cadastrado antes de criar uma loja.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new CampoObrigatorioException("O nome da loja é obrigatório.");
        }

        Loja novaLoja = new Loja(nome.trim(), descricao, capa);
        lojaDAO.salvar(novaLoja, vendedor.getId());
        vendedor.setLoja(novaLoja);
        return novaLoja;
    }

    public List<Loja> listarLojas(int pagina, int tamanho) {
        return lojaDAO.listarPaginado(pagina, tamanho);
    }

    public Loja buscarPorId(long id) { return null; }

    public void atualizar(Loja loja) {}

    public void remover(long id) {}

    public int contarLojas() {
        return lojaDAO.contarTotalLojas();
    }

    public int totalDePaginas(int tamanho) {
        if (tamanho <= 0) {
            throw new ValorInvalidoException("Erro: O tamanho da página deve ser maior que zero.");
        }
        return (contarLojas() + tamanho - 1) / tamanho;
    }
}
