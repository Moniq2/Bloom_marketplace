
package org.bcc.bloom.dao;

import org.bcc.bloom.model.Vendedor;
import java.util.List;

public interface IVendedorDAO {

    void salvarVendedor(Vendedor vendedor);
    Vendedor buscarPorId(long id);
    Vendedor buscarPorEmail(String email);
    List<Vendedor> listarPaginado(int pagina, int tamanho);
    int contarTotal();
    void atualizar(Vendedor vendedor);
    void atualizarSenha(long id, String senhaCriptografada);
    void atualizarSaldo(long id, long novoSaldo);
    void vincularLoja(long vendedorId, long lojaId);
    void remover(long id);
}
