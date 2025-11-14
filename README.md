### Sistema de E-commerce (Terminal) — Projeto Acadêmico
---
**Entrega 02 – Projeto de Software (UPE – 2025.2)**

Este projeto é um **sistema de e-commerce executado via terminal**, desenvolvido em **Java**, como parte das entregas avaliativas da disciplina **Projeto de Software**.
O sistema simula o funcionamento básico de uma loja virtual, permitindo cadastro de usuários, administração de produtos, simulação de carrinho e geração de pedidos.

Este mesmo projeto está sendo estendido e reaproveitado também para a disciplina **Programação III**, onde são exigidos padrões de projeto adicionais.

### Objetivo Geral do Projeto:

Construir um sistema de e-commerce completo em Java, com forte foco em **engenharia de software**, utilizando:

* Modelagem UML (caso de uso + classes)
* Arquitetura organizada em camadas
* Persistência em arquivos (entrega 2) e futura integração com **PostgreSQL** (entrega 3)
* Implementação de **padrões de projeto** (Factory Method, State, Observer — este último ainda não finalizado)
* Interface textual interativa no terminal

O projeto é **100% acadêmico**, com foco em aprendizagem de boas práticas, padrões de projeto e arquitetura limpa.

### Estrutura Atual do Projeto:

```
src/
 ├── core/          → Entidades centrais do projeto
 │    ├── EntidadeBase.java
 │    ├── ItemPedido.java
 │    ├── Pedido.java
 │    └── Produto.java
 │
 ├── domain/        → Classes auxiliares
 │    ├── Carrinho.java
 │    └── ExibivelInterface.java
 │
 ├── repository/    → Persistência em arquivo
 │    ├── GerenciadorDeArquivos.java
 │    ├── UsuarioRepository.java
 │    └── MensagemRepository.java
 │
 ├── user/          → Usuários do sistema
 │    ├── Usuario.java
 │    ├── Cliente.java
 │    └── Administrador.java
 │
 ├── observer/      → Padrão Observer (Notifica quando um produto volta ao estoque)
 │
 ├── factoryMethod/ → Padrão Factory Method (PIX, Cartão, Boleto)
 │
 ├── state/         → Padrão State para Pedido (Processando, Enviado, etc.)
 │
 └── view/          → Interface de terminal
      ├── Loja.java
      ├── PainelAdministrador.java
      └── menu/MenuCliente.java
```

### Funcionalidades Implementadas na Entrega 02

### Back-end:

* Cadastro e login de clientes
* Login de administrador
* CRUD completo de produtos
* Carrinho de compras funcional
* Finalização de pedido
* Atualização de estoque
* Mensagens para clientes (via arquivo)
* Persistência em arquivos com IDs automáticos
* Implementação dos padrões:  
  * **Factory Method** (PIX, Cartão, Boleto)
  * **State** (Status do Pedido)
  * **Observer** → *Notificar quando um produto voltar ao estoque, estrutura pronta, mas ainda NÃO funcional*

### Interface de Terminal:

* Menu inicial (Entrar / Cadastrar-se / Ver catálogo / Sair)
* Menu completo do cliente
* Painel do administrador
* Leituras e validações de entrada

### Observações Importantes da Entrega 02:

* O **Observer** está parcialmente implementado, mas ainda **não está ativo** (não notifica clientes automaticamente).
* O sistema funciona totalmente por **arquivos .txt**, mas já está preparado para migração fácil ao banco na Entrega 03.
* Arquivos criados por usuário seguirão o formato:  
  usuario_(ID).txt, mensagens_cliente_(ID).txt e pedidos_cliente_(ID).txt

### Como Executar:

1. Importar o projeto no **Eclipse** ou **IntelliJ**
2. Executar a classe principal:
```
   view.Loja
```
3. O terminal exibirá o menu inicial

### Autoria:

Projeto desenvolvido por **Wevertton Souza**  
Disciplina: *Projeto de Software*  
Professor: *Augusto César Oliveira*
Instituição: *UPE*  
Ano/Semestre: *2025.2*  
