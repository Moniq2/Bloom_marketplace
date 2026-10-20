package org.bcc.bloom.model;

public class Comprador extends Usuario {
    private String endereco;
    public Comprador() {
        super();
    }

    public Comprador(String nome, String email, String senha, Long id, String endereco, String cpf) {
        super(nome, email, senha, id, cpf);
        this.endereco = endereco;
    }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
}