
package org.bcc.bloom.dao;

import org.bcc.bloom.model.Loja;
import java.util.List;

public interface ILojaDAO {

    void salvar(Loja loja, long vendedorId);
    Loja buscarPorId(long id);
    Loja buscarPorVendedor(long vendedorId);
    List<Loja> listarPaginado(int pagina, int tamanho);
    int contarTotalLojas();
    void atualizar(Loja loja);
    void remover(long id);
}
