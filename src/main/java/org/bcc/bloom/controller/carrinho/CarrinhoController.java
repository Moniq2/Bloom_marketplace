package org.bcc.bloom.controller.carrinho;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.bcc.bloom.dto.CarrinhoDTO;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Item;
import org.bcc.bloom.service.CarrinhoService;
import org.bcc.bloom.util.Navegador;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

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
    private VBox resumoDoPedidoBox;

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

    private Optional<CarrinhoDTO> infoDoCarrinho = Optional.empty();
    private final CarrinhoService carrinhoService = new CarrinhoService();
    private Comprador comprador;


    private String formatarPreco(double valor) {
        return String.format(Locale.of("pt", "BR"), "R$ %.2f", valor);
    }

    public void setup(Comprador comprador) {
        this.comprador = comprador;
        atualizarTela();
    }

    private void carregarItens(){
        if (comprador == null || comprador.getId() == null) {
            infoDoCarrinho = Optional.empty();
            return;
        }
        try {
            Long carrinhoId = carrinhoService.buscarOuCriarParaComprador(comprador);
            infoDoCarrinho = carrinhoService.buscarItens(carrinhoId);
        }
        catch (Exception e){
            mostrarAlerta(Alert.AlertType.ERROR, "Erro no carrinho", mensagemErro(e));
            infoDoCarrinho = Optional.empty();
        }
    }

    private void mostrarItens(){
        resetarTela();

        if (comprador == null || infoDoCarrinho.isEmpty() || infoDoCarrinho.get().isEmpty()) {
            estadoVazio.setManaged(true);
            estadoVazio.setVisible(true);
            resumoDoPedidoBox.setVisible(false);
            resumoDoPedidoBox.setManaged(false);
            rotuloQuantidade.setText("0 itens");
            rotuloSubtotal.setText(formatarPreco(0));
            rotuloEntrega.setText(formatarPreco(0));
            rotuloTotal.setText(formatarPreco(0));
            return;
        }

        int quantidadeTotal = 0;
        double subtotalTotal = 0;
        for (Item item : infoDoCarrinho.get().getItens()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/carrinho/ItemCarrinho.fxml"));
                Parent root = loader.load();
                ItemCarrinhoController controller = loader.getController();
                controller.definirDados(item);
                controller.definirControladorCarrinho(this);
                listaLojas.getChildren().add(root);
                quantidadeTotal += item.getQuantidade();
                subtotalTotal += item.getSubtotal();
            }
            catch (IOException e){
                mostrarAlerta(Alert.AlertType.ERROR, "Carrinho", "Não foi possível carregar um item do carrinho.");
            }
        }
        rotuloQuantidade.setText(quantidadeTotal + (quantidadeTotal == 1 ? " item" : " itens"));
        rotuloSubtotal.setText(formatarPreco(subtotalTotal));
        rotuloEntrega.setText(formatarPreco(0));
        rotuloTotal.setText(formatarPreco(subtotalTotal));
    }

    private void resetarTela(){
        listaLojas.getChildren().clear();
        estadoVazio.setManaged(false);
        estadoVazio.setVisible(false);

        resumoDoPedidoBox.setVisible(true);
        resumoDoPedidoBox.setManaged(true);
    }

    @FXML
    private void aoClicarFinalizar(){
        mostrarAlerta(Alert.AlertType.INFORMATION, "Finalizar pedido", "A finalização de pedidos ainda não está disponível.");
    }

    @FXML
    private void aoClicarAplicarCupom(){
        rotuloMensagemCupom.setText("Cupom não encontrado.");
        rotuloMensagemCupom.setManaged(true);
        rotuloMensagemCupom.setVisible(true);
    }

    @FXML
    private void aoClicarContinuarComprando() {
        Navegador.trocarTela("/fxml/loja/Lojas.fxml");
    }

    public void atualizarTela() {
        carregarItens();
        mostrarItens();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private String mensagemErro(Exception e) {
        return e.getMessage() == null ? "Ocorreu um erro ao carregar o carrinho." : e.getMessage();
    }
}
