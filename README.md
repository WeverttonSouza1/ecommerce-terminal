# Sistema de E-commerce (Terminal) — Projeto Acadêmico
---
**Entrega 03 – Projeto de Software (UPE – 2025.2)**

Este projeto é um **sistema de e-commerce executado via terminal**, desenvolvido em **Java**, como parte das entregas avaliativas da disciplina **Projeto de Software**.
O sistema simula o funcionamento básico de uma loja virtual, permitindo cadastro de usuários, administração de produtos, simulação de carrinho e geração de pedidos.

Este projeto foi estendido e finalizado para a disciplina **Programação III**, com foco na implementação e aplicação de **Padrões de Projeto** para garantir uma arquitetura flexível e organizada.

## Objetivo Geral do Projeto:

Construir um sistema de e-commerce completo em Java, com forte foco em **engenharia de software** e **arquitetura limpa**, utilizando:

* Modelagem UML completa (caso de uso + classes) que reflete o código final.
* Arquitetura organizada em camadas (View, Domain, Core, Repository).
* Persistência em arquivos, com o **Modelo Conceitual Relacional** definido para futura migração a um SGBD.
* Implementação final dos três **padrões de projeto** (Factory Method, State e Observer).
* Interface textual interativa no terminal.

O projeto é **100% acadêmico**, focado em aprendizagem de boas práticas, padrões de projeto e arquitetura.

## Como Executar o Projeto

Para executar o projeto, siga os passos abaixo:

1.  **Clone o Repositório:**
    ```bash
    git clone [https://github.com/WeverttonSouza1/ecommerce-terminal](https://github.com/WeverttonSouza1/ecommerce-terminal)
    cd ecommerce-terminal
    ```

2.  **Compile o Código:**
    Assumindo que você está no diretório raiz do projeto (`ecommerce-terminal`) e possui o JDK instalado:
    ```bash
    javac -d bin src/**/*.java
    ```

3.  **Execute a Aplicação:**
    Inicie a aplicação pela classe principal `Loja.java`:
    ```bash
    java -cp bin view.Loja
    ```
    O sistema será iniciado no terminal.

## Estrutura Final do Projeto:

```
src/
 ├── core/          → Entidades centrais do projeto
 │    ├── EntidadeBase.java
 │    ├── ItemPedido.java
 │    ├── Pedido.java
 │    └── Produto.java
 │
 ├── domain/        → Classe auxiliar
 │    └──  Carrinho.java
 │
 ├── repository/    → Persistência em arquivo
 │    ├── GerenciadorDeArquivos.java
 │    ├── MensagemRepository.java
 │    ├── ObserverRepository.java
 │    └── UsuarioRepository.java
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
      ├── InputUtils.java
      ├── Loja.java
      ├── PainelAdministrador.java
      └── MenuCliente.java
```

### Interface de Terminal:

* Menu inicial (Entrar / Cadastrar-se / Ver catálogo / Sair)
* Menus e painéis completos para Cliente e Administrador.
* Login de administrador (`email: admin@admin.com`, `senha: admin`).
* Leituras e validações de entrada robustas.

## Documentação UML e Persistência (Entrega 03)

Os diagramas e o modelo de dados a seguir representam a arquitetura final do sistema. Os seus codigos podem ser encontrados na pasta **docs**, eles foram escritos em PlantUML.

### 1. Diagrama de Classes Detalhado

O diagrama demonstra a estrutura completa do código e a implementação dos padrões **Factory Method**, **State** e **Observer** nas suas respectivas *packages*.

![image alt](https://github.com/WeverttonSouza1/ecommerce-terminal/blob/759a6b4166448c9c30e80d354918171531f29f93/imagens/Diagrama%20de%20Classes.png)

### 2. Diagrama de Caso de Uso
O diagrama de Caso de Uso mapeia todas as interações do sistema.

![image alt](https://github.com/WeverttonSouza1/ecommerce-terminal/blob/759a6b4166448c9c30e80d354918171531f29f93/imagens/Diagrama%20de%20Caso%20de%20Uso.png)

### 3. Modelo de Banco de Dados Relacional

![image alt](https://github.com/WeverttonSouza1/ecommerce-terminal/blob/ad625ddac35700df1e90d9aea0f60d05a5b7f849/imagens/Modelo%20de%20Banco%20de%20Dados%20Relacional.png)

## Observações Importantes:

* O sistema funciona totalmente por **arquivos .txt**, mas já está preparado para migração fácil ao banco de dados.
* Arquivos criados para cada usuário seguirão o formato:  
  mensagens_cliente_(ID).txt

## Autoria:

Projeto desenvolvido por **Wevertton Souza**  
Disciplina: *Projeto de Software*  
Professor: *Augusto César Oliveira*  
Instituição: *UPE*  
Ano/Semestre: *2025.2*  
