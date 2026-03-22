# abre-system
Agentic Backend Reliability Engineer - AI-powered incident detection and diagnosis

## Architecture Overview

This project implements an AI-powered incident detection and diagnosis system for backend reliability engineering. The architecture consists of:

- **agent-control-plane/**: Core orchestration layer for AI agents that detect incidents and coordinate responses across monitored services
- **rag-engine/**: Retrieval-Augmented Generation engine providing contextual knowledge for incident diagnosis and resolution recommendations
- **services/**: Sample microservices ecosystem being monitored, including:
    - `gateway-service/`: API gateway handling request routing and load balancing
    - `order-service/`: Order processing and management service
    - `payment-service/`: Payment processing and transaction handling
- **eval/**: Evaluation framework with test cases, reports, and scoring mechanisms for validating AI agent performance

## Data Flows

- Services generate operational data (logs, metrics, events) consumed by the agent-control-plane for incident detection
- RAG engine provides domain knowledge to enrich incident analysis and suggest remediation steps
- Control plane orchestrates automated responses or alerts based on detected incidents

## Key Structural Decisions

- Microservices architecture allows independent scaling and monitoring of business domains (orders, payments)
- Separation of AI components (control plane, RAG) from monitored services enables focused development and testing
- Gateway service centralizes external API access for consistent monitoring and security

## Developer Workflows

*Note: Project appears to be in early architectural planning phase with no implemented build system or runtime commands yet.*

## Project Conventions

- Service directories follow `{domain}-service/` naming pattern (e.g., `order-service/`, `payment-service/`)
- AI components use descriptive names without service suffix (`agent-control-plane/`, `rag-engine/`)
- Evaluation components organized under `eval/` with subdirectories for `cases/`, `reports/`, `scorers/`

## Integration Patterns

- Services designed for inter-service communication through the gateway
- Agent components integrate via monitoring hooks into service operational data streams
- RAG engine serves as knowledge provider to control plane agents

## Key Directories

- `services/`: Core business logic and monitoring targets
- `agent-control-plane/`: Primary AI orchestration code
- `rag-engine/`: Knowledge retrieval and generation logic
- `eval/cases/`: Test scenarios for agent evaluation
