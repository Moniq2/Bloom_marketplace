package org.bcc.bloom;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class MinhaLojaController {

    // ----- Elementos do FXML (nomes iguais aos fx:id) -----

    @FXML
    private VBox conteudoLoja;          // estado: vendedor com loja

    @FXML
    private VBox estadoSemLoja;         // estado: vendedor sem loja

    @FXML
    private VBox bannerLoja;

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
    private FlowPane painelProdutos;    // aqui entram os cards de produto

    @FXML
    private Label rotuloSemProdutos;

    // ----- Dados -----

    // Loja do vendedor logado (null se ele ainda nao criou a loja)
    private Loja lojaDoVendedor;

    // Produtos da loja (voce precisa criar a classe Produto)
    private List<Produto> produtos = new ArrayList<>();

    // O JavaFX chama este metodo sozinho depois de carregar o FXML
    @FXML
    public void initialize() {
        carregarLojaDoVendedor();
        atualizarTela();
    }

    // TODO: buscar a loja pelo id do vendedor logado e guardar em lojaDoVendedor
    //       (cada vendedor so tem uma loja; se nao existir, deixe null)
    //       depois buscar os produtos dessa loja e guardar em produtos
    private void carregarLojaDoVendedor() {
    }

    // Redesenha a tela. Public para o card de produto poder pedir atualizacao
    public void atualizarTela() {
        // TODO Passo 1: se lojaDoVendedor for null, chamar mostrarEstadoSemLoja(true) e parar
        // TODO Passo 2: chamar mostrarEstadoSemLoja(false)
        // TODO Passo 3: chamar preencherCabecalho()
        // TODO Passo 4: chamar mostrarProdutos()
    }

    // TODO: preencher rotuloNomeLoja, rotuloCategoria, rotuloNota, rotuloStatus,
    //       imagemCapa e imagemLogo com os dados da loja
    //       Dicas: imagemCapa.fitWidthProperty().bind(bannerLoja.widthProperty())
    //              e para a logo redonda: imagemLogo.setClip(new Circle(34, 34, 34))
    private void preencherCabecalho() {
    }

    // TODO Passo 1: painelProdutos.getChildren().clear()
    // TODO Passo 2: se produtos estiver vazio, mostrar rotuloSemProdutos
    // TODO Passo 3: para cada produto, chamar adicionarCardProduto(produto)
    // TODO Passo 4: atualizar rotuloTotalProdutos (ex.: "4 produtos")
    private void mostrarProdutos() {
    }

    // TODO: carregar /fxml/CardProdutoView.fxml com FXMLLoader, pegar o
    //       CardProdutoController, chamar definirDados(produto) e
    //       definirControladorLoja(this), e adicionar o card no painelProdutos
    private void adicionarCardProduto(Produto produto) {
    }

    // Chamado pelo lapis do card de produto
    // TODO: abrir a tela ou janela de edicao do produto recebido
    public void abrirEdicaoProduto(Produto produto) {
    }

    // ----- Acoes dos botoes -----

    @FXML
    private void aoClicarEditarLoja() {
        // TODO: abrir a tela de edicao da loja
    }

    @FXML
    private void aoClicarNovoProduto() {
        // TODO: abrir a tela de cadastro de produto
    }

    @FXML
    private void aoClicarCriarLoja() {
        // TODO: abrir a tela de criacao da loja
    }

    // ----- Auxiliares -----

    // Alterna entre vendedor com loja e vendedor sem loja
    private void mostrarEstadoSemLoja(boolean semLoja) {
        estadoSemLoja.setVisible(semLoja);
        estadoSemLoja.setManaged(semLoja);
        conteudoLoja.setVisible(!semLoja);
        conteudoLoja.setManaged(!semLoja);
    }
}
