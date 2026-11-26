package org.bcc.bloom.controller.pedido;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Pedido;
import org.bcc.bloom.model.Produto;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CardPedidoVendedorController {
    @FXML private Label rotuloCliente;
    @FXML private Label rotuloNumeroData;
    @FXML private Label rotuloStatus;
    @FXML private Label rotuloResumoItens;
    @FXML private Label rotuloQuantidadeItens;
    @FXML private Label rotuloEndereco;
    @FXML private Label rotuloTotal;
    @FXML private ComboBox<String> campoStatus;
    @FXML private Button botaoAtualizar;

    private Pedido pedido;
    private PedidosVendedorController controladorPedidos;

    public void definirDados(Pedido pedido, Comprador comprador, List<Produto> produtos,
                             String status, PedidosVendedorController controladorPedidos) {
        campoStatus.getItems().setAll("Pedido feito", "Em preparo", "A caminho", "Entregue", "Cancelado");
        this.pedido = pedido;
        this.controladorPedidos = controladorPedidos;
        rotuloCliente.setText(comprador == null ? "Cliente" : comprador.getNome());
        rotuloNumeroData.setText("Pedido #" + pedido.getId() + " · data indisponível");
        rotuloResumoItens.setText(produtos.stream()
                .map(produto -> "1x " + produto.getNome())
                .collect(Collectors.joining("\n")));
        rotuloQuantidadeItens.setText(produtos.size() + (produtos.size() == 1 ? " item" : " itens"));
        String endereco = comprador == null ? null : comprador.getEndereco();
        rotuloEndereco.setText(endereco == null || endereco.isBlank()
                ? "Entrega: endereço não informado"
                : "Entrega: " + endereco);
        rotuloTotal.setText(String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", pedido.getValorTotal()));
        campoStatus.setValue(status);
        atualizarEtiqueta(status);
    }

    @FXML
    private void aoClicarAtualizar() {
        if (pedido == null || controladorPedidos == null || campoStatus.getValue() == null) {
            return;
        }
        controladorPedidos.atualizarStatus(pedido, campoStatus.getValue());
        atualizarEtiqueta(campoStatus.getValue());
        botaoAtualizar.setDisable(true);
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Status do pedido");
        alerta.setHeaderText(null);
        alerta.setContentText("Status atualizado nesta sessão.");
        alerta.showAndWait();
    }

    private void atualizarEtiqueta(String status) {
        rotuloStatus.setText(status);
        rotuloStatus.getStyleClass().removeAll(
                "etiqueta-pedido-andamento",
                "etiqueta-pedido-entregue",
                "etiqueta-pedido-cancelado"
        );
        if ("Entregue".equals(status)) {
            rotuloStatus.getStyleClass().add("etiqueta-pedido-entregue");
        } else if ("Cancelado".equals(status)) {
            rotuloStatus.getStyleClass().add("etiqueta-pedido-cancelado");
        } else {
            rotuloStatus.getStyleClass().add("etiqueta-pedido-andamento");
        }
    }
}
