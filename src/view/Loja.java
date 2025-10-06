package view;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import core.ItemPedido;
import core.Produto;
import repository.GerenciadorDeArquivos;
import repository.Pedido;

public class Loja { // Interface do usuario
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		Scanner sc = new Scanner(System.in);
		List<Produto> produtos = GerenciadorDeArquivos.carregarProdutos();
		Pedido pedido = new Pedido();

		int opcao;
		do {
			System.out.println("\n--- Menu SchutzLoja 2 ---");
			System.out.println("1 - Ver produtos");
			System.out.println("2 - Adicionar item ao carrinho");
			System.out.println("3 - Remover item do carrinho");
			System.out.println("4 - Ver carrinho");
			System.out.println("5 - Finalizar compra");
			System.out.println("6 - Acesso administrador");
			System.out.println("0 - Sair");
			System.out.print("Escolha: ");
			opcao = sc.nextInt();

			switch (opcao) {
			case 1:
				System.out.println();
				for (Produto p : produtos) {
					System.out.println(p);
				}
				break;
			case 2:
				System.out.print("Digite o ID do produto: ");
				int id = sc.nextInt();
				Produto selecionado = null;
				for (Produto p : produtos) {
					if (p.getId() == id) {
						selecionado = p;
						break;
					}
				} // mostrar produtos disponiveis
				if (selecionado != null) {
					System.out.print("Quantidade: ");
					int qtd = sc.nextInt();
					try {
						ItemPedido item = new ItemPedido(selecionado, qtd);
						pedido.adicionarItem(item);
						System.out.println("Item adicionado.");
					} catch (IllegalArgumentException e) {
						System.out.println(e.getMessage());
					}
				} else {
					System.out.println("Produto não encontrado.");
				}
				break;
			case 3:
			    if (pedido.getItens().isEmpty()) { // Verifica se está vazio
			        System.out.println("Carrinho vazio.");
			        break;
			    }

			    pedido.exibirPedido();

			    System.out.print("Digite o ID do produto a remover: ");
			    int idRemover = sc.nextInt();

			    System.out.print("Quantas unidades deseja remover? ");
			    int qtdRemover = sc.nextInt();

			    if (qtdRemover <= 0) {
			        System.out.println("Quantidade inválida.");
			    } else {
			        boolean sucesso = pedido.removerQuantidadeDeItem(idRemover, qtdRemover); 
			        if (sucesso) {
			            System.out.println("Item atualizado no carrinho.");
			        } else {
			            System.out.println("Produto não encontrado no carrinho.");
			        }
			    }
			    break;
			case 4:
				if (pedido.getItens().size() == 0) {
					System.out.println("Carrinho vazio.");
				} else {
					pedido.exibirPedido();
				}
				break;
			case 5:
				if (pedido.getItens().size() == 0) {
					System.out.println("Carrinho vazio.");
				} else {
					pedido.exibirPedido();
					GerenciadorDeArquivos.salvarPedido(pedido); // Adiciona os novos pedidos em pedidos.txt
					GerenciadorDeArquivos.salvarProdutos(produtos); // Salva o produto.txt com o novo estoque
					pedido = new Pedido(); // limpa o carrinho
					System.out.println("Compra finalizada.");
				}
				break;
			case 6:
			    PainelAdministrador.exibirMenuAdmin(produtos, sc);
			    break;
			case 0:
				System.out.println("Saindo...");
				break;
			default:
				System.out.println("Opção inválida.");
			}
		} while (opcao != 0);

		sc.close();
	}
}
