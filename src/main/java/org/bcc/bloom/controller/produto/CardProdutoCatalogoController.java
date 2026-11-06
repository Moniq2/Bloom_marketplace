package org.bcc.bloom.controller.produto;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.bcc.bloom.CatalogoController;
import org.bcc.bloom.model.Produto;

public class CardProdutoCatalogoController {

    // ----- Elementos do FXML (nomes iguais aos fx:id) -----

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

    // ----- Dados -----

    // Produto que este card representa
    private Produto produto;

    // Controller do catalogo, usado para pedir que o produto va para o carrinho
    private CatalogoController controladorCatalogo;

    // Chamado pelo CatalogoController logo depois de carregar este card
    public void definirDados(Produto produto) {
        this.produto = produto;
        // TODO Passo 1: preencher imagemProduto, rotuloNome, rotuloPreco e rotuloLoja
        // TODO Passo 2: se o produto estiver sem estoque, desativar botaoAdicionar
        //               (botaoAdicionar.setDisable(true)) e trocar o texto para "Indisponível"
        // TODO (opcional): recortar a imagem com cantos de cima arredondados
    }

    // Guarda a referencia do catalogo
    public void definirControladorCatalogo(CatalogoController controladorCatalogo) {
        this.controladorCatalogo = controladorCatalogo;
    }

    // ----- Acoes dos botoes -----

    @FXML
    private void aoClicarAdicionar() {
        // TODO: chamar controladorCatalogo.adicionarAoCarrinho(produto)
        // TODO (opcional): dar um retorno visual, como trocar o texto do botao
        //                  para "Adicionado ✓" por um instante
    }
}
