package org.bcc.bloom.controller.carrinho;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.bcc.bloom.model.Produto;

public class ItemCarrinhoController {
    
    @FXML
    private ImageView imagemproduto;

    @FXML
    private Label nome;

    @FXML
    private Label precoUnitario;

    @FXML
    private Button botaoDiminuir;

    @FXML
    private Label quantidade;

    @FXML
    private Button botaoAumentar;

    @FXML
    private Label subtotal;

    @FXML
    private Button botaoRemover;
    
    private Produto produto;

    // Controller da tela do carrinho, usado para pedir que ela se atualize
    private CarrinhoController controladorCarrinho;

    public void definirDados(Produto produto) {
        this.produto = produto;
        this.nome.setText(produto.getNome());
        this.imagemproduto.setImage(produto.getImagem());
        this.precoUnitario.setText(String.valueOf(produto.getPreco()));
        this.quantidade.setText(String.valueOf(produto.getEstoque()));
    }

    // Guarda a referência do carrinho para avisar quando algo mudar
    public void definirControladorCarrinho(CarrinhoController controladorCarrinho) {
        this.controladorCarrinho = controladorCarrinho;
    }

    @FXML
    private void aoClicarAumentar() {
        // TODO: aumentar a quantidade do produto e atualizar a linha
        // TODO: no final, chamar controladorCarrinho.atualizarTela()
    }

    @FXML
    private void aoClicarDiminuir() {
        // TODO: diminuir a quantidade (nao deixar passar de 1, use Remover para tirar)
        // TODO: no final, chamar controladorCarrinho.atualizarTela()
    }

    @FXML
    private void aoClicarRemover() {
        // TODO: remover o produto do carrinho
        // TODO: no final, chamar controladorCarrinho.atualizarTela()
    }
}
