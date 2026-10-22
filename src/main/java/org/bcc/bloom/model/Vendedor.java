package org.bcc.bloom.model;

public class Vendedor extends Usuario {
    private Loja loja;
    private byte[] perfil;
    private String cnpj;

    public Vendedor() {
        super();
    }

    public Vendedor(String nome, String email, String senha, String cnpj) {
        super(nome, email, senha);
        this.cnpj = cnpj;
    }

    public Loja getLoja() { return loja; }
    public void setLoja(Loja loja) { this.loja = loja; }

    public byte[] getPerfil() { return perfil; }
    public void setPerfil(byte[] perfil) { this.perfil = perfil; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
}