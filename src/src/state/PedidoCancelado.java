package state;

import core.Pedido;

public class PedidoCancelado implements PedidoState {
	private final Pedido pedido;

	public PedidoCancelado(Pedido pedido) {
		this.pedido = pedido;
	}

	@Override
	public void exibirStatus() {
		System.out.println("Pedido cancelado.");
	}
}
