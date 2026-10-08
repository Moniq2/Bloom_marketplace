package org.bcc.bloom.model;

public class Comprador extends Usuario {
    private String cpf;
    private String endereco;

    public Comprador() {}
    public Comprador(String nome, String email, String senha, Long id, String cpf, String endereco) {
        super(nome, email, senha, id);
        this.cpf = cpf;
        this.endereco = endereco;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
