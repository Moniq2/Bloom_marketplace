package org.bcc.bloom.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField campoEmail;

    @FXML
    private PasswordField campoSenha;

    @FXML
    private Label rotuloErro;

    @FXML
    private void aoClicarEntrar() {
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

        System.out.println("Tentando entrar com: " + email);
    }

    @FXML
    private void aoClicarEsqueciSenha() {
        System.out.println("Esqueci minha senha");
    }

    @FXML
    private void aoClicarCriarConta() {
        System.out.println("Criar conta");
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
