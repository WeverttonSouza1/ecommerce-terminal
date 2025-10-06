package repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import core.ItemPedido;
import core.Produto;

public class GerenciadorDeArquivos {

    	private static final String ARQUIVO_PRODUTOS = "C:\\Users\\wever\\eclipse-workspace\\E-Commerce2\\src\\produtos2.txt"; // id;nome;preco;quantidadeEstoque
    	private static final String ARQUIVO_PEDIDOS = "C:\\Users\\wever\\eclipse-workspace\\E-Commerce2\\src\\pedidos2.txt";
    public static List<Produto> carregarProdutos() {
    	
        List<Produto> produtos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_PRODUTOS))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue; // ignora linhas vazias
                String[] partes = linha.split(";");
                int id = Integer.parseInt(partes[0]);
                String nome = partes[1];
                double preco = Double.parseDouble(partes[2]);
                int estoque = Integer.parseInt(partes[3]);

                Produto produto = new Produto(id, nome, preco, estoque);
                produtos.add(produto);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de produtos: " + e.getMessage());
        }

        return produtos;
    }

    public static void salvarPedido(Pedido pedido) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO_PEDIDOS, true))) {
            for (ItemPedido item : pedido.getItens()) {
                Produto p = item.getProduto();
                pw.println(p.getId() + ";" + p.getNome() + ";" + item.getQuantidade() + ";" + item.getSubtotal());
            }
            pw.println("TOTAL=" + pedido.calcularTotal());
            pw.println("---");
        } catch (IOException e) {
            System.out.println("Erro ao salvar o pedido: " + e.getMessage());
        }
    }

    public static void salvarProdutos(List<Produto> produtos) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO_PRODUTOS))) {
            for (Produto p : produtos) {
                pw.println(p.getId() + ";" + p.getNome() + ";" + p.getPreco() + ";" + p.getQuantidadeEstoque());
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar produtos: " + e.getMessage());
        }
    }
}
