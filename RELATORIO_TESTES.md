# Relatório de Testes e Validação — API REST Almoxarifado

**Projeto:** Sistema de Controle de Almoxarifado — Indústria de Bebidas  
**Disciplina:** Programação de Aplicativos — Projeto Integrador Módulo 2  
**Ferramenta de Verificação:** Postman (`postman/industria-bebidas-collection.json`)  
**Base URL:** `http://localhost:8080/api`

---

## 1. Resumo da Execução

Foram executados e validados os **22 Casos Mínimos de Aceitação (`CT01` a `CT22`)** definidos no Contrato Técnico da API REST, cobrindo autenticação JWT, gestão de cadastros, inativação lógica com preservação de histórico, regras de movimentação de estoque, alertas críticos, integridade transacional e controle de concorrência.

* **Total de casos previstos:** 22
* **Casos aprovados:** 22 (100%)
* **Coleção reproduzível:** Disponível em `postman/industria-bebidas-collection.json`

---

## 2. Matriz de Resultados (`CT01` a `CT22`)

| Caso | Cenário / Entrada | Resultado Esperado | Resultado Observado | Evidência / Endpoint | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **CT01** | Cadastrar usuário válido (`nome`, `login`, `senha` >= 8 chars) | HTTP `201 Created`; JSON sem o campo `senha` | HTTP `201 Created` retornado com `id`, `nome`, `login` e `ativo: true`; senha omitida | `POST /api/usuarios` | Aprovado |
| **CT02** | Tentar cadastrar usuário com `login` já existente | HTTP `409 Conflict` | HTTP `409 Conflict` com mensagem padronizada de duplicidade e `timestamp` | `POST /api/usuarios` | Aprovado |
| **CT03** | Autenticar com credenciais válidas de usuário ativo | HTTP `200 OK` e token JWT válido por 3600s | HTTP `200 OK` retornando `token`, `tipo: "Bearer"` e `expiraEm: 3600` | `POST /api/auth/login` | Aprovado |
| **CT04** | Autenticar com senha incorreta ou usuário inativo | HTTP `401 Unauthorized` | HTTP `401 Unauthorized` bloqueando emissão do token | `POST /api/auth/login` | Aprovado |
| **CT05** | Requisitar endpoint protegido sem cabeçalho `Authorization` | HTTP `401 Unauthorized` | HTTP `401 Unauthorized` com mensagem `"Autenticação necessária"` | `GET /api/usuarios/me` | Aprovado |
| **CT06** | Criar setor e fornecedor com dados válidos e CNPJ de 14 dígitos | HTTP `201 Created` e persistência no MySQL | Registros persistidos em `setores` e `fornecedores` com `ativo: true` | `POST /api/setores` e `POST /api/fornecedores` | Aprovado |
| **CT07** | Criar insumo vinculado a fornecedor ativo | HTTP `201 Created` com `quantidadeAtual: 0` | HTTP `201 Created` com saldo inicial exatamente igual a `0` | `POST /api/insumos` | Aprovado |
| **CT08** | Listar e pesquisar insumos com filtro parcial (`?nome=garrafa`) | HTTP `200 OK`; busca case-insensitive em ordem alfabética | Retornada lista filtrada ignorando caixa e ordenada crescentemente por `nome` | `GET /api/insumos?nome=garrafa` | Aprovado |
| **CT09** | Registrar movimentação de `ENTRADA` com quantidade `100` | HTTP `201 Created`; `saldoAtual = saldoAnterior + 100` | Saldo incrementado em `100` e registro gravado em `movimentacoes` | `POST /api/movimentacoes` | Aprovado |
| **CT10** | Registrar movimentação de `SAIDA` com quantidade `30` e setor ativo | HTTP `201 Created`; `saldoAtual = saldoAnterior - 30` | Saldo reduzido em `30`, vínculo com `setorId` e `usuarioId` do JWT | `POST /api/movimentacoes` | Aprovado |
| **CT11** | Registrar `SAIDA` com quantidade superior ao saldo disponível | HTTP `409 Conflict`; sem alteração parcial de saldo | HTTP `409 Conflict` (`"Quantidade solicitada superior ao estoque disponível"`) | `POST /api/movimentacoes` | Aprovado |
| **CT12** | Registrar movimentação com quantidade zero ou negativa (`<= 0`) | HTTP `400 Bad Request` | HTTP `400 Bad Request` (`"A quantidade deve ser maior que zero"`) | `POST /api/movimentacoes` | Aprovado |
| **CT13** | Registrar movimentação para insumo com `ativo = false` | HTTP `409 Conflict` | HTTP `409 Conflict` (`"Insumo inativo"`) impedindo alteração de saldo | `POST /api/movimentacoes` | Aprovado |
| **CT14** | Consultar histórico aplicando filtros (`tipo`, `insumoId`, período) | HTTP `200 OK`; apenas registros compatíveis ordenados por `dataRegistro` desc | Lista retornada respeitando todos os filtros e ordenação decrescente | `GET /api/movimentacoes` | Aprovado |
| **CT15** | Consultar painel de alertas de estoque crítico | HTTP `200 OK`; apenas insumos ativos com `quantidadeAtual <= estoqueMinimo` | Retornados somente insumos ativos críticos ordenados por nome com `situacao: "CRITICO"` | `GET /api/alertas/estoque` | Aprovado |
| **CT16** | Excluir (`DELETE`) insumo que já possui histórico de movimentações | HTTP `204 No Content`; inativação lógica (`ativo: false`) | Retornado `204`; consulta por ID mantém registro com `ativo: false` e histórico intacto | `DELETE /api/insumos/{id}` | Aprovado |
| **CT17** | Reativar cadastro inativo enviando `ativo: true` via `PUT` | HTTP `200 OK`; registro volta a `ativo: true` | Cadastro reativado com sucesso e visível novamente nas listagens padrão | `PUT /api/insumos/{id}` | Aprovado |
| **CT18** | Listar cadastros utilizando parâmetro `?ativo=todos` | HTTP `200 OK`; inclui registros ativos e inativos | Lista completa retornada exibindo registros com `ativo: true` e `ativo: false` | `GET /api/insumos?ativo=todos` | Aprovado |
| **CT19** | Simular falha durante gravação de movimentação | Rollback transacional integral (`@Transactional`) | Nenhuma alteração persistida no saldo do insumo nem na tabela de histórico | `MovimentacaoService` | Aprovado |
| **CT20** | Executar duas retiradas simultâneas disputando saldo insuficiente | Apenas a primeira conclui (`201`); excedente falha com `409` sem saldo negativo | Bloqueio pessimista no banco (`PESSIMISTIC_WRITE`) impediu saldo negativo; segunda retornou `409` | `POST /api/movimentacoes` (concorrência) | Aprovado |
| **CT21** | Informar `dataMovimentacao` posterior à data atual do servidor | HTTP `400 Bad Request` | HTTP `400 Bad Request` (`"A data da movimentação não pode ser futura"`) | `POST /api/movimentacoes` | Aprovado |
| **CT22** | Registrar `ENTRADA` informando `lote` e `validade` opcionais | HTTP `201 Created`; valores preservados no histórico | Registro criado mantendo `lote` e `validade` exatamente como enviados | `POST /api/movimentacoes` | Aprovado |

---

## 3. Como Reproduzir os Testes

1. Importar o arquivo `postman/industria-bebidas-collection.json` no Postman.
2. Executar a requisição **01. Cadastrar Usuário** e em seguida **02. Login** (o script de teste da coleção armazena o token JWT automaticamente na variável de ambiente).
3. Executar sequencialmente as pastas **Setores**, **Fornecedores**, **Insumos**, **Movimentações** e **Alertas**, ou utilizar o **Collection Runner** do Postman para validar todas as rotas em lote.