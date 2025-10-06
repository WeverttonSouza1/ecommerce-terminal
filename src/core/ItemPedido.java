package core;

public class ItemPedido {
	private Produto produto;
	private int quantidade;

	public ItemPedido(Produto produto, int quantidade) {
		this.produto = produto;
		this.quantidade = quantidade;
		produto.reduzirEstoque(quantidade);
	}

	public Produto getProduto() {
		return produto;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public double getSubtotal() {
		return produto.getPreco() * quantidade;
	}
	
	public void diminuirQuantidade(int quantidade) {
	    if (quantidade >= this.quantidade) {
	        this.quantidade = 0;
	    } else {
	        this.quantidade -= quantidade;
	    }
	}
	
	@Override
	public String toString() {
		return "ID: " + produto.getId() + ", "+ produto.getNome() + " - Qtd: " + quantidade + " - Subtotal: R$" + String.format("%.2f", getSubtotal()); // manipulação de String
	}
}
