package org.bcc.bloom;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import org.bcc.bloom.controller.ItemCarrinhoController;
import org.bcc.bloom.model.ItemCarrinho;
import org.bcc.bloom.model.Loja;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CarrinhoController {

    // ----- Elementos do FXML (nomes iguais aos fx:id) -----

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

    // ----- Dados -----

    // Itens que estao no carrinho (voce precisa criar a classe ItemCarrinho)
    private List<ItemCarrinho> itensDoCarrinho = new ArrayList<>();

    private double desconto = 0.0;

    // O JavaFX chama este metodo sozinho depois de carregar o FXML
    @FXML
    public void initialize() {
        carregarItensDoCarrinho();
        atualizarTela();
    }

    
    private void carregarItensDoCarrinho() {
        
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

        listaLojas.getChildren().clear();

      

        Map<String, List<ItemCarrinho>> itensPorLoja = new LinkedHashMap<>();

        for (ItemCarrinho item : itensDoCarrinho) {
            String nomeLoja = item.getLoja().getName();

            itensPorLoja
                    .computeIfAbsent(nomeLoja, chave -> new ArrayList<>())
                    .add(item);
        }

        for (Map.Entry<String, List<ItemCarrinho>> entrada : itensPorLoja.entrySet()) {

            VBox grupo = criarGrupoLoja(entrada.getKey());

            for (ItemCarrinho item : entrada.getValue()) {
                adicionarLinhaItem(grupo, item);
            }

            listaLojas.getChildren().add(grupo);
        }

        
        int quantidadeTotal = 0;

        for (ItemCarrinho item : itensDoCarrinho) {
            quantidadeTotal += item.getQuantidade();
        }

        int quantidadeLojas = itensPorLoja.size();

        String textoItens = quantidadeTotal == 1 ? "item" : "itens";
        String textoLojas = quantidadeLojas == 1 ? "loja" : "lojas";

        rotuloQuantidade.setText(
                quantidadeTotal + " " + textoItens + " de " +
                quantidadeLojas + " " + textoLojas
        );

        // TODO Passo 6: chamar atualizarResumo()
        atualizarResumo();
    }

    
    private VBox criarGrupoLoja(String nomeLoja) {

        VBox grupo = new VBox();
        grupo.getStyleClass().add("loja-grupo");

        Label cabecalho = new Label(nomeLoja);
        cabecalho.getStyleClass().add("loja-cabecalho");

        grupo.getChildren().add(cabecalho);

        return grupo;
    }

    
    private void adicionarLinhaItem(VBox grupo, ItemCarrinho item) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ItemCarrinhoView.fxml")
            );

            Parent linha = loader.load();

            ItemCarrinhoController controller = loader.getController();

            controller.definirDados(item);
            controller.definirControladorCarrinho(this);

            grupo.getChildren().add(linha);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void atualizarResumo() {

        double subtotal = 0.0;

        for (ItemCarrinho item : itensDoCarrinho) {
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

            for (ItemCarrinho item : itensDoCarrinho) {
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
       

    @FXML
    private void aoClicarContinuarComprando() {
       
    }

    
    public void removerItem(ItemCarrinho item) {
        itensDoCarrinho.remove(item);
        atualizarTela();
    }

    
    private void mostrarEstadoVazio(boolean vazio) {
        estadoVazio.setVisible(vazio);
        estadoVazio.setManaged(vazio);
        conteudoCarrinho.setVisible(!vazio);
        conteudoCarrinho.setManaged(!vazio);
    }

    private String formatarPreco(double valor) {
        return String.format(Locale.of("pt", "BR"), "R$ %.2f", valor);
    }
}