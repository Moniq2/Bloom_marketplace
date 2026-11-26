package org.bcc.bloom.controller.produto;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.bcc.bloom.controller.vendedor.MainVendedorController;
import org.bcc.bloom.model.Categoria;
import org.bcc.bloom.model.Loja;
import org.bcc.bloom.model.Produto;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.service.ProdutoService;
import org.bcc.bloom.util.Navegador;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.Locale;

public class CadastroProdutoController {
    @FXML
    private Label rotuloTitulo;
    @FXML
    private ImageView imagemProduto;
    @FXML
    private Label rotuloSemImagem;
    @FXML
    private Button botaoEscolherImagem;
    @FXML
    private TextField campoNome;
    @FXML
    private ComboBox<String> campoCategoria;
    @FXML
    private TextField campoPreco;
    @FXML
    private TextField campoEstoque;
    @FXML
    private TextArea campoDescricao;
    @FXML
    private Label rotuloContador;
    @FXML
    private Label rotuloErro;
    @FXML
    private Button botaoCancelar;
    @FXML
    private Button botaoSalvar;

    private final ProdutoService produtoService = new ProdutoService();
    private Vendedor vendedor;
    private Produto produto;
    private byte[] imagemSelecionada;

    @FXML
    private void initialize() {
        campoCategoria.getItems().setAll("frutas", "verduras", "ervas", "grãos", "legumes");
        campoDescricao.textProperty().addListener((observable, anterior, atual) -> atualizarContador(atual));
        esconderErro();
        atualizarContador(campoDescricao.getText());
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        if (produto == null) {
            rotuloTitulo.setText("Novo produto");
            botaoSalvar.setText("Salvar produto");
            return;
        }
        rotuloTitulo.setText("Editar produto");
        botaoSalvar.setText("Salvar alterações");
        campoNome.setText(produto.getNome());
        campoCategoria.setValue(produto.getCategoria() == null ? null : categoriaParaFormulario(produto.getCategoria()));
        campoPreco.setText(String.format(Locale.forLanguageTag("pt-BR"), "%.2f", produto.getPreco()));
        campoEstoque.setText(String.valueOf(produto.getEstoque()));
        campoDescricao.setText(produto.getDescricao());
        imagemSelecionada = produto.getImagemBytes();
        atualizarImagem();
    }

    @FXML
    private void aoClicarEscolherImagem() {
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Selecionar imagem do produto");
        seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));
        if (imagemProduto.getScene() == null) {
            return;
        }
        File arquivo = seletor.showOpenDialog(imagemProduto.getScene().getWindow());
        if (arquivo == null) {
            return;
        }
        try {
            imagemSelecionada = Files.readAllBytes(arquivo.toPath());
            atualizarImagem();
            esconderErro();
        } catch (IOException e) {
            mostrarErro("Não foi possível ler a imagem selecionada.");
        }
    }

    @FXML
    private void aoClicarSalvar(ActionEvent event) {
        esconderErro();
        Loja loja = vendedor == null ? null : vendedor.getLoja();
        if (loja == null || loja.getId() == null) {
            mostrarErro("O vendedor conectado ainda não tem uma loja cadastrada.");
            return;
        }

        String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
        String descricao = campoDescricao.getText() == null ? "" : campoDescricao.getText().trim();
        if (nome.isBlank() || nome.length() > 50) {
            mostrarErro("Informe um nome de até 50 caracteres.");
            return;
        }
        if (descricao.isBlank() || descricao.length() > 500) {
            mostrarErro("Informe uma descrição de até 500 caracteres.");
            return;
        }
        if (campoCategoria.getValue() == null) {
            mostrarErro("Selecione uma categoria.");
            return;
        }

        double preco;
        int estoque;
        try {
            preco = lerPreco(campoPreco.getText());
            estoque = Integer.parseInt(campoEstoque.getText().trim());
        } catch (RuntimeException e) {
            mostrarErro("Informe um preço válido e um estoque inteiro.");
            return;
        }
        if (!Double.isFinite(preco) || preco <= 0) {
            mostrarErro("O preço deve ser maior que zero.");
            return;
        }
        if (estoque < 0) {
            mostrarErro("O estoque não pode ser negativo.");
            return;
        }

        try {
            Categoria categoria = categoriaDoFormulario(campoCategoria.getValue());
            if (produto == null) {
                produtoService.cadastrarProduto(loja, nome, descricao, estoque, preco, categoria, imagemSelecionada);
            } else {
                Produto atualizado = new Produto(nome, descricao, estoque, preco, categoria, imagemSelecionada);
                atualizado.setId(produto.getId());
                produtoService.atualizar(atualizado);
                produto.setNome(atualizado.getNome());
                produto.setDescricao(atualizado.getDescricao());
                produto.setEstoque(atualizado.getEstoque());
                produto.setPreco(atualizado.getPreco());
                produto.setCategoria(atualizado.getCategoria());
                produto.setImagem(atualizado.getImagemBytes());
            }
            voltarParaLoja(event);
        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void aoClicarCancelar(ActionEvent event) {
        voltarParaLoja(event);
    }

    private void voltarParaLoja(ActionEvent event) {
        if (vendedor == null) {
            mostrarAlerta("Produtos", "Não foi possível retornar à loja do vendedor.");
            return;
        }
        MainVendedorController controller = Navegador.abrirPagina(event, "/fxml/vendedor/MainVendedor.fxml");
        controller.setVendedor(vendedor);
    }

    private double lerPreco(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new NumberFormatException("Preço vazio.");
        }
        String limpo = valor.trim().replace("R$", "").replace(" ", "");
        if (limpo.contains(",") && limpo.contains(".")) {
            limpo = limpo.replace(".", "").replace(',', '.');
        } else {
            limpo = limpo.replace(',', '.');
        }
        return new BigDecimal(limpo).doubleValue();
    }

    private Categoria categoriaDoFormulario(String valor) {
        String normalizada = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
        return Categoria.valueOf(normalizada);
    }

    private String categoriaParaFormulario(Categoria categoria) {
        return switch (categoria) {
            case FRUTAS -> "frutas";
            case VERDURAS -> "verduras";
            case ERVAS -> "ervas";
            case GRAOS -> "grãos";
            case LEGUMES -> "legumes";
        };
    }

    private void atualizarContador(String texto) {
        String valor = texto == null ? "" : texto;
        rotuloContador.setText(valor.length() + " / 500");
        rotuloContador.setStyle(valor.length() > 500 ? "-fx-text-fill: #c0392b;" : "");
    }

    private void atualizarImagem() {
        boolean semImagem = imagemSelecionada == null || imagemSelecionada.length == 0;
        rotuloSemImagem.setVisible(semImagem);
        rotuloSemImagem.setManaged(semImagem);
        imagemProduto.setImage(semImagem ? null : new Image(new ByteArrayInputStream(imagemSelecionada)));
    }

    private void mostrarErro(String mensagem) {
        rotuloErro.setText(mensagem == null || mensagem.isBlank() ? "Não foi possível salvar o produto." : mensagem);
        rotuloErro.setVisible(true);
        rotuloErro.setManaged(true);
    }

    private void esconderErro() {
        rotuloErro.setText("");
        rotuloErro.setVisible(false);
        rotuloErro.setManaged(false);
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
