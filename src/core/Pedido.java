package core;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import state.PedidoProcessando;
import state.PedidoState;
import state.PedidoEntregue;
import state.PedidoEnviado;
import state.PedidoCancelado;

public class Pedido extends EntidadeBase {

    private Long clienteId;
    private String enderecoEntrega;
    private List<ItemPedido> itens = new ArrayList<>();
    private LocalDateTime dataCriacao;
    private String status = "PROCESSANDO";
    private BigDecimal total = BigDecimal.ZERO; // Garante que o total é 0, não null. evita erro no getTotal()
    private PedidoState state = new PedidoProcessando(this);

    public Pedido() {
        this.dataCriacao = LocalDateTime.now();
    }

    public Pedido(Long id, Long clienteId, String enderecoEntrega, LocalDateTime dataCriacao,
                  BigDecimal total, String status, List<ItemPedido> itens) {
        this.setId(id);
        this.clienteId = clienteId;
        this.enderecoEntrega = enderecoEntrega;
        this.dataCriacao = dataCriacao;
        this.total = total;
        this.itens = itens;
        this.setStatus(status);
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        calcularTotal();
    }

    public void calcularTotal() {
        total = BigDecimal.ZERO;
        for (ItemPedido it : itens) {
            total = total.add(it.calcularSubtotal());
        }
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    // ===========================
    // STATUS / STATE PATTERN
    // ===========================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        switch (status.toUpperCase()) {
            case "PROCESSANDO" -> this.state = new PedidoProcessando(this);
            case "ENVIADO" -> this.state = new PedidoEnviado(this);
            case "ENTREGUE" -> this.state = new PedidoEntregue(this);
            case "CANCELADO" -> this.state = new PedidoCancelado(this);
            default -> this.state = new PedidoProcessando(this);
        }
    }

    public PedidoState getState() {
        return state;
    }

    public void mudarEstadoPara(String novoStatus) {
        System.out.print("Transição: Pedido #" + this.getId() + " de '" + this.status + "' para '" + novoStatus + "'... ");
        setStatus(novoStatus);
        this.state.exibirStatus();
    }

    @Override
    public String toString() {
        return String.format(
                "Pedido ID: %d | Cliente ID: %d | Endereço: %s | Status: %s | Total: R$ %s | Data: %s",
                getId(), clienteId, enderecoEntrega != null ? enderecoEntrega : "(não informado)",
                status, total.toPlainString(), dataCriacao.toLocalDate());
    }
}
