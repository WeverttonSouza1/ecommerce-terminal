package repository;

import user.Cliente;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public class MensagemRepository {

    private final Path pastaBase = Paths.get("src", "dados");

    public MensagemRepository() {
        try {
            if (!Files.exists(pastaBase)) Files.createDirectories(pastaBase);
        } catch (IOException e) {
            System.err.println("Erro ao criar pasta de mensagens: " + e.getMessage());
        }
    }

    private Path arquivoCliente(Cliente c) {
        return pastaBase.resolve("mensagens_cliente_" + c.getId() + ".txt");
    }

    public void enviarMensagem(Cliente cliente, String remetente, String conteudo) {
        Path arquivo = arquivoCliente(cliente);
        try (BufferedWriter bw = Files.newBufferedWriter(arquivo, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write("Remetente: " + remetente);
            bw.newLine();
            bw.write("Data: " + LocalDateTime.now());
            bw.newLine();
            bw.write("Mensagem: " + conteudo);
            bw.newLine();
            bw.write("------------------------------------------------");
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
        }
    }

    public List<String> listarMensagens(Cliente cliente) {
        Path arquivo = arquivoCliente(cliente);
        List<String> mensagens = new ArrayList<>();
        if (!Files.exists(arquivo)) return mensagens;

        try {
            mensagens = Files.readAllLines(arquivo);
        } catch (IOException e) {
            System.err.println("Erro ao ler mensagens: " + e.getMessage());
        }
        return mensagens;
    }

    public void limparMensagens(Cliente cliente) {
        Path arquivo = arquivoCliente(cliente);
        try {
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            System.err.println("Erro ao limpar mensagens: " + e.getMessage());
        }
    }
}
