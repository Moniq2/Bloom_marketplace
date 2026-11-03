package org.bcc.bloom;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class CardPedidoController {

    // ----- Elementos do FXML (nomes iguais aos fx:id) -----

    @FXML
    private ImageView imagemLoja;

    @FXML
    private Label rotuloLoja;

    @FXML
    private Label rotuloNumeroData;

    @FXML
    private Label rotuloStatus;

    @FXML
    private HBox miniaturas;

    @FXML
    private Label rotuloResumoItens;

    @FXML
    private Label rotuloQuantidadeItens;

    // Linha do tempo: 4 circulos e 3 conectores entre eles
    @FXML
    private HBox linhaTempo;

    @FXML
    private Label etapa1;

    @FXML
    private Label etapa2;

    @FXML
    private Label etapa3;

    @FXML
    private Label etapa4;

    @FXML
    private Region conector1;

    @FXML
    private Region conector2;

    @FXML
    private Region conector3;

    @FXML
    private Label rotuloTotal;

    @FXML
    private Button botaoDetalhes;

    @FXML
    private Button botaoPedirNovamente;

    // ----- Dados -----

    // Pedido que este card representa (voce precisa criar a classe Pedido)
    private Pedido pedido;

    // Controller da tela Meus pedidos, usado para abrir detalhes e pedir novamente
    private PedidosController controladorPedidos;

    // Chamado pelo PedidosController logo depois de carregar este card
    public void definirDados(Pedido pedido) {
        this.pedido = pedido;
        // TODO Passo 1: preencher imagemLoja, rotuloLoja, rotuloNumeroData,
        //               rotuloResumoItens, rotuloQuantidadeItens e rotuloTotal
        // TODO Passo 2: criar uma miniatura (ImageView redondo) para cada produto
        //               e adicionar em miniaturas.getChildren()
        // TODO Passo 3: ajustar o status (veja definirStatus)
        // TODO Passo 4: logo da loja redonda: imagemLoja.setClip(new Circle(19, 19, 19))
    }

    // TODO: trocar o texto e a cor da etiqueta conforme a situacao do pedido.
    //       Para trocar a cor, remova a classe antiga e adicione a nova:
    //         rotuloStatus.getStyleClass().remove("etiqueta-pedido-andamento");
    //         rotuloStatus.getStyleClass().add("etiqueta-pedido-entregue");
    //       Classes disponiveis: etiqueta-pedido-andamento, etiqueta-pedido-entregue,
    //       etiqueta-pedido-cancelado
    //       Regras: so mostre linhaTempo (setVisible e setManaged) se estiver em andamento;
    //       so mostre botaoPedirNovamente se estiver entregue ou cancelado
    private void definirStatus() {
    }

    // TODO: pintar a linha do tempo. Para cada etapa ate a atual:
    //       - etapas ja concluidas: classe "etapa-concluida" e texto "✓"
    //       - etapa atual: classe "etapa-atual"
    //       - conectores entre etapas ja alcancadas: classe "conector-ativo"
    //       etapaAtual vai de 1 (pedido feito) a 4 (entregue)
    private void marcarEtapas(int etapaAtual) {
    }

    // Guarda a referencia da tela Meus pedidos
    public void definirControladorPedidos(PedidosController controladorPedidos) {
        this.controladorPedidos = controladorPedidos;
    }

    // ----- Acoes dos botoes -----

    @FXML
    private void aoClicarDetalhes() {
        // TODO: chamar controladorPedidos.abrirDetalhesPedido(pedido)
    }

    @FXML
    private void aoClicarPedirNovamente() {
        // TODO: chamar controladorPedidos.pedirNovamente(pedido)
    }
}
