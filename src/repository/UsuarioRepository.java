package repository;

import user.Usuario;
import user.Cliente;
import user.Administrador;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class UsuarioRepository {

    private final Path pastaBase = Paths.get("src", "dados");
    private final Path usuariosFile = pastaBase.resolve("usuarios.txt");
    private final AtomicLong idGen;

    public UsuarioRepository() {
        try {
            if (!Files.exists(pastaBase)) Files.createDirectories(pastaBase);
            if (!Files.exists(usuariosFile)) Files.createFile(usuariosFile);
        } catch (IOException e) {
            System.err.println("Erro ao inicializar UsuarioRepository: " + e.getMessage());
        }
        idGen = new AtomicLong(loadLastId());
    }

    private long loadLastId() { // evitar repetição de id e manter a ordem
        if (!Files.exists(usuariosFile)) return 0;
        long max = 0;
        try (BufferedReader br = Files.newBufferedReader(usuariosFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] cols = line.split(";");
                long id = Long.parseLong(cols[0]);
                if (id > max) max = id;
            }
        } catch (Exception ignored) {}
        return max;
    }

    public void salvarUsuario(Usuario u) {
        try {
            if (u.getId() == null) u.setId(idGen.incrementAndGet());

            List<Usuario> todos = listarUsuarios();
            boolean atualizado = false;

            for (int i = 0; i < todos.size(); i++) {
                if (Objects.equals(todos.get(i).getId(), u.getId())) {
                    todos.set(i, u);
                    atualizado = true;
                    break;
                }
            }

            if (!atualizado) todos.add(u);

            try (BufferedWriter bw = Files.newBufferedWriter(usuariosFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Usuario us : todos) {
                    String tipo = (us instanceof Administrador) ? "ADMIN" : "CLIENTE";
                    String endereco = (us instanceof Cliente) ? ((Cliente) us).getEndereco() : "";
                    bw.write(String.format("%d;%s;%s;%s;%s;%s%n",
                            us.getId(),
                            us.getNome(),
                            us.getEmail(),
                            us.getSenha(),
                            tipo,
                            endereco));
                }
            }

            // Garante que exista arquivo individual do cliente (se for cliente)
            if (u instanceof Cliente c) {
                Path userFile = pastaBase.resolve("usuario_" + c.getId() + ".txt");
                if (!Files.exists(userFile)) Files.createFile(userFile);
                Path pedidosFile = pastaBase.resolve("pedidos_cliente_" + c.getId() + ".txt");
                if (!Files.exists(pedidosFile)) Files.createFile(pedidosFile);
                Path msgFile = pastaBase.resolve("mensagens_cliente_" + c.getId() + ".txt");
                if (!Files.exists(msgFile)) Files.createFile(msgFile);
            }

        } catch (IOException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
        }
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return listarUsuarios().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return listarUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        if (!Files.exists(usuariosFile)) return lista;

        try (BufferedReader br = Files.newBufferedReader(usuariosFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] cols = line.split(";");
                if (cols.length < 5) continue;

                Long id = Long.parseLong(cols[0]);
                String nome = cols[1];
                String email = cols[2];
                String senha = cols[3];
                String tipo = cols[4];
                String endereco = (cols.length > 5) ? cols[5] : "";

                Usuario u;
                if ("ADMIN".equalsIgnoreCase(tipo)) {
                    u = new Administrador();
                } else {
                    Cliente c = new Cliente();
                    c.setEndereco(endereco);
                    u = c;
                }

                u.setId(id);
                u.setNome(nome);
                u.setEmail(email);
                u.setSenha(senha);
                lista.add(u);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
        }

        return lista;
    }
}
