package org.bcc.bloom.controller.produto;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.bcc.bloom.model.Produto;

import java.util.Locale;
import java.util.function.Consumer;

public class CardProdutoCatalogoController {

    @FXML
    private ImageView imagemProduto;

    @FXML
    private Label rotuloNome;

    @FXML
    private Label rotuloPreco;

    @FXML
    private Label rotuloLoja;

    @FXML
    private Button botaoAdicionar;

    private Produto produto;

    private CatalogoController controladorCatalogo;

    private Consumer<Produto> acaoAdicionar;

    public void definirDados(Produto produto) {
        this.produto = produto;
        imagemProduto.setImage(produto.getImagem());
        rotuloNome.setText(produto.getNome());
        rotuloPreco.setText(String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f / un", produto.getPreco()));
        rotuloLoja.setText("Loja não informada");
        boolean semEstoque = produto.getEstoque() <= 0;
        botaoAdicionar.setDisable(semEstoque);
        botaoAdicionar.setText(semEstoque ? "Indisponível" : "Adicionar");
    }

    public void definirControladorCatalogo(CatalogoController controladorCatalogo) {
        this.controladorCatalogo = controladorCatalogo;
    }

    public void definirAcaoAdicionar(Consumer<Produto> acaoAdicionar) {
        this.acaoAdicionar = acaoAdicionar;
    }

    public void definirNomeLoja(String nomeLoja) {
        rotuloLoja.setText(nomeLoja == null || nomeLoja.isBlank() ? "Loja não informada" : nomeLoja);
    }

    @FXML
    private void aoClicarAdicionar() {
        if (acaoAdicionar != null && produto != null) {
            acaoAdicionar.accept(produto);
        } else if (controladorCatalogo != null && produto != null) {
            controladorCatalogo.adicionarAoCarrinho(produto);
        }
    }
}
