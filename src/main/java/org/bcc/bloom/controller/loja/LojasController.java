package org.bcc.bloom.controller.loja;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.service.LojaService;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class LojasController {
    private final LojaService lojaService = new LojaService();
    private int paginaAtual = 0;
    private int totalPaginas = 1;
    private static final int LOJAS_POR_PAGINA = 10;
    private List<Loja> lojasEncontradas = new ArrayList<>();
    private Comprador comprador;

    @FXML
    private TextField campoBusca;
    @FXML
    private ScrollPane rolagem;
    @FXML
    private Button botaoAnterior;
    @FXML
    private Button botaoProxima;
    @FXML
    private Label rotuloPagina;

    @FXML
    private FlowPane painelLojas;

    @FXML
    private Label rotuloVazio;

    public void initialize() {
        setupCards();
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
        setupCards();
    }

    public void setupCards() {
        painelLojas.getChildren().clear();
        atualizarLojasEncontradas();
        totalPaginas = Math.max(1, (lojasEncontradas.size() + LOJAS_POR_PAGINA - 1) / LOJAS_POR_PAGINA);
        paginaAtual = Math.min(paginaAtual, totalPaginas - 1);
        int inicio = paginaAtual * LOJAS_POR_PAGINA;
        int fim = Math.min(inicio + LOJAS_POR_PAGINA, lojasEncontradas.size());
        List<Loja> lojas = lojasEncontradas.subList(inicio, fim);
        boolean vazio = lojasEncontradas.isEmpty();
        rotuloVazio.setVisible(vazio);
        rotuloVazio.setManaged(vazio);

        for (Loja loja : lojas) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loja/LojaCard.fxml"));
                Parent root = loader.load();
                CardLojaController controller = loader.getController();
                controller.setupCard(loja, comprador);
                painelLojas.getChildren().add(root);
            } catch (Exception e) {
                mostrarErro("Não foi possível carregar uma loja.");
            }
        }
        rotuloPagina.setText("Página " + (paginaAtual + 1) + " de " + totalPaginas);
        botaoAnterior.setDisable(paginaAtual == 0);
        botaoProxima.setDisable(paginaAtual >= totalPaginas - 1);
        rolagem.setVvalue(0);
    }

    private void atualizarLojasEncontradas() {
        String busca = normalizar(campoBusca.getText());
        int paginas = lojaService.totalDePaginas(100);
        List<Loja> todas = new ArrayList<>();
        for (int pagina = 0; pagina < paginas; pagina++) {
            todas.addAll(lojaService.listarLojas(pagina, 100));
        }
        lojasEncontradas = todas.stream()
                .filter(loja -> busca.isEmpty() || normalizar(loja.getNome()).contains(busca))
                .toList();
    }

    @FXML
    public void aoClicarProxima() {
        if (paginaAtual < totalPaginas - 1) {
            paginaAtual++;
            setupCards();
        }
    }

    @FXML
    public void aoClicarAnterior() {
        if (paginaAtual == 0) return;
        paginaAtual--;
        setupCards();
    }

    @FXML
    public void aoBuscar() {
        paginaAtual = 0;
        setupCards();
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(java.util.Locale.ROOT);
    }

    private void mostrarErro(String mensagem) {
        javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alerta.setTitle("Lojas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
