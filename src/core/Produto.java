package core;

import domain.Exibivel;

public class Produto extends EntidadeBase implements Exibivel { // Herança
    protected String nome;
    protected double preco;
    protected int quantidadeEstoque;

    public Produto(int id, String nome, double preco, int quantidadeEstoque) {
        super(id);
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public String getNome() {
    	return nome; 
    	}

    public double getPreco() {
    	return preco;
    	}
    
    public int getQuantidadeEstoque() {
    	return quantidadeEstoque;
    	}

    public void reduzirEstoque(int quantidade) {
        if (quantidade > quantidadeEstoque) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto: " + nome); // tratamento de erro
        }
        quantidadeEstoque -= quantidade;
    }

    public void devolverEstoque(int quantidade) {
        quantidadeEstoque += quantidade;
    }

    public String getDescricao() {
        return nome + " por R$" + preco;
    }

    @Override
    public void exibirInformacoes() {
        System.out.println(getDescricao() + " (Estoque: " + quantidadeEstoque + ")");
    }

    @Override
    public String toString() {
        return "ID: " + id + ", " + getDescricao() + ", Estoque: " + quantidadeEstoque;
    }
}
