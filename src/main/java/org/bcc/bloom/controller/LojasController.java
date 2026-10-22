package org.bcc.bloom.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.service.LojaService;

import java.util.List;

public class LojasController {
    private final LojaService lojaService = new LojaService();
    private int paginaAtual = 0;

    @FXML
    private FlowPane painelLojas;

    @FXML
    private Label rotuloVazio;

    public void setupCards(){
        painelLojas.getChildren().clear();

        List<Loja> lojas = lojaService.listarLojas(paginaAtual, 10);

        if (lojas.isEmpty()){
            rotuloVazio.setOpacity(1);
            rotuloVazio.setManaged(false);
        }

        for  (Loja loja : lojas) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LojaCard.fxml"));
                Parent root = loader.load();
                CardLojaController controller = loader.getController();
                controller.setupCard(loja);
                painelLojas.getChildren().add(root);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    public void aoClicarProxima() {
        paginaAtual++;
        setupCards();
    }

    @FXML
    public void aoClicarAnterior(){
        if (paginaAtual == 0) {
            return;
        }
        paginaAtual--;
        setupCards();
    }

    @FXML
    public void aoBuscar(){
        System.out.println("Buscando lojas");
    }

}
