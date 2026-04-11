# ABRE System

**AI-powered incident response system for microservices.**

Detects service failures through observability metrics, diagnoses root causes using a RAG-powered runbook database, and executes automated recovery (container restarts, circuit breaker adjustments) via infrastructure APIs. Built with LangGraph4j agent orchestration, multi-tier LLM routing ((GPT-4o-mini → Claude Sonnet → GPT-4o), and PostgreSQL pgvector for semantic search.

## Architecture Overview

ABRE orchestrates autonomous incident response through five integrated components:
```
        Observability          Agent Control Plane           Infrastructure
      ┌──────────────┐       ┌──────────────────┐         ┌──────────────┐
      │ Prometheus   │──────→│  LangGraph4j FSM │────────→│ Docker API   │
      │ Loki         │       │  ModelRouter     │         │ Railway API  │
      │ Tempo        │       │  Tool Layer      │         └──────────────┘
      └──────────────┘       └────────┬─────────┘
              ↑                       │
              │                       ↓
      ┌───────────────┐       ┌──────────────┐
      │ Services      │       │ RAG Engine   │
      │ Orders (8081) │       │ pgvector DB  │
      │ Payments(8082)│       │ Runbooks     │
      └───────────────┘       └──────────────┘
```
**Flow:** Services fail → Agent detects via metrics → RAG searches past incidents → ModelRouter picks LLM tier → Tool layer executes fix → Resolution stored in knowledge base

### Components

**Agent Control Plane** — LangGraph4j state machine orchestrates incident response through 9 nodes with conditional multi-turn loops. Routes LLM calls across 3 tiers (GPT-4o-mini → Claude Sonnet → GPT-4o) based on confidence scoring.

**RAG Engine** — PostgreSQL database with pgvector extension stores runbook corpus. Provides semantic search for similar past incidents and auto-generates new runbooks when encountering novel failures.

**Tool Layer** — Executes read operations (metrics queries, log searches) and write operations (container restarts, circuit breaker adjustments) with four-layer safety guardrails.

**Monitored Services** — Two Spring Boot microservices (Orders, Payments) with configurable failure injection for realistic incident simulation.

**Observability Stack** — Prometheus for metrics, Loki for logs, Tempo for traces. Agent queries these systems to detect and diagnose incidents.

## 🎥 Demo

> **Live deployment**: [TBD - Will be deployed on Railway]  
> **Video walkthrough**: [TBD - 3-minute Loom demo]  
> **Public dashboard**: [TBD - Grafana Cloud read-only access]


## Tech Stack

**Backend**: Java 21 • Spring Boot 3.5 • LangGraph4j  
**AI/LLM**: OpenAI GPT-4o • Anthropic Claude • Spring AI  
**Database**: PostgreSQL 16 with pgvector (semantic search)  
**Observability**: Grafana Cloud • Prometheus • Loki • Tempo  
**Deployment**: Railway • Docker

## Key Features

- **Real infrastructure actions** - Restarts containers via Railway API
- **Self-improving knowledge base** - Auto-generates runbooks from novel incidents using RAG
- **Multi-tier LLM routing** - Escalates from cheap models (GPT-4o-mini) to more expensive ones (GPT-4o) based on confidence
- **Production-grade safety** - 4-layer guardrails prevent destructive actions (allowlists, rate limits, cooldowns, confidence gates)
- **Measurable performance tracking** - Evaluation suite with 15 test cases and 5-way baseline comparator

## Data Flows

- Services generate operational data (logs, metrics, events) consumed by the agent-control-plane for incident detection
- RAG engine provides domain knowledge to enrich incident analysis and suggest remediation steps
- Control plane orchestrates automated responses or alerts based on detected incidents
- 
## Quick Start

### Prerequisites
- Java 21+
- Docker & Docker Compose
- OpenAI API key
- Anthropic API key (for Claude Sonnet tier)

### Setup

```bash
# Clone and navigate
git clone https://github.com/gladyshernandez/abre-system.git
cd abre-system

# Start infrastructure (Postgres, Prometheus, Grafana)
cd infra && docker-compose up -d

# Set up environment variables
cp .env.example .env
# Edit .env and add your OPENAI_API_KEY

# Run the services
cd services/order-service && ./gradlew bootRun
# In another terminal:
cd services/payment-service && ./gradlew bootRun

# Test it works
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"test-123","amount":99.99}'
```

**View dashboards**: Prometheus at `localhost:9090`, Grafana at `localhost:3000` (admin/admin)

## Why I Built This

I wanted to build something that takes action - detects the failure, diagnoses the cause, and executes the fix.

The risk is letting an AI make infrastructure changes. I added layered safety: allowlists restrict which services can be touched, rate limits cap actions per incident, cooldowns prevent restart loops, and confidence gates block uncertain moves. The RAG database validates against past successful fixes before executing anything.
##  Architecture Decisions

**Multi-tier LLM routing**: Instead of using one expensive model for everything, ABRE starts with GPT-4o-mini ($0.008/incident) and only escalates to Claude Sonnet or GPT-4o when confidence is low. This routing strategy is designed to balance cost and quality—starting with cheap models for simple incidents and only escalating to expensive models when needed.

**Real API calls**: The agent calls Railway's API to restart containers.

**Self-improving corpus**: When the agent encounters a new type of incident it hasn't seen before, it generates a runbook and stores it in pgvector. Future similar incidents resolve faster without escalating to expensive models.

## Evaluation Results

*Results will be added after completing 5-way baseline comparison*

| Approach | Accuracy | Action Recall | Avg Cost | Latency |
|----------|----------|---------------|----------|---------|
| Single-turn baseline | TBD | TBD | TBD | TBD |
| Tier 1 only (GPT-4o-mini) | TBD | TBD | TBD | TBD |
| Tier 2 only (Claude Sonnet) | TBD | TBD | TBD | TBD |
| Tier 3 only (GPT-4o) | TBD | TBD | TBD | TBD |
| **Dynamic router (ABRE)** | **TBD** | **TBD** | **TBD** | **TBD** |

**Target**: TBD% accuracy, +TBD% action recall vs baseline at 3.4x cost

## Project Structure

```
abre-system/
├── services/           # Microservices being monitored (Orders, Payments)
├── agent-control-plane/   # LangGraph4j state machine, ModelRouter, Tool layer
├── rag-engine/         # pgvector + runbook generation
├── eval/               # 15 test cases, baseline comparator, LLM-as-judge implementations
├── infra/              # Docker Compose for local dev
└── docs/               # Architecture decisions, API docs
```

## 📝 License

MIT License - see [LICENSE](LICENSE)

---