package org.bcc.bloom.controller.loja;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.bcc.bloom.controller.vendedor.MainVendedorController;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.service.LojaService;
import org.bcc.bloom.util.Navegador;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class LojaSignupController {
    private final LojaService lojaService = new LojaService();
    private Vendedor vendedor;
    private byte[] capa;

    @FXML
    private Label rotuloErro;
    @FXML
    private TextField campoNome;
    @FXML
    private TextArea campoDescricao;
    @FXML
    private ImageView capaPrevia;

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    @FXML
    private void selecionarImagem() {
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Selecionar capa");
        seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images","*.png", "*.jpg","*.jpeg"));

        if (capaPrevia.getScene() == null) {
            return;
        }
        File arquivo = seletor.showOpenDialog(capaPrevia.getScene().getWindow());
        if (arquivo!=null){
            Image imagem  = new Image(arquivo.toURI().toString());
            capaPrevia.setImage(imagem);
            try {
                this.capa = Files.readAllBytes(arquivo.toPath());
            }
            catch (IOException e) {
                mostrarErro("Não foi possível ler a imagem selecionada.");
            }
        }
    }

    @FXML
    private void cadastrarLoja(ActionEvent event) {
        esconderErro();
        if (campoNome.getText().isBlank() || campoDescricao.getText().isBlank()) {
            mostrarErro("Por favor, preencha todos os campos para prosseguir.");
            return;
        }
        if (capa == null) {
            mostrarErro("Por favor, escolha uma capa.");
            return;
        }
        if (vendedor == null) {
            mostrarErro("Não foi possível identificar o vendedor conectado.");
            return;
        }
        try {
            lojaService.cadastrarLoja(vendedor, campoNome.getText(), campoDescricao.getText(), capa);
            MainVendedorController controller = Navegador.abrirPagina(event, "/fxml/vendedor/MainVendedor.fxml");
            controller.setVendedor(vendedor);
        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void mostrarErro(String mensagem) {
        rotuloErro.setText(mensagem == null ? "Ocorreu um erro ao cadastrar a loja." : mensagem);
        rotuloErro.setVisible(true);
        rotuloErro.setManaged(true);
    }

    private void esconderErro() {
        rotuloErro.setVisible(false);
        rotuloErro.setManaged(false);
    }
}
