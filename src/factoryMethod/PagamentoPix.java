package factoryMethod;

import java.math.BigDecimal;

public class PagamentoPix implements PagamentoInterface {
	@Override
	public boolean processarPagamento(BigDecimal valor) {
		System.out.println("Processando Pix de R$ " + valor.toPlainString());
		return true;
	}

	@Override
	public String getNome() {
		return "PIX";
	}
}
