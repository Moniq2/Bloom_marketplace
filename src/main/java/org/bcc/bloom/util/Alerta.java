package org.bcc.bloom.util;

import javafx.scene.control.Alert;

public class Alerta {
    public static void mostrarErro(String titulo, String mensagem){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
    }
}
