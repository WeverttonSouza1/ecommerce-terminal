package factoryMethod;

public class PagamentoFactory {
	public static PagamentoInterface criarPagamento(String tipo) {
		if (tipo == null)
			return null;
		switch (tipo.toLowerCase()) {
		case "pix":
			return new PagamentoPix();
		case "cartao":
			return new PagamentoCartao();
		case "boleto":
			return new PagamentoBoleto();
		default:
			return null;
		}
	}
}
