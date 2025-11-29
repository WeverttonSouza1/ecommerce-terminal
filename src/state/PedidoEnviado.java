package state;

import core.Pedido;

public class PedidoEnviado implements PedidoState {
	private final Pedido pedido;

	public PedidoEnviado(Pedido pedido) {
		this.pedido = pedido;
	}

	@Override
	public void exibirStatus() {
		System.out.println("Pedido enviado.");
	}
}
