package org.bcc.bloom.controller.loja;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.util.Navegador;

import java.io.ByteArrayInputStream;

public class CardLojaController {
    private Loja loja;
    private Comprador comprador;

    @FXML
    private Label nomeLojaLabel;
    @FXML
    private Label descricaoLabel;
    @FXML
    private ImageView capaImg;
    @FXML
    private VBox containerLoja;

    @FXML
    private void initialize(){
        capaImg.setFitHeight(140);
        capaImg.setPreserveRatio(false);
    }

    @FXML
    private void verLoja(ActionEvent event){
        if (loja == null) {
            return;
        }
        LojaClienteViewController controller = Navegador.trocarTela("/fxml/loja/LojaClienteView.fxml");
        if (controller != null) {
            controller.setup(loja, comprador);
        }
    }

    public void setupCard(Loja loja, Comprador comprador){
        this.loja = loja;
        this.comprador = comprador;
        nomeLojaLabel.setText(loja.getNome());
        descricaoLabel.setText(loja.getDescricao() == null ? "" : loja.getDescricao());

        if (loja.getCapa() != null && loja.getCapa().length > 0){
            ByteArrayInputStream inputStream = new ByteArrayInputStream(loja.getCapa());
            Image imagem = new Image(inputStream);
            capaImg.setImage(imagem);
        }
    }

}
