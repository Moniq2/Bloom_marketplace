package org.bcc.bloom.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class PedidosController {

    // ----- Elementos do FXML (nomes iguais aos fx:id) -----

    @FXML
    private VBox conteudoPedidos;       // estado: usuario com pedidos

    @FXML
    private VBox estadoSemPedidos;      // estado: usuario sem pedidos

    @FXML
    private ToggleGroup grupoFiltro;    // filtros: Todos, Em andamento, Entregues, Cancelados

    @FXML
    private ScrollPane rolagem;

    @FXML
    private VBox listaPedidos;          // aqui entram os cards de pedido

    @FXML
    private Label rotuloSemResultado;   // "Nenhum pedido nesta categoria."

    @FXML
    private VBox rodapeLista;

    @FXML
    private Button botaoVerMais;

    @FXML
    private Label rotuloContagem;       // "Mostrando 3 de 8 pedidos"

    private static final int PEDIDOS_POR_VEZ = 5;

    // Quantos pedidos ja estao na tela (serve tambem como OFFSET na consulta ao banco)
    private int quantidadeExibida = 0;

    // Filtro atual: "TODOS", "EM_ANDAMENTO", "ENTREGUE" ou "CANCELADO"
    private String filtroAtual = "TODOS";


}
