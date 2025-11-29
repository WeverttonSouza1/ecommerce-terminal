package view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import core.Pedido;
import core.Produto;
import observer.NotificacaoProduto;
import repository.GerenciadorDeArquivos;
import repository.MensagemRepository;
import repository.UsuarioRepository;
import repository.ObserverRepository;
import user.Administrador;
import user.Cliente;
import user.Usuario;

public class PainelAdministrador {

    private final Administrador admin;
    private final GerenciadorDeArquivos arquivo = new GerenciadorDeArquivos();
    private final MensagemRepository msgRepo = new MensagemRepository();
    private final UsuarioRepository usuarioRepo;
    private final ObserverRepository observerRepo = new ObserverRepository();
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

            // TRATAMENTO DE ERRO: InputUtils garante que não quebre
            BigDecimal preco = InputUtils.lerBigDecimal(sc, "Preço (ex: 29.90): R$ ");
            int estoque = InputUtils.lerInt(sc, "Estoque inicial: ");

            Produto p = new Produto(null, nome, descricao, preco, estoque, "ATIVO");
            arquivo.salvarProduto(p);

            System.out.println("Produto cadastrado com sucesso!");
        } catch (Exception e) {
            // Tratamento genérico caso ocorra erro inesperado na gravação
            System.out.println("Erro inesperado ao cadastrar produto: " + e.getMessage());
        }
    }

    private void editarProduto() {
        System.out.println("\n=== EDITAR PRODUTO ===");
        try {
        	
            listarProdutos();
            Long id = InputUtils.lerLong(sc, "ID do produto: ");

            Optional<Produto> op = arquivo.buscarProdutoPorId(id);
            if (op.isEmpty()) {
                System.out.println("Produto não encontrado.");
                return;
            }
            
            Produto p = op.get();
            int estoqueAntigo = p.getEstoque();

            System.out.println("\nProduto atual: " + p);
            System.out.println("* Deixe em branco para manter o valor atual.");

            System.out.print("Novo nome (" + p.getNome() + "): ");
            String nome = sc.nextLine();
            if (!nome.isBlank()) p.setNome(nome);

            System.out.print("Nova descrição (" + p.getDescricao() + "): ");
            String desc = sc.nextLine();
            if (!desc.isBlank()) p.setDescricao(desc);

            // TRATAMENTO DE ERRO: Lógica específica para campo opcional
            System.out.print("Novo preço (" + p.getPreco() + "): R$ ");
            String precoStr = sc.nextLine();
            if (!precoStr.isBlank()) {
                try {
                    p.setPreco(new BigDecimal(precoStr.replace(",", ".")));
                } catch (NumberFormatException e) {
                    System.out.println("Formato de preço inválido. Valor mantido.");
                }
            }

            // TRATAMENTO DE ERRO: Lógica específica para campo opcional
            System.out.print("Novo estoque (" + p.getEstoque() + "): ");
            String estoqueStr = sc.nextLine();
            if (!estoqueStr.isBlank()) {
                try {
                    p.setEstoque(Integer.parseInt(estoqueStr));
                } catch (NumberFormatException e) {
                    System.out.println("Formato de estoque inválido. Valor mantido.");
                }
            }
            
            System.out.print("Ativar produto removido? (S/N): ");
            String reativar = sc.nextLine();
            if (reativar.equalsIgnoreCase("S")) p.setStatus("ATIVO");

            arquivo.salvarProduto(p);
            System.out.println("Produto atualizado com sucesso!");

            if (estoqueAntigo == 0 && p.getEstoque() > 0) {
                notificarObservadores(p);
            }

        } catch (Exception e) {
            System.out.println("Erro ao editar produto: " + e.getMessage());
        }
    }

    private void notificarObservadores(Produto p) {
        List<Long> idsClientes = observerRepo.recuperarObservadores(p.getId());
        if (idsClientes.isEmpty()) return;

        System.out.println("Notificando " + idsClientes.size() + " clientes interessados...");
        
        List<Cliente> clientes = usuarioRepo.buscarClientesPorIds(idsClientes);
        NotificacaoProduto notificacao = new NotificacaoProduto("O produto '" + p.getNome() + "' voltou ao estoque!");

        for (Cliente c : clientes) {
            c.update(notificacao);
        }

        observerRepo.removerObservadoresDoProduto(p.getId());
    }

    private void removerProduto() {
        System.out.println("\n=== REMOVER PRODUTO ===");
        try {
            listarProdutos();
            // TRATAMENTO DE ERRO
            Long id = InputUtils.lerLong(sc, "ID do produto: ");
            
            arquivo.deletarProduto(id);
            System.out.println("Produto marcado como REMOVIDO (ainda visível em históricos).");
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

        // TRATAMENTO DE ERRO
        Long id = InputUtils.lerLong(sc, "Digite o ID do pedido: ");

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
        System.out.println("Status do pedido atualizado e cliente notificado!");
    }
    // ===========================
    // MENSAGENS 
    // ===========================

    private void enviarMensagemCliente() {
        System.out.println("\n=== ENVIAR MENSAGEM PARA CLIENTE ===");
        try {
            // TRATAMENTO DE ERRO
            Long idCliente = InputUtils.lerLong(sc, "ID do cliente: ");

            Optional<Usuario> op = usuarioRepo.buscarPorId(idCliente);
            if (op.isEmpty() || !(op.get() instanceof Cliente cliente)) {
                System.out.println("Cliente não encontrado.");
                return;
            }

            System.out.print("Mensagem: ");
            String mensagem = sc.nextLine();

            msgRepo.enviarMensagem(cliente, "Administrador (" + admin.getNome() + ")", mensagem);
            System.out.println("Mensagem enviada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem: " + e.getMessage());
        }
    }
}