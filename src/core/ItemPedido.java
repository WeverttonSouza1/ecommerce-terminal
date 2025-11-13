package core;

import java.math.BigDecimal;

// Representa um item dentro de um pedido ou carrinho de compras.

public class ItemPedido {
	private Produto produto;
	private int quantidade;
	private BigDecimal precoUnitario;

	public ItemPedido() {
	}

	public ItemPedido(Produto produto, int quantidade) {
		this.produto = produto;
		this.quantidade = quantidade;
		this.precoUnitario = produto.getPreco();
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getPrecoUnitario() {
		return precoUnitario;
	}

	public void setPrecoUnitario(BigDecimal precoUnitario) {
		this.precoUnitario = precoUnitario;
	}

	public BigDecimal calcularSubtotal() {
		return precoUnitario.multiply(BigDecimal.valueOf(quantidade)); // Calcula o subtotal de um item do pedido
																	   // multiplicando o preço unitário
		                                                               // converter tipos numéricos primitivos ou wrappers
	}

	@Override
	public String toString() {
		return String.format("%s | Qtd: %d | Unit: R$ %s | Subtotal: R$ %s", produto.getNome(), quantidade,
				precoUnitario.toPlainString(), calcularSubtotal().toPlainString());
	}
}
