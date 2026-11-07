package org.bcc.bloom.controller.carrinho;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.bcc.bloom.dto.CarrinhoDTO;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Item;
import org.bcc.bloom.service.CarrinhoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CarrinhoController {
    @FXML
    private VBox conteudoCarrinho;      // estado com itens

    @FXML
    private VBox estadoVazio;           // estado de carrinho vazio

    @FXML
    private Label rotuloQuantidade;     // "3 itens de 2 lojas"

    @FXML
    private ScrollPane rolagem;

    @FXML
    private VBox listaLojas;            // aqui entram os grupos de lojas

    @FXML
    private TextField campoCupom;

    @FXML
    private Label rotuloMensagemCupom;  // mensagem de cupom valido ou invalido

    @FXML
    private Label rotuloSubtotal;

    @FXML
    private Label rotuloEntrega;

    @FXML
    private Label rotuloTotal;

    @FXML
    private Button botaoFinalizar;

    // Itens que estao no carrinho (voce precisa criar a classe Item)
    private CarrinhoDTO itensDoCarrinho;
    private double desconto = 0.0;
    private CarrinhoService carrinhoService = new CarrinhoService();
    private Comprador comprador;

    // O JavaFX chama este metodo sozinho depois de carregar o FXML
    @FXML
    public void initialize() {
        carregarItensDoCarrinho();
        atualizarTela();
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    private void carregarItensDoCarrinho() {
        this.itensDoCarrinho = carrinhoService.buscarItens(comprador.getCarrinho_id());
    }


    public void atualizarTela() {
        if (itensDoCarrinho == null || itensDoCarrinho.isEmpty()) {
            mostrarEstadoVazio(true);

            listaLojas.getChildren().clear();
            rotuloQuantidade.setText("0 itens");
            atualizarResumo();

            return;
        }

        mostrarEstadoVazio(false);

        for (Item item : itensDoCarrinho) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/carrinho/itemCarrinho.fxml"));
                Parent card = loader.load();
                ItemCarrinhoController controller = loader.getController();

            }
            catch (IOException ex) {
                ex.printStackTrace();
            }

            String nomeLoja = item.getLoja(
            ).getNome();
        }
    }


    private VBox criarGrupoLoja(String nomeLoja) {

        VBox grupo = new VBox();
        grupo.getStyleClass().add("loja-grupo");

        Label cabecalho = new Label(nomeLoja);
        cabecalho.getStyleClass().add("loja-cabecalho");

        grupo.getChildren().add(cabecalho);

        return grupo;
    }


    private void adicionarLinhaItem(VBox grupo, Item item) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ItemView.fxml")
            );

            Parent linha = loader.load();

            ItemCarrinhoController controller = loader.getController();

            controller.definirControladorCarrinho(this);

            grupo.getChildren().add(linha);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: calcular subtotal, entrega e total, e preencher os 3 rotulos
    //       usando formatarPreco()
    private void atualizarResumo() {

        double subtotal = 0.0;

        for (Item item : itensDoCarrinho) {
            subtotal += item.getSubtotal();
        }


        double entrega = 0.0;

        double total = subtotal + entrega - desconto;

        if (total < 0) {
            total = 0;
        }

        rotuloSubtotal.setText(formatarPreco(subtotal));
        rotuloEntrega.setText(formatarPreco(entrega));
        rotuloTotal.setText(formatarPreco(total));
    }


    @FXML
    private void aoClicarAplicarCupom() {

        String cupom = campoCupom.getText();

        if (cupom == null || cupom.trim().isEmpty()) {
            desconto = 0.0;

            rotuloMensagemCupom.setText("Digite um cupom.");
            atualizarResumo();

            return;
        }

        cupom = cupom.trim().toUpperCase(Locale.ROOT);



        if (cupom.equals("BLOOM10")) {

            double subtotal = 0.0;

            for (Item item : itensDoCarrinho) {
                subtotal += item.getSubtotal();
            }

            desconto = subtotal * 0.10;

            rotuloMensagemCupom.setText("Cupom aplicado! 10% de desconto.");

        } else {

            desconto = 0.0;

            rotuloMensagemCupom.setText("Cupom inválido.");
        }

        atualizarResumo();
    }

    @FXML
    private void aoClicarFinalizar() {
    }

    @FXML
    private void aoClicarContinuarComprando() {

    }



    private void mostrarEstadoVazio(boolean vazio) {
        estadoVazio.setVisible(vazio);
        estadoVazio.setManaged(vazio);
        conteudoCarrinho.setVisible(!vazio);
        conteudoCarrinho.setManaged(!vazio);
    }

    // Formata um numero como preco. Ex.: 41.2 vira "R$ 41,20"
    private String formatarPreco(double valor) {
        return String.format(Locale.of("pt", "BR"), "R$ %.2f", valor);
    }
}
