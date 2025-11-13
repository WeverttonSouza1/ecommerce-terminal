package view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import core.Pedido;
import core.Produto;
import repository.GerenciadorDeArquivos;
import repository.MensagemRepository;
import repository.UsuarioRepository;
import user.Administrador;
import user.Cliente;
import user.Usuario;

public class PainelAdministrador {

    private final Administrador admin;
    private final GerenciadorDeArquivos arquivo = new GerenciadorDeArquivos();
    private final MensagemRepository msgRepo = new MensagemRepository();
    private final UsuarioRepository usuarioRepo;
    private final Scanner sc = new Scanner(System.in);

    public PainelAdministrador(Administrador admin, UsuarioRepository usuarioRepo) {
        this.admin = admin;
        this.usuarioRepo = usuarioRepo;
    }

    public void iniciar() {
        while (true) {
            System.out.println("\n=== PAINEL ADMINISTRADOR: " + admin.getNome() + " ===");
            System.out.println("1 - Cadastrar produto");
            System.out.println("2 - Editar produto");
            System.out.println("3 - Remover produto");
            System.out.println("4 - Listar produtos");
            System.out.println("5 - Listar pedidos");
            System.out.println("6 - Alterar status de pedido");
            System.out.println("7 - Enviar mensagem a cliente");
            System.out.println("8 - Sair");
            System.out.print("Escolha: ");
            String op = sc.nextLine();

            switch (op) {
                case "1" -> cadastrarProduto();
                case "2" -> editarProduto();
                case "3" -> removerProduto();
                case "4" -> listarProdutos();
                case "5" -> listarPedidos();
                case "6" -> alterarStatusPedido();
                case "7" -> enviarMensagemCliente();
                case "8" -> {
                    System.out.println("Saindo do painel do administrador...");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    // ===========================
    // PRODUTOS
    // ===========================

    private void cadastrarProduto() {
        System.out.println("\n=== CADASTRAR PRODUTO ===");
        try {
            System.out.print("Nome: ");
            String nome = sc.nextLine();

            System.out.print("Descrição: ");
            String descricao = sc.nextLine();

            System.out.print("Preço (ex: 29.90): R$ ");
            BigDecimal preco = new BigDecimal(sc.nextLine());

            System.out.print("Estoque inicial: ");
            int estoque = Integer.parseInt(sc.nextLine());

            Produto p = new Produto(null, nome, descricao, preco, estoque);
            arquivo.salvarProduto(p);

            System.out.println("✅ Produto cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar produto: " + e.getMessage());
        }
    }

    private void editarProduto() {
        System.out.println("\n=== EDITAR PRODUTO ===");
        try {
            listarProdutos();
            System.out.print("ID do produto: ");
            Long id = Long.parseLong(sc.nextLine());

            Optional<Produto> op = arquivo.buscarProdutoPorId(id);
            if (op.isEmpty()) {
                System.out.println("Produto não encontrado.");
                return;
            }
            
            Produto p = op.get();
            System.out.println("\nProduto atual: " + p);
            
            System.out.println("Deixe em branco para manter o valor atual.");

            System.out.print("Novo nome (" + p.getNome() + "): ");
            String nome = sc.nextLine();
            if (!nome.isBlank()) p.setNome(nome);

            System.out.print("Nova descrição (" + p.getDescricao() + "): ");
            String desc = sc.nextLine();
            if (!desc.isBlank()) p.setDescricao(desc);

            System.out.print("Novo preço (" + p.getPreco() + "): R$ ");
            String precoStr = sc.nextLine();
            if (!precoStr.isBlank()) p.setPreco(new BigDecimal(precoStr));

            System.out.print("Novo estoque (" + p.getEstoque() + "): ");
            String estStr = sc.nextLine();
            if (!estStr.isBlank()) p.setEstoque(Integer.parseInt(estStr));

            arquivo.salvarProduto(p);
            System.out.println("✅ Produto atualizado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao editar produto: " + e.getMessage());
        }
    }

    private void removerProduto() {
        System.out.println("\n=== REMOVER PRODUTO ===");
        try {
            listarProdutos();
            System.out.print("ID do produto: ");
            Long id = Long.parseLong(sc.nextLine());
            arquivo.deletarProduto(id);
            System.out.println("✅ Produto removido com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao remover produto: " + e.getMessage());
        }
    }

    private void listarProdutos() {
        System.out.println("\n=== LISTA DE PRODUTOS ===");
        List<Produto> produtos = arquivo.carregarProdutos();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            produtos.forEach(System.out::println);
        }
    }

    // ===========================
    // PEDIDOS
    // ===========================

    private void listarPedidos() {
        System.out.println("\n=== LISTA DE PEDIDOS ===");
        List<Pedido> pedidos = arquivo.carregarPedidos();
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
        } else {
            pedidos.forEach(System.out::println);
        }
    }

    private void alterarStatusPedido() {
        System.out.println("\n=== ALTERAR STATUS DO PEDIDO ===");
        listarPedidos();

        System.out.print("Digite o ID do pedido: ");
        Long id = Long.parseLong(sc.nextLine());

        Optional<Pedido> op = arquivo.buscarPedidoPorId(id);
        if (op.isEmpty()) {
            System.out.println("Pedido não encontrado.");
            return;
        }

        Pedido pedido = op.get();
        System.out.println("Status atual: " + pedido.getStatus());
        System.out.println("1 - Enviado");
        System.out.println("2 - Entregue");
        System.out.println("3 - Cancelado");
        System.out.print("Escolha o novo status: ");
        String opcao = sc.nextLine();

        String novoStatus = switch (opcao) {
            case "1" -> "ENVIADO";
            case "2" -> "ENTREGUE";
            case "3" -> "CANCELADO";
            default -> null;
        };

        if (novoStatus == null) {
            System.out.println("Opção inválida.");
            return;
        }

        pedido.mudarEstadoPara(novoStatus);
        arquivo.salvarPedidoComStatus(pedido);

        Optional<Usuario> clienteOp = usuarioRepo.buscarPorId(pedido.getClienteId());
        if (clienteOp.isPresent() && clienteOp.get() instanceof Cliente c) {
            String mensagem = "Seu pedido #" + pedido.getId() + " agora está com status: " + novoStatus;
            msgRepo.enviarMensagem(c, "Administrador", mensagem);
        }

        System.out.println("✅ Status do pedido atualizado e cliente notificado!");
    }

    // ===========================
    // MENSAGENS 
    // ===========================

    private void enviarMensagemCliente() {
        System.out.println("\n=== ENVIAR MENSAGEM PARA CLIENTE ===");
        try {
            System.out.print("ID do cliente: ");
            Long idCliente = Long.parseLong(sc.nextLine());

            Optional<Usuario> op = usuarioRepo.buscarPorId(idCliente);
            if (op.isEmpty() || !(op.get() instanceof Cliente cliente)) {
                System.out.println("Cliente não encontrado.");
                return;
            }

            System.out.print("Mensagem: ");
            String mensagem = sc.nextLine();

            msgRepo.enviarMensagem(cliente, "Administrador (" + admin.getNome() + ")", mensagem);
            System.out.println("✅ Mensagem enviada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem: " + e.getMessage());
        }
    }
}
