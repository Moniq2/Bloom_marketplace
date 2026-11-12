package org.bcc.bloom.dao;

public class DAOFactory {

    private DAOFactory() {
    }

    public static ICompradorDAO getCompradorDAO() {
        return new CompradorDAO();
    }

    public static IVendedorDAO getVendedorDAO() {
        return new VendedorDAO();
    }

    public static ILojaDAO getLojaDAO() {
        return new LojaDAO();
    }

    public static IProdutoDAO getProdutoDAO() {
        return new ProdutoDAO();
    }

    public static IItemDAO getItemDAO() {
        return new ItemDAO();
    }

    public static ICarrinhoDAO getCarrinhoDAO() {
        return new CarrinhoDAO();
    }

    public static ICompraDAO getCompraDAO() {
        return new CompraDAO();
    }

    public static IPedidoDAO getPedidoDAO() {
        return new PedidoDAO();
    }
}