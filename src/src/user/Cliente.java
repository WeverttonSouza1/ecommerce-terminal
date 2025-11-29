package user;

import observer.Observer;
import observer.NotificacaoProduto;
import repository.MensagemRepository;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario implements Observer {
	private String endereco;
	private List<Long> favoritos = new ArrayList<>();

	private MensagemRepository mensagemRepo = new MensagemRepository();

	public Cliente() {
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	@Override
	public void update(NotificacaoProduto msg) {
		// ao receber notificação, grava como mensagem
		mensagemRepo.enviarMensagem(this, "Sistema", msg.getMensagem());
	}
}
