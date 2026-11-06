package org.bcc.bloom;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.bcc.bloom.model.Produto;

public class CardProdutoController {

    // ----- Elementos do FXML (nomes iguais aos fx:id) -----

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

    // Produto que este card representa
    private Produto produto;

    // Chamado pelo MinhaLojaController logo depois de carregar este card
    public void definirDados(Produto produto) {
        this.produto = produto;
        // TODO: preencher imagemProduto, rotuloNome, rotuloPreco e rotuloEstoque
        // TODO: se o estoque for 0, trocar o texto de rotuloStatus para "Sem estoque"
        //       e a classe de estilo de "etiqueta-ativo" para "etiqueta-sem-estoque":
        //       rotuloStatus.getStyleClass().remove("etiqueta-ativo");
        //       rotuloStatus.getStyleClass().add("etiqueta-sem-estoque");
        // TODO (opcional): recortar a imagem com cantos de cima arredondados
    }

    @FXML
    private void aoClicarEditar() {
        // TODO: chamar controladorLoja.abrirEdicaoProduto(produto)
    }
}
