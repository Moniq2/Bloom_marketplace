package org.bcc.bloom.model;

import java.util.ArrayList;

public class Vendedor extends Usuario {
    private String cnpj;
    private ArrayList<Loja> lojas;
    private byte[] perfil;

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

    public ArrayList<Loja> getLoja() {
        return lojas;
    }
    public void addLoja(Loja loja) {
        this.lojas.add(loja);
    }

    public void setPerfil(byte[] perfil) {
        this.perfil = perfil;
    }
    public byte[] getPerfil() {
        return perfil;
    }
}
