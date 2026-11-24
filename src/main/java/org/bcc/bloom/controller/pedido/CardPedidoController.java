package org.bcc.bloom.controller.pedido;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.bcc.bloom.model.Pedido;
import org.bcc.bloom.model.Produto;

import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CardPedidoController {

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

    private Pedido pedido;

    private PedidosController controladorPedidos;

    public void definirDados(Pedido pedido) {
        this.pedido = pedido;
        rotuloLoja.setText("Pedido");
        rotuloNumeroData.setText("Pedido #" + pedido.getId() + " · data indisponível");
        List<Produto> produtos = pedido.getProdutos() == null ? new ArrayList<>() : pedido.getProdutos();
        miniaturas.getChildren().clear();
        for (Produto produto : produtos) {
            ImageView miniatura = new ImageView(produto.getImagem());
            miniatura.setFitWidth(34);
            miniatura.setFitHeight(34);
            miniatura.setPreserveRatio(false);
            miniatura.setClip(new Circle(17, 17, 17));
            miniaturas.getChildren().add(miniatura);
        }
        rotuloResumoItens.setText(produtos.isEmpty() ? "Sem itens" : produtos.get(0).getNome());
        rotuloQuantidadeItens.setText(produtos.size() + (produtos.size() == 1 ? " item" : " itens"));
        rotuloTotal.setText(String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", pedido.getValorTotal()));
        imagemLoja.setImage(null);
        imagemLoja.setClip(new Circle(19, 19, 19));
        definirStatus();
        marcarEtapas(1);
    }

    private void definirStatus() {
        rotuloStatus.setText("Pedido recebido");
        rotuloStatus.getStyleClass().removeAll("etiqueta-pedido-andamento", "etiqueta-pedido-entregue", "etiqueta-pedido-cancelado");
        rotuloStatus.getStyleClass().add("etiqueta-pedido-andamento");
        linhaTempo.setVisible(true);
        linhaTempo.setManaged(true);
        botaoPedirNovamente.setVisible(false);
        botaoPedirNovamente.setManaged(false);
    }

    private void marcarEtapas(int etapaAtual) {
        List<Label> etapas = List.of(etapa1, etapa2, etapa3, etapa4);
        List<Region> conectores = List.of(conector1, conector2, conector3);
        for (int i = 0; i < etapas.size(); i++) {
            Label etapa = etapas.get(i);
            etapa.getStyleClass().removeAll("etapa-concluida", "etapa-atual");
            if (i + 1 < etapaAtual) {
                etapa.getStyleClass().add("etapa-concluida");
                etapa.setText("✓");
            } else if (i + 1 == etapaAtual) {
                etapa.getStyleClass().add("etapa-atual");
                etapa.setText(String.valueOf(i + 1));
            } else {
                etapa.setText("");
            }
        }
        for (int i = 0; i < conectores.size(); i++) {
            conectores.get(i).getStyleClass().remove("conector-ativo");
            if (i + 1 < etapaAtual) {
                conectores.get(i).getStyleClass().add("conector-ativo");
            }
        }
    }

    public void definirControladorPedidos(PedidosController controladorPedidos) {
        this.controladorPedidos = controladorPedidos;
    }

    @FXML
    private void aoClicarDetalhes() {
        if (controladorPedidos != null && pedido != null) {
            controladorPedidos.abrirDetalhesPedido(pedido);
        }
    }

    @FXML
    private void aoClicarPedirNovamente() {
        if (controladorPedidos != null && pedido != null) {
            controladorPedidos.pedirNovamente(pedido);
        }
    }
}
