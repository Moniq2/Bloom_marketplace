package org.bcc.bloom.controller.produto;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import org.bcc.bloom.dto.CarrinhoDTO;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Item;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.service.CarrinhoService;
import org.bcc.bloom.service.ItemService;
import org.bcc.bloom.service.ProdutoService;

import java.io.IOException;
import java.util.List;

public class CatalogoController {

    @FXML
    private ScrollPane rolagem;

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

    private static final int PRODUTOS_POR_PAGINA = 12;

    private int paginaAtual = 0;

    private int totalPaginas = 1;

    private final ProdutoService produtoService = new ProdutoService();
    private final CarrinhoService carrinhoService = new CarrinhoService();
    private final ItemService itemService = new ItemService();
    private Comprador comprador;

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    @FXML
    public void initialize() {
        carregarPagina();
    }

    private void carregarPagina() {
        painelProdutos.getChildren().clear();
        try {
            totalPaginas = Math.max(1, produtoService.totalDePaginas(PRODUTOS_POR_PAGINA));
            paginaAtual = Math.min(paginaAtual, totalPaginas - 1);
            List<Produto> produtos = produtoService.listarProdutos(paginaAtual, PRODUTOS_POR_PAGINA);
            boolean vazio = produtos.isEmpty();
            rotuloSemProdutos.setVisible(vazio);
            rotuloSemProdutos.setManaged(vazio);
            barraPaginacao.setVisible(!vazio);
            barraPaginacao.setManaged(!vazio);
            for (Produto produto : produtos) {
                adicionarCardProduto(produto);
            }
            atualizarPaginacao();
            rolagem.setVvalue(0);
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Catálogo", mensagemErro(e));
            rotuloSemProdutos.setVisible(true);
            rotuloSemProdutos.setManaged(true);
            barraPaginacao.setVisible(false);
            barraPaginacao.setManaged(false);
        }
    }

    private void adicionarCardProduto(Produto produto) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/produto/CardProdutoCatalogo.fxml"));
        try {
            Parent root = loader.load();
            CardProdutoCatalogoController controller = loader.getController();
            controller.definirDados(produto);
            controller.definirControladorCatalogo(this);
            painelProdutos.getChildren().add(root);
        }
        catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Catálogo", "Não foi possível carregar um produto.");
        }
    }

    public void adicionarAoCarrinho(Produto produto) {
        if (comprador == null || comprador.getId() == null || produto == null || produto.getId() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Carrinho", "Entre novamente na sua conta para adicionar produtos.");
            return;
        }
        try {
            Long carrinhoId = carrinhoService.buscarOuCriarParaComprador(comprador);
            if (produto.getEstoque() < 1) {
                mostrarAlerta(Alert.AlertType.ERROR, "Carrinho", "Este produto está sem estoque.");
                return;
            }
            CarrinhoDTO carrinho = carrinhoService.buscarItens(carrinhoId).orElse(null);
            if (carrinho != null) {
                for (Item item : carrinho.getItens()) {
                    if (item.getProduto() != null && produto.getId().equals(item.getProduto().getId())) {
                        int novaQuantidade = item.getQuantidade() + 1;
                        if (novaQuantidade > produto.getEstoque()) {
                            mostrarAlerta(Alert.AlertType.ERROR, "Carrinho", "Não há estoque suficiente para aumentar a quantidade.");
                            return;
                        }
                        itemService.atualizar(item.getId(), novaQuantidade);
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Carrinho", produto.getNome() + " foi adicionado ao carrinho.");
                        return;
                    }
                }
            }
            itemService.criar(carrinhoId, produto.getId(), 1);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Carrinho", produto.getNome() + " foi adicionado ao carrinho.");
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Carrinho", mensagemErro(e));
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

    private void atualizarPaginacao() {
        rotuloPagina.setText("Página " + (paginaAtual + 1) + " de " + totalPaginas);
        botaoAnterior.setDisable(paginaAtual == 0);
        botaoProxima.setDisable(paginaAtual >= totalPaginas - 1);
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
