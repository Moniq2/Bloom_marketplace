package org.bcc.bloom.controller.comprador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.bcc.bloom.exception.EmailJaCadastradoException;
import org.bcc.bloom.service.UsuarioService;
import org.bcc.bloom.util.Navegador;

public class UsuarioSignupController {
    private final UsuarioService usuarioService;
    @FXML
    private Label rotuloErro;
    public UsuarioSignupController() {
        this.usuarioService = new UsuarioService();
    }

    public UsuarioSignupController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @FXML
    private TextField campoNome;
    @FXML
    private TextField campoEmail;
    @FXML
    private TextField campoCPF;
    @FXML
    private PasswordField campoSenha1;
    @FXML
    private PasswordField campoSenha2;

    @FXML
    private void voltar(ActionEvent event) {
        Navegador.abrirPagina(event, "/fxml/login/LoginPage.fxml");
    }

    @FXML
    private void cadastrar(ActionEvent event) {
        String nome = campoNome.getText();
        String email = campoEmail.getText();
        String cpf = campoCPF.getText();
        String senha1 = campoSenha1.getText();
        String senha2 = campoSenha2.getText();

        esconderErro();
        if (!senha1.equals(senha2)) {
            mostrarErro("As senhas precisam ser iguais.");
            return;
        }
        try {
            usuarioService.cadastrar(nome, email, senha1, cpf, "comprador");
        }
        catch (Exception e){
            if (e instanceof EmailJaCadastradoException){
                mostrarErro("Já existe uma conta cadastrada com esse email.");
                return;
            }
            mostrarErro(e.getMessage());
            return;
        }
        Navegador.abrirPagina(event, "/fxml/login/LoginPage.fxml");
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
