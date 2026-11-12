package org.bcc.bloom.controller.carrinho;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.bcc.bloom.dto.CarrinhoDTO;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.service.CarrinhoService;
import java.util.Locale;

public class CarrinhoController {
    @FXML
    private VBox conteudoCarrinho;
    @FXML
    private VBox estadoVazio;
    @FXML
    private Label rotuloQuantidade;
    @FXML
    private ScrollPane rolagem;
    @FXML
    private VBox listaLojas;
    @FXML
    private TextField campoCupom;

    @FXML
    private Label rotuloMensagemCupom;
    @FXML
    private Label rotuloSubtotal;
    @FXML
    private Label rotuloEntrega;
    @FXML
    private Label rotuloTotal;
    @FXML
    private Button botaoFinalizar;

    private CarrinhoDTO itensDoCarrinho;
    private CarrinhoService carrinhoService = new CarrinhoService();
    private Comprador comprador;


    // Formata um número como preco. Ex.: 41.2 vira "R$ 41,20"
    private String formatarPreco(double valor) {
        return String.format(Locale.of("pt", "BR"), "R$ %.2f", valor);
    }

    private void carregarItens(){

    }
}
