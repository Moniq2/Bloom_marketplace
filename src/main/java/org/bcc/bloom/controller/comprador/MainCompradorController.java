package org.bcc.bloom.controller.comprador;

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
import org.bcc.bloom.controller.carrinho.CarrinhoController;
import org.bcc.bloom.controller.loja.LojasController;
import org.bcc.bloom.controller.pedido.PedidosController;
import org.bcc.bloom.controller.produto.CatalogoController;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.util.Navegador;

import java.io.IOException;

public class MainCompradorController {

    @FXML
    private VBox barraLateral;

    @FXML
    private Button botaoMenu;

    @FXML
    private StackPane areaConteudo;

    private Comprador usuario;
    private static final double LARGURA_ABERTA = 220;

    private boolean barraAberta = true;

    @FXML
    public void initialize() {
        Navegador.configurar(areaConteudo);

        Rectangle recorte = new Rectangle();
        recorte.widthProperty().bind(barraLateral.widthProperty());
        recorte.heightProperty().bind(barraLateral.heightProperty());

        barraLateral.setClip(recorte);
    }

    public void setup (Comprador usuario) {
        this.usuario = usuario;
        mostrarLojas();
    }

    public void mostrarLojas(){
        LojasController controller = trocarTela("/fxml/loja/Lojas.fxml");
        controller.setComprador(usuario);
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
        CatalogoController controller = trocarTela("/fxml/catalogo/Catalogo.fxml");
        controller.setComprador(usuario);
    }

    @FXML
    private void aoClicarLojas() {
        mostrarLojas();
    }

    @FXML
    private void aoClicarCarrinho() {
        CarrinhoController controller = trocarTela("/fxml/carrinho/Carrinho.fxml");
        controller.setup(usuario);
    }

    @FXML
    private void aoClicarMeusPedidos() {
        PedidosController controller = trocarTela("/fxml/pedido/PedidosView.fxml");
        controller.setup(usuario);
    }

    @FXML
    private void aoClicarMeusDados() {
        DadosUsuarioController controller = trocarTela("/fxml/comprador/DadosUsuario.fxml");
        controller.setComprador(usuario);
    }

    @FXML
    private void aoClicarQueroSerUmVendedor(ActionEvent event) {
        Navegador.abrirPagina(event, "/fxml/vendedor/VendedorSignup.fxml");
    }

    @FXML
    private void aoClicarSair(ActionEvent event) {
        Navegador.abrirPagina(event, "/fxml/login/LoginPage.fxml");
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
