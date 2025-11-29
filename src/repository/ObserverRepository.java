package repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ObserverRepository {

    private final Path observersFile = Paths.get("src/observadores.txt");

    private static class ProdutoObservado {
        private final Long produtoId;
        private final List<Long> clientesObservadores;

        public ProdutoObservado(Long produtoId, List<Long> clientesObservadores) {
            this.produtoId = produtoId;
            this.clientesObservadores = clientesObservadores;
        }

        public Long getProdutoId() {
            return produtoId;
        }

        public List<Long> getClientesObservadores() {
            return clientesObservadores;
        }
    }

    public void adicionarObservador(Long produtoId, Long clienteId) { // Adiciona um observador
        List<ProdutoObservado> lista = carregarTodosObservadores();
        ProdutoObservado entry = buscarEntry(lista, produtoId).orElse(null); // Entry é o id do produto buscado para observar

        if (entry == null) {
            // Se o produto não está na lista, cria uma nova entrada
            List<Long> novaListaClientes = new ArrayList<>();
            novaListaClientes.add(clienteId);
            ProdutoObservado novaEntry = new ProdutoObservado(produtoId, novaListaClientes);
            lista.add(novaEntry);
        } else {
            // Se o produto já existe, adiciona o cliente se ele não estiver na lista
            List<Long> clientes = entry.getClientesObservadores();
            if (!clientes.contains(clienteId)) {
                clientes.add(clienteId);
            }
        }
        
        salvarTodosObservadores(lista);
    }

    public List<Long> recuperarObservadores(Long produtoId) { // Carrega os IDs dos clientes que observam um produto
        List<ProdutoObservado> lista = carregarTodosObservadores();
        Optional<ProdutoObservado> opEntry = buscarEntry(lista, produtoId);
        
        return opEntry.map(ProdutoObservado::getClientesObservadores).orElseGet(ArrayList::new); // .map mapeia somente se o valor estiver presente. extrai a lista de clientes do objeto
                                                                                // retorna essa ArrayList vazia).
    }

    public void removerObservadoresDoProduto(Long produtoId) { // Remove os observadores de um produto (usado após notificar)
        List<ProdutoObservado> lista = carregarTodosObservadores();
        
        // Remove a entrada do produto da lista, se existir
        boolean removido = lista.removeIf(entry -> entry.getProdutoId().equals(produtoId));

        if (removido) {
            salvarTodosObservadores(lista);
        }
    }

    private Optional<ProdutoObservado> buscarEntry(List<ProdutoObservado> lista, Long produtoId) { // Método auxiliar para buscar uma entrada na lista pelo produtoId
        for (ProdutoObservado entry : lista) {
            if (entry.getProdutoId().equals(produtoId)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    private List<ProdutoObservado> carregarTodosObservadores() {
        List<ProdutoObservado> lista = new ArrayList<>();
        if (!Files.exists(observersFile)) return lista;

        try (BufferedReader br = Files.newBufferedReader(observersFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                
                String[] parts = line.split(";");
                if (parts.length < 2) continue; // Precisa pelo menos produto e 1 cliente

                try {
                    Long prodId = Long.parseLong(parts[0]);
                    List<Long> clientes = new ArrayList<>();
                    
                    for (int i = 1; i < parts.length; i++) {
                        if (!parts[i].isBlank()) {
                            clientes.add(Long.parseLong(parts[i]));
                        }
                    }
                    // Adiciona o novo objeto na lista
                    lista.add(new ProdutoObservado(prodId, clientes));
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao carregar ID em observadores.txt: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private void salvarTodosObservadores(List<ProdutoObservado> lista) {
        try (BufferedWriter bw = Files.newBufferedWriter(observersFile)) {
            for (ProdutoObservado entry : lista) {
                StringBuilder sb = new StringBuilder(); // stringBuilder é ideal para o append 
                sb.append(entry.getProdutoId());
                
                // Itera sobre a lista de clientes dentro do objeto
                for (Long clienteId : entry.getClientesObservadores()) {
                    sb.append(";").append(clienteId);
                }
                
                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}