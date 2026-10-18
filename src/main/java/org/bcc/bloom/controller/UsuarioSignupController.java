package org.bcc.bloom.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.bcc.bloom.service.UsuarioService;
import org.bcc.bloom.util.Navegador;

public class UsuarioSignupController {
    private final UsuarioService usuarioService;
    UsuarioSignupController() {
        this.usuarioService = new UsuarioService();
    }
    UsuarioSignupController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @FXML
    private TextField campoNome;
    @FXML
    private TextField campoEmail;
    @FXML
    private TextField campoCPF;
    @FXML
    private TextField campoSenha1;
    @FXML
    private TextField campoSenha2;

    @FXML
    private void voltar(ActionEvent event) {
        Navegador.abrirPagina(event, "/fxml/LoginPage.fxml");
    }

    @FXML
    private void cadastrar(ActionEvent event) {
        String nome = campoNome.getText();
        String email = campoEmail.getText();
        String cpf = campoCPF.getText();
        String senha1 = campoSenha1.getText();
        String senha2 = campoSenha2.getText();

        usuarioService.validarDados(nome, email, cpf, senha);
        usuarioService.cadastrarComprador(nome, email, senha, cpf);
    }

}
