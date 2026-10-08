package org.bcc.bloom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.text.Font;
import java.util.Objects;

public class BloomApplication extends Application {

    @Override
    public void start(Stage palco) throws Exception {
        Font lora = Font.loadFont(getClass().getResourceAsStream("/fonts/Lora-Medium.ttf"), 14);
        Font nunitoRegular = Font.loadFont(getClass().getResourceAsStream("/fonts/Nunito-Regular.ttf"), 14);
        Font nunitoBold = Font.loadFont(getClass().getResourceAsStream("/fonts/Nunito-Bold.ttf"), 14);

        System.out.println("Lora: " + lora.getFamily());
        System.out.println("Nunito regular: " + nunitoRegular.getFamily());
        System.out.println("Nunito bold: " + nunitoBold.getFamily());

        Parent raiz = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/LoginView.fxml")));

        Scene cena = new Scene(raiz);
        cena.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/bloom.css")).toExternalForm());

        palco.setTitle("Bloom");
        palco.setScene(cena);
        palco.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
