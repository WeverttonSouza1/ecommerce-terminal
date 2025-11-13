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

    private final AtomicLong produtoIdGen = new AtomicLong(loadLastId(produtosFile)); // evita erro caso tenha mais de um utilizando o e-commerce
    private final AtomicLong pedidoIdGen = new AtomicLong(loadLastId(pedidosFile));

    public List<Pedido> carregarPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        if (!Files.exists(pedidosFile)) return pedidos;

        List<Produto> produtosDisponiveis = carregarProdutos();

        try (BufferedReader br = Files.newBufferedReader(pedidosFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                // formato: id;clienteId;enderecoEntrega;data;total;status;itens
                String[] cols = line.split(";", 7);
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

                        Optional<Produto> produtoOpt = produtosDisponiveis.stream()
                                .filter(pr -> pr.getId().equals(produtoId))
                                .findFirst();

                        if (produtoOpt.isPresent()) { // Verifica se o produto com o produtoId correspondente foi encontrado na lista de produtos disponíveis
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
                    System.err.println("Erro ao carregar pedido: " + line + " -> " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pedidos;
    }

    private long loadLastId(Path p) {
        if (!Files.exists(p)) return 0;
        long max = 0;
        try (Stream<String> s = Files.lines(p)) { // Abre o arquivo para leitura linha por linha (Files.lines). O bloco try-with-resources
        	                                     // garante que o recurso (Stream s) será fechado automaticamente.
            for (String line : (Iterable<String>) s::iterator) { //Percorre cada linha do arquivo.
                if (line.trim().isEmpty()) continue;
                String[] cols = line.split(";");
                try {
                    long id = Long.parseLong(cols[0]);
                    if (id > max) max = id;
                } catch (Exception ignored) {}
            }
        } catch (IOException ignored) {}
        return max;
    }

    public Long getProximoProdutoId() {
        return produtoIdGen.incrementAndGet();
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
                if (line.isBlank()) continue;
                String[] cols = line.split(";");
                if (cols.length < 5) continue;

                Long id = Long.parseLong(cols[0]);
                String nome = cols[1];
                String descricao = cols[2];
                BigDecimal preco = new BigDecimal(cols[3]);
                int estoque = Integer.parseInt(cols[4]);
                list.add(new Produto(id, nome, descricao, preco, estoque));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Produto salvarProduto(Produto p) {
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

            try (BufferedWriter bw = Files.newBufferedWriter(produtosFile)) {
                for (Produto pr : all) {
                    bw.write(String.format("%d;%s;%s;%s;%d%n",
                            pr.getId(), pr.getNome(), pr.getDescricao(),
                            pr.getPreco().toPlainString(), pr.getEstoque()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public Optional<Produto> buscarProdutoPorId(Long id) {
        return carregarProdutos().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public void deletarProduto(Long id) {
        List<Produto> all = carregarProdutos();
        all.removeIf(p -> p.getId().equals(id)); // o primeiro p é o parametro de cada elemento da lista all
        try (BufferedWriter bw = Files.newBufferedWriter(produtosFile)) {
            for (Produto pr : all) {
                bw.write(String.format("%d;%s;%s;%s;%d%n",
                        pr.getId(), pr.getNome(), pr.getDescricao(),
                        pr.getPreco().toPlainString(), pr.getEstoque()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===========================
    // PEDIDOS
    // ===========================
    
    public void salvarPedido(Pedido pedido) {
        if (pedido.getId() == null) pedido.setId(pedidoIdGen.incrementAndGet());
        StringBuilder sb = new StringBuilder();
        sb.append(pedido.getId()).append(";")
          .append(pedido.getClienteId()).append(";")
          .append(pedido.getEnderecoEntrega()).append(";")
          .append(pedido.getDataCriacao()).append(";")
          .append(pedido.getTotal().toPlainString()).append(";")
          .append(pedido.getStatus()).append(";");

        for (int i = 0; i < pedido.getItens().size(); i++) {
            if (i > 0) sb.append("|");
            ItemPedido item = pedido.getItens().get(i);
            sb.append(item.getProduto().getId()).append(":")
              .append(item.getQuantidade()).append(":")
              .append(item.getPrecoUnitario().toPlainString());
        }

        try (BufferedWriter bw = Files.newBufferedWriter(pedidosFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) { 
                                                                    // StandardOpenOption.CREATE: Diz ao Java para criar o arquivo se ele não existir.
        	                                                        // StandardOpenOption.APPEND: Diz ao Java para adicionar o novo conteúdo ao final do arquivo.
            bw.write(sb.toString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<Pedido> buscarPedidoPorId(Long id) {
        return carregarPedidos().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public void salvarPedidoComStatus(Pedido pedido) {
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
                StringBuilder sb = new StringBuilder();
                sb.append(p.getId()).append(";")
                  .append(p.getClienteId()).append(";")
                  .append(p.getEnderecoEntrega()).append(";")
                  .append(p.getDataCriacao()).append(";")
                  .append(p.getTotal().toPlainString()).append(";")
                  .append(p.getStatus()).append(";");

                for (int i = 0; i < p.getItens().size(); i++) {
                    if (i > 0) sb.append("|");
                    ItemPedido item = p.getItens().get(i);
                    sb.append(item.getProduto().getId()).append(":")
                      .append(item.getQuantidade()).append(":")
                      .append(item.getPrecoUnitario().toPlainString());
                }
                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

