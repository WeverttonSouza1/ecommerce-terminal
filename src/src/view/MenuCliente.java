package view;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import core.ItemPedido;
import core.Pedido;
import core.Produto;
import domain.Carrinho;
import factoryMethod.PagamentoInterface;
import factoryMethod.PagamentoFactory;
import repository.GerenciadorDeArquivos;
import repository.MensagemRepository;
import repository.UsuarioRepository;
import repository.ObserverRepository;
import user.Cliente;

public class MenuCliente {
    
    private final Cliente cliente;
    private final GerenciadorDeArquivos arquivo = new GerenciadorDeArquivos();
    private final MensagemRepository msgRepo = new MensagemRepository();
    private final ObserverRepository observerRepo = new ObserverRepository();
    private final UsuarioRepository usuarioRepo;
    private final Carrinho carrinho = new Carrinho();
    private final Scanner sc = new Scanner(System.in);

    public MenuCliente(Cliente c, UsuarioRepository usuarioRepo) {
        this.cliente = c;
        this.usuarioRepo = usuarioRepo;
    }

    public void iniciar() {
        while (true) {
            // Tratamento: Evita divisão por zero se a lista estiver vazia, embora o size() retorne 0
            int qtdMensagens = msgRepo.listarMensagens(cliente).size();
            int displayQtd = (qtdMensagens > 0) ? qtdMensagens / 4 : 0;

            System.out.println("\n=== MENU CLIENTE: " + cliente.getNome() + " ===");
            System.out.println("1 - Listar Produtos");
            System.out.println("2 - Adicionar produto ao carrinho");
            System.out.println("3 - Ver Carrinho");
            System.out.println("4 - Finalizar compra");
            System.out.println("5 - Editar Meus Dados");
            System.out.println("6 - Assinar Notificação de Estoque");
            System.out.println("7 - Caixa de Mensagens (" + displayQtd + " novas)");
            System.out.println("8 - Histórico de Pedidos");
            System.out.println("9 - Logout");
            System.out.print("Escolha: ");
            String op = sc.nextLine();

            switch (op) {
                case "1" -> listarProdutos();
                case "2" -> adicionarProdutoAoCarrinho();
                case "3" -> verCarrinho();
                case "4" -> finalizarCompra();
                case "5" -> editarDados();
                case "6" -> assinarNotificacaoEstoque();
                case "7" -> caixaMensagens();
                case "8" -> historicoPedidos();
                case "9" -> {
                    System.out.println("Logout realizado.");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void listarProdutos() {
        System.out.println("\n=== CATÁLOGO DE PRODUTOS ===");
        List<Produto> produtos = arquivo.carregarProdutos().stream()
                .filter(Produto::isAtivo)
                .toList();

        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            produtos.forEach(System.out::println);
        }
    }

    private void verCarrinho() {
        System.out.println("\n=== CARRINHO ===");
        carrinho.exibirCarrinho();
    }

    private void adicionarProdutoAoCarrinho() {
        listarProdutos();
        // TRATAMENTO DE ERRO: Uso do InputUtils
        Long id = InputUtils.lerLong(sc, "ID do produto: ");
        int qtd = InputUtils.lerInt(sc, "Quantidade: ");

        Optional<Produto> op = arquivo.buscarProdutoPorId(id);
        if (op.isEmpty() || !op.get().isAtivo()) {
            System.out.println("Produto não encontrado ou indisponível.");
            return;
        }

        Produto p = op.get();

        if (qtd <= 0 || qtd > p.getEstoque()) {
            System.out.println("Estoque insuficiente ou quantidade inválida.");
            return;
        }

        carrinho.adicionar(p, qtd);
        System.out.println(String.format("Adicionado: %s (x%d)", p.getNome(), qtd));
    }

    private void finalizarCompra() { 
        if (carrinho.getItens().isEmpty()) {
            System.out.println("Carrinho vazio.");
            return;
        }

        System.out.println("\nTotal da compra: R$ " + carrinho.total().toPlainString());
        System.out.println("Escolha o método de pagamento:");
        System.out.println("1 - Pix");
        System.out.println("2 - Cartão");
        System.out.println("3 - Boleto");
        System.out.print("Opção: ");
        String tipoPagamento = sc.nextLine();

        String nomePagamento = switch (tipoPagamento) {
            case "1" -> "pix";
            case "2" -> "cartao";
            case "3" -> "boleto";
            default -> null;
        };

        if (nomePagamento == null) {
            System.out.println("Opção de pagamento inválida.");
            return;
        }

        PagamentoInterface pagamento = PagamentoFactory.criarPagamento(nomePagamento);

        if (pagamento == null || !pagamento.processarPagamento(carrinho.total())) {
            System.out.println("Pagamento falhou. Compra cancelada.");
            return;
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(cliente.getId());
        pedido.setEnderecoEntrega(cliente.getEndereco());

        for (ItemPedido item : carrinho.getItens()) { // salva um produto por vez
            pedido.adicionarItem(item);
            Optional<Produto> op = arquivo.buscarProdutoPorId(item.getProduto().getId());
            if (op.isPresent()) {
                Produto p = op.get();
                // Simples verificação de segurança (embora já verificado no add carrinho)
                if(p.getEstoque() >= item.getQuantidade()) {
                    p.setEstoque(p.getEstoque() - item.getQuantidade());
                    arquivo.salvarProduto(p);
                } else {
                    System.out.println("Aviso: Estoque alterado durante a compra para o item " + p.getNome());
                }
            }
        }

        pedido.setStatus("PROCESSANDO");
        arquivo.salvarPedido(pedido);
        carrinho.limpar();

        System.out.println("\nCompra concluída!");
        System.out.println("Pedido ID: " + pedido.getId());
    }

    private void editarDados() {
        System.out.println("\n=== EDITAR MEUS DADOS ===");
        System.out.print("Nome (" + cliente.getNome() + "): ");
        String nome = sc.nextLine();
        if (!nome.isBlank()) cliente.setNome(nome); // isBlank serve para verificar se uma string está vazia ou contém apenas espaços em branco

        System.out.print("Senha: ");
        String senha = sc.nextLine();
        if (!senha.isBlank()) cliente.setSenha(senha);

        System.out.print("Endereço (" + cliente.getEndereco() + "): ");
        String endereco = sc.nextLine();
        if (!endereco.isBlank()) cliente.setEndereco(endereco);

        usuarioRepo.salvarUsuario(cliente);
        System.out.println("Dados atualizados com sucesso!");
    }

    private void assinarNotificacaoEstoque() {
        System.out.println("\n=== ASSINAR NOTIFICAÇÃO DE ESTOQUE ===");
        arquivo.carregarProdutos().stream()
               .filter(Produto::isAtivo) // chamar o método isAtivo() em cada objeto Produto da lista. // FILTRA: Mantém APENAS aqueles cujo isAtivo() é TRUE
               .forEach(System.out::println);
        
        // TRATAMENTO DE ERRO: Input seguro
        Long id = InputUtils.lerLong(sc, "ID do produto: ");

        Optional<Produto> op = arquivo.buscarProdutoPorId(id);
        if (op.isEmpty() || !op.get().isAtivo()) {
            System.out.println("Produto não encontrado.");
            return;
        }

        Produto p = op.get();
        if (p.getEstoque() > 0) {
            System.out.println("Produto já possui estoque disponível.");
            return;
        }

        observerRepo.adicionarObservador(p.getId(), cliente.getId());
        
        System.out.println("Inscrição feita! Você será notificado quando o produto voltar ao estoque.");
    }

    private void caixaMensagens() {
        while (true) {
            System.out.println("\n=== CAIXA DE MENSAGENS ===");
            List<String> msgs = msgRepo.listarMensagens(cliente);
            System.out.println("1 - Ver mensagens");
            System.out.println("2 - Limpar mensagens");
            System.out.println("3 - Voltar");
            System.out.print("Escolha: ");
            String op = sc.nextLine();

            switch (op) {
                case "1" -> {
                    if (msgs.isEmpty()) System.out.println("Nenhuma mensagem.");
                    else msgs.forEach(System.out::println);
                }
                case "2" -> {
                    msgRepo.limparMensagens(cliente);
                    System.out.println("Mensagens apagadas.");
                }
                case "3" -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void historicoPedidos() {
        System.out.println("\n=== HISTÓRICO DE PEDIDOS ===");
        List<Pedido> pedidos = arquivo.carregarPedidos();
        boolean encontrou = false;
        for (Pedido p : pedidos) {
            if (p.getClienteId().equals(cliente.getId())) {
                System.out.println(p);
                encontrou = true;
            }
        }
        if (!encontrou) {
            System.out.println("Nenhum pedido encontrado.");
        }
    }
}
