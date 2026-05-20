# AI Markdown Guide — New Spring Boot application in Hexagonal Architecture

> Objective: provide the AI with the rules to follow to generate a new Spring Boot application compliant with the Confluence recommendations reviewed. > ## > Design principle: this document is exclusively derived from the Confluence pages listed below. No external rule, dependency version, or convention not present in these pages should be considered normative. ## 1. Confluence documentation scope Traversed root page:

| ID | Title | Confluence URL |
|---:|---|---|
| 5942182362 | Hexagonal Architecture - in practice | `/spaces/SIPWM/pages/5942182362/Hexagonal+Architecture+-+in+practice` |

Descendant pages identified and traversed via `ancestor = **5942182362**`:

| ID | Title | Confluence URL |
|---:|---|---|
| 5941462863 | API to Domain Validation Strategy | `/spaces/SIPWM/pages/5941462863/API+to+Domain+Validation+Strategy` |
| 5948047452 | Command/Reply Queue Validation Strategy | `/spaces/SIPWM/pages/5948047452/Command+Reply+Queue+Validation+Strategy` |
| 5947424769 | Event Queue Validation Strategy | `/spaces/SIPWM/pages/5947424769/Event+Queue+Validation+Strategy` |
| 5941692148 | Handling Complex Transactions (Transactional Worker Pattern) | `/spaces/SIPWM/pages/5941692148/Handling+Complex+Transactions+Transactional+Worker+Pattern` |
| 5942019639 | Mapping Between Layers (DTOs, Entities, Domain) | `/spaces/SIPWM/pages/5942019639/Mapping+Between+Layers+DTOs+Entities+Domain` |
| 5942019537 | Naming Conventions for Outbound Ports | `/spaces/SIPWM/pages/5942019537/Naming+Conventions+for+Outbound+Ports` |

## The returned set of descendant pages contains 6 pages. The recommendations below synthesize the root page and these 6 pages.

## 2. General instruction to the generating AI The AI must generate a Spring Boot application strictly following a Maven multi-module hexagonal architecture. The business core must remain independent of technology. Any dependency must point inward: technical layers depend on the core, never the other way around.

Priority rules: ## Clearly separate `domain`, `application`, `adapter-in`, `adapter-out`, and `bootstrap`. ## Never introduce HTTP logic, database logic, messaging, or serialization logic in the `domain`. ## Never place `@Transactional` in the `application` layer. ## Never let generated DTOs, JPA/R2DBC entities, or technical objects leak into the `domain`. ## Validate technical contracts at the inbound or outbound adapter level, before calling the application core or before sending a message. ## Use ports to express the needs of the application core, and adapters for the technical implementations. ## 7. Use mappers located in the adapters to translate between DTOs, entities, and domain objects. ## 3. Expected Maven structure The root page recommends a Maven multi-module setup intended to strengthen the separation of responsibilities. ```text project-structure.txt root/ ├── pom.xml ├── domain/ ├── application/ ├── adapter/ │   ├── adapter-in/ │   └── adapter-out/ └── bootstrap/ ```

### 3.1. Root parent module

The root `pom.xml`:
- contains no source code;
- acts as the main Maven aggregator;
- declares the sub-modules in `<modules>`;
- centralizes versions via `<dependencyManagement>`;
- holds common properties (e.g., the Java version if the project defines one).

### 3.2. `domain` module

Role: the application’s absolute core. It carries business knowledge.

Expected content:
- pure business model;
- business rules;
- invariants;
- guard clauses;
- inbound and outbound ports;
- domain services when business logic does not naturally fit within a single business object.

Recommended package structure:

| Package | Role |
|---|---|
| `<root>.domain.model` | DDD entities and Value Objects, not JPA, rich in business logic. |
| `<root>.domain.port.in` | Inbound ports: what the application knows how to do. |
| `<root>.domain.port.out` | Outbound ports: what the application needs. |
| `<root>.domain.service` | Pure domain services. |
| `<root>.domain.common` | Cross-cutting business objects, e.g., `Notification` and structured errors. |

Rules:
- no coupling to Spring;
- pure Java objects;
- no persistence annotations such as `@Entity`;
- no serialization annotations such as `@JsonProperty`;
- no input/output logic;
- dependencies limited to pure, framework-agnostic libraries with no I/O.

Dependencies cited as acceptable by the Confluence pages, as long as they remain framework-agnostic and without I/O:
- `reactor-core` for reactive types;
- `lombok` for code generation;
- `org.apache.commons:commons-lang3`;
- `org.apache.commons:commons-collections4`.

### 3.3. `application` module

Role: orchestrate domain objects to execute use cases.

Responsibilities:
- implement the inbound ports defined in `domain`;
- coordinate the steps of a use case;
- call domain models and outbound ports;
- act as a mediator between adapters and business rules.

Indicated structure:

| Package | Role |
|---|---|
| `<root>.application.service` | Application services / use case implementations. |
| `<root>.application.validation` | Application validations requiring outbound ports, when necessary. |

Strong constraints:
- depends only on `domain`;
- no **HTTP** logic;
- no direct database queries;
- no messaging logic;
- no infrastructure dependency;
- `@Transactional` is strictly forbidden in this layer.

The `domain` / `application` separation is described as a pragmatic choice: it makes it possible to distinguish timeless business rules from the orchestration of use cases. If this separation is adopted, `application` must be protected with the same rigor as `domain`.

Regarding Spring in `application`:
- the dependency on Spring must be avoided;
- if it is indispensable for dependency injection, it must be limited to the strict minimum, for example `spring-context`;
- the main risk is the accidental use of technical annotations such as `@Async` or `@Cacheable`, which would pollute business orchestration.

### 3.4. `adapter` module

The `adapter` module serves as the Maven parent for inbound and outbound adapters, to share configuration if needed.

#### 3.4.1. `adapter-in` module

Role: connect external clients to the application.

Recommended packages:

| Package | Role |
|---|---|
| `<root>.adapter.in.web.v1` | REST controllers for API version 1. |
| `<root>.adapter.in.messaging` | Message listeners. |
| `<root>.adapter.in.web.exception` | Centralized HTTP exception handling. |
| `<root>.adapter.in.web.mapper` | Mapping API DTOs to domain or pure commands, and vice versa. |

Characteristics:
- depends on `application` to call use cases;
- contains Spring beans exposing the application, e.g., `@RestController` or `@JmsListener`;
- contains **API** DTOs or DTOs generated from OpenAPI;
- performs technical contract validation on the inbound side.

#### 3.4.2. `adapter-out` module

(…continuation not provided in your text…)