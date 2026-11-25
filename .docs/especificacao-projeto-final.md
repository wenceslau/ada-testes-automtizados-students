# AdaLib — Sistema de Gestão de Biblioteca & Empréstimos

**Especificação de Projeto Final: Testes Automatizados**  
Projeto final do módulo: aplica, em uma aplicação **Java 17 + Spring Boot 3**, toda a **Pirâmide de Testes** vista nas Aulas 01 a 08 — Testes Unitários (JUnit 5 + Mockito), Testes de Fatia Web (`@WebMvcTest` / `MockMvc`), Desenvolvimento Guiado por Testes (TDD), Testes de Integração (`@SpringBootTest` + H2), Testes de Interface Web (Selenium WebDriver + Page Object Model) e Cobertura de Código (JaCoCo).

**Equipe:** 3–4 pessoas · **Entrega:** Aula 09 · **Apresentação:** 6 min + 2 min de perguntas

---

## Sumário

1. [Visão geral](#01--visão-geral)
2. [Formação das equipes e aplicação própria](#02--formação-das-equipes-e-aplicação-própria)
3. [Escopo funcional obrigatório](#03--escopo-funcional-obrigatório)
4. [Requisitos técnicos obrigatórios (Pirâmide de Testes)](#04--requisitos-técnicos-obrigatórios-pirâmide-de-testes)
5. [Casos de uso](#05--casos-de-uso)
6. [Arquitetura sugerida](#06--arquitetura-sugerida)
7. [Contrato de API](#07--contrato-de-api)
8. [Modelo de dados](#08--modelo-de-dados)
9. [Tratamento de erros](#09--tratamento-de-erros)
10. [Mapa da Pirâmide de Testes e Decisões de Qualidade](#10--mapa-da-pirâmide-de-testes-e-decisões-de-qualidade)
11. [Regra da Feature em TDD (`CalculadoraMultaAtrasoService`)](#11--regra-da-feature-em-tdd-calculadoramultaatrasoservice)
12. [Cronograma sugerido](#12--cronograma-sugerido)
13. [Entregáveis](#13--entregáveis)
14. [Critérios de avaliação](#14--critérios-de-avaliação)
15. [Extras (escopo aberto)](#15--extras-escopo-aberto)
16. [Referências](#16--referências)

---

## 01 · Visão geral

A **AdaLib**, uma biblioteca universitária e comunitária fictícia, precisa modernizar o controle do seu acervo e dos empréstimos de livros. O sistema gerencia o cadastro de livros, o controle de exemplares disponíveis, os empréstimos realizados pelos leitores, as devoluções com cálculo automático de multa por atraso e uma tela web simples de consulta/simulação.

O que é avaliado neste projeto **não é a quantidade de telas ou endpoints**, e sim a **qualidade, confiabilidade e arquitetura da suíte de testes automatizados**: se cada camada é testada com a ferramenta adequada, se casos de borda e exceções são cobertos, se a funcionalidade de multas foi construída com TDD, se a cobertura do JaCoCo mede *branches* reais (e não testes sem asserções) e se a equipe sabe **justificar suas decisões de teste**.

Como vai funcionar a avaliação?
- A execução completa e reproduzível da suíte em um único comando (`mvn clean test jacoco:check`);
- A **cobertura real de linhas e ramificações (*branches*)** no relatório HTML do JaCoCo (`target/site/jacoco/index.html`);
- A **justificativa das decisões de qualidade**: por que determinado cenário foi testado como Unitário em vez de Integração, por que usamos `WebDriverWait` em vez de `Thread.sleep`, e quais falhas/casos de borda os testes protegem.

---

## 02 · Formação das equipes e aplicação própria

Mesma equipe usada no modulo anterior

---

## 03 · Escopo funcional obrigatório

Quatro capacidades integradas em uma única aplicação Spring Boot (`ada-biblioteca`):

### 1. Gestão do Acervo de Livros
Cadastrar livros com `isbn`, `titulo`, `autor`, `categoria` (`COMUM`, `TECNICO`, `RARO`), `valorReposicao` e `copiasDisponiveis`; consultar livro pelo `isbn` e listar o acervo. Não permite ISBN duplicado, cópias negativas nem valor de reposição inválido.

### 2. Empréstimos de Exemplares
Registrar o empréstimo de um livro para um leitor (`emailLeitor`) definindo a data prevista de devolução. Regras obrigatórias:
- Só empresta se o livro existir e possuir **pelo menos 1 cópia disponível** (`copiasDisponiveis > 0`);
- Cada leitor pode ter no máximo **3 empréstimos ativos simultaneamente** (`StatusEmprestimo.ATIVO`);
- Ao confirmar o empréstimo, o sistema **decrementa em 1** `copiasDisponiveis`, grava o empréstimo como `ATIVO` e aciona o `NotificacaoBibliotecaService` para avisar o leitor.

### 3. Devolução e Cálculo de Multa por Atraso (Feature em TDD)
Registrar a devolução de um empréstimo ativo informando a data real da devolução e se o leitor é estudante (`leitorEstudante`):
- Impede devolver um empréstimo inexistente ou **já devolvido**;
- Calcula automaticamente os dias de atraso e a multa através do **`CalculadoraMultaAtrasoService`** (construído em ciclos TDD conforme a seção 11);
- Atualiza o status para `DEVOLVIDO`, **devolve `+1`** para `copiasDisponiveis` do livro e retorna o resumo com dias de atraso e valor da multa.

### 4. Interface Web da Biblioteca (`biblioteca.html`)
Uma página HTML estática servida pelo próprio Spring Boot (`/biblioteca.html`) permitindo cadastrar/visualizar livros no catálogo e simular o cálculo de multa por atraso, com feedback visual assíncrono (`200ms`) para exercitar **Selenium WebDriver + `WebDriverWait` + Page Object Model**.

> **Nota sobre código base (*Starter*) e Bugs Ocultos:** Se o instrutor disponibilizar o projeto inicial (`starter`) sem testes, ele poderá conter **bugs propositais sutis** nas regras de negócio (por exemplo, condição de fronteira incorreta ou falta de atualização de cópias). É parte do trabalho da equipe **encontrar esses bugs através dos testes automatizados, corrigi-los e documentá-los no README**!

---

## 04 · Requisitos técnicos obrigatórios (Pirâmide de Testes)

Cada linha remete a uma aula do módulo. Na apresentação, a equipe deve conseguir apontar no código onde cada item foi implementado.

| Requisito | O que precisa existir | Aula de referência |
| :--- | :--- | :--- |
| **Testes Unitários & JUnit 5** | Padrão **AAA**, princípios **F.I.R.S.T.**, `@BeforeEach`, `@DisplayName` claros, validação de exceções com `assertThrows` e ao menos um `@ParameterizedTest` (`@CsvSource`). | Aula 01 |
| **Isolamento com Mockito** | Testes unitários de `LivroService` e `EmprestimoService` com `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`, `when/thenReturn`, `verify(..., times(1))`, `verify(..., never())` e ao menos um `ArgumentCaptor`. | Aula 02 |
| **Testes de Fatia Web** | Testes de `LivroController` e `EmprestimoController` com `@WebMvcTest`, `MockMvc` e `@MockitoBean` (ou `@MockBean`), validando rotas, `200`, `201` + header `Location`, `400`, `404`, `422`, `JsonPath` e `@RestControllerAdvice`. | Aula 03 |
| **Desenvolvimento com TDD** | `CalculadoraMultaAtrasoService` construído em ciclos `Red → Green → Refactor`, cobrindo 100% das regras da seção 11 sem código morto nem números mágicos. | Aulas 04 e 05 |
| **Testes de Integração + H2** | Suíte com `@SpringBootTest`, `@AutoConfigureMockMvc`, `@Transactional` e banco H2 configurado em `src/test/resources/application.properties`, fazendo **dupla verificação** (resposta HTTP + estado real gravado no banco via `Repository`). | Aula 06 |
| **Interface Web com Selenium** | Suíte com `@SpringBootTest(webEnvironment = RANDOM_PORT)`, Selenium 4 (`ChromeDriver` em modo `--headless=new`), **Page Object Model (`BibliotecaPage`)**, seletores resilientes (`By.id` / `By.cssSelector` / `Select`), `WebDriverWait` (zero `Thread.sleep`) e `driver.quit()` no `@AfterEach`. | Aula 07 |
| **Cobertura & Quality Gate (JaCoCo)** | `jacoco-maven-plugin` configurado no `pom.xml` gerando `target/site/jacoco/index.html` e passando em `mvn test jacoco:check` com **mínimo de 80% de Line Coverage e Branch Coverage** (e **100% de Branch Coverage** na classe `CalculadoraMultaAtrasoService`). | Aula 08 |
| **Documentação & Defesa** | `README.md` completo com divisão de frentes, como rodar, Mapa da Pirâmide de Testes (seção 10), bugs encontrados (se houver) e extras (seção 15). | Aula 09 |

---

## 05 · Casos de uso

### UC1 · Cadastrar livro
- **Fluxo principal:** Recebe `isbn`, `titulo`, `autor`, `categoria`, `valorReposicao` e `copiasDisponiveis`. Valida que o ISBN não está cadastrado, que `valorReposicao > 0` e `copiasDisponiveis >= 0`. Salva no banco e responde **201 Created** com header `Location: /api/livros/{isbn}`.
- **Exceções:** ISBN duplicado → **422** · Campos inválidos (valor `<= 0`, cópias negativas, título/ISBN em branco) → **400**.

### UC2 · Consultar livro por ISBN e listar acervo
- **Fluxo principal:** `GET /api/livros` retorna lista (`200 OK`); `GET /api/livros/{isbn}` retorna o livro (`200 OK`).
- **Exceção:** ISBN inexistente → **404 Not Found**.

### UC3 · Realizar empréstimo
- **Fluxo principal:** Recebe `isbn`, `emailLeitor` e `diasPrazo` (padrão/mínimo `1` dia). Busca o livro, verifica `copiasDisponiveis > 0`, verifica que o leitor possui menos de **3 empréstimos com status `ATIVO`**, decrementa `copiasDisponiveis`, persiste `Emprestimo` com status `ATIVO`, aciona `NotificacaoBibliotecaService.notificarEmprestimo(...)` e responde **201 Created** com header `Location: /api/emprestimos/{id}`.
- **Exceções:** Livro inexistente → **404** · Sem cópias disponíveis ou leitor já com 3 empréstimos ativos → **422** · E-mail/prazo inválido → **400**.

### UC4 · Consultar empréstimo por ID
- **Fluxo principal:** `GET /api/emprestimos/{id}` retorna os dados do empréstimo (`200 OK`).
- **Exceção:** ID inexistente → **404 Not Found**.

### UC5 · Registrar devolução e calcular multa
- **Fluxo principal:** `PATCH /api/emprestimos/{id}/devolver` recebe `dataDevolucao` e `leitorEstudante`. Verifica que o empréstimo existe e está `ATIVO`, calcula os dias de atraso em relação à `dataPrevistaDevolucao`, delega o valor da multa ao `CalculadoraMultaAtrasoService`, marca o empréstimo como `DEVOLVIDO`, incrementa `copiasDisponiveis` do livro em `+1` e responde **200 OK** com o resumo da devolução.
- **Exceções:** Empréstimo inexistente → **404** · Empréstimo já devolvido → **422** · Data de devolução anterior à data do empréstimo → **400**.

### UC6 · Interagir com o Portal Web (`biblioteca.html`)
- **Fluxo principal:** Via navegador Chrome controlado pelo Selenium WebDriver, abrir `/biblioteca.html`, verificar itens iniciais, cadastrar um novo livro pelo formulário validando alerta assíncrono (`WebDriverWait`), validar erro visual em campo obrigatório vazio e simular um cálculo de multa selecionando categoria (`<select>`) e checkbox de estudante.

---

## 06 · Arquitetura sugerida

```text
com.ada.biblioteca
├── BibliotecaApplication.java
├── domain/        (CategoriaLivro, StatusEmprestimo, Livro, Emprestimo)
├── repository/    (LivroRepository, EmprestimoRepository)
├── service/       (LivroService, EmprestimoService, CalculadoraMultaAtrasoService, NotificacaoBibliotecaService, ConsoleNotificacaoBibliotecaService)
├── controller/    (LivroController, EmprestimoController, dto/*)
└── exception/     (RecursoNaoEncontradoException, RegraNegocioException, ApiErroResponse, GlobalExceptionHandler)
```

Nos testes (`src/test/java/com/ada/biblioteca/`), espelhe a estrutura separando claramente as camadas da Pirâmide (`service/`, `controller/`, `integration/`, `ui/`).

---

## 07 · Contrato de API

| Método | Rota | Corpo / Parâmetros | Sucesso | Erros Esperados |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/livros` | `{"isbn", "titulo", "autor", "categoria", "valorReposicao", "copiasDisponiveis"}` | `201 Created` + Header `Location` | `400`, `422` (ISBN duplicado) |
| `GET` | `/api/livros` | — | `200 OK` (lista JSON) | — |
| `GET` | `/api/livros/{isbn}` | Path `isbn` | `200 OK` | `404` |
| `POST` | `/api/emprestimos` | `{"isbn", "emailLeitor", "diasPrazo"}` | `201 Created` + Header `Location` | `400`, `404`, `422` (sem cópia / limite 3 ativos) |
| `GET` | `/api/emprestimos/{id}` | Path `id` | `200 OK` | `404` |
| `PATCH` | `/api/emprestimos/{id}/devolver` | `{"dataDevolucao": "YYYY-MM-DD", "leitorEstudante": true}` | `200 OK` (`DevolucaoResponse`) | `400`, `404`, `422` (já devolvido) |

---

## 08 · Modelo de dados

- **`Livro`:** `id` (Long), `isbn` (String único, obrigatório), `titulo` (String obrigatório), `autor` (String obrigatório), `categoria` (`COMUM`, `TECNICO`, `RARO`), `valorReposicao` (double `> 0`), `copiasDisponiveis` (int `>= 0`).
- **`Emprestimo`:** `id` (Long), `livro` (`@ManyToOne` `Livro`), `emailLeitor` (String obrigatório contendo `@`), `dataEmprestimo` (`LocalDate`), `dataPrevistaDevolucao` (`LocalDate`), `dataDevolucaoReal` (`LocalDate`, nulo enquanto ativo), `valorMulta` (double), `status` (`ATIVO`, `DEVOLVIDO`).

---

## 09 · Tratamento de erros

Todas as respostas de erro devem ser interceptadas por `GlobalExceptionHandler` (`@RestControllerAdvice`) e retornar o formato padronizado `ApiErroResponse`:

```json
{
  "status": 422,
  "erro": "Violação de regra de negócio",
  "mensagem": "Leitor já atingiu o limite máximo de 3 empréstimos ativos.",
  "timestamp": "2026-12-09T15:30:00"
}
```

| Exceção | Status HTTP |
| :--- | :---: |
| `IllegalArgumentException` | `400 Bad Request` |
| `RecursoNaoEncontradoException` | `404 Not Found` |
| `RegraNegocioException` | `422 Unprocessable Entity` |

---

## 10 · Mapa da Pirâmide de Testes e Decisões de Qualidade

No `README.md` do projeto, a equipe deve preencher uma tabela justificando **onde** cada tipo de cenário foi testado e **por quê** (este mapa será um dos pontos centrais das perguntas da apresentação):

| Camada da Pirâmide | Classes de Teste do Projeto | O que valida na prática? | Por que testar nesta camada (e não em outra)? |
| :--- | :--- | :--- | :--- |
| **Unitário (JUnit 5 + Mockito)** | `LivroServiceTest`, `EmprestimoServiceTest`, `CalculadoraMultaAtrasoServiceTest` | Regras de negócio, fronteiras, exceções, `verify`/`never` e `ArgumentCaptor` | Executa em milissegundos, isola dependências externas e permite testar todas as ramificações (`&&`/`\|\|`) sem banco |
| **Fatia Web (`@WebMvcTest`)** | `LivroControllerTest`, `EmprestimoControllerTest` | Rotas, verbos HTTP, `201+Location`, `JsonPath`, `404`/`422` via `@RestControllerAdvice` | Valida o contrato HTTP/JSON sem pagar o custo de subir Hibernate e banco H2 |
| **Integração (`@SpringBootTest` + H2)** | `BibliotecaIntegrationTest` | Fluxo real Controller → Service → JPA → H2 com baixa e estorno de cópias | Garante que mapeamentos `@Entity`, consultas Spring Data e transações funcionam juntos |
| **Interface Web (Selenium + POM)** | `BibliotecaPage`, `BibliotecaSeleniumTest` | Fluxos críticos visuais em `biblioteca.html` com `WebDriverWait` | Garante que HTML, formulários e JavaScript assíncrono respondem corretamente no navegador |
| **Cobertura (JaCoCo)** | `pom.xml` (`jacoco:check`) | Cobertura de linhas e *branches* (`>= 80%` global, `100%` no serviço TDD) | Funciona como radar contra caminhos esquecidos e trava builds sem cobertura no CI |

---

## 11 · Regra da Feature em TDD (`CalculadoraMultaAtrasoService`)

Esta classe **deve ser desenvolvida com TDD (`Red → Green → Refactor`)** e atingir **100% de Line Coverage e 100% de Branch Coverage** no relatório do JaCoCo:

1. **Validações de entrada (*Guard Clauses*):**
   - `diasAtraso < 0`, `categoria == null` ou `valorReposicao <= 0` → lança `IllegalArgumentException`.
2. **Período de carência (*Grace Period*):**
   - Atraso de até **2 dias corridos** (`diasAtraso <= 2`) → multa de **`R$ 0,00`** (isento).
3. **Tarifa diária por categoria (para atrasos acima de 2 dias, cobrada sobre todos os dias de atraso):**
   - `COMUM`: **`R$ 2,00`** por dia de atraso;
   - `TECNICO`: **`R$ 4,00`** por dia de atraso;
   - `RARO`: **`R$ 10,00`** por dia de atraso.
4. **Desconto de Estudante:**
   - Se `leitorEstudante == true` **e** a categoria **não** for `RARO` (`categoria != CategoriaLivro.RARO`), aplica **50% de desconto** sobre o valor calculado da multa. *(Livros raros não possuem desconto de estudante!)*
5. **Teto Máximo da Multa:**
   - A multa nunca pode ultrapassar **100% do `valorReposicao`** do livro (`Math.min(multaCalculada, valorReposicao)`).
6. **Arredondamento:**
   - Resultado arredondado para 2 casas decimais.

---

## 12 · Cronograma sugerido

O projeto evolui em paralelo às aulas do módulo (início em **23/11**):

| Quando | Meta Sugerida para a Equipe |
| :--- | :--- |
| **Aula 01** | Leitura da especificação e compreensão do domínio `AdaLib`. |
| **Até a Aula 03** | Equipes formadas (3–4 pessoas), divisão de frentes registrada e testes unitários de Service (`Aulas 01–02`) + Controller (`Aula 03`) iniciados. |
| **Aulas 04 e 05** | Desenvolvimento da `CalculadoraMultaAtrasoService` em ciclos **TDD (`Red → Green → Refactor`)** e escolha dos **Extras** (seção 15) junto ao instrutor. |
| **Aula 06** | Implementação da suíte de integração `BibliotecaIntegrationTest` com `@SpringBootTest` + H2 + `@Transactional`. |
| **Aula 07** | Implementação do Page Object `BibliotecaPage` e dos testes Selenium (`BibliotecaSeleniumTest`) contra `biblioteca.html`. |
| **Aula 08** | Configuração do `jacoco-maven-plugin`, eliminação de losangos amarelos (*Branch Coverage*), validação do `mvn clean test jacoco:check` e fechamento do `README.md`. |
| **Aula 09** | Últimos ajustes, upload do `.zip` no LMS, apresentação (6 min + 2 min de perguntas), autoavaliação e devolutiva pela rubrica. |

---

## 13 · Entregáveis

- **Código (`.zip` sem a pasta `target/`):** upload no LMS. *Importante:* o projeto é em equipe, mas **o upload é individual** — cada integrante envia o mesmo `.zip` no LMS.
- **`README.md`** contendo:
  - nomes dos **integrantes** e a **divisão de frentes** (quem ficou responsável por qual frente);
  - comando exato para rodar todos os testes e gerar o JaCoCo (`mvn clean test jacoco:check`);
  - o **Mapa da Pirâmide de Testes e Decisões de Qualidade** (seção 10) preenchido pela equipe;
  - o **Relatório de Bugs Encontrados e Corrigidos** (se o grupo partiu do projeto *starter* com bugs ocultos);
  - descrição dos **extras** (seção 15) implementados e como verificá-los.
- **Apresentação da equipe na Aula 09** (**6 minutos + 2 minutos de perguntas**). Todos os integrantes precisam estar presentes e **qualquer integrante** pode ser chamado a responder às perguntas:
  1. **Visão geral e divisão de frentes** (1 min);
  2. **Demonstração ao vivo** (3 min): rodar `mvn test jacoco:check`, abrir `target/site/jacoco/index.html` mostrando `CalculadoraMultaAtrasoService` (100% verde) e executar 1 teste Selenium (pode mostrar visualmente ou em headless!);
  3. **Uma decisão de testes justificada + Extras implementados** (1 min);
  4. **Um bug ou dificuldade real encontrada e como os testes ajudaram a resolver** (1 min).

---

## 14 · Critérios de avaliação

O projeto é a principal evidência para a **avaliação por rubrica individual** do módulo. Os 4 níveis seguem o padrão oficial da escola:

1. **Não conhece** a habilidade.
2. **Conhece**, mas não é capaz de aplicar.
3. **É capaz de aplicar em escopo fechado:** reproduz com qualidade o que foi construído em aula, aplicado ao projeto `AdaLib`, atendendo toda a seção 04.
4. **É capaz de aplicar em escopo aberto:** resolve algo novo ou decide por conta própria e **justifica** (extras da seção 15 funcionando, domínio próprio ou aprofundamento técnico além do exigido).

| Critério | O que é observado |
| :--- | :--- |
| **Unitários & JUnit 5** | Padrão AAA, F.I.R.S.T., casos de borda, `assertThrows` e `@ParameterizedTest`. |
| **Mockito (Service)** | `@Mock`, `@InjectMocks`, `when/thenReturn`, `verify(..., never())` e `ArgumentCaptor`. |
| **Controller (`MockMvc`)** | `@WebMvcTest`, `@MockitoBean`, status HTTP (`200`, `201+Location`, `400`, `404`, `422`), `JsonPath` e `@RestControllerAdvice`. |
| **TDD (`Red → Green → Refactor`)** | `CalculadoraMultaAtrasoService` limpo, cobrindo 100% das regras e sem código desnecessário. |
| **Integração (`@SpringBootTest` + H2)** | Fluxo completo real com dupla verificação (HTTP + estado no banco H2) e isolamento via `@Transactional`. |
| **Interface Web (Selenium)** | Page Object Model, seletores resilientes, `WebDriverWait` (sem `Thread.sleep`), `--headless=new` e `driver.quit()`. |
| **Cobertura (JaCoCo)** | `mvn clean test jacoco:check` passando (`>= 80%` global, `100%` branch no serviço TDD) e **zero testes sem assert**. |
| **Extras & Apresentação** | Pelo menos **1 extra** (ou **2** para equipes de 4+ integrantes) funcionando e explicado, além da clareza nas respostas individuais. |

> **Avaliação individual dentro da equipe:** Mesmo com entrega em grupo, a rubrica é **individual** (integrantes da mesma equipe podem ficar em níveis diferentes), considerando as respostas individuais às perguntas da apresentação, a divisão de frentes no `README.md` e a participação nas aulas.

---

## 15 · Extras (escopo aberto)

Escolha **pelo menos 1 extra** (**2 extras** para equipes de 4 ou mais pessoas) e combine com o instrutor até a **Aula 05**. Faça poucos e faça-os muito bem:

- **Teste de Mutação com PITest (`pitest-maven`):** Configurar o plugin PITest para a camada `service`, rodar `mvn test-compile org.pitest:pitest-maven:mutationCoverage` e documentar no `README.md` a taxa de mutantes mortos (*Mutation Score*) e algum mutante sobrevivente eliminado!
- **Testes de Arquitetura com ArchUnit:** Adicionar `archunit-junit5` garantindo regras automáticas (ex.: *"Classes em `controller` não podem acessar diretamente classes em `repository`"* e *"Classes anotadas com `@Service` devem ficar no pacote `service`"*).
- **Testcontainers com PostgreSQL:** Substituir o H2 por um container Docker real de PostgreSQL (`org.testcontainers:postgresql`) em uma suíte de integração dedicada.
- **Pipeline de CI no GitHub Actions:** Criar `.github/workflows/ci.yml` rodando automaticamente `mvn clean test jacoco:check` em Ubuntu com Chrome Headless a cada *push* ou *Pull Request* (mostrando o badge ou execução verde!).
- **Simulação de API Externa com WireMock:** Integrar uma consulta de metadados por ISBN ou envio de notificação HTTP externa e testá-la isoladamente com **WireMock**.
- **Fluxo Extra de Selenium com Filtro/Busca Dinâmica:** Adicionar busca por título/categoria na `biblioteca.html` e criar um segundo Page Object ou testes de filtragem dinâmica com `WebDriverWait`.
- **Testes de Regressão de Validação com Bean Validation (`@Valid`):** Adicionar `spring-boot-starter-validation` nos DTOs e testar no `MockMvc` as mensagens detalhadas por campo (`400 Bad Request`).

---

## 16 · Referências

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing Reference](https://docs.spring.io/spring-boot/reference/testing/index.html)
- [Selenium WebDriver Documentation](https://www.selenium.dev/documentation/webdriver/)
- [JaCoCo Maven Plugin Documentation](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [PIT Mutation Testing (PITest)](https://pitest.org/)
- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html)
