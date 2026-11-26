package org.bcc.bloom.controller.vendedor;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.bcc.bloom.controller.loja.MinhaLojaController;
import org.bcc.bloom.controller.pedido.PedidosVendedorController;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.util.Navegador;

import java.io.IOException;

public class MainVendedorController {

    @FXML
    private VBox barraLateral;

    @FXML
    private Button botaoMenu;

    @FXML
    private StackPane areaConteudo;

    private Vendedor vendedor;

    private static final double LARGURA_ABERTA = 220;

    private boolean barraAberta = true;

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
        mostrarMinhaLoja();
    }

    @FXML
    private void initialize() {
        Rectangle recorte = new Rectangle();
        recorte.widthProperty().bind(barraLateral.widthProperty());
        recorte.heightProperty().bind(barraLateral.heightProperty());

        barraLateral.setClip(recorte);
    }

    @FXML
    private void aoClicarBotaoMenu() {
        double larguraFinal;
        if (barraAberta) {
            larguraFinal = 0;
        } else {
            larguraFinal = LARGURA_ABERTA;
        }

        KeyValue valorFinal = new KeyValue(
                barraLateral.prefWidthProperty(), larguraFinal, Interpolator.EASE_BOTH);
        KeyFrame quadroFinal = new KeyFrame(Duration.millis(250), valorFinal);
        Timeline animacao = new Timeline(quadroFinal);
        animacao.play();

        barraAberta = !barraAberta;
    }

    @FXML
    private void aoClicarInicio() {
        mostrarMinhaLoja();
    }

    @FXML
    private void aoClicarConta() {
        DadosVendedorController controller = trocarTela("/fxml/vendedor/DadosVendedor.fxml");
        controller.setVendedor(vendedor);
    }

    @FXML
    private void aoClicarPedidos() {
        PedidosVendedorController controller = trocarTela("/fxml/pedido/PedidosVendedor.fxml");
        controller.setVendedor(vendedor);
    }

    @FXML
    private void aoClicarMinhaLoja(){
        mostrarMinhaLoja();
    }
    @FXML
    private void aoClicarSair(ActionEvent event) {
        Navegador.abrirPagina(event, "/fxml/login/LoginPage.fxml");
    }

    private void mostrarMinhaLoja() {
        MinhaLojaController controller = trocarTela("/fxml/loja/MinhaLoja.fxml");
        controller.setVendedor(vendedor);
    }

    private <T> T trocarTela(String url){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
            Parent root = loader.load();
            areaConteudo.getChildren().clear();
            areaConteudo.getChildren().setAll(root);
            Navegador.maximizarJanela(areaConteudo.getScene());
            return loader.getController();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
