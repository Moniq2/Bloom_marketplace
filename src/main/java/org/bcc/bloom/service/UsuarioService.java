package org.bcc.bloom.service;

import org.bcc.bloom.exception.CampoObrigatorioException;
import org.bcc.bloom.exception.CnpjInvalidoException;
import org.bcc.bloom.exception.CpfInvalidoException;
import org.bcc.bloom.exception.CredenciaisInvalidasException;
import org.bcc.bloom.exception.EmailInvalidoException;
import org.bcc.bloom.exception.EmailJaCadastradoException;
import org.bcc.bloom.exception.RecursoNaoEncontradoException;
import org.bcc.bloom.exception.SenhaInvalidaException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Usuario;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.dao.CompradorDAO;
import org.bcc.bloom.dao.LojaDAO;
import org.bcc.bloom.dao.VendedorDAO;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioService {

    private final VendedorDAO vendedorDAO = new VendedorDAO();
    private final CompradorDAO compradorDAO = new CompradorDAO();
    private final LojaDAO lojaDAO = new LojaDAO();



    public Comprador cadastrarComprador(String nome, String email, String senha, String cpf) {
        String emailLimpo = email == null ? null : email.trim();
        String cpfLimpo = apenasDigitos(cpf);


        validarDados(nome, emailLimpo, cpfLimpo, senha);
        garantirEmailLivre(emailLimpo);

        String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());
        Comprador comprador = new Comprador(nome.trim(), emailLimpo, senhaCriptografada, cpfLimpo);
        compradorDAO.salvar(comprador);
        return comprador;
    }

    public Vendedor cadastrarVendedor(String nome, String email, String senha, String cnpj) {
        String emailLimpo = email == null ? null : email.trim();
        String cnpjLimpo = apenasDigitos(cnpj);

        validarDadosVendedor(nome, emailLimpo, cnpjLimpo, senha);
        garantirEmailLivre(emailLimpo);

        String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());
        Vendedor vendedor = new Vendedor(nome.trim(), emailLimpo, senhaCriptografada, cnpjLimpo);
        vendedorDAO.salvarVendedor(vendedor);
        return vendedor;
    }




    public Vendedor cadastrar(String nome, String email, String senha, String cadastro, String tipo) {
        if ("Vendedor".equalsIgnoreCase(tipo)) {
            return cadastrarVendedor(nome, email, senha, cadastro);
        }
        if ("Comprador".equalsIgnoreCase(tipo)) {
            cadastrarComprador(nome, email, senha, cadastro);
            return null;
        }
        throw new ValorInvalidoException("Erro: Tipo de utilizador inválido: " + tipo);
    }




    public Usuario login(String email, String senha) {
        if (email == null || senha == null) {
            throw new CredenciaisInvalidasException("Credenciais inválidas! Verifique o e-mail e a senha.");
        }
        String emailLimpo = email.trim();

        Usuario usuario = compradorDAO.buscarPorEmail(emailLimpo);
        if (usuario == null) {
            Vendedor vendedor = vendedorDAO.buscarPorEmail(emailLimpo);
            if (vendedor != null) {
                vendedor.setLoja(lojaDAO.buscarPorVendedor(vendedor.getId()));
            }
            usuario = vendedor;
        }

        if (usuario == null || !senhaConfere(senha, usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Credenciais inválidas! Verifique o e-mail e a senha.");
        }
        return usuario;
    }

    private boolean senhaConfere(String senha, String hash) {
        try {
            return BCrypt.checkpw(senha, hash);
        } catch (IllegalArgumentException e) {

            return false;
        }
    }




    public void depositar(Usuario usuario, Long valor) {
        if (usuario == null) {
            throw new RecursoNaoEncontradoException("Erro: Usuário não informado.");
        }
        if (valor == null || valor <= 0) {
            throw new ValorInvalidoException("Erro: O valor do depósito deve ser maior que zero.");
        }

        long novoSaldo = usuario.getSaldo() + valor;


        if (usuario.getId() != null) {
            if (usuario instanceof Comprador) {
                compradorDAO.atualizarSaldo(usuario.getId(), novoSaldo);
            } else if (usuario instanceof Vendedor) {
                vendedorDAO.atualizarSaldo(usuario.getId(), novoSaldo);
            }
        }
        usuario.setSaldo(novoSaldo);
    }



    public void validarDados(String nome, String email, String cpf, String senha) {
        exigirCampos(nome, email, cpf, senha, "CPF");
        validarEmail(email);
        if (!cpfValido(cpf)) {
            throw new CpfInvalidoException("Erro: CPF inválido.");
        }
        validarSenha(senha);
    }

    public void validarDadosVendedor(String nome, String email, String cnpj, String senha) {
        exigirCampos(nome, email, cnpj, senha, "CNPJ");
        validarEmail(email);
        if (!cnpjValido(cnpj)) {
            throw new CnpjInvalidoException("Erro: CNPJ inválido.");
        }
        validarSenha(senha);
    }

    private void exigirCampos(String nome, String email, String documento, String senha, String rotuloDocumento) {
        if (nome == null || nome.isBlank()) {
            throw new CampoObrigatorioException("Erro: O nome é obrigatório.");
        }
        if (email == null || email.isBlank()) {
            throw new CampoObrigatorioException("Erro: O e-mail é obrigatório.");
        }
        if (documento == null || documento.isBlank()) {
            throw new CampoObrigatorioException("Erro: O " + rotuloDocumento + " é obrigatório.");
        }
        if (senha == null || senha.isBlank()) {
            throw new CampoObrigatorioException("Erro: A senha é obrigatória.");
        }
    }

    private void validarEmail(String email) {
        if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new EmailInvalidoException("Erro: E-mail inválido.");
        }
    }

    private void validarSenha(String senha) {
        if (senha.length() < 8
                || !senha.matches(".*[A-Z].*")
                || !senha.matches(".*\\d.*")
                || !senha.matches(".*[^A-Za-z0-9].*")) {
            throw new SenhaInvalidaException(
                    "Erro: A senha deve ter 8 ou mais caracteres, com letra maiúscula, número e símbolo.");
        }
    }

   
    private void garantirEmailLivre(String email) {
        if (compradorDAO.buscarPorEmail(email) != null || vendedorDAO.buscarPorEmail(email) != null) {
            throw new EmailJaCadastradoException("Erro: Já existe um utilizador registado com este e-mail.");
        }
    }


    private String apenasDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }

    private boolean cpfValido(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            return false;
        }
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }
        return cpf.charAt(9) - '0' == calcularDigito(cpf, 9)
                && cpf.charAt(10) - '0' == calcularDigito(cpf, 10);
    }

    private int calcularDigito(String cpf, int quantidade) {
        int soma = 0;
        int peso = quantidade + 1;
        for (int i = 0; i < quantidade; i++) {
            soma += (cpf.charAt(i) - '0') * peso;
            peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private boolean cnpjValido(String cnpj) {
        if (cnpj == null || !cnpj.matches("\\d{14}")) {
            return false;
        }
        if (cnpj.chars().distinct().count() == 1) {
            return false;
        }
        return cnpj.charAt(12) - '0' == calcularDigitoCnpj(cnpj, 12)
                && cnpj.charAt(13) - '0' == calcularDigitoCnpj(cnpj, 13);
    }

    private int calcularDigitoCnpj(String cnpj, int quantidade) {
        int soma = 0;
        int peso = quantidade - 7;
        for (int i = 0; i < quantidade; i++) {
            soma += (cnpj.charAt(i) - '0') * peso;
            peso--;
            if (peso < 2) {
                peso = 9;
            }
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
