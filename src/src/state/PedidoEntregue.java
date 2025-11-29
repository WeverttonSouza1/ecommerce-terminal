package state;

import core.Pedido;

public class PedidoEntregue implements PedidoState {
	private final Pedido pedido;

	public PedidoEntregue(Pedido pedido) {
		this.pedido = pedido;
	}

	@Override
	public void exibirStatus() {
		System.out.println("Pedido entregue ao cliente.");
	}
}
