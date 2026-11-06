package org.bcc.bloom;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import org.bcc.bloom.model.Produto;

public class CatalogoController {

    // ----- Elementos do FXML (nomes iguais aos fx:id) -----

    @FXML
    private ScrollPane rolagem;

    @FXML
    private FlowPane painelProdutos;    // aqui entram os cards de produto

    @FXML
    private Label rotuloSemProdutos;

    @FXML
    private HBox barraPaginacao;

    @FXML
    private Button botaoAnterior;

    @FXML
    private Label rotuloPagina;

    @FXML
    private Button botaoProxima;

    // ----- Dados -----

    // Quantos produtos aparecem em cada pagina
    private static final int PRODUTOS_POR_PAGINA = 12;

    // Pagina atual. Comeca em 0, ou seja, 0 = primeira pagina
    private int paginaAtual = 0;

    // Total de paginas (calculado a partir do total de produtos no banco)
    private int totalPaginas = 1;

    // O JavaFX chama este metodo sozinho depois de carregar o FXML
    @FXML
    public void initialize() {
        carregarPagina();
    }

    // Busca no banco apenas os produtos da pagina atual e mostra na tela
    private void carregarPagina() {
        // TODO Passo 1: painelProdutos.getChildren().clear()
        // TODO Passo 2: contar os produtos disponiveis (SELECT COUNT(*) FROM produtos ...)
        //               e calcular totalPaginas = total / PRODUTOS_POR_PAGINA,
        //               somando 1 se sobrar resto. Se o total for 0, use totalPaginas = 1
        // TODO Passo 3: calcular o deslocamento: paginaAtual * PRODUTOS_POR_PAGINA
        // TODO Passo 4: buscar os produtos da pagina, junto com o nome da loja
        //               (JOIN com a tabela de lojas) usando
        //               LIMIT PRODUTOS_POR_PAGINA OFFSET deslocamento
        // TODO Passo 5: se nao veio nenhum produto, mostrar rotuloSemProdutos
        //               e esconder barraPaginacao (setVisible e setManaged)
        // TODO Passo 6: para cada produto, chamar adicionarCardProduto(produto)
        // TODO Passo 7: chamar atualizarPaginacao()
        // TODO Passo 8: rolagem.setVvalue(0) para voltar ao topo
    }

    // TODO: carregar /fxml/CardProdutoCatalogoView.fxml com FXMLLoader, pegar o
    //       CardProdutoCatalogoController, chamar definirDados(produto) e
    //       definirControladorCatalogo(this), e adicionar o card em painelProdutos
    private void adicionarCardProduto(Produto produto) {
    }

    // Chamado pelo botao "Adicionar" do card
    // TODO: adicionar o produto ao carrinho do usuario logado
    public void adicionarAoCarrinho(Produto produto) {
    }

    // ----- Acoes dos botoes -----

    @FXML
    private void aoClicarAnterior() {
        // TODO: se paginaAtual > 0, diminuir 1 e chamar carregarPagina()
    }

    @FXML
    private void aoClicarProxima() {
        // TODO: se paginaAtual < totalPaginas - 1, aumentar 1 e chamar carregarPagina()
    }

    // ----- Auxiliares -----

    // Atualiza o texto "Pagina X de Y" e ativa ou desativa os botoes
    private void atualizarPaginacao() {
        rotuloPagina.setText("Página " + (paginaAtual + 1) + " de " + totalPaginas);
        botaoAnterior.setDisable(paginaAtual == 0);
        botaoProxima.setDisable(paginaAtual >= totalPaginas - 1);
    }
}
