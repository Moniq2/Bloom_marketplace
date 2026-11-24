package org.bcc.bloom.controller.produto;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.bcc.bloom.controller.loja.MinhaLojaController;
import org.bcc.bloom.model.Produto;

import java.util.Locale;

public class CardProdutoController {

    @FXML
    private ImageView imagemProduto;

    @FXML
    private Button botaoEditar;

    @FXML
    private Label rotuloNome;

    @FXML
    private Label rotuloPreco;

    @FXML
    private Label rotuloEstoque;

    @FXML
    private Label rotuloStatus;

    private Produto produto;
    private MinhaLojaController controladorLoja;

    public void definirDados(Produto produto) {
        this.produto = produto;
        imagemProduto.setImage(produto.getImagem());
        rotuloNome.setText(produto.getNome());
        rotuloPreco.setText(String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f / un", produto.getPreco()));
        rotuloEstoque.setText("Estoque: " + produto.getEstoque());
        boolean semEstoque = produto.getEstoque() == 0;
        rotuloStatus.setText(semEstoque ? "Sem estoque" : "Ativo");
        rotuloStatus.getStyleClass().removeAll("etiqueta-ativo", "etiqueta-sem-estoque");
        rotuloStatus.getStyleClass().add(semEstoque ? "etiqueta-sem-estoque" : "etiqueta-ativo");
        botaoEditar.setDisable(false);
    }

    public void definirControladorLoja(MinhaLojaController controladorLoja) {
        this.controladorLoja = controladorLoja;
    }

    @FXML
    private void aoClicarEditar() {
        if (controladorLoja != null && produto != null) {
            controladorLoja.abrirEdicaoProduto(produto);
        }
    }
}
