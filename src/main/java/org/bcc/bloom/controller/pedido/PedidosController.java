package org.bcc.bloom.controller.pedido;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Pedido;
import org.bcc.bloom.service.PedidoService;
import org.bcc.bloom.util.Navegador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PedidosController {

    @FXML
    private VBox conteudoPedidos;

    @FXML
    private VBox estadoSemPedidos;

    @FXML
    private ScrollPane rolagem;

    @FXML
    private VBox listaPedidos;

    @FXML
    private Label rotuloSemResultado;

    @FXML
    private VBox rodapeLista;

    @FXML
    private Button botaoVerMais;

    @FXML
    private Label rotuloContagem;

    private static final int PEDIDOS_POR_VEZ = 5;

    private int quantidadeExibida = 0;

    private String filtroAtual = "TODOS";

    private final PedidoService pedidoService = new PedidoService();
    private Comprador comprador;
    private List<Pedido> pedidos = new ArrayList<>();
    private List<Pedido> pedidosFiltrados = new ArrayList<>();

    @FXML
    private void initialize() {
        mostrarPedidos();
    }

    public void setup(Comprador comprador) {
        this.comprador = comprador;
        mostrarPedidos();
    }

    private void mostrarPedidos() {
        if (comprador == null || comprador.getId() == null) {
            atualizarEstados(false, false);
            return;
        }
        try {
            pedidos = pedidoService.listarPorComprador(comprador.getId());
            pedidosFiltrados = filtrarPedidos();
            atualizarLista();
        } catch (RuntimeException e) {
            mostrarAlerta("Pedidos", e.getMessage());
            pedidos = new ArrayList<>();
            pedidosFiltrados = new ArrayList<>();
            atualizarLista();
        }
    }

    private List<Pedido> filtrarPedidos() {
        if ("TODOS".equals(filtroAtual) || "EM_ANDAMENTO".equals(filtroAtual)) {
            return new ArrayList<>(pedidos);
        }
        return new ArrayList<>();
    }

    private void atualizarLista() {
        listaPedidos.getChildren().clear();
        quantidadeExibida = Math.min(PEDIDOS_POR_VEZ, pedidosFiltrados.size());
        boolean semPedidos = pedidos.isEmpty();
        atualizarEstados(!semPedidos, semPedidos);
        boolean semResultado = !semPedidos && pedidosFiltrados.isEmpty();
        rotuloSemResultado.setVisible(semResultado);
        rotuloSemResultado.setManaged(semResultado);
        rodapeLista.setVisible(!semResultado && !semPedidos);
        rodapeLista.setManaged(!semResultado && !semPedidos);
        renderizarMaisPedidos();
    }

    private void renderizarMaisPedidos() {
        listaPedidos.getChildren().clear();
        for (int i = 0; i < quantidadeExibida; i++) {
            Pedido pedido = pedidosFiltrados.get(i);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pedido/CardPedidoView.fxml"));
                Parent card = loader.load();
                CardPedidoController controller = loader.getController();
                controller.definirDados(pedido);
                controller.definirControladorPedidos(this);
                listaPedidos.getChildren().add(card);
            } catch (IOException e) {
                mostrarAlerta("Pedidos", "Não foi possível carregar um pedido.");
            }
        }
        botaoVerMais.setVisible(quantidadeExibida < pedidosFiltrados.size());
        botaoVerMais.setManaged(quantidadeExibida < pedidosFiltrados.size());
        rotuloContagem.setText("Mostrando " + quantidadeExibida + " de " + pedidosFiltrados.size() + " pedidos");
    }

    private void atualizarEstados(boolean mostrarConteudo, boolean mostrarVazio) {
        conteudoPedidos.setVisible(mostrarConteudo);
        conteudoPedidos.setManaged(mostrarConteudo);
        estadoSemPedidos.setVisible(mostrarVazio);
        estadoSemPedidos.setManaged(mostrarVazio);
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
        renderizarMaisPedidos();
    }

    @FXML
    private void aoClicarVerLojas() {
        Navegador.trocarTela("/fxml/loja/Lojas.fxml");
    }

    public void abrirDetalhesPedido(Pedido pedido) {
        String produtos = pedido.getProdutos().stream().map(p -> p.getNome()).reduce((a, b) -> a + "\n" + b).orElse("Sem itens");
        mostrarAlerta("Pedido #" + pedido.getId(), produtos + "\nTotal: " + String.format(java.util.Locale.forLanguageTag("pt-BR"), "R$ %.2f", pedido.getValorTotal()));
    }

    public void pedirNovamente(Pedido pedido) {
        mostrarAlerta("Pedir novamente", "O pedido não guarda as quantidades originais dos produtos.");
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem == null ? "Ocorreu um erro ao carregar os pedidos." : mensagem);
        alerta.showAndWait();
    }


}
