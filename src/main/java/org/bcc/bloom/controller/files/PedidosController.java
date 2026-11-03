package org.bcc.bloom;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class PedidosController {

    // ----- Elementos do FXML (nomes iguais aos fx:id) -----

    @FXML
    private VBox conteudoPedidos;       // estado: usuario com pedidos

    @FXML
    private VBox estadoSemPedidos;      // estado: usuario sem pedidos

    @FXML
    private ToggleGroup grupoFiltro;    // filtros: Todos, Em andamento, Entregues, Cancelados

    @FXML
    private ScrollPane rolagem;

    @FXML
    private VBox listaPedidos;          // aqui entram os cards de pedido

    @FXML
    private Label rotuloSemResultado;   // "Nenhum pedido nesta categoria."

    @FXML
    private VBox rodapeLista;

    @FXML
    private Button botaoVerMais;

    @FXML
    private Label rotuloContagem;       // "Mostrando 3 de 8 pedidos"

    // ----- Dados -----

    // Quantos pedidos sao adicionados a cada clique em "Ver mais"
    private static final int PEDIDOS_POR_VEZ = 5;

    // Quantos pedidos ja estao na tela (serve tambem como OFFSET na consulta ao banco)
    private int quantidadeExibida = 0;

    // Filtro atual: "TODOS", "EM_ANDAMENTO", "ENTREGUE" ou "CANCELADO"
    private String filtroAtual = "TODOS";

    // O JavaFX chama este metodo sozinho depois de carregar o FXML
    @FXML
    public void initialize() {
        recarregarLista();
    }

    // Limpa a lista e comeca de novo (usado na abertura da tela e ao trocar de filtro)
    private void recarregarLista() {
        // TODO Passo 1: listaPedidos.getChildren().clear()
        // TODO Passo 2: quantidadeExibida = 0
        // TODO Passo 3: chamar carregarMaisPedidos()
    }

    // Adiciona mais pedidos no final da lista
    private void carregarMaisPedidos() {
        // TODO Passo 1: buscar os proximos PEDIDOS_POR_VEZ pedidos do usuario logado,
        //               respeitando filtroAtual, comecando em quantidadeExibida
        //               (no SQL: LIMIT PEDIDOS_POR_VEZ OFFSET quantidadeExibida)
        // TODO Passo 2: para cada pedido, chamar adicionarCardPedido(pedido)
        // TODO Passo 3: quantidadeExibida = quantidadeExibida + (pedidos adicionados)
        // TODO Passo 4: buscar o total de pedidos do filtro (SELECT COUNT(*)) e
        //               chamar atualizarRodape(totalDePedidos)
    }

    // TODO: carregar /fxml/CardPedidoView.fxml com FXMLLoader, pegar o
    //       CardPedidoController, chamar definirDados(pedido) e
    //       definirControladorPedidos(this), e adicionar o card em listaPedidos
    private void adicionarCardPedido(Pedido pedido) {
    }

    // TODO Passo 1: se totalDePedidos for 0, mostrar rotuloSemResultado, esconder
    //               rodapeLista e (se o filtro for TODOS) chamar mostrarEstadoSemPedidos(true)
    // TODO Passo 2: atualizar rotuloContagem ("Mostrando X de Y pedidos")
    // TODO Passo 3: esconder o botaoVerMais quando quantidadeExibida >= totalDePedidos
    private void atualizarRodape(int totalDePedidos) {
    }

    // Chamado pelo botao "Ver detalhes" do card
    // TODO: abrir a tela de detalhes do pedido recebido
    public void abrirDetalhesPedido(Pedido pedido) {
    }

    // Chamado pelo botao "Pedir novamente" do card
    // TODO: colocar os itens do pedido de volta no carrinho
    public void pedirNovamente(Pedido pedido) {
    }

    // ----- Acoes dos botoes -----

    @FXML
    private void aoClicarFiltro() {
        // TODO Passo 1: pegar o filtro selecionado:
        //               grupoFiltro.getSelectedToggle().getUserData().toString()
        // TODO Passo 2: guardar em filtroAtual e chamar recarregarLista()
        // Dica: se getSelectedToggle() for null (usuario clicou no filtro ja
        //       selecionado), selecione o filtro de novo para nao ficar sem nenhum
    }

    @FXML
    private void aoClicarVerMais() {
        carregarMaisPedidos();
    }

    @FXML
    private void aoClicarVerLojas() {
        // TODO: voltar para a tela da lista de lojas
    }

    // ----- Auxiliares -----

    // Alterna entre usuario com pedidos e usuario sem pedidos
    private void mostrarEstadoSemPedidos(boolean semPedidos) {
        estadoSemPedidos.setVisible(semPedidos);
        estadoSemPedidos.setManaged(semPedidos);
        conteudoPedidos.setVisible(!semPedidos);
        conteudoPedidos.setManaged(!semPedidos);
    }
}
