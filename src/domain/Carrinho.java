package domain;

import core.ItemPedido;
import core.Produto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Carrinho {
	private List<ItemPedido> itens = new ArrayList<>();

	public void adicionar(Produto produto, int qtd) {
		ItemPedido itemProduto = new ItemPedido(produto, qtd);
		itens.add(itemProduto);
	}

	public void removerPorProdutoId(Long produtoId) {
		itens.removeIf(i -> i.getProduto().getId().equals(produtoId));
	}

	public BigDecimal total() {
	    BigDecimal total = BigDecimal.ZERO;
	    for (ItemPedido item : itens) {
	        total = total.add(item.calcularSubtotal());
	    }
	    return total;
	}

	public List<ItemPedido> getItens() {
		return itens;
	}

	public void limpar() {
		itens.clear();
	}
	
	public void exibirCarrinho() {
	    if (itens.isEmpty()) {
	        System.out.println("Seu carrinho está vazio.");
	        return;
	    }

	    System.out.println("=== SEU CARRINHO ===");
	    for (ItemPedido item : itens) {
	        System.out.println("- " + item.getProduto().getNome()
	            + " | Qtd: " + item.getQuantidade()
	            + " | Preço: R$ " + item.getPrecoUnitario()
	            + " | Subtotal: R$ " + item.calcularSubtotal());
	    }

	    System.out.println("Total: R$ " + total());
	}
}
