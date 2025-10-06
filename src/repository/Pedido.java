package repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import core.ItemPedido;
import core.Produto;

public class Pedido {
    private List<ItemPedido> itens;

    public Pedido() {
        itens = new ArrayList<>();
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
    }

    public void adicionarItem(Produto produto, int quantidade) { // Sobrecarga: permite adicionar sem criar o ItemPedido manualmente
        ItemPedido item = new ItemPedido(produto, quantidade);
        adicionarItem(item);
    }
    
    public boolean removerQuantidadeDeItem(int idProduto, int qtdRemover) { // pergunta quantos itens quer remover
        Iterator<ItemPedido> it = itens.iterator(); // Pecorre a lista / evita erro de concorrência
        while (it.hasNext()) { // Verifica se tem mais elementos na lista para pecorrer
            ItemPedido item = it.next();
            if (item.getProduto().getId() == idProduto) {
                if (qtdRemover >= item.getQuantidade()) {
                    item.getProduto().devolverEstoque(item.getQuantidade());
                    it.remove();
                } else {
                    item.getProduto().devolverEstoque(qtdRemover);
                    item.diminuirQuantidade(qtdRemover);
                }
                return true;
            }
        }
        return false;
    }

    
    public double calcularTotal() {
        double total = 0;
        for (ItemPedido item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void exibirPedido() {
        for (ItemPedido item : itens) {
            System.out.println(item);
        }
        System.out.println(String.format("Total: R$%.2f", calcularTotal())); // Manipulação de String
    }

    public List<ItemPedido> getItens() {
        return itens;
    }
} 

// Falta adicionar a confirmação se o cliente quer confirmar a compra ao ele selecionar a opção 5 "Finalizar compra"