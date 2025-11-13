package state;

import core.Pedido;

public class PedidoProcessando implements PedidoState {
	private final Pedido pedido;

	public PedidoProcessando(Pedido pedido) {
		this.pedido = pedido;
	}

	@Override
	public void exibirStatus() {
		System.out.println("Pedido em processamento.");
	}
}
