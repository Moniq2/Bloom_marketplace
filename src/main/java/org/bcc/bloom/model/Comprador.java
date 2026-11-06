package org.bcc.bloom.model;

public class Comprador extends Usuario {
    private String endereco;
    private String cpf;
    private Long carrinho_id;

    public Comprador() {
        super();
    }

    public Comprador(String nome, String email, String senha, String cpf) {
        super(nome, email, senha);
        this.cpf = cpf;
    }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public Long getCarrinho_id() { return carrinho_id; }
    public void SetCarrinho_id(Long id) { this.carrinho_id = id; }
}