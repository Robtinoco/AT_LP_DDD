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

## Instruções de Build e Testes

Os microsserviços são independentes entre si e não requerem contêineres Docker nem servidor RabbitMQ ativo para compilação e execução dos testes. O banco de dados H2 é inicializado automaticamente em memória pela própria aplicação Spring Boot.

Para compilar e testar cada microsserviço, execute os comandos abaixo a partir da raiz do respectivo módulo:

```bash
# Almoxarifado
cd PetFriends_Almoxarifado
mvn clean package
mvn test

# Transporte
cd PetFriends_Transporte
mvn clean package
mvn test
```

> **Observação técnica:** A propriedade `spring.rabbitmq.listener.simple.auto-startup=false` está definida no `application.properties` de ambos os microsserviços. Essa configuração impede que o listener tente se conectar ao broker RabbitMQ durante a inicialização, permitindo que o ciclo de build e os testes sejam executados em ambientes sem o broker disponível. Os testes validam a lógica de negócio chamando os serviços diretamente, sem passar pela camada de mensageria.
