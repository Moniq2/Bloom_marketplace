package org.bcc.bloom.dao;

import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Usuario;

import java.util.List;

public interface ICompradorDAO {

    void salvar(Comprador comprador);

    Comprador buscarPorId(long id);

    Comprador buscarPorEmail(String email);

    List<Comprador> listarPaginado(int pagina, int tamanho);

    int contarTotal();

    void atualizar(Comprador comprador);

    void atualizarSenha(long id, String senhaCriptografada);

    void atualizarSaldo(long id, long novoSaldo);

    void remover(long id);

    Usuario buscar(String email, String senha);
}