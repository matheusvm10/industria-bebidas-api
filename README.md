# Sistema de Controle de Almoxarifado — Indústria de Bebidas

API RESTful desenvolvida em **Java 17** com **Spring Boot 3.3.5** e **MySQL 8.0** para o **Projeto Integrador — Módulo 2**. O sistema gere insumos fabris, fornecedores, setores produtivos, movimentações de entrada/saída e alertas automáticos de estoque crítico.

---

## Equippe de Desenvolvimento

* **Matheus**
* **Andre**
* **Guilherme**
* **Caio**
* **Carlos**
* **Moisés**
* **Pablo**

---

## 1. Tecnologias e Arquitetura

* **Linguagem:** Java 17 (LTS)
* **Framework:** Spring Boot 3.3.5 (Spring Web, Spring Data JPA, Spring Security, Bean Validation)
* **Segurança:** Autenticação stateless via **JWT (Bearer Token)** e hashing de senhas com **BCrypt**
* **Base de Dados:** MySQL 8.0 (`bebidas_db`, codificação `utf8mb4`)
* **Arquitetura em Camadas:** `Controller` $\rightarrow$ `Service` $\rightarrow$ `Repository` $\rightarrow$ `Entity` / `DTO`, com tratamento global de exceções padronizado via `@RestControllerAdvice`.

---

## 2. Modelagem e Dicionário de Dados

### Relacionamentos (DER Textual)
* `usuarios (1) ────< (N) movimentacoes` — Rastreia o operador autenticado que registou cada operação.
* `fornecedores (1) ────< (N) insumos` — Vincula cada insumo ao seu fornecedor principal.
* `insumos (1) ────< (N) movimentacoes` — Mantém o histórico completo de entradas e saídas por item.
* `setores (1) ────< (N) movimentacoes` — Identifica obrigatoriamente o setor produtivo de destino nas saídas.

### Estrutura das Tabelas (`bebidas_db`)

| Tabela | Colunas Principais | Restrições e Regras |
| :--- | :--- | :--- |
| **`usuarios`** | `id`, `nome`, `login`, `senha`, `ativo` | `login` único; `senha` encriptada com BCrypt; inativação lógica (`ativo`). |
| **`setores`** | `id`, `nome`, `descricao`, `ativo` | `nome` único (ex.: *Linha de Envase 01*, *Xaroparia e Formulação*). |
| **`fornecedores`** | `id`, `razao_social`, `cnpj`, `telefone`, `email`, `ativo` | `cnpj` único; registo prévio obrigatório para associação de insumos. |
| **`insumos`** | `id`, `codigo`, `nome`, `unidade_medida`, `quantidade_atual`, `estoque_minimo`, `fornecedor_id`, `ativo` | `codigo` único; `unidade_medida` (`UN`, `KG` ou `L`); `quantidade_atual` inicia em `0`. |
| **`movimentacoes`** | `id`, `tipo`, `quantidade`, `saldo_anterior`, `saldo_posterior`, `data_hora`, `observacao`, `insumo_id`, `setor_id`, `usuario_id` | `tipo` (`ENTRADA` ou `SAIDA`); auditoria transacional de saldos antes e depois da operação. |

---

## 3. Regras de Negócio e Tratamento de Erros

Todas as respostas de erro seguem o contrato JSON padrão:
```json```
{
  "status": 400,
  "mensagem": "Descrição detalhada do erro",
  "timestamp": "2026-11-24T19:58:18-03:00"
}
* **Autenticação Obrigatória (`401 Unauthorized`):** Apenas `POST /usuarios` e `POST /auth/login` são públicos; todas as restantes rotas exigem `Authorization: Bearer <token>`.
* **Bloqueio de Saldo Insuficiente (`409 Conflict`):** Saídas com quantidade superior ao saldo disponível (`quantidadeAtual`) são rejeitadas sem alterar a base de dados.
* **Validação de Unidade Inteira (`400 Bad Request`):** Insumos medidos em unidades (`UN`) rejeitam quantidades com casas decimais.
* **Setor Obrigatório na Saída (`400 Bad Request`):** Toda movimentação do tipo `SAIDA` exige um `setorId` válido e ativo.
* **Monitorização de Estoque Crítico:** Insumos ativos com `quantidadeAtual < estoqueMinimo` são sinalizados automaticamente como `"CRITICO"`.

---

## 4. Catálogo de Endpoints (26 Rotas)

| Módulo | Métodos | Endpoints | Descrição |
| :--- | :--- | :--- | :--- |
| **Autenticação** | `POST` | `/auth/login` | Autentica o operador e devolve o token JWT (`Bearer`). |
| **Usuários** | `POST`, `GET`, `PUT`, `DELETE` | `/usuarios`, `/usuarios/me`, `/usuarios/{id}` | Registo público, perfil autenticado, consulta, edição e inativação. |
| **Setores** | `POST`, `GET`, `PUT`, `DELETE` | `/setores`, `/setores/{id}` | Gestão completa dos setores fabris. |
| **Fornecedores** | `POST`, `GET`, `PUT`, `DELETE` | `/fornecedores`, `/fornecedores/{id}` | Gestão completa dos fornecedores de insumos. |
| **Insumos** | `POST`, `GET`, `PUT`, `DELETE` | `/insumos`, `/insumos/{id}` | Gestão de matérias-primas e embalagens com controlo de estoque mínimo. |
| **Movimentações** | `POST`, `GET` | `/movimentacoes`, `/movimentacoes/{id}` | Registo transacional de entradas/saídas e consulta de histórico. |
| **Alertas** | `GET` | `/movimentacoes/alertas` | Relatório em tempo real de insumos em situação crítica. |

---

## 5. Como Executar e Testar

1. Crie a base de dados no MySQL 8.0:
   ```sql
   CREATE DATABASE bebidas_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Configure o utilizador e a palavra-passe do MySQL em `src/main/resources/application.properties`.
3. Execute a aplicação através da classe `IndustriaBebidasApplication` (porta `8080`).
4. Importe a coleção Postman disponível na pasta `postman/` do repositório, execute **01. Cadastrar Usuário** e **02. Login** para autenticar automaticamente e teste os restantes fluxos.