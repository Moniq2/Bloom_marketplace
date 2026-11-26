package org.bcc.bloom.controller.loja;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.bcc.bloom.controller.produto.CadastroProdutoController;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.service.LojaService;
import org.bcc.bloom.service.ProdutoService;
import org.bcc.bloom.util.Navegador;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MinhaLojaController {

    @FXML
    private VBox conteudoLoja;

    @FXML
    private VBox estadoSemLoja;

    @FXML
    private VBox bannerLoja;

    @FXML
    private StackPane containerCapa;

    @FXML
    private ImageView imagemCapa;

    @FXML
    private ImageView imagemLogo;

    @FXML
    private Label rotuloNomeLoja;

    @FXML
    private Label rotuloCategoria;

    @FXML
    private Label rotuloNota;

    @FXML
    private Label rotuloStatus;

    @FXML
    private Button botaoEditarLoja;

    @FXML
    private Label rotuloTotalProdutos;

    @FXML
    private Button botaoNovoProduto;

    @FXML
    private ScrollPane rolagem;

    @FXML
    private FlowPane painelProdutos;

    @FXML
    private Label rotuloSemProdutos;

    private Loja lojaDoVendedor;
    private Vendedor vendedor;
    private final LojaService lojaService = new LojaService();
    private final ProdutoService produtoService = new ProdutoService();

    private List<Produto> produtos = new ArrayList<>();

    @FXML
    public void initialize() {
        imagemCapa.fitWidthProperty().bind(containerCapa.widthProperty());
        carregarLojaDoVendedor();
        atualizarTela();
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
        carregarLojaDoVendedor();
        atualizarTela();
    }

    private void carregarLojaDoVendedor() {
        lojaDoVendedor = vendedor == null ? null : vendedor.getLoja();
        produtos.clear();
        if (lojaDoVendedor == null && vendedor != null && vendedor.getId() != null) {
            lojaDoVendedor = lojaService.buscarPorVendedor(vendedor.getId());
            vendedor.setLoja(lojaDoVendedor);
        }
        if (lojaDoVendedor == null || lojaDoVendedor.getId() == null) {
            return;
        }
        int totalProdutos = produtoService.contarProdutosDaLoja(lojaDoVendedor.getId());
        if (totalProdutos > 0) {
            produtos.addAll(produtoService.listarProdutosDaLoja(lojaDoVendedor.getId(), 0, totalProdutos));
        }
    }

    public void atualizarTela() {
        boolean semLoja = lojaDoVendedor == null;
        mostrarEstadoSemLoja(semLoja);
        if (semLoja) {
            return;
        }
        preencherCabecalho();
        mostrarProdutos();
    }

    private void preencherCabecalho() {
        rotuloNomeLoja.setText(lojaDoVendedor.getNome());
        rotuloCategoria.setText("Categoria não informada");
        rotuloNota.setText("Sem avaliações");
        rotuloStatus.setText("Status não informado");
        byte[] capa = lojaDoVendedor.getCapa();
        if (capa != null && capa.length > 0) {
            Image imagem = new Image(new ByteArrayInputStream(capa));
            imagemCapa.setImage(imagem);
            imagemLogo.setImage(imagem);
        } else {
            imagemCapa.setImage(null);
            imagemLogo.setImage(null);
        }
        imagemLogo.setClip(new Circle(34, 34, 34));
    }

    private void mostrarProdutos() {
        painelProdutos.getChildren().clear();
        rotuloTotalProdutos.setText(produtos.size() + (produtos.size() == 1 ? " produto" : " produtos"));
        boolean vazio = produtos.isEmpty();
        rotuloSemProdutos.setVisible(vazio);
        rotuloSemProdutos.setManaged(vazio);
        for (Produto produto : produtos) {
            adicionarCardProduto(produto);
        }
    }

    private void adicionarCardProduto(Produto produto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/produto/CardProduto.fxml"));
            Parent root = loader.load();
            org.bcc.bloom.controller.produto.CardProdutoController controller = loader.getController();
            controller.definirDados(produto);
            controller.definirControladorLoja(this);
            painelProdutos.getChildren().add(root);
        } catch (IOException e) {
            mostrarAlerta("Produtos", "Não foi possível carregar um produto.");
        }
    }

    public void abrirEdicaoProduto(Produto produto, ActionEvent event) {
        CadastroProdutoController controller = Navegador.abrirPagina(event, "/fxml/produto/CadastroProduto.fxml");
        controller.setVendedor(vendedor);
        controller.setProduto(produto);
    }

    @FXML
    private void aoClicarEditarLoja(ActionEvent event) {
        EditarLojaController controller = Navegador.abrirPagina(event, "/fxml/loja/EditarLoja.fxml");
        controller.setVendedor(vendedor);
    }

    @FXML
    private void aoClicarNovoProduto(ActionEvent event) {
        CadastroProdutoController controller = Navegador.abrirPagina(event, "/fxml/produto/CadastroProduto.fxml");
        controller.setVendedor(vendedor);
    }

    @FXML
    private void aoClicarCriarLoja(ActionEvent event) {
        LojaSignupController controller = Navegador.abrirPagina(event, "/fxml/loja/LojaSignup.fxml");
        controller.setVendedor(vendedor);
    }

    private void mostrarEstadoSemLoja(boolean semLoja) {
        estadoSemLoja.setVisible(semLoja);
        estadoSemLoja.setManaged(semLoja);
        conteudoLoja.setVisible(!semLoja);
        conteudoLoja.setManaged(!semLoja);
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
