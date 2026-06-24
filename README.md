# PetFriends — Microsserviços com DDD e Comunicação Assíncrona

Trabalho acadêmico de pós-graduação que demonstra a decomposição de um sistema monolítico em microsserviços independentes, aplicando princípios de Domain-Driven Design (DDD) e comunicação assíncrona baseada em eventos de domínio com RabbitMQ.

## Visão Geral da Arquitetura

O projeto é organizado como um monorepo com três módulos independentes que representam contextos delimitados (Bounded Contexts) distintos:

| Módulo | Papel |
|---|---|
| `PetFriends_Pedidos` | Origem dos eventos de domínio. Define os contratos (`PedidoEmPreparacaoEvent` e `EntregaSolicitadaEvent`) publicados no broker de mensagens. |
| `PetFriends_Almoxarifado` | Microsserviço consumidor. Processa os eventos de preparação de pedidos e gerencia o ciclo de vida das Ordens de Separação. |
| `PetFriends_Transporte` | Microsserviço consumidor. Processa os eventos de entrega solicitada e gerencia o ciclo de vida das Entregas. |

## Estrutura de Pastas

```
Roberto_Tinoco_DR4_AT/
├── README.md
├── docs/
│   ├── respostas-conceituais.md
│   └── diagramas/
│       ├── diagrama1.jpg   (Estados do agregado Pedido)
│       ├── diagrama2.jpg   (Interação PetFriends_Web com os microsserviços)
│       └── diagrama3.jpg   (Comunicação assíncrona entre os microsserviços)
├── PetFriends_Pedidos/
│   └── src/main/java/com/petfriends/pedidos/events/
│       ├── PedidoEmPreparacaoEvent.java
│       └── EntregaSolicitadaEvent.java
├── PetFriends_Almoxarifado/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/petfriends/almoxarifado/
│       │   ├── domain/
│       │   │   ├── OrdemSeparacao.java
│       │   │   ├── ItemSeparacao.java
│       │   │   ├── StatusOrdemSeparacao.java
│       │   │   └── OrdemSeparacaoRepository.java
│       │   └── infra/
│       │       ├── PedidoEmPreparacaoEvent.java
│       │       ├── RabbitMQConfig.java
│       │       └── PedidoEmPreparacaoConsumer.java
│       └── test/java/com/petfriends/almoxarifado/
│           └── AlmoxarifadoServiceTest.java
└── PetFriends_Transporte/
    ├── pom.xml
    └── src/
        ├── main/java/com/petfriends/transporte/
        │   ├── domain/
        │   │   ├── Entrega.java
        │   │   ├── EnderecoEntrega.java
        │   │   ├── StatusEntrega.java
        │   │   └── EntregaRepository.java
        │   └── infra/
        │       ├── EntregaSolicitadaEvent.java
        │       ├── RabbitMQConfig.java
        │       └── EntregaSolicitadaConsumer.java
        └── test/java/com/petfriends/transporte/
            └── TransporteServiceTest.java
```

## Tecnologias Utilizadas

- **Linguagem:** Java 17
- **Framework:** Spring Boot 3.2.4
- **Persistência:** Spring Data JPA com banco H2 em memória
- **Mensageria:** Spring AMQP (RabbitMQ) com Topic Exchange
- **Observabilidade:** Spring Boot Actuator e Micrometer
- **Build:** Apache Maven

## Configuração de Mensageria (RabbitMQ)

| Recurso | Valor |
|---|---|
| Exchange | `petfriends.pedidos.exchange` (Topic) |
| Fila — Almoxarifado | `petfriends.almoxarifado.pedido-em-preparacao.queue` |
| Fila — Transporte | `petfriends.transporte.entrega-solicitada.queue` |
| Routing Key — Almoxarifado | `pedido.em-preparacao` |
| Routing Key — Transporte | `pedido.entrega-solicitada` |

## Instruções para Execução e Validação

Para rodar o projeto em um ambiente local com RabbitMQ ativo e validar a comunicação assíncrona, siga os passos abaixo:

### 1. Subir o RabbitMQ
Certifique-se de ter um servidor RabbitMQ rodando na porta padrão (`5672`). Você pode usar Docker:
```bash
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.13-management
```

### 2. Iniciar os Microsserviços
Você pode rodar as aplicações diretamente pelo IntelliJ IDEA (executando as classes principais) ou via Maven. Cada microsserviço rodará em uma porta diferente:

* **PetFriends_Pedidos** (Publisher - Porta `8080`):
  Execute a classe `PedidosApplication.java`.
* **PetFriends_Almoxarifado** (Consumer - Porta `8081`):
  Execute a classe `AlmoxarifadoApplication.java`.
* **PetFriends_Transporte** (Consumer - Porta `8082`):
  Execute a classe `TransporteApplication.java`.

### 3. Realizar a Validação (via IntelliJ HTTP Client)
Na raiz do projeto existe um arquivo chamado `petfriends-requests.http`. Abra ele no IntelliJ e clique no botão verde de play ao lado de cada requisição para enviar os comandos:

1. **Disparar evento "Pedido Em Preparação":**
   Execute o `POST http://localhost:8080/pedidos/publicar-em-preparacao`
   Isso fará o microsserviço de Pedidos enviar uma mensagem JSON para o RabbitMQ.
   
2. **Validar no Almoxarifado:**
   Execute o `GET http://localhost:8081/almoxarifado/ordens-separacao`
   O microsserviço de Almoxarifado interceptará a mensagem e salvará no banco de dados. O GET listará a Ordem de Separação criada.

3. **Disparar evento "Entrega Solicitada":**
   Execute o `POST http://localhost:8080/pedidos/publicar-entrega-solicitada`
   Isso publicará a mensagem de entrega no RabbitMQ.

4. **Validar no Transporte:**
   Execute o `GET http://localhost:8082/transporte/entregas`
   O microsserviço de Transporte interceptará a mensagem, criará a Entrega no banco e ela será listada na requisição.

> **Observação técnica sobre a serialização:** 
> Para garantir independência entre os contextos delimitados (evitando acoplamento de classes e `ClassCastException`), os eventos são publicados pelo microsserviço de Pedidos como **JSON Strings**. Os consumers do Almoxarifado e do Transporte recebem o texto JSON e realizam o *parsing* (usando Jackson `ObjectMapper`) diretamente para os seus próprios DTOs locais correspondentes, atestando o isolamento dos microsserviços.
