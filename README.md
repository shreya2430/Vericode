# VeriCode — Automated Code Review Platform

> **Verify + Code.** A collaborative pull request review platform where developers submit code, get automated multi-layer analysis (security, lint, style), and move submissions through a structured peer review lifecycle — built entirely around Gang of Four design patterns.

---

## Table of Contents

- [Demo](#demo)
- [Presentation](#presentation)
- [Running the Project](#running-the-project)
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [UML](#uml)
- [Design Patterns](#design-patterns)
- [Team](#team)

---

## Demo

> **Video walkthrough of VeriCode in action.**

[![VeriCode Demo](https://img.shields.io/badge/Watch%20Demo-Video-red?style=for-the-badge&logo=youtube)](https://youtu.be/3yHJnBltjtQ)

<!-- Replace the link above with your actual demo video URL (YouTube / Google Drive) -->

---

## Presentation

> **Project slides covering architecture, design patterns, and a live demo walkthrough.**

[![View Presentation](https://img.shields.io/badge/View%20Slides-Presentation-blue?style=for-the-badge&logo=files)](https://canva.link/xcf7610jwbd706q)

---

## Running the Project

Start all four services, each in a separate terminal.

### 1. Backend — Spring Boot (port 8080)

> Requires Java 17 and Maven

```bash
    cd backend
    mvn spring-boot:run
```

### 2. Python Checker Microservice (port 5001)

> Requires Python 3 and pip

```bash
    cd python-checker
    python3 -m venv venv
    source venv/bin/activate      # Windows: venv\Scripts\activate
    pip install flask pylint
    python app.py
```

### 3. JavaScript Checker Microservice (port 5002)

> Requires Node.js

```bash
    cd js-checker
    npm install
    npm start
```

### 4. Frontend — React + Vite (port 3000)

> Requires Node.js

```bash
    cd frontend
    npm install
    npm run dev
```

Open **http://localhost:3000** in your browser once all services are running.

---

## Overview

Software teams struggle to maintain code quality when multiple developers collaborate on a shared codebase. Without a structured review process, bugs, security vulnerabilities, and style issues frequently slip into production.

VeriCode addresses this by providing a platform where:

- Developers **submit pull requests** with code in Java, Python, or JavaScript
- Code is **automatically analyzed** through a three-layer pipeline: security, lint, and style checks
- Peer reviewers **examine results**, leave comments, and move PRs through a strict lifecycle
- **Real-time WebSocket updates** keep all viewers in sync as PR status changes

Every subsystem is grounded in a **Gang of Four design pattern**, demonstrating how classic software design principles map directly to real-world engineering workflows.

---

## Features

| Feature | Description |
|---|---|
| **PR Submission** | Submit code with title, description, and language selection |
| **Automated Check Pipeline** | Security, lint, and style checks run automatically on submission |
| **Real-time Updates** | WebSocket sync — all viewers see status changes instantly |
| **Review Lifecycle** | Draft → In Review → Changes Requested → Approved → Merged / Closed |
| **Role-based Rules** | Authors cannot approve, merge, or request changes on their own PR |
| **Pipeline Guardrail** | Users cannot approve a PR if the automated pipeline failed |
| **Delete PR** | Authors can delete their own PRs |
| **Multi-language Support** | Java (Checkstyle), Python (Pylint), JavaScript (ESLint) |
| **Deduplication** | Same violation on the same line is only reported once |
| **React Frontend** | PR list, detail view, check results panel, status controls, toast notifications |

---

## Architecture

```
┌─────────────────────────────────────────────────┐
│                React Frontend (5173)            │
└────────────────────┬────────────────────────────┘
                     │ REST + WebSocket
┌────────────────────▼────────────────────────────┐
│            Spring Boot Backend (8080)           │
│                                                 │
│  CheckerFactory                                 │
│    └── SecurityDecorator                        │
│          └── StyleDecorator                     │
│                └── LintDecorator                │
│                      └── StrategyCheckerAdapter │
│                            ├── JavaCheckStrategy   → Checkstyle
│                            ├── PythonCheckStrategy → :5001
│                            └── JSCheckStrategy     → :5002
└─────────────────────────────────────────────────┘
         │                          │
┌────────▼───────┐        ┌─────────▼──────┐
│ Python Checker │        │  JS Checker    │
│   Flask (5001) │        │ Express (5002) │
│   Pylint       │        │ ESLint         │
└────────────────┘        └────────────────┘
```

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Backend | Java, Spring Boot | 17 / 3.2.4 |
| API | REST (JSON), Spring MVC, WebSocket | — |
| Frontend | React, Vite | 18.3.1 / 5.3.1 |
| Build Tool | Maven | — |
| Database | MySQL | — |
| Python Microservice | Flask, Pylint | — |
| JS Microservice | Node.js, Express, ESLint | Express 4.21 / ESLint 8.57 |
| Version Control | Git, GitHub | — |

---

## UML

![VeriCode UML](/Code Review Process Pipeline-2026-03-16-020819.png)

---

## Design Patterns

| # | Pattern | Where Used |
|---|---|---|
| 1 | **Decorator** | `LintDecorator`, `StyleDecorator`, `SecurityDecorator` wrap the base checker in a chain |
| 2 | **Strategy** | `JavaCheckStrategy`, `PythonCheckStrategy`, `JSCheckStrategy` — swappable per language |
| 3 | **Factory** | `CheckerFactory` builds the correct decorator + strategy chain at runtime |
| 4 | **Proxy** | `CachingCheckerProxy` caches results to avoid redundant analysis |
| 5 | **Template Method** | `RemoteCheckTemplate` defines fixed skeleton; subclasses supply URL and service name |
| 6 | **Adapter** | `CheckstyleAdapter` wraps Checkstyle; `StrategyCheckerAdapter` bridges `CheckStrategy` → `CodeChecker` |
| 7 | **Flyweight** | `CheckRulePool` — lint, style, and security rules stored once, shared across all PR checks |
| 8 | **Observer** | `PRStatusObserver` — all listeners notified on status change |
| 9 | **State** | `DraftState`, `InReviewState`, `ChangesRequestedState`, `ApprovedState`, `MergedState` |
| 10 | **Facade** | `ReviewFacade` — single entry point coordinating all subsystems |
| 11 | **Command** | `ApproveCommand`, `RejectCommand`, `CommentCommand` — logged and undoable |
| 12 | **Builder** | `PullRequestBuilder` constructs PR objects step by step |
| 13 | **Composite** | `Review` contains a collection of `Comment` objects |
| 14 | **Prototype** | `ReviewTemplateRegistry` — pre-built checklists cloned per PR |
| 15 | **Singleton** | `ReviewSessionManager` — one shared instance across the application |
| 16 | **Bridge** | `NotificationService` decoupled from `DeliveryChannel` (email, in-app, WebSocket) |
| 17 | **Chain of Responsibility** | `PRValidationHandler` → `TitleValidationHandler` → `AuthorValidationHandler` → `LanguageValidationHandler` → `CodeSnippetValidationHandler` — each handler validates or passes to next |
| 18 | **API / Loose Coupling** | REST controllers — frontend never touches business logic directly |

---

## Team

| Name | Email |
|---|---|
| Shreya Wanisha | wanisha.s@northeastern.edu |
| Keya Goswami | goswami.ke@northeastern.edu |
| Arundhati Bandopadhyaya | bandopadhyaya.a@northeastern.edu |
| Maitri Mukesh Pasale | pasale.m@northeastern.edu |

---

> **CSYE 6300 — Software Design Patterns · Spring 2026 · Group 3 · Northeastern University**
