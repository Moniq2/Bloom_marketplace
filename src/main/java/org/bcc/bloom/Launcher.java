package org.bcc.bloom;

import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.model.Categoria;
import org.bcc.bloom.service.UsuarioService;
import org.bcc.bloom.service.LojaService;
import org.bcc.bloom.service.ProdutoService;
import org.bcc.bloom.service.CompraService;

public class Launcher {
    public static void main(String[] args) {
        System.out.println("=== A INICIAR TESTES DO BLOOM MARKETPLACE (FLUXO DE COMPRA) ===");

        UsuarioService usuarioService = new UsuarioService();
        LojaService lojaService = new LojaService();
        ProdutoService produtoService = new ProdutoService();
        CompraService compraService = new CompraService();

        
        Comprador comprador = usuarioService.cadastrarComprador(
            "Momoniq", "muniq@email.com", "123456", 1L, "123.456.789-00", "Rua A, 123"
        );
        
        
        usuarioService.depositar(comprador, 5000L);
        System.out.println("Comprador: " + comprador.getNome() + " | Saldo após depósito: R$" + (comprador.getSaldo() / 100.0));

        
        Vendedor vendedor = usuarioService.cadastrarVendedor(
            "Zaio Hortifrúti", "sainho@email.com", "abcdef", 2L, "12.345.678/0001-99"
        );
        Loja loja = lojaService.cadastrarLoja(vendedor, "Hortifrúti do Carlos", "Frutas frescas", null);
        
        Produto produto = produtoService.cadastrarProduto(
            loja, "Maçã Gala", "Maçã fresca direto do produtor", 10, 6.50, Categoria.FRUTAS, null
        );
        System.out.println("Produto: " + produto.getNome() + " | Preço unitário: R$" + produto.getPreco() + " | Stock inicial: " + produto.getEstoque());

      
        System.out.println("\n--- A EXECUTAR COMPRA ---");
        compraService.realizarCompra(comprador, produto, vendedor, 4);

        System.out.println("\n--- ESTADO APÓS A COMPRA ---");
        System.out.println("Novo saldo do comprador: R$" + (comprador.getSaldo() / 100.0));
        System.out.println("Novo saldo do vendedor: R$" + (vendedor.getSaldo() / 100.0));
        System.out.println("Stock restante do produto: " + produto.getEstoque());

        System.out.println("\n=== TESTES CONCLUÍDOS COM SUCESSO! ===");
    }
}