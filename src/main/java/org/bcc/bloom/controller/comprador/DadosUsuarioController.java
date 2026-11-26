package org.bcc.bloom.controller.comprador;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import org.bcc.bloom.dao.CompradorDAO;
import org.bcc.bloom.dao.DAOFactory;
import org.bcc.bloom.dao.VendedorDAO;
import org.bcc.bloom.model.Comprador;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class DadosUsuarioController {
    @FXML
    private Label rotuloInicial;
    @FXML
    private Label rotuloNomeTitulo;
    @FXML
    private Label rotuloSaldo;
    @FXML
    private TextField campoNome;
    @FXML
    private TextField campoEmail;
    @FXML
    private TextField campoCpf;
    @FXML
    private TextField campoEndereco;
    @FXML
    private Label rotuloErro;
    @FXML
    private HBox botoesEdicao;
    @FXML
    private Button botaoEditar;
    @FXML
    private Button botaoCancelar;
    @FXML
    private Button botaoSalvar;

    private final CompradorDAO compradorDAO;
    private final VendedorDAO vendedorDAO;
    private Comprador comprador;

    public DadosUsuarioController() {
        this(DAOFactory.getCompradorDAO(), DAOFactory.getVendedorDAO());
    }

    public DadosUsuarioController(CompradorDAO compradorDAO, VendedorDAO vendedorDAO) {
        this.compradorDAO = Objects.requireNonNull(compradorDAO);
        this.vendedorDAO = Objects.requireNonNull(vendedorDAO);
    }

    @FXML
    private void initialize() {
        esconderErro();
        definirModoEdicao(false);
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
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
        preencherCampos();
        definirModoEdicao(false);
    }

    @FXML
    private void aoClicarSalvar() {
        esconderErro();
        if (comprador == null || comprador.getId() == null) {
            mostrarErro("Não foi possível identificar o comprador conectado.");
            return;
        }

        String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
        String email = campoEmail.getText() == null ? "" : campoEmail.getText().trim();
        String endereco = campoEndereco.getText() == null ? "" : campoEndereco.getText().trim();

        if (nome.isBlank() || email.isBlank()) {
            mostrarErro("Preencha o nome e o e-mail.");
            return;
        }
        if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            mostrarErro("Digite um e-mail válido.");
            return;
        }

        try {
            Comprador compradorComEmail = compradorDAO.buscarPorEmail(email);
            if (compradorComEmail != null && !Objects.equals(compradorComEmail.getId(), comprador.getId())) {
                mostrarErro("Já existe uma conta cadastrada com esse e-mail.");
                return;
            }
            if (vendedorDAO.buscarPorEmail(email) != null) {
                mostrarErro("Já existe uma conta cadastrada com esse e-mail.");
                return;
            }

            Comprador dadosAtualizados = new Comprador(nome, email, comprador.getSenha(), comprador.getCpf());
            dadosAtualizados.setId(comprador.getId());
            dadosAtualizados.setSaldo(comprador.getSaldo());
            dadosAtualizados.setEndereco(endereco);
            dadosAtualizados.SetCarrinho_id(comprador.getCarrinho_id());
            compradorDAO.atualizar(dadosAtualizados);

            comprador.setNome(nome);
            comprador.setEmail(email);
            comprador.setEndereco(endereco);
            preencherCampos();
            definirModoEdicao(false);
        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void aoClicarAlterarSenha() {
        if (comprador == null || comprador.getId() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Alterar senha", "Não foi possível identificar o comprador conectado.");
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

        if (!senhaConfere(senhaAtual.getText(), comprador.getSenha())) {
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
            compradorDAO.atualizarSenha(comprador.getId(), senhaCriptografada);
            comprador.setSenha(senhaCriptografada);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Alterar senha", "Senha alterada com sucesso.");
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Alterar senha", mensagemErro(e));
        }
    }

    private void preencherCampos() {
        if (comprador == null) {
            return;
        }
        String nome = comprador.getNome() == null ? "" : comprador.getNome();
        rotuloInicial.setText(nome.isBlank() ? "U" : nome.substring(0, 1).toUpperCase(Locale.ROOT));
        rotuloNomeTitulo.setText(nome);
        rotuloSaldo.setText(String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", comprador.getSaldo() / 100.0));
        campoNome.setText(nome);
        campoEmail.setText(comprador.getEmail());
        campoCpf.setText(formatarCpf(comprador.getCpf()));
        campoEndereco.setText(comprador.getEndereco());
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            return cpf == null ? "" : cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
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
        campoCpf.setEditable(false);
        campoEndereco.setEditable(editando);
        botaoEditar.setVisible(!editando);
        botaoEditar.setManaged(!editando);
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
