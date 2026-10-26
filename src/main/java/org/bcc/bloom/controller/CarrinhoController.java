package org.bcc.bloom;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    // O JavaFX chama este metodo sozinho depois de carregar o FXML
    @FXML
    public void initialize() {
        carregarItensDoCarrinho();
        atualizarTela();
    }

    // TODO: buscar os itens do carrinho (banco, API ou classe de sessao)
    private void carregarItensDoCarrinho() {
    }

    // Redesenha a tela inteira. Public porque cada linha de item chama
    // este metodo quando a quantidade muda ou o item e removido
    public void atualizarTela() {
        // TODO Passo 1: se itensDoCarrinho estiver vazio, chamar mostrarEstadoVazio(true) e parar
        // TODO Passo 2: chamar mostrarEstadoVazio(false)
        // TODO Passo 3: listaLojas.getChildren().clear()
        // TODO Passo 4: agrupar os itens por loja; para cada loja:
        //               VBox grupo = criarGrupoLoja(nomeDaLoja);
        //               para cada item da loja: adicionarLinhaItem(grupo, item);
        //               listaLojas.getChildren().add(grupo);
        // TODO Passo 5: atualizar rotuloQuantidade (ex.: "3 itens de 2 lojas")
        // TODO Passo 6: chamar atualizarResumo()
    }

    // TODO: criar um VBox com styleClass "loja-grupo" contendo um Label
    //       com styleClass "loja-cabecalho" e o nome da loja
    private VBox criarGrupoLoja(String nomeLoja) {
        return null;
    }

    // TODO: carregar /fxml/ItemCarrinhoView.fxml com FXMLLoader, pegar o
    //       ItemCarrinhoController, chamar definirDados(item) e
    //       definirControladorCarrinho(this), e adicionar a linha no grupo
    private void adicionarLinhaItem(VBox grupo, ItemCarrinho item) {
    }

    // TODO: calcular subtotal, entrega e total, e preencher os 3 rotulos
    //       usando formatarPreco()
    private void atualizarResumo() {
    }

    // ----- Acoes dos botoes -----

    @FXML
    private void aoClicarAplicarCupom() {
        // TODO: validar o texto de campoCupom, aplicar o desconto
        //       e mostrar o resultado em rotuloMensagemCupom
    }

    @FXML
    private void aoClicarFinalizar() {
        // TODO: criar o pedido e abrir a tela de pagamento ou confirmacao
    }

    @FXML
    private void aoClicarContinuarComprando() {
        // TODO: voltar para a tela da lista de lojas
    }

    // ----- Auxiliares -----

    // Alterna entre carrinho vazio e carrinho com itens
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
