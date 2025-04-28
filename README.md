
# 📄 Desafio API - Documentação

Essa API gerencia operações de pagamentos, como criação, atualização de status, consulta e exclusão lógica.
A base já está pré-carregada com registros de exemplo.

## 📂 Endpoints

### 1. Criar um novo pagamento
**POST /pagamentos**

Exemplo de JSON para criação:

```json
{
  "codigoDebito": 12345,
  "cpfOuCnpj": "12345678901",
  "metodoPagamento": "Cartão de Crédito",
  "numeroCartao": "1234 5678 9012 3456",
  "valorPagamento": 150.00
}
```

### 2. Atualizar status do pagamento
**PUT /pagamentos/{id}/status**

Exemplo de JSON para atualização:

```json
{
  "statusPagamento": "Processado com Sucesso"
}
```

O status enviado deve estar na lista de status aceitos.

Caso tente atualizar para um status inválido, será retornado um erro.

### 3. Listar pagamentos com filtros
**GET /pagamentos**

#### Parâmetros de Filtro (tags):

| Nome da tag      | Tipo   | Descrição                                                                |
|------------------|--------|--------------------------------------------------------------------------|
| codigoDebito     | Integer| Código de débito associado ao pagamento.                                 |
| cpfOuCnpj        | String | CPF ou CNPJ associado ao pagamento (parcial).                            |
| statusPagamento  | String | Status atual do pagamento (parcial ou completo).                        |

Todos os filtros são opcionais e podem ser combinados entre si.

#### 🎯 Observação sobre a paginação

Foi optado por utilizar paginação no endpoint de listagem com filtros para garantir a performance e escalabilidade da API.
Isso evita o retorno de grandes volumes de dados em uma única requisição, mantendo a resposta ágil e leve.

A paginação utiliza parâmetros padrão do Spring:

- **page** (número da página, começando em 0)
- **size** (quantidade de itens por página)
- **sort** (campo e direção de ordenação, ex.: `sort=cpfOuCnpj,asc`)

#### Exemplo de requisição paginada:

```bash
GET /pagamentos?codigoDebito=12345&cpfOuCnpj=123&statusPagamento=Processado&page=0&size=5&sort=valorPagamento,desc
```

#### Exemplo de resposta paginada:

```json
{
  "content": [
    {
      "id": 1,
      "codigoDebito": 12345,
      "cpfOuCnpj": "12345678901",
      "metodoPagamento": "Cartão de Crédito",
      "numeroCartao": "1234 5678 9012 3456",
      "valorPagamento": 150.00,
      "statusPagamento": "Processado com Sucesso",
      "status": true
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "size": 5,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

### 4. Exclusão lógica de pagamento
**DELETE /pagamentos/{id}**

A exclusão lógica só será realizada se o status atual do pagamento for "Pendente de Processamento".

Caso o pagamento esteja com outro status, a operação será bloqueada.

#### Exemplo de requisição:

```bash
DELETE /pagamentos/2
```

#### Exemplo de resposta em sucesso:

```json
{
  "message": "Pagamento inativado com sucesso."
}
```

#### Se o status não for "Pendente de Processamento", será retornado erro:

```json
{
  "error": "Apenas pagamentos com status 'Pendente de Processamento' podem ser inativados."
}
```

## 📋 Status de Pagamento Aceitos

A aplicação só aceita atualização para os seguintes status:

- Pendente de Processamento
- Processado com Sucesso
- Processado com Falha
- Cancelado

Tentativas de alterar o status para qualquer valor diferente desses resultarão em erro.

## 📦 Dados pré-carregados

Já existem dados no banco de dados para facilitar testes manuais. Exemplos de registros incluem:

| Código Débito | CPF/CNPJ       | Método de Pagamento | Valor   | Status Pagamento           |
|---------------|----------------|---------------------|---------|----------------------------|
| 12345         | 12345678901    | Cartão de Crédito   | 150.00  | Processado com Sucesso     |
| 67890         | 09876543210    | Boleto              | 250.00  | Pendente de Processamento  |
| 54321         | 98765432100    | Pix                 | 75.50   | Pendente de Processamento  |
