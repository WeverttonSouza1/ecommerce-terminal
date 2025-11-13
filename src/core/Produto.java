package core;

import java.math.BigDecimal;
import observer.Subject;
import observer.NotificacaoProduto;

public class Produto extends Subject {

	private Long id;
	private String nome;
	private String descricao;
	private BigDecimal preco;
	private int estoque;

	public Produto() {
	}

	public Produto(Long id, String nome, String descricao, BigDecimal preco, int estoque) {
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.preco = preco;
		this.estoque = estoque;
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

	public void setEstoque(int novoEstoque) {
		int estoqueAnterior = this.estoque;
		this.estoque = novoEstoque;

		// Notifica clientes se o produto voltar ao estoque
		if (estoqueAnterior == 0 && novoEstoque > 0) {
			notifyObservers(new NotificacaoProduto("O produto '" + nome + "' voltou ao estoque!"));
		}
	}

	@Override
	public String toString() {
		return String.format("ID: %d | %s | %s | R$ %s | Estoque: %d", id, nome, descricao, preco.toPlainString(),
				estoque);
	}
}
