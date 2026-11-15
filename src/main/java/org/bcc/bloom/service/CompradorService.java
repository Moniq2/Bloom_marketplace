
package org.bcc.bloom.service;

import org.bcc.bloom.dao.DAOFactory;
import org.bcc.bloom.dao.ICompradorDAO;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Comprador;

import java.util.ArrayList;
import java.util.List;

public class CompradorService {

    private final ICompradorDAO compradorDAO;

    public CompradorService() {
        this(DAOFactory.getCompradorDAO());
    }

    public CompradorService(ICompradorDAO compradorDAO) {
        if (compradorDAO == null) {
            throw new IllegalArgumentException("DAO não pode ser nulo.");
        }
        this.compradorDAO = compradorDAO;
    }

    public void criar(Comprador comprador) {
        if (comprador == null) {
            throw new RecursoNaoEncontradoException("Comprador não informado.");
        }
        if (comprador.getNome() == null || comprador.getNome().isBlank()
                || comprador.getEmail() == null || comprador.getEmail().isBlank()
                || comprador.getCpf() == null || comprador.getCpf().isBlank()
                || comprador.getSenha() == null || comprador.getSenha().isBlank()) {
            throw new ValorInvalidoException("Preencha todos os dados do comprador.");
        }
        compradorDAO.salvar(comprador);
    }

    public Comprador buscarPorId(long id) {
        if (id <= 0) {
            throw new ValorInvalidoException("ID inválido.");
        }
        Comprador comprador = compradorDAO.buscarPorId(id);
        if (comprador == null) {
            throw new RecursoNaoEncontradoException("Comprador não encontrado.");
        }
        return comprador;
    }

    public List<Comprador> listar() {
        int total = compradorDAO.contarTotal();
        if (total == 0) {
            return new ArrayList<>();
        }
        return compradorDAO.listarPaginado(0, total);
    }

    public void atualizar(Comprador comprador) {
        if (comprador == null || comprador.getId() == null
                || comprador.getId() <= 0) {
            throw new ValorInvalidoException("Comprador ou ID inválido.");
        }
        compradorDAO.atualizar(comprador);
    }

    public void remover(long id) {
        if (id <= 0) {
            throw new ValorInvalidoException("ID inválido.");
        }
        compradorDAO.remover(id);
    }
}
