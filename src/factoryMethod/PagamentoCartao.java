package factoryMethod;

import java.math.BigDecimal;

public class PagamentoCartao implements PagamentoInterface {
	@Override
	public boolean processarPagamento(BigDecimal valor) {
		System.out.println("Processando Cartão de R$ " + valor.toPlainString()); // o .toPlainString() converte um valor BigDecimal em String
		return true;
	}

	@Override
	public String getNome() {
		return "CARTAO";
	}
}
