package org.bcc.bloom.model;

public abstract class Usuario {
    private String nome;
    private String email;
    private String senha;
    private Long id;
    private Long saldo = 0L;

    public Usuario(){}
    public Usuario(String nome, String email, String senha, Long id) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSaldo() {
        return saldo;
    }

    public void setSaldo(Long saldo) {
        this.saldo += saldo;
    }
}
