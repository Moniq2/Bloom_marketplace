package org.bcc.bloom.model;

import java.util.ArrayList;

public class Vendedor extends Usuario {
    private String cnpj;
    private ArrayList<Loja> lojas;
    private byte[] perfil;

    public Vendedor() {
        super();
        this.lojas = new ArrayList<>();
    }

    public Vendedor(String nome, String email, String senha, Long id, String cnpj) {
        super(nome, email, senha, id);
        this.cnpj = cnpj;
        this.lojas = new ArrayList<>();
    }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public ArrayList<Loja> getLojas() { return lojas; }
    public void setLojas(ArrayList<Loja> lojas) { this.lojas = lojas; }

    public byte[] getPerfil() { return perfil; }
    public void setPerfil(byte[] perfil) { this.perfil = perfil; }
}