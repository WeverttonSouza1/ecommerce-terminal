package factoryMethod;

import java.math.BigDecimal;

public interface PagamentoInterface {
	boolean processarPagamento(BigDecimal valor);

	String getNome();
}
