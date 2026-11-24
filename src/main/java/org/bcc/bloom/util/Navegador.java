package org.bcc.bloom.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Navegador {
    public static <T> T abrirPagina(ActionEvent event, String url) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(Navegador.class.getResource(url));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
            return loader.getController();
        }
        catch (IOException e) {
            System.out.println("Erro aqui");
            throw new RuntimeException(e);
        }
    }

    private static StackPane container;

    public static void configurar(StackPane pane) {
        container = pane;
    }

    public static <T> T trocarTela(String caminho) {
        try {
            FXMLLoader loader = new FXMLLoader(Navegador.class.getResource(caminho));
            Parent tela = loader.load();
            container.getChildren().setAll(tela);
            return loader.getController();
        }
        catch (IOException e) {
            throw new RuntimeException("Não foi possível abrir a tela: " + caminho, e);
        }
    }
}
