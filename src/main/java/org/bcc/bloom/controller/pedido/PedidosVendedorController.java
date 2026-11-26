package org.bcc.bloom.controller.pedido;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import org.bcc.bloom.dao.CompradorDAO;
import org.bcc.bloom.dao.DAOFactory;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Pedido;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.service.PedidoService;
import org.bcc.bloom.service.ProdutoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PedidosVendedorController {
    private static final int PEDIDOS_POR_VEZ = 5;
    private static final int COMPRADORES_POR_PAGINA = 100;
    private static final Map<String, String> STATUS_DA_SESSAO = new HashMap<>();
    private static final Map<String, String> STATUS_POR_FILTRO = Map.of(
            "PEDIDO_FEITO", "Pedido feito",
            "EM_PREPARO", "Em preparo",
            "A_CAMINHO", "A caminho",
            "ENTREGUE", "Entregue",
            "CANCELADO", "Cancelado"
    );

    @FXML private ScrollPane rolagem;
    @FXML private VBox listaPedidos;
    @FXML private Label rotuloSemResultado;
    @FXML private VBox rodapeLista;
    @FXML private Button botaoVerMais;
    @FXML private Label rotuloContagem;

    private final CompradorDAO compradorDAO;
    private final PedidoService pedidoService;
    private final ProdutoService produtoService;
    private Vendedor vendedor;
    private String filtroAtual = "TODOS";
    private List<PedidoDaLoja> pedidos = new ArrayList<>();
    private List<PedidoDaLoja> pedidosFiltrados = new ArrayList<>();
    private int quantidadeExibida;

    public PedidosVendedorController() {
        this(DAOFactory.getCompradorDAO(), new PedidoService(), new ProdutoService());
    }

    public PedidosVendedorController(CompradorDAO compradorDAO, PedidoService pedidoService,
                                     ProdutoService produtoService) {
        this.compradorDAO = compradorDAO;
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
        carregarPedidos();
    }

    private void carregarPedidos() {
        pedidos = new ArrayList<>();
        if (vendedor == null || vendedor.getLoja() == null || vendedor.getLoja().getId() == null) {
            atualizarLista();
            return;
        }

        try {
            long lojaId = vendedor.getLoja().getId();
            int totalProdutos = produtoService.contarProdutosDaLoja(lojaId);
            if (totalProdutos == 0) {
                atualizarLista();
                return;
            }

            Set<Long> idsProdutosDaLoja = new HashSet<>();
            for (Produto produto : produtoService.listarProdutosDaLoja(lojaId, 0, totalProdutos)) {
                if (produto.getId() != null) {
                    idsProdutosDaLoja.add(produto.getId());
                }
            }

            int totalCompradores = compradorDAO.contarTotal();
            int paginas = (totalCompradores + COMPRADORES_POR_PAGINA - 1) / COMPRADORES_POR_PAGINA;
            for (int pagina = 0; pagina < paginas; pagina++) {
                for (Comprador comprador : compradorDAO.listarPaginado(pagina, COMPRADORES_POR_PAGINA)) {
                    if (comprador.getId() == null) {
                        continue;
                    }
                    for (Pedido pedido : pedidoService.listarPorComprador(comprador.getId())) {
                        List<Produto> produtosDaLoja = pedido.getProdutos().stream()
                                .filter(produto -> produto.getId() != null && idsProdutosDaLoja.contains(produto.getId()))
                                .toList();
                        if (!produtosDaLoja.isEmpty()) {
                            double totalDaLoja = produtosDaLoja.stream().mapToDouble(Produto::getPreco).sum();
                            Pedido pedidoDaLoja = new Pedido(pedido.getId(), comprador, totalDaLoja,
                                    new ArrayList<>(produtosDaLoja));
                            pedidos.add(new PedidoDaLoja(pedidoDaLoja, comprador, produtosDaLoja));
                        }
                    }
                }
            }
            pedidos.sort(Comparator.comparingLong((PedidoDaLoja linha) -> linha.pedido().getId()).reversed());
            pedidosFiltrados = filtrarPedidos();
            atualizarLista();
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Pedidos", mensagemErro(e));
            pedidos = new ArrayList<>();
            pedidosFiltrados = new ArrayList<>();
            atualizarLista();
        }
    }

    @FXML
    private void aoClicarFiltro(ActionEvent event) {
        ToggleButton selecionado = (ToggleButton) event.getSource();
        if (selecionado.getUserData() != null) {
            filtroAtual = selecionado.getUserData().toString();
            pedidosFiltrados = filtrarPedidos();
            atualizarLista();
        }
    }

    @FXML
    private void aoClicarVerMais() {
        quantidadeExibida = Math.min(quantidadeExibida + PEDIDOS_POR_VEZ, pedidosFiltrados.size());
        renderizarPedidos();
    }

    private List<PedidoDaLoja> filtrarPedidos() {
        if ("TODOS".equals(filtroAtual)) {
            return new ArrayList<>(pedidos);
        }
        String status = STATUS_POR_FILTRO.get(filtroAtual);
        return pedidos.stream()
                .filter(linha -> status != null && status.equals(buscarStatus(linha.pedido())))
                .toList();
    }

    private void atualizarLista() {
        pedidosFiltrados = filtrarPedidos();
        quantidadeExibida = Math.min(PEDIDOS_POR_VEZ, pedidosFiltrados.size());
        boolean semResultado = pedidosFiltrados.isEmpty();
        rotuloSemResultado.setVisible(semResultado);
        rotuloSemResultado.setManaged(semResultado);
        rodapeLista.setVisible(!semResultado);
        rodapeLista.setManaged(!semResultado);
        renderizarPedidos();
    }

    private void renderizarPedidos() {
        listaPedidos.getChildren().clear();
        for (int i = 0; i < quantidadeExibida; i++) {
            PedidoDaLoja linha = pedidosFiltrados.get(i);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pedido/CardPedidoVendedor.fxml"));
                Parent card = loader.load();
                CardPedidoVendedorController controller = loader.getController();
                controller.definirDados(linha.pedido(), linha.comprador(), linha.produtos(),
                        buscarStatus(linha.pedido()), this);
                listaPedidos.getChildren().add(card);
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Pedidos", "Não foi possível carregar um pedido.");
            }
        }
        botaoVerMais.setVisible(quantidadeExibida < pedidosFiltrados.size());
        botaoVerMais.setManaged(quantidadeExibida < pedidosFiltrados.size());
        rotuloContagem.setText("Mostrando " + quantidadeExibida + " de " + pedidosFiltrados.size() + " pedidos");
        rolagem.setVvalue(0);
    }

    public void atualizarStatus(Pedido pedido, String status) {
        if (vendedor == null || vendedor.getLoja() == null || vendedor.getLoja().getId() == null) {
            return;
        }
        STATUS_DA_SESSAO.put(chaveStatus(pedido), status);
    }

    private String buscarStatus(Pedido pedido) {
        return STATUS_DA_SESSAO.getOrDefault(chaveStatus(pedido), "Pedido feito");
    }

    private String chaveStatus(Pedido pedido) {
        return vendedor.getLoja().getId() + ":" + pedido.getId();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private String mensagemErro(RuntimeException e) {
        return e.getMessage() == null ? "Ocorreu um erro ao carregar os pedidos." : e.getMessage();
    }

    private record PedidoDaLoja(Pedido pedido, Comprador comprador, List<Produto> produtos) {}
}
