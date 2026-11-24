package org.bcc.bloom.controller.carrinho;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.bcc.bloom.model.Item;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.service.ItemService;

import java.util.Locale;

public class ItemCarrinhoController {
    
    @FXML
    private ImageView imagemProduto;

    @FXML
    private Label rotuloNome;

    @FXML
    private Label rotuloPrecoUnitario;

    @FXML
    private Button botaoDiminuir;

    @FXML
    private Label rotuloQuantidade;

    @FXML
    private Button botaoAumentar;

    @FXML
    private Label rotuloSubtotal;

    @FXML
    private Button botaoRemover;
    
    private Item item;

    private final ItemService itemService = new ItemService();

    private CarrinhoController controladorCarrinho;

    public void definirDados(Item item) {
        this.item = item;
        Produto produto = item.getProduto();
        imagemProduto.setImage(produto == null ? null : produto.getImagem());
        rotuloNome.setText(produto == null ? "Produto" : produto.getNome());
        rotuloPrecoUnitario.setText(formatarPreco(item.getValorUnitario()) + " / un");
        rotuloQuantidade.setText(String.valueOf(item.getQuantidade()));
        rotuloSubtotal.setText(formatarPreco(item.getSubtotal()));
        botaoAumentar.setDisable(produto == null || item.getQuantidade() >= produto.getEstoque());
        botaoDiminuir.setDisable(item.getQuantidade() <= 1);
    }

    public void definirControladorCarrinho(CarrinhoController controladorCarrinho) {
        this.controladorCarrinho = controladorCarrinho;
    }

    @FXML
    private void aoClicarAumentar() {
        if (item == null || item.getProduto() == null || item.getId() == null) {
            return;
        }
        int novaQuantidade = item.getQuantidade() + 1;
        if (novaQuantidade > item.getProduto().getEstoque()) {
            mostrarErro("Não há estoque suficiente para este produto.");
            return;
        }
        atualizarQuantidade(novaQuantidade);
    }

    @FXML
    private void aoClicarDiminuir() {
        if (item != null && item.getQuantidade() > 1) {
            atualizarQuantidade(item.getQuantidade() - 1);
        }
    }

    @FXML
    private void aoClicarRemover() {
        if (item == null || item.getId() == null) {
            return;
        }
        try {
            itemService.remover(item.getId());
            if (controladorCarrinho != null) {
                controladorCarrinho.atualizarTela();
            }
        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void atualizarQuantidade(int quantidade) {
        try {
            itemService.atualizar(item.getId(), quantidade);
            if (controladorCarrinho != null) {
                controladorCarrinho.atualizarTela();
            }
        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    private String formatarPreco(double valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", valor);
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Carrinho");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem == null ? "Ocorreu um erro no carrinho." : mensagem);
        alerta.showAndWait();
    }
}
