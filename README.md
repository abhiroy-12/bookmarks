# Bookmarks

A monorepo with two deployables:

- **`api/`** — Spring Boot 3 REST service (Java 21, Maven). Manages bookmarks.
- **`web/`** — React + Vite + TypeScript SPA that consumes the API.

> Status: not yet scaffolded — `api/` and `web/` don't exist on disk yet.
> The commands and conventions below define how the projects are intended to be built.

## Prerequisites

- Java 21
- Node.js 20+ and npm

## Getting started

### API (`api/`)

```powershell
cd api
.\mvnw.cmd spring-boot:run     # run locally
.\mvnw.cmd verify              # build + run tests
```

The service is served under the base path `/api`.

### Web (`web/`)

```powershell
cd web
npm install
npm run dev                    # start the Vite dev server
npm run build                  # production build
```

## Conventions

- **Java:** package `com.example.bookmarks`. Constructor injection, records for DTOs,
  `jakarta.validation` for input validation. No field injection.
- **REST:** base path `/api`. Controllers return DTOs, never JPA entities.
- **TypeScript:** strict mode on, no `any`. Functional components + hooks.
- **Tests:** required for every service method and controller (JUnit 5 + MockMvc).
- **Commits:** Conventional Commits (`feat:`, `fix:`, `test:`, `chore:`).

## Configuration

Configuration is supplied via environment variables — no secrets are committed.

## License

TBD
