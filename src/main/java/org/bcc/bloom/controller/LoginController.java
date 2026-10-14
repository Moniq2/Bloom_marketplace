package org.bcc.bloom.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.bcc.bloom.service.UsuarioService;
import org.bcc.bloom.util.Navegador;

public class LoginController {
    private UsuarioService usuarioService = new UsuarioService();

    public LoginController() {};
    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @FXML
    private TextField campoEmail;

    @FXML
    private PasswordField campoSenha;

    @FXML
    private Label rotuloErro;

    @FXML
    private void aoClicarEntrar(ActionEvent event) {
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            mostrarErro("Preencha o e-mail e a senha.");
            return;
        }

        if (!email.contains("@")) {
            mostrarErro("Digite um e-mail válido.");
            return;
        }
        esconderErro();
        try {
            usuarioService.login(email, senha);
            Navegador.abrirPagina(event,"/fxml/Main.fxml");
        }
        catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void aoClicarEsqueciSenha(ActionEvent event) {
        System.out.println("Esqueci minha senha");
    }

    @FXML
    private void aoClicarCriarConta(ActionEvent event) {
        Navegador.abrirPagina(event, "/fxml/SignupOptions.fxml");
    }

    private void mostrarErro(String mensagem) {
        rotuloErro.setText(mensagem);
        rotuloErro.setVisible(true);
        rotuloErro.setManaged(true);
    }

    private void esconderErro() {
        rotuloErro.setVisible(false);
        rotuloErro.setManaged(false);
    }
}
