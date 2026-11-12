package org.bcc.bloom.dao;

public class DAOFactory {

    private DAOFactory() {
    }

    public static CompradorDAO getCompradorDAO() {
        return new CompradorDAOImpl();
    }

    public static VendedorDAO getVendedorDAO() {
        return new VendedorDAOImpl();
    }

    public static LojaDAO getLojaDAO() {
        return new LojaDAOImpl();
    }

    public static ProdutoDAO getProdutoDAO() {
        return new ProdutoDAOImpl();
    }

    public static ItemDAO getItemDAO() {
        return new ItemDAOImpl();
    }

    public static CarrinhoDAO getCarrinhoDAO() {
        return new CarrinhoDAOImpl();
    }

    public static CompraDAO getCompraDAO() {
        return new CompraDAOImpl();
    }

    public static PedidoDAO getPedidoDAO() {
        return new PedidoDAOImpl();
    }
}