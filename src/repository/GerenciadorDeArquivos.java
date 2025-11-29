package repository;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import core.ItemPedido;
import core.Pedido;
import core.Produto;

public class GerenciadorDeArquivos {

    private final Path produtosFile = Paths.get("src/produtos.txt");
    private final Path pedidosFile = Paths.get("src/pedidos.txt");

    // Inicializa IDs baseado no último encontrado
    private final AtomicLong produtoIdGen = new AtomicLong(loadLastId(produtosFile)); // evita erro caso tenha mais de um utilizando o e-commerce
    private final AtomicLong pedidoIdGen = new AtomicLong(loadLastId(pedidosFile));

     private long loadLastId(Path p) { // Descobre o maior ID registrado no arquivo para garantir que o próximo ID gerado seja único (max + 1).
         if (!Files.exists(p)) return 0;
         long max = 0;
         
         try (Stream<String> s = Files.lines(p)) { // O try-with-resources garante que o Stream de leitura do arquivo será fechado automaticamente.
             List<String> linhas = s.toList();
             
             for (String line : linhas) { 
                 if (line.isBlank()) continue; 
                 String[] cols = line.split(";");
                 try {
                     long id = Long.parseLong(cols[0]);
                     if (id > max) max = id;
                 } catch (Exception ignored) {}
             }
         } catch (IOException ignored) {}
         return max;
     }

    // ===========================
    // PRODUTOS
    // ===========================
    public List<Produto> carregarProdutos() { 
        List<Produto> list = new ArrayList<>();
        if (!Files.exists(produtosFile)) return list;

        try (BufferedReader br = Files.newBufferedReader(produtosFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue; // o isBlank verifica se a linha está vazia ou so contém espaços para voltar o loop em outra linha
                String[] cols = line.split(";");
                // Estrutura: id;nome;descricao;preco;estoque;status
                if (cols.length < 5) continue;

                Long id = Long.parseLong(cols[0]);
                String nome = cols[1];
                String descricao = cols[2];
                BigDecimal preco = new BigDecimal(cols[3]);
                int estoque = Integer.parseInt(cols[4]);
                // Leitura do status (atualizou os produtos antigos tinha status)
                String status = (cols.length >= 6) ? cols[5] : "ATIVO";

                list.add(new Produto(id, nome, descricao, preco, estoque, status));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Produto salvarProduto(Produto p) { // salva produto após a venda com o novo estoque
        try {
            if (p.getId() == null) p.setId(produtoIdGen.incrementAndGet());
            List<Produto> all = carregarProdutos();

            boolean found = false;
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getId().equals(p.getId())) {
                    all.set(i, p);
                    found = true;
                    break;
                }
            }
            if (!found) all.add(p);

            reescreverArquivoProdutos(all);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    public Optional<Produto> buscarProdutoPorId(Long id) {
        return carregarProdutos().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    // Muda status para REMOVIDO em vez de apagar a linha
    public void deletarProduto(Long id) {
        List<Produto> all = carregarProdutos();
        boolean alterado = false;
        
        for (Produto p : all) {
            if (p.getId().equals(id)) {
                p.setStatus("REMOVIDO"); // Apenas muda o status
                alterado = true;
                break;
            }
        }

        if (alterado) {
            reescreverArquivoProdutos(all);
        }
    }
    
    private void reescreverArquivoProdutos(List<Produto> produtos) {
        try (BufferedWriter bw = Files.newBufferedWriter(produtosFile)) {
            for (Produto pr : produtos) {
                // Formato: id;nome;descricao;preco;estoque;status
                bw.write(String.format("%d;%s;%s;%s;%d;%s%n",
                        pr.getId(), pr.getNome(), pr.getDescricao(),
                        pr.getPreco().toPlainString(), pr.getEstoque(), pr.getStatus()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===========================
    // PEDIDOS
    // ===========================
    
    public List<Pedido> carregarPedidos() { 
        List<Pedido> pedidos = new ArrayList<>();
        if (!Files.exists(pedidosFile)) return pedidos;

        // Carrega todos os produtos, inclusive os REMOVIDOS, para montar o histórico corretamente
        List<Produto> produtosDisponiveis = carregarProdutos();

        try (BufferedReader br = Files.newBufferedReader(pedidosFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                // formato: id;clienteId;enderecoEntrega;data;total;status;itens
                String[] cols = line.split(";");
                if (cols.length < 7) continue;

                try {
                    Long id = Long.parseLong(cols[0]);
                    Long clienteId = Long.parseLong(cols[1]);
                    String enderecoEntrega = cols[2];
                    LocalDateTime data = LocalDateTime.parse(cols[3]);
                    BigDecimal total = new BigDecimal(cols[4]);
                    String status = cols[5];

                    String[] itensData = cols[6].split("\\|");
                    List<ItemPedido> itens = new ArrayList<>();

                    for (String itemStr : itensData) {
                        if (itemStr.isBlank()) continue;
                        String[] itemCols = itemStr.split(":");
                        if (itemCols.length != 3) continue;

                        Long produtoId = Long.parseLong(itemCols[0]);
                        int quantidade = Integer.parseInt(itemCols[1]);
                        BigDecimal precoUnitario = new BigDecimal(itemCols[2]);

                        Optional<Produto> produtoOpt = produtosDisponiveis.stream() // verifica qual o produto que o criente pediu
                                .filter(pr -> pr.getId().equals(produtoId))
                                .findFirst();

                        if (produtoOpt.isPresent()) {  // Verifica se o produto com o produtoId correspondente foi encontrado na lista de produtos disponíveis
                            Produto produto = produtoOpt.get();
                            ItemPedido item = new ItemPedido();
                            item.setProduto(produto);
                            item.setQuantidade(quantidade);
                            item.setPrecoUnitario(precoUnitario);
                            itens.add(item);
                        }
                    }
                    pedidos.add(new Pedido(id, clienteId, enderecoEntrega, data, total, status, itens));

                } catch (Exception e) {
                    System.err.println("Erro ao carregar pedido: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    public void salvarPedido(Pedido pedido) {  // salvar um pedido no arquivo quando ele está sendo criado pela primeira vez
        if (pedido.getId() == null) pedido.setId(pedidoIdGen.incrementAndGet());
        
        try (BufferedWriter bw = Files.newBufferedWriter(pedidosFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) { 
            														  // StandardOpenOption.CREATE: Diz ao Java para criar o arquivo se ele não existir.
            																					// StandardOpenOption.APPEND: Diz ao Java para adicionar o novo conteúdo ao final do arquivo.
            bw.write(formatarPedidoParaLinha(pedido));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<Pedido> buscarPedidoPorId(Long id) {
        return carregarPedidos().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public void salvarPedidoComStatus(Pedido pedido) { // usado pelo PainelAdministrador para mudar o estado de um pedido existente.
        List<Pedido> pedidos = carregarPedidos();
        boolean found = false;

        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).getId().equals(pedido.getId())) {
                pedidos.set(i, pedido);
                found = true;
                break;
            }
        }
        if (!found) pedidos.add(pedido);

        try (BufferedWriter bw = Files.newBufferedWriter(pedidosFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Pedido p : pedidos) {
                bw.write(formatarPedidoParaLinha(p));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String formatarPedidoParaLinha(Pedido pedido) { // transforma em texto para armazenar em pedidos.txt
        StringBuilder sb = new StringBuilder(); // ideal para o append
        sb.append(pedido.getId()).append(";")
          .append(pedido.getClienteId()).append(";")
          .append(pedido.getEnderecoEntrega()).append(";")
          .append(pedido.getDataCriacao()).append(";")
          .append(pedido.getTotal().toPlainString()).append(";")
          .append(pedido.getStatus()).append(";");

        for (int i = 0; i < pedido.getItens().size(); i++) { // id;quantidade;preço
            if (i > 0) sb.append("|"); // para caso tenha mais de um item
            ItemPedido item = pedido.getItens().get(i);
            sb.append(item.getProduto().getId()).append(":")
              .append(item.getQuantidade()).append(":")
              .append(item.getPrecoUnitario().toPlainString());
        }
        return sb.toString();
    }
}