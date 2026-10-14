package org.bcc.bloom.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.bcc.bloom.util.Navegador;

public class UsuarioSignupController {
    @FXML
    private void voltar(ActionEvent event) {
        Navegador.abrirPagina(event, "/fxml/LoginPage.fxml");
    }
}
