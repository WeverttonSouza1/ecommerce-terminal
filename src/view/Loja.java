package view;

import java.util.Optional;
import java.util.Scanner;

import core.Produto;
import repository.GerenciadorDeArquivos;
import repository.UsuarioRepository;
import user.Administrador;
import user.Cliente;
import user.Usuario;

public class Loja {

    private final Scanner sc = new Scanner(System.in);
    private final UsuarioRepository usuarioRepo = new UsuarioRepository();

    public static void main(String[] args) {
        new Loja().iniciar();
    }

    public void iniciar() {
        while (true) {
            System.out.println("\n=== BEM-VINDO À LOJA ===");
            System.out.println("1 - Entrar");
            System.out.println("2 - Cadastrar-se");
            System.out.println("3 - Ver Catálogo (sem login)");
            System.out.println("4 - Sair");
            System.out.print("Escolha: ");
            String op = sc.nextLine();

            switch (op) {
                case "1" -> login();
                case "2" -> cadastrar();
                case "3" -> verCatalogo();
                case "4" -> {
                    System.out.println("Saindo...");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void login() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Senha: ");
        String senha = sc.nextLine();

        Optional<Usuario> opUser = usuarioRepo.buscarPorEmail(email);
        if (opUser.isEmpty()) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        Usuario user = opUser.get();
        if (!user.getSenha().equals(senha)) {
            System.out.println("Senha incorreta.");
            return;
        }

        if (user instanceof Administrador admin) { // instanceof simplifica a verificação e a conversão de tipo
            new PainelAdministrador(admin, usuarioRepo).iniciar();
        } else if (user instanceof Cliente cliente) {
            new MenuCliente(cliente, usuarioRepo).iniciar();
        } else {
            System.out.println("Tipo de usuário desconhecido.");
        }
    }

    private void cadastrar() {
        System.out.println("\n=== CADASTRO DE CLIENTE ===");
        Cliente cliente = new Cliente();
        System.out.print("Nome: ");
        cliente.setNome(sc.nextLine());
        System.out.print("Email: ");
        cliente.setEmail(sc.nextLine());
        System.out.print("Senha: ");
        cliente.setSenha(sc.nextLine());
        System.out.print("Endereço de entrega: ");
        cliente.setEndereco(sc.nextLine());

        usuarioRepo.salvarUsuario(cliente);
        System.out.println("\nCadastro concluído com sucesso!");
    }

    private void verCatalogo() {
        System.out.println("\n=== CATÁLOGO (Acesso público) ===");
        GerenciadorDeArquivos arquivo = new GerenciadorDeArquivos();
        var produtos = arquivo.carregarProdutos();
        
        var produtosAtivos = produtos.stream().filter(Produto::isAtivo).toList();

        if (produtosAtivos.isEmpty()) {
            System.out.println("Nenhum produto disponível.");
        } else {
            produtosAtivos.forEach(System.out::println);
        }
        System.out.println("------------------------------");
    }
}