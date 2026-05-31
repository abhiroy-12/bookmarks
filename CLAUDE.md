# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is
A monorepo with two deployables:
- `api/`  — Spring Boot 3 REST service (Java 21, Maven). Manages bookmarks.
- `web/`  — React + Vite + TypeScript SPA that consumes the API.

> Note: the codebase is not yet scaffolded — neither `api/` nor `web/` exists on
> disk yet. The conventions below define how new code should be created.

## Conventions
- Java: package `com.example.bookmarks`. Constructor injection, records for DTOs,
  `jakarta.validation` for input validation. No field injection.
- REST: base path `/api`. Return DTOs, never JPA entities, from controllers.
- Tests required for every service method and controller (JUnit 5 + MockMvc).
- TypeScript: strict mode on. No `any`. Functional components + hooks.
- Conventional Commits (`feat:`, `fix:`, `test:`, `chore:`).

## Commands (Windows)
- API build/test:   cd api ; .\mvnw.cmd verify
- API run locally:  cd api ; .\mvnw.cmd spring-boot:run
- API single test:  cd api ; .\mvnw.cmd test "-Dtest=ClassName#methodName"
- Web dev server:   cd web ; npm run dev
- Web build:        cd web ; npm run build

## Definition of done
- Code compiles, tests pass (.\mvnw.cmd verify and npm run build are green).
- New endpoints have a controller test. New logic has a unit test.
- No secrets committed. Config via env vars.
