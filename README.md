# Vericode - A Code Review Platform

> Verify + Code. A lightweight code review platform where developers submit pull requests, get automated multi-layer analysis, and receive structured peer feedback, all grounded in Gang of Four design patterns.

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Design Patterns](#design-patterns)
- [Team](#team)

---

## Overview

Software development teams struggle to maintain code quality when multiple developers collaborate on a shared codebase. Without a structured review process, bugs, security vulnerabilities, and style violations frequently slip into production.

Vericode addresses this by providing a platform where:
- Developers **submit pull requests** with code snippets
- Code is **automatically analyzed** through a three-layer pipeline: lint, style, and security
- Peer reviewers **examine results**, leave line-level comments, and move submissions through a strict lifecycle
- Every action is **logged and undoable** via a full audit trail

Every subsystem is grounded in a **Gang of Four design pattern**, demonstrating how abstract software design principles map directly to real-world engineering workflows.

---

## Features

- **PR Submission** - submit code with title, description, and language selection
- **Automated Check Pipeline** - lint, style, and security checks run automatically on submission
- **Review Lifecycle** - Draft, In Review, Changes Requested, Approved, Merged
- **Reviewer Actions** - approve, reject, request changes, add line-level comments
- **Undo Support** - every action is a Command object with full undo capability
- **Notifications** - status changes notify all registered observers via email, in-app, or WebSocket
- **Multi-language Support** - Java, Python, JavaScript
- **React Frontend** - PR list, detail view, check results panel, inline comments, status controls

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.x |
| API | REST (JSON), Spring MVC |
| Frontend | React 18, HTML5, CSS3 |
| Build Tool | Maven |
| Database | H2 (in-memory) |
| Python Microservice | Flask, Pylint |
| JavaScript Microservice | Node.js, Express, ESLint |
| Version Control | Git, GitHub |

---

## UML
![Vericode UML](/Code Review Process Pipeline-2026-03-16-020819.png)

---

## Design Patterns

| # | Pattern | Where Used |
|---|---|---|
| 1 | API and Loose Coupling | REST controllers - frontend never touches business logic directly |
| 2 | OOP Principles | Base classes `CodeChecker`, `PullRequest`, `Review`, `Comment` |
| 3 | Adapter | `CheckstyleAdapter` wraps Checkstyle library to fit `CodeChecker` interface |
| 4 | Bridge | `NotificationService` decoupled from `DeliveryChannel` (email, in-app, WebSocket) |
| 5 | Builder | `PullRequestBuilder` constructs PR objects step by step |
| 6 | Command | `ApproveCommand`, `RejectCommand`, `CommentCommand` - logged, undoable |
| 7 | Composite | `Review` contains a collection of `Comment` objects |
| 8 | Decorator | `LintDecorator`, `StyleDecorator`, `SecurityDecorator` chain |
| 9 | Facade | `ReviewFacade` - single entry point coordinating all subsystems |
| 10 | Factory | `CheckerFactory` returns correct language checker at runtime |
| 11 | Flyweight | `CheckRulePool` - lint and style rules stored once, shared across all PR checks |
| 12 | Observer | `PRStatusObserver` - all listeners notified on status change |
| 13 | Prototype | `ReviewTemplateRegistry` - pre-built checklists cloned per PR |
| 14 | Singleton | `ReviewSessionManager` - one shared instance across the application |
| 15 | State | `DraftState`, `InReviewState`, `ChangesRequestedState`, `ApprovedState`, `MergedState` |
| 16 | Strategy | `JavaCheckStrategy`, `PythonCheckStrategy`, `JSCheckStrategy` |
| 17 | Template | `CodeReviewTemplate` - fixed skeleton with overridable steps |

---

## Team

Arundhati Bandopadhyaya - bandopadhyaya.a@northeastern.edu    
Keya Goswami - goswami.ke@northeastern.edu  
Shreya Wanisha - wanisha.s@northeastern.edu   
TBD

---

> CSYE 6300 - Software Design Patterns - Spring 2026 - Group 3