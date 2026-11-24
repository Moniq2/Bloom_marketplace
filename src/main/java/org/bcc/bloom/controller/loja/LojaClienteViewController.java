package org.bcc.bloom.controller.loja;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import org.bcc.bloom.controller.produto.CardProdutoCatalogoController;
import org.bcc.bloom.dto.CarrinhoDTO;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Item;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.service.CarrinhoService;
import org.bcc.bloom.service.ItemService;
import org.bcc.bloom.service.ProdutoService;
import org.bcc.bloom.util.Navegador;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class LojaClienteViewController {

    private static final int PRODUTOS_POR_PAGINA = 12;

    @FXML
    private ScrollPane rolagem;
    @FXML
    private StackPane containerCapa;
    @FXML
    private ImageView imagemCapa;
    @FXML
    private ImageView imagemLogo;
    @FXML
    private Label nomeLoja;
    @FXML
    private Label rotuloCategoria;
    @FXML
    private Label rotuloNota;
    @FXML
    private Label rotuloDescricao;
    @FXML
    private Label rotuloEntrega;
    @FXML
    private FlowPane painelEtiquetas;
    @FXML
    private Label rotuloTotalProdutos;
    @FXML
    private FlowPane painelProdutos;
    @FXML
    private Label rotuloSemProdutos;
    @FXML
    private HBox barraPaginacao;
    @FXML
    private Button botaoAnterior;
    @FXML
    private Label rotuloPagina;
    @FXML
    private Button botaoProxima;

    private final ProdutoService produtoService = new ProdutoService();
    private final CarrinhoService carrinhoService = new CarrinhoService();
    private final ItemService itemService = new ItemService();

    private Loja loja;
    private Comprador comprador;
    private int paginaAtual;
    private int totalPaginas = 1;

    @FXML
    private void initialize() {
        imagemCapa.fitWidthProperty().bind(containerCapa.widthProperty());
        imagemLogo.setClip(new Circle(34, 34, 34));
        painelEtiquetas.getChildren().clear();
        rotuloSemProdutos.setVisible(false);
        rotuloSemProdutos.setManaged(false);
        atualizarPaginacao();
    }

    public void setup(Loja loja, Comprador comprador) {
        this.comprador = comprador;
        setLoja(loja);
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    public void setLoja(Loja loja) {
        this.loja = loja;
        paginaAtual = 0;
        preencherCabecalho();
        carregarPagina();
    }

    private void preencherCabecalho() {
        if (loja == null) {
            nomeLoja.setText("Loja não encontrada");
            rotuloCategoria.setText("Categoria não informada");
            rotuloNota.setText("Sem avaliações");
            rotuloDescricao.setText("");
            rotuloEntrega.setText("Entrega a combinar");
            imagemCapa.setImage(null);
            imagemLogo.setImage(null);
            return;
        }

        nomeLoja.setText(valorOuPadrao(loja.getNome(), "Loja"));
        rotuloCategoria.setText("Categoria não informada");
        rotuloNota.setText("Sem avaliações");
        rotuloDescricao.setText(valorOuPadrao(loja.getDescricao(), "Esta loja ainda não tem descrição."));
        rotuloEntrega.setText("Entrega a combinar");

        byte[] capa = loja.getCapa();
        Image imagem = capa == null || capa.length == 0
                ? null
                : new Image(new ByteArrayInputStream(capa));
        imagemCapa.setImage(imagem);
        imagemLogo.setImage(imagem);
    }

    private void carregarPagina() {
        painelProdutos.getChildren().clear();

        if (loja == null || loja.getId() == null || loja.getId() <= 0) {
            atualizarResumoProdutos(0);
            mostrarEstadoVazio(true);
            atualizarPaginacao();
            return;
        }

        try {
            int totalProdutos = produtoService.contarProdutosDaLoja(loja.getId());
            totalPaginas = Math.max(1, (totalProdutos + PRODUTOS_POR_PAGINA - 1) / PRODUTOS_POR_PAGINA);
            paginaAtual = Math.min(paginaAtual, totalPaginas - 1);
            List<Produto> produtos = produtoService.listarProdutosDaLoja(
                    loja.getId(), paginaAtual, PRODUTOS_POR_PAGINA);

            atualizarResumoProdutos(totalProdutos);
            mostrarEstadoVazio(produtos.isEmpty());
            for (Produto produto : produtos) {
                adicionarCardProduto(produto);
            }
            atualizarPaginacao();
            rolagem.setVvalue(0);
        } catch (RuntimeException e) {
            painelProdutos.getChildren().clear();
            atualizarResumoProdutos(0);
            mostrarEstadoVazio(true);
            atualizarPaginacao();
            mostrarAlerta(Alert.AlertType.ERROR, "Loja", mensagemErro(e));
        }
    }

    private void adicionarCardProduto(Produto produto) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/produto/CardProdutoCatalogo.fxml"));
            Parent card = loader.load();
            CardProdutoCatalogoController controller = loader.getController();
            controller.definirDados(produto);
            controller.definirNomeLoja(loja.getNome());
            controller.definirAcaoAdicionar(this::adicionarAoCarrinho);
            painelProdutos.getChildren().add(card);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Loja", "Não foi possível carregar um produto.");
        }
    }

    public void adicionarAoCarrinho(Produto produto) {
        if (comprador == null || comprador.getId() == null || produto == null || produto.getId() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Carrinho", "Entre novamente na sua conta para adicionar produtos.");
            return;
        }
        if (produto.getEstoque() < 1) {
            mostrarAlerta(Alert.AlertType.ERROR, "Carrinho", "Este produto está sem estoque.");
            return;
        }

        try {
            Long carrinhoId = carrinhoService.buscarOuCriarParaComprador(comprador);

            CarrinhoDTO carrinho = carrinhoService.buscarItens(carrinhoId).orElse(null);
            if (carrinho != null) {
                for (Item item : carrinho.getItens()) {
                    if (item.getProduto() != null && produto.getId().equals(item.getProduto().getId())) {
                        int novaQuantidade = item.getQuantidade() + 1;
                        if (novaQuantidade > produto.getEstoque()) {
                            mostrarAlerta(Alert.AlertType.ERROR, "Carrinho",
                                    "Não há estoque suficiente para aumentar a quantidade.");
                            return;
                        }
                        itemService.atualizar(item.getId(), novaQuantidade);
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Carrinho",
                                produto.getNome() + " foi adicionado ao carrinho.");
                        return;
                    }
                }
            }

            itemService.criar(carrinhoId, produto.getId(), 1);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Carrinho",
                    produto.getNome() + " foi adicionado ao carrinho.");
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Carrinho", mensagemErro(e));
        }
    }

    @FXML
    private void aoClicarVoltar() {
        LojasController controller = Navegador.trocarTela("/fxml/loja/Lojas.fxml");
        if (controller != null) {
            controller.setComprador(comprador);
        }
    }

    @FXML
    private void aoClicarAnterior() {
        if (paginaAtual > 0) {
            paginaAtual--;
            carregarPagina();
        }
    }

    @FXML
    private void aoClicarProxima() {
        if (paginaAtual < totalPaginas - 1) {
            paginaAtual++;
            carregarPagina();
        }
    }

    private void atualizarResumoProdutos(int total) {
        rotuloTotalProdutos.setText(total + (total == 1 ? " produto" : " produtos"));
    }

    private void mostrarEstadoVazio(boolean vazio) {
        rotuloSemProdutos.setVisible(vazio);
        rotuloSemProdutos.setManaged(vazio);
        barraPaginacao.setVisible(!vazio);
        barraPaginacao.setManaged(!vazio);
    }

    private void atualizarPaginacao() {
        rotuloPagina.setText("Página " + (paginaAtual + 1) + " de " + totalPaginas);
        botaoAnterior.setDisable(paginaAtual == 0);
        botaoProxima.setDisable(paginaAtual >= totalPaginas - 1);
    }

    private String valorOuPadrao(String valor, String padrao) {
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private String mensagemErro(RuntimeException e) {
        return e.getMessage() == null ? "Ocorreu um erro inesperado." : e.getMessage();
    }
}
