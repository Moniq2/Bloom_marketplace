package org.bcc.bloom.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.bcc.bloom.util.Navegador;

public class SignupOptionsController {
    @FXML
    private void abrirTelaCadastroVendedor(ActionEvent event) {
        Navegador.abrirPagina(event, "/fxml/VendedorSignup.fxml");
    }

    @FXML
    private void abrirTelaCadastroUsuario(ActionEvent event) {
        Navegador.abrirPagina(event, "/fxml/UsuarioSignup.fxml");
    }

    @FXML
    private void voltar(ActionEvent event) {
        Navegador.abrirPagina(event, "/fxml/LoginPage.fxml");
    }
}
