package org.bcc.bloom.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.bcc.bloom.controller.comprador.MainCompradorController;
import org.bcc.bloom.controller.vendedor.MainVendedorController;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Usuario;
import org.bcc.bloom.model.Vendedor;
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
    private void initialize() {
        campoEmail.setText("usuario@gmail.com");
        campoSenha.setText("Abacaxi@123");
    }
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
            Usuario usuario = usuarioService.login(email, senha);
            if (usuario instanceof Comprador comprador) {
                MainCompradorController controller = Navegador.abrirPagina(event, "/fxml/comprador/MainComprador.fxml");
                controller.setup(comprador);
            } else if (usuario instanceof Vendedor vendedor) {
                MainVendedorController controller = Navegador.abrirPagina(event, "/fxml/vendedor/MainVendedor.fxml");
                controller.setVendedor(vendedor);
            }
        }
        catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void aoClicarEsqueciSenha(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Recuperar senha");
        alerta.setHeaderText(null);
        alerta.setContentText("A recuperação de senha ainda não está disponível.");
        alerta.showAndWait();
    }

    @FXML
    private void aoClicarCriarConta(ActionEvent event) {
        Navegador.abrirPagina(event, "/fxml/login/SignupOptions.fxml");
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
