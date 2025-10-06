package view;

import java.util.List;
import java.util.Scanner;

import core.Produto;
import repository.GerenciadorDeArquivos;

public class PainelAdministrador {
	private static final String SENHA_ADMIN = "1234";

	public static void exibirMenuAdmin(List<Produto> produtos, Scanner sc) { // Reutiliza o Scanner
		System.out.print("Digite a senha de administrador: ");
		String senha = sc.next();

		if (!senha.equals(SENHA_ADMIN)) { // verifica se senha é igual a senha do adm
			System.out.println("Senha incorreta. Acesso negado.");
			return;
		}

		int opcao;
		do {
			System.out.println("\n--- Painel do Administrador ---");
			System.out.println("1 - Cadastrar novo produto");
			System.out.println("2 - Remover produto existente");
			System.out.println("0 - Voltar ao menu principal");
			System.out.print("Escolha: ");
			opcao = sc.nextInt();

			switch (opcao) {
			case 1:
				cadastrarProduto(produtos, sc);
				break;
			case 2:
				removerProduto(produtos, sc);
				break;
			case 0:
				System.out.println("Voltando ao menu...");
				break;
			default:
				System.out.println("Opção inválida.");
			}
		} while (opcao != 0);
	}

	private static void cadastrarProduto(List<Produto> produtos, Scanner sc) {
		System.out.print("Digite o ID do novo produto: ");
		int id = sc.nextInt();
		sc.nextLine(); // consumir quebra de linha

		// Verifica se ID já existe
		for (Produto p : produtos) {
			if (p.getId() == id) {
				System.out.println("Erro: Já existe um produto com esse ID.");
				return;
			}
		}

		System.out.print("Nome do produto: ");
		String nome = sc.nextLine();

		// Verifica se nome já existe (ignorando maiúsculas/minúsculas)
		for (Produto p : produtos) {
			if (p.getNome().equalsIgnoreCase(nome)) {
				System.out.println("Erro: Já existe um produto com esse nome.");
				return;
			}
		}

		System.out.print("Preço: ");
		double preco = sc.nextDouble();

		System.out.print("Quantidade em estoque: ");
		int estoque = sc.nextInt();

		Produto novo = new Produto(id, nome, preco, estoque);
		produtos.add(novo);
		GerenciadorDeArquivos.salvarProdutos(produtos);

		System.out.println("Produto cadastrado com sucesso.");
	}

	private static void removerProduto(List<Produto> produtos, Scanner sc) { // Remove produto de produtos.txt
		System.out.print("Digite o ID do produto a remover: ");
		int id = sc.nextInt();

		Produto encontrado = null;
		for (Produto p : produtos) {
			if (p.getId() == id) {
				encontrado = p;
				break;
			}
		}

		if (encontrado != null) {
			produtos.remove(encontrado);
			GerenciadorDeArquivos.salvarProdutos(produtos);
			System.out.println("Produto removido com sucesso.");
		} else {
			System.out.println("Produto não encontrado.");
		}
	}
}
