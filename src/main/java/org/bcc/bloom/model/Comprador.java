package org.bcc.bloom.model;

public class Comprador extends Usuario {
    String cpf;

    public Comprador() {}
    public Comprador(String nome, String email, String senha, Long id, String cpf) {
        super(nome, email, senha, id);
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
