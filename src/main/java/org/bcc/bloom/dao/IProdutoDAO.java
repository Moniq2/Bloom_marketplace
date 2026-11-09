package org.bcc.bloom.dao;

import org.bcc.bloom.model.Categoria;
import org.bcc.bloom.model.Produto;

import java.util.List;

public interface IProdutoDAO {

    void salvar(Produto produto, long lojaId);

    Produto buscarPorId(long id);

    List<Produto> listarPaginado(int pagina, int tamanho);

    List<Produto> listarPorLoja(Long lojaId, int pagina, int tamanho);

    List<Produto> listarPorCategoria(Categoria categoria, int pagina, int tamanho);

    int contarTotal();

    int contarPorLoja(Long lojaId);

    void atualizar(Produto produto);

    void atualizarEstoque(long id, int novoEstoque);

    void remover(long id);

    List<Produto> listarPorIds(List<Long> ids);
}