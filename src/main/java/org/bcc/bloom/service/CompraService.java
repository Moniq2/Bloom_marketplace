package org.bcc.bloom.service;

import org.bcc.bloom.exception.EstoqueInsuficienteException;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.SaldoInsuficienteException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.model.Vendedor;

public class CompraService {
    public void realizarCompra(Comprador comprador, Produto produto, Vendedor vendedorDono, int quantidade) {
        if (comprador == null) {
            throw new RecursoNaoEncontradoException("Erro: Comprador inválido.");
        }
        if (produto == null) {
            throw new RecursoNaoEncontradoException("Erro: Produto inválido.");
        }
        if (vendedorDono == null) {
            throw new RecursoNaoEncontradoException("Erro: Vendedor inválido.");
        }
        if (quantidade <= 0) {
            throw new ValorInvalidoException("Erro: A quantidade deve ser maior que zero.");
        }

        if (produto.getEstoque() < quantidade) {
            throw new EstoqueInsuficienteException("Erro: Stock insuficiente. Disponível: " + produto.getEstoque());
        }

        long custoTotalEmCentavos = Math.round((produto.getPreco() * quantidade) * 100);

        if (comprador.getSaldo() < custoTotalEmCentavos) {
            throw new SaldoInsuficienteException("Erro: Saldo insuficiente. Saldo atual: R$" + (comprador.getSaldo() / 100.0) +
                " | Custo total: R$" + (custoTotalEmCentavos / 100.0));
        }

        comprador.setSaldo(comprador.getSaldo() - custoTotalEmCentavos);
        vendedorDono.setSaldo(vendedorDono.getSaldo() + custoTotalEmCentavos);
        produto.setEstoque(produto.getEstoque() - quantidade);

        System.out.println("Compra realizada com sucesso! " + quantidade + "x " + produto.getNome() + " comprado por R$" + (custoTotalEmCentavos / 100.0));
    }
}
