package org.bcc.bloom.service;

import org.bcc.bloom.exception.CampoObrigatorioException;
import org.bcc.bloom.exception.CpfInvalidoException;
import org.bcc.bloom.exception.CredenciaisInvalidasException;
import org.bcc.bloom.exception.EmailInvalidoException;
import org.bcc.bloom.exception.EmailJaCadastradoException;
import org.bcc.bloom.exception.SenhaInvalidaException;
import org.bcc.bloom.exception.ValorInvalidoException;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Usuario;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.repository.VendedorRepository;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioService {

    private final VendedorRepository vendedorRepository = new VendedorRepository();

    //O parâmetro cadastro refere-se a de pessoa física(CPF) ou pessoa jurídica(CNPJ)
    public void cadastrar(String nome, String email, String senha, String cadastro, String tipo){
        if (vendedorRepository.buscarPorEmail(email) != null) {
            throw new EmailJaCadastradoException("Erro: Já existe um utilizador registado com este e-mail.");
        }
        if (tipo.equalsIgnoreCase("Vendedor")) {
            String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());
            Vendedor vendedor = new Vendedor(nome, email, senhaCriptografada, cadastro);
            vendedorRepository.salvarVendedor(vendedor);
        }
        else if (tipo.equalsIgnoreCase("Comprador")) {
            validarDados(nome, email, cadastro, senha);
            String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());
            Comprador comprador = new Comprador(nome, email, senhaCriptografada, cadastro);
            vendedorRepository.salvarComprador(comprador);
        }
    }

    public Usuario login(String email, String senha) {
        Usuario usuario = vendedorRepository.buscarPorEmail(email);
        if (usuario == null || senha == null || !BCrypt.checkpw(senha, usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Credenciais inválidas! Verifique o e-mail e a senha.");
        }
        return usuario;
    }

    public void depositar(Usuario usuario, Long valor) {
        if (usuario == null) {
            throw new NullPointerException("Usuario não pode ser nulo.");
        }
        if (valor == null || valor <= 0) {
            throw new ValorInvalidoException("Erro: O valor do depósito deve ser maior que zero.");
        }
        usuario.setSaldo(usuario.getSaldo() + valor);
    }

    public void validarDados(String nome, String email, String cpf, String senha) {
        if (nome == null || nome.isBlank()) {
            throw new CampoObrigatorioException("Erro: O nome é obrigatório.");
        }
        if (email == null || email.isBlank()) {
            throw new CampoObrigatorioException("Erro: O e-mail é obrigatório.");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new CampoObrigatorioException("Erro: O CPF é obrigatório.");
        }
        if (senha == null || senha.isBlank()) {
            throw new CampoObrigatorioException("Erro: A senha é obrigatória.");
        }

        if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new EmailInvalidoException("Erro: E-mail inválido.");
        }

        if (!cpfValido(cpf)) {
            throw new CpfInvalidoException("Erro: CPF inválido.");
        }

        if (senha.length() < 8
                || !senha.matches(".*[A-Z].*")
                || !senha.matches(".*\\d.*")
                || !senha.matches(".*[^A-Za-z0-9].*")) {
            throw new SenhaInvalidaException(
                    "Erro: A senha deve ter 8 ou mais caracteres, com letra maiúscula, número e símbolo.");
        }
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
}