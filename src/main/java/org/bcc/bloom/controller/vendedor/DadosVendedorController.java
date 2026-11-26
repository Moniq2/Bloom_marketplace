package org.bcc.bloom.controller.vendedor;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.bcc.bloom.dao.CompradorDAO;
import org.bcc.bloom.dao.DAOFactory;
import org.bcc.bloom.dao.VendedorDAO;
import org.bcc.bloom.model.Vendedor;
import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class DadosVendedorController {
    @FXML
    private Label rotuloInicial;
    @FXML
    private ImageView imagemPerfil;
    @FXML
    private Label rotuloNomeTitulo;
    @FXML
    private Label rotuloSaldo;
    @FXML
    private TextField campoNome;
    @FXML
    private TextField campoEmail;
    @FXML
    private TextField campoCnpj;
    @FXML
    private Label rotuloErro;
    @FXML
    private HBox botoesEdicao;
    @FXML
    private Button botaoEditar;
    @FXML
    private Button botaoTrocarFoto;

    private final VendedorDAO vendedorDAO;
    private final CompradorDAO compradorDAO;
    private Vendedor vendedor;
    private byte[] perfilSelecionado;

    public DadosVendedorController() {
        this(DAOFactory.getVendedorDAO(), DAOFactory.getCompradorDAO());
    }

    public DadosVendedorController(VendedorDAO vendedorDAO, CompradorDAO compradorDAO) {
        this.vendedorDAO = Objects.requireNonNull(vendedorDAO);
        this.compradorDAO = Objects.requireNonNull(compradorDAO);
    }

    @FXML
    private void initialize() {
        esconderErro();
        definirModoEdicao(false);
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
        perfilSelecionado = vendedor == null ? null : vendedor.getPerfil();
        preencherCampos();
    }

    @FXML
    private void aoClicarEditar() {
        esconderErro();
        definirModoEdicao(true);
        campoNome.requestFocus();
    }

    @FXML
    private void aoClicarCancelar() {
        esconderErro();
        perfilSelecionado = vendedor == null ? null : vendedor.getPerfil();
        preencherCampos();
        definirModoEdicao(false);
    }

    @FXML
    private void aoClicarTrocarFoto() {
        if (imagemPerfil.getScene() == null) {
            return;
        }
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Selecionar foto de perfil");
        seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));
        File arquivo = seletor.showOpenDialog(imagemPerfil.getScene().getWindow());
        if (arquivo == null) {
            return;
        }
        try {
            perfilSelecionado = Files.readAllBytes(arquivo.toPath());
            imagemPerfil.setImage(new Image(new ByteArrayInputStream(perfilSelecionado)));
            rotuloInicial.setVisible(false);
        } catch (IOException e) {
            mostrarErro("Não foi possível carregar a imagem selecionada.");
        }
    }

    @FXML
    private void aoClicarSalvar() {
        esconderErro();
        if (vendedor == null || vendedor.getId() == null) {
            mostrarErro("Não foi possível identificar o vendedor conectado.");
            return;
        }

        String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
        String email = campoEmail.getText() == null ? "" : campoEmail.getText().trim();
        if (nome.isBlank() || email.isBlank()) {
            mostrarErro("Preencha o nome e o e-mail.");
            return;
        }
        if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            mostrarErro("Digite um e-mail válido.");
            return;
        }

        try {
            Vendedor vendedorComEmail = vendedorDAO.buscarPorEmail(email);
            if (vendedorComEmail != null && !Objects.equals(vendedorComEmail.getId(), vendedor.getId())) {
                mostrarErro("Já existe uma conta cadastrada com esse e-mail.");
                return;
            }
            if (compradorDAO.buscarPorEmail(email) != null) {
                mostrarErro("Já existe uma conta cadastrada com esse e-mail.");
                return;
            }

            Vendedor dadosAtualizados = new Vendedor(nome, email, vendedor.getSenha(), vendedor.getCnpj());
            dadosAtualizados.setId(vendedor.getId());
            dadosAtualizados.setSaldo(vendedor.getSaldo());
            dadosAtualizados.setLoja(vendedor.getLoja());
            dadosAtualizados.setPerfil(perfilSelecionado);
            vendedorDAO.atualizar(dadosAtualizados);

            vendedor.setNome(nome);
            vendedor.setEmail(email);
            vendedor.setPerfil(perfilSelecionado);
            preencherCampos();
            definirModoEdicao(false);
        } catch (RuntimeException e) {
            mostrarErro(mensagemErro(e));
        }
    }

    @FXML
    private void aoClicarAlterarSenha() {
        if (vendedor == null || vendedor.getId() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Alterar senha", "Não foi possível identificar o vendedor conectado.");
            return;
        }

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Alterar senha");
        dialogo.setHeaderText("Confirme sua senha atual e escolha uma nova senha.");
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField senhaAtual = new PasswordField();
        PasswordField senhaNova = new PasswordField();
        PasswordField confirmarSenha = new PasswordField();
        senhaAtual.setPromptText("Senha atual");
        senhaNova.setPromptText("Nova senha");
        confirmarSenha.setPromptText("Confirme a nova senha");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(12));
        formulario.add(new Label("Senha atual"), 0, 0);
        formulario.add(senhaAtual, 1, 0);
        formulario.add(new Label("Nova senha"), 0, 1);
        formulario.add(senhaNova, 1, 1);
        formulario.add(new Label("Confirmar senha"), 0, 2);
        formulario.add(confirmarSenha, 1, 2);
        dialogo.getDialogPane().setContent(formulario);

        Optional<ButtonType> resultado = dialogo.showAndWait();
        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
            return;
        }
        if (!senhaConfere(senhaAtual.getText(), vendedor.getSenha())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Alterar senha", "A senha atual está incorreta.");
            return;
        }
        if (!senhaValida(senhaNova.getText())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Alterar senha", "A nova senha deve ter ao menos 8 caracteres, letra maiúscula, número e símbolo.");
            return;
        }
        if (!senhaNova.getText().equals(confirmarSenha.getText())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Alterar senha", "A confirmação da nova senha não confere.");
            return;
        }

        try {
            String senhaCriptografada = BCrypt.hashpw(senhaNova.getText(), BCrypt.gensalt());
            vendedorDAO.atualizarSenha(vendedor.getId(), senhaCriptografada);
            vendedor.setSenha(senhaCriptografada);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Alterar senha", "Senha alterada com sucesso.");
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Alterar senha", mensagemErro(e));
        }
    }

    private void preencherCampos() {
        if (vendedor == null) {
            return;
        }
        String nome = vendedor.getNome() == null ? "" : vendedor.getNome();
        rotuloInicial.setText(nome.isBlank() ? "V" : nome.substring(0, 1).toUpperCase(Locale.ROOT));
        rotuloNomeTitulo.setText(nome);
        rotuloSaldo.setText(String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", vendedor.getSaldo() / 100.0));
        campoNome.setText(nome);
        campoEmail.setText(vendedor.getEmail());
        campoCnpj.setText(formatarCnpj(vendedor.getCnpj()));
        campoCnpj.setEditable(false);
        if (perfilSelecionado != null && perfilSelecionado.length > 0) {
            imagemPerfil.setImage(new Image(new ByteArrayInputStream(perfilSelecionado)));
            rotuloInicial.setVisible(false);
        } else {
            imagemPerfil.setImage(null);
            rotuloInicial.setVisible(true);
        }
    }

    private String formatarCnpj(String cnpj) {
        if (cnpj == null || !cnpj.matches("\\d{14}")) {
            return cnpj == null ? "" : cnpj;
        }
        return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8)
                + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12);
    }

    private boolean senhaConfere(String senha, String hash) {
        if (senha == null || hash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(senha, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean senhaValida(String senha) {
        return senha != null && senha.length() >= 8
                && senha.matches(".*[A-Z].*")
                && senha.matches(".*\\d.*")
                && senha.matches(".*[^A-Za-z0-9].*");
    }

    private void definirModoEdicao(boolean editando) {
        campoNome.setEditable(editando);
        campoEmail.setEditable(editando);
        campoCnpj.setEditable(false);
        botaoEditar.setVisible(!editando);
        botaoEditar.setManaged(!editando);
        botaoTrocarFoto.setVisible(editando);
        botaoTrocarFoto.setManaged(editando);
        botoesEdicao.setVisible(editando);
        botoesEdicao.setManaged(editando);
    }

    private void mostrarErro(String mensagem) {
        rotuloErro.setText(mensagem == null || mensagem.isBlank() ? "Não foi possível salvar os dados." : mensagem);
        rotuloErro.setVisible(true);
        rotuloErro.setManaged(true);
    }

    private void esconderErro() {
        rotuloErro.setText("");
        rotuloErro.setVisible(false);
        rotuloErro.setManaged(false);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private String mensagemErro(RuntimeException e) {
        return e.getMessage() == null ? "Ocorreu um erro inesperado." : e.getMessage();
    }
}
