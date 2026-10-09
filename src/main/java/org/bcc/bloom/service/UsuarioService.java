package org.bcc.bloom.service;

import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.model.Usuario;
import org.bcc.bloom.repository.UsuarioRepository;

public class UsuarioService {

    public Comprador cadastrarComprador(String nome, String email, String senha, Long id, String cpf, String endereco) {
        if (UsuarioRepository.buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("Erro: Já existe um utilizador registado com este e-mail.");
        }
        Comprador comprador = new Comprador(nome, email, senha, id, cpf, endereco);
        UsuarioRepository.salvarComprador(comprador);
        return comprador;
    }

    public Vendedor cadastrarVendedor(String nome, String email, String senha, Long id, String cnpj) {
        if (UsuarioRepository.buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("Erro: Já existe um utilizador registado com este e-mail.");
        }
        Vendedor vendedor = new Vendedor(nome, email, senha, id, cnpj);
        UsuarioRepository.salvarVendedor(vendedor);
        return vendedor;
    }

    public Usuario login(String email, String senha) {
        Usuario usuario = UsuarioRepository.buscarUsuario(email, senha);
        if (usuario == null) {
            throw new SecurityException("Credenciais inválidas! Verifique o e-mail e a senha.");
        }
        return usuario;
    }

    public void depositar(Usuario usuario, Long valor) {
        if (usuario == null) {
            throw new IllegalArgumentException("Erro: Utilizador não pode ser nulo.");
        }
        if (valor == null || valor <= 0) {
            throw new IllegalArgumentException("Erro: O valor do depósito deve ser maior que zero.");
        }
        usuario.setSaldo(usuario.getSaldo() + valor);
    }
}