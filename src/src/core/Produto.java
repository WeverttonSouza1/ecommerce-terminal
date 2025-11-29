package core;

import java.math.BigDecimal;

public class Produto {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int estoque;
    private String status = "ATIVO";

    public Produto() {
    }

    public Produto(Long id, String nome, String descricao, BigDecimal preco, int estoque) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
        this.status = "ATIVO"; // construtor para criação de novos produtos
    }

    public Produto(Long id, String nome, String descricao, BigDecimal preco, int estoque, String status) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    // Verifica se o produto está ativo para venda
    public boolean isAtivo() {
        return "ATIVO".equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        String estadoStr = isAtivo() ? "" : " [INDISPONÍVEL]";
        return String.format("ID: %d | %s | %s | R$ %s | Estoque: %d%s", 
                id, nome, descricao, preco.toPlainString(), estoque, estadoStr);
    }
}