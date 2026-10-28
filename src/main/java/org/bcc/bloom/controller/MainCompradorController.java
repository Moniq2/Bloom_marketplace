package org.bcc.bloom.controller;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Usuario;
import org.bcc.bloom.service.LojaService;
import java.io.IOException;
import java.util.List;

public class MainCompradorController {

    private LojaService lojaService = new LojaService();

    @FXML
    private VBox barraLateral;

    @FXML
    private Button botaoMenu;

    @FXML
    private StackPane areaConteudo;

    private Usuario usuario;
    private static final double LARGURA_ABERTA = 220;

    private boolean barraAberta = true;

    @FXML
    public void initialize() {
        Rectangle recorte = new Rectangle();
        recorte.widthProperty().bind(barraLateral.widthProperty());
        recorte.heightProperty().bind(barraLateral.heightProperty());

        barraLateral.setClip(recorte);
    }

    public void setup (Usuario usuario) {
        this.usuario = usuario;
        mostrarLojas();
    }

    public void mostrarLojas(){
        LojasController controller = trocarTela("/fxml/Lojas.fxml");
        controller.setupCards();
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
        trocarTela("/fxml/Lojas.fxml");
    }

    @FXML
    private void aoClicarCatalogo() {
        trocarTela("/fxml/catalogo.fxml");
    }

    @FXML
    private void aoClicarCarrinho() {
        trocarTela("/fxml/carrinho.fxml");
    }

    @FXML
    private void aoClicarPedidos() {
        trocarTela("/fxml/pedidos.fxml");
    }

    @FXML
    private void aoClicarSair() {
        System.out.println("Sair");
    }

    private <T> T trocarTela(String url){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
            Parent root = loader.load();
            areaConteudo.getChildren().clear();
            areaConteudo.getChildren().setAll(root);
            return loader.getController();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
