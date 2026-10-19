package org.bcc.bloom.service;

import org.bcc.bloom.exception.RegraNegocioException;
import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Usuario;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    public Comprador cadastrarComprador(String nome, String email, String senha, String cpf ){
        validarDados(nome, email, cpf, senha);
        if (usuarioRepository.buscarPorEmail(email) != null) {
            throw new RegraNegocioException("Erro: Já existe um utilizador registado com este e-mail.");
        }
        String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());
        Comprador comprador = new Comprador(nome, email, senhaCriptografada, null, cpf, null);
        usuarioRepository.salvarComprador(comprador);
        return comprador;
    }

    public Vendedor cadastrarVendedor(String nome, String email, String senha, Long id, String cnpj) {
        if (usuarioRepository.buscarPorEmail(email) != null) {
            throw new RegraNegocioException("Erro: Já existe um utilizador registado com este e-mail.");
        }
        String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());
        Vendedor vendedor = new Vendedor(nome, email, senhaCriptografada, id, cnpj);
        usuarioRepository.salvarVendedor(vendedor);
        return vendedor;
    }

    //USAR BCRYPT
    public Usuario login(String email, String senha) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email);
        if (usuario == null || senha == null || !BCrypt.checkpw(senha, usuario.getSenha())) {
            throw new RegraNegocioException("Credenciais inválidas! Verifique o e-mail e a senha.");
        }
        return usuario;
    }

    public void depositar(Usuario usuario, Long valor) {
        if (usuario == null) {
            throw new RegraNegocioException("Erro: Utilizador não pode ser nulo.");
        }
        if (valor == null || valor <= 0) {
            throw new RegraNegocioException("Erro: O valor do depósito deve ser maior que zero.");
        }
        usuario.setSaldo(usuario.getSaldo() + valor);
    }

    //CRIAR EXCECOES PERSONALIZADAS
    public void validarDados(String nome, String email, String cpf, String senha) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Erro: O nome é obrigatório.");
        }
        if (email == null || email.isBlank()) {
            throw new RegraNegocioException("Erro: O e-mail é obrigatório.");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new RegraNegocioException("Erro: O CPF é obrigatório.");
        }
        if (senha == null || senha.isBlank()) {
            throw new RegraNegocioException("Erro: A senha é obrigatória.");
        }

        if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new RegraNegocioException("Erro: E-mail inválido.");
        }

        if (!cpfValido(cpf)) {
            throw new RegraNegocioException("Erro: CPF inválido.");
        }

        if (senha.length() < 8
                || !senha.matches(".*[A-Z].*")
                || !senha.matches(".*\\d.*")
                || !senha.matches(".*[^A-Za-z0-9].*")) {
            throw new RegraNegocioException(
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