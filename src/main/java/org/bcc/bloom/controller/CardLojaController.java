package org.bcc.bloom.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.util.Navegador;

import java.io.ByteArrayInputStream;

public class CardLojaController {
    private long id;

    @FXML
    private Label nomeLojaLabel;
    @FXML
    private Label descricaoLabel;
    @FXML
    private ImageView capaImg;

    @FXML
    private void verLoja(ActionEvent event){
        Navegador.abrirPagina(event, "/fxml/Loja.fxml");
    }

    private void setupCard(Loja loja){
        nomeLojaLabel.setText(loja.getNome());
        descricaoLabel.setText(loja.getDescricao());

        if (loja.getCapa().length > 0 || loja.getCapa() != null){
            ByteArrayInputStream inputStream = new ByteArrayInputStream(loja.getCapa());
            Image imagem = new Image(inputStream);
            capaImg.setImage(imagem);
        }
         this.id = loja.getId();
    }

}
