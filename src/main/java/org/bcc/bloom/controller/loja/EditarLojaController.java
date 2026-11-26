package org.bcc.bloom.controller.loja;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.bcc.bloom.controller.vendedor.MainVendedorController;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.service.LojaService;
import org.bcc.bloom.util.Navegador;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class EditarLojaController {
    @FXML
    private ImageView imagemCapa;
    @FXML
    private Label rotuloSemImagem;
    @FXML
    private Button botaoTrocarCapa;
    @FXML
    private TextField campoNome;
    @FXML
    private TextArea campoDescricao;
    @FXML
    private Label rotuloContador;
    @FXML
    private Label rotuloErro;

    private final LojaService lojaService = new LojaService();
    private Vendedor vendedor;
    private Loja loja;
    private byte[] capaSelecionada;

    @FXML
    private void initialize() {
        campoDescricao.textProperty().addListener((observable, anterior, atual) -> atualizarContador(atual));
        esconderErro();
        atualizarContador(campoDescricao.getText());
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
        setLoja(vendedor == null ? null : vendedor.getLoja());
    }

    public void setLoja(Loja loja) {
        this.loja = loja;
        if (loja == null) {
            return;
        }
        campoNome.setText(loja.getNome());
        campoDescricao.setText(loja.getDescricao());
        capaSelecionada = loja.getCapa();
        atualizarImagem();
    }

    @FXML
    private void aoClicarTrocarCapa() {
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Selecionar foto de capa");
        seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));
        if (imagemCapa.getScene() == null) {
            return;
        }
        File arquivo = seletor.showOpenDialog(imagemCapa.getScene().getWindow());
        if (arquivo == null) {
            return;
        }
        try {
            capaSelecionada = Files.readAllBytes(arquivo.toPath());
            atualizarImagem();
            esconderErro();
        } catch (IOException e) {
            mostrarErro("Não foi possível ler a imagem selecionada.");
        }
    }

    @FXML
    private void aoClicarSalvar(ActionEvent event) {
        esconderErro();
        if (loja == null || loja.getId() == null) {
            mostrarErro("Não foi possível identificar a loja do vendedor.");
            return;
        }

        String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
        String descricao = campoDescricao.getText() == null ? "" : campoDescricao.getText().trim();
        if (nome.isBlank() || nome.length() > 50) {
            mostrarErro("Informe um nome de loja de até 50 caracteres.");
            return;
        }
        if (descricao.length() > 500) {
            mostrarErro("A descrição pode ter no máximo 500 caracteres.");
            return;
        }

        try {
            Loja atualizada = new Loja(nome, descricao, capaSelecionada);
            atualizada.setId(loja.getId());
            atualizada.setProdutos(loja.getProdutos());
            lojaService.atualizar(atualizada);
            loja.setNome(nome);
            loja.setDescricao(descricao);
            loja.setCapa(capaSelecionada);
            voltarParaLoja(event);
        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void aoClicarCancelar(ActionEvent event) {
        voltarParaLoja(event);
    }

    private void voltarParaLoja(ActionEvent event) {
        if (vendedor == null) {
            mostrarErro("Não foi possível retornar à área do vendedor.");
            return;
        }
        MainVendedorController controller = Navegador.abrirPagina(event, "/fxml/vendedor/MainVendedor.fxml");
        controller.setVendedor(vendedor);
    }

    private void atualizarContador(String texto) {
        String valor = texto == null ? "" : texto;
        rotuloContador.setText(valor.length() + " / 500");
        rotuloContador.setStyle(valor.length() > 500 ? "-fx-text-fill: #c0392b;" : "");
    }

    private void atualizarImagem() {
        boolean semImagem = capaSelecionada == null || capaSelecionada.length == 0;
        rotuloSemImagem.setVisible(semImagem);
        rotuloSemImagem.setManaged(semImagem);
        imagemCapa.setImage(semImagem ? null : new Image(new ByteArrayInputStream(capaSelecionada)));
    }

    private void mostrarErro(String mensagem) {
        rotuloErro.setText(mensagem == null || mensagem.isBlank() ? "Não foi possível salvar as alterações." : mensagem);
        rotuloErro.setVisible(true);
        rotuloErro.setManaged(true);
    }

    private void esconderErro() {
        rotuloErro.setText("");
        rotuloErro.setVisible(false);
        rotuloErro.setManaged(false);
    }
}
