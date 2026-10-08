package org.bcc.bloom.model;

public class Vendedor extends Usuario {
    String cnpj;

    public Vendedor() {}
    public Vendedor(String nome, String email, String senha, Long id, String cnpj) {
        super(nome, email, senha, id);
        this.cnpj = cnpj;
    }

    String getCnpj() {
        return cnpj;
    }
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
