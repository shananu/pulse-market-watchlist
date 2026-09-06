# PULSE

### Your watchlist watches the market. You watch what matters.

Pulse is an intelligent market watchlist designed around a simple question:

> **What actually deserves my attention?**

Traditional watchlists show investors everything that happened. Pulse filters the noise, detects meaningful changes, measures their significance, explains why they matter, and shows the evidence behind the conclusion.

And when nothing important happens, Pulse says so.

---

## The Problem

A watchlist is supposed to help investors monitor the stocks they care about.

In practice, it creates another problem:

- Prices move constantly.
- Volume changes constantly.
- News arrives constantly.
- Markets move for reasons that may or may not matter.
- Investors still have to manually scan everything.

The result is **information overload**.

The difficult problem is not:

> "Can we show market data?"

It is:

> **"Can we determine what changed enough to deserve a person's attention?"**

Pulse is built around solving that problem.

---

# The Pulse Loop

```text
DETECT
   ↓
FILTER
   ↓
CORRELATE
   ↓
RANK
   ↓
EXPLAIN
   ↓
EVIDENCE
   ↓
REVIEW
   ↓
REMEMBER

Pulse turns raw market observations into an attention-oriented experience.

1. Detect

Identify unusual price, volume, and relative-performance behavior.

2. Filter

Avoid turning every small movement into an alert.

3. Correlate

Look for multiple independent signals occurring together.

4. Rank

Calculate a deterministic significance score.

5. Explain

Translate the signals into a concise explanation.

6. Evidence

Show the observations supporting the conclusion.

7. Review

Let the user mark an attention item as reviewed.

8. Remember

Track the user's checkpoint so Pulse can tell what changed since their last visit.

Product Experience
Attention Center

The home screen answers:

What deserves my attention?

Instead of displaying a wall of market data, Pulse surfaces only meaningful changes.

Example:

HDFC Bank                         SIGNIFICANT

-3.31%       12.0σ       2.69×       -3.81%
Price Move    Anomaly     Volume      vs Market

Price moved unusually while volume accelerated.

Three independent signals agree this deserves attention.

Attention Score
80 / 100

The remaining stocks are summarized as:

5 other stocks — Nothing material detected.

This is intentional.

Silence is a valid product outcome.

What Makes a Change Meaningful?

Pulse uses a deterministic significance engine rather than asking an AI model to decide whether a market movement matters.

The current score combines:

Signal	Weight
Price anomaly	30%
Relative performance	25%
Event relevance	20%
Volume anomaly	15%
Persistence	10%

A convergence bonus is applied when multiple strong independent signals agree.

Significance =
    30% Price Anomaly
  + 25% Relative Performance
  + 20% Event Relevance
  + 15% Volume Anomaly
  + 10% Persistence
  + Convergence Bonus
Significance Bands
0 – 30       NORMAL
30 – 50      WATCH
50 – 70      NOTABLE
70 – 100     SIGNIFICANT

The system intentionally favors precision over recall.

A noisy alert system destroys attention.

Signal Intelligence
Price Anomaly

Pulse compares the current return against historical volatility.

daily return =
(current price - previous price) / previous price

z-score =
absolute(current return) / historical volatility

The baseline uses recent historical observations rather than a hardcoded threshold.

Volume Anomaly

Current volume is compared against the historical average.

volume ratio =
current volume / historical average volume

Higher ratios increase the volume anomaly score.

Relative Performance

A stock's movement is compared with the broader market.

divergence =
stock return - benchmark return

This prevents Pulse from treating a market-wide move as a stock-specific event.

For example:

HDFC Bank       -3.31%
Benchmark       +0.50%

Divergence      -3.81%
Persistence

Pulse considers whether an unusual movement persists across observations.

A temporary spike should not automatically become a major attention event.

Evidence, Not Guesswork

Pulse separates detection from explanation.

The intelligence engine determines:

what changed
how unusual it was
how strongly the signals agree
how significant the event is

The explanation layer receives those structured observations and turns them into understandable context.

The system does not allow an AI model to invent market facts or determine significance.

Design Principle

Deterministic intelligence decides what matters. AI helps explain it.

If an explanation service is unavailable, the product can still present the underlying evidence and deterministic explanation.

Attention Lifecycle

Every meaningful event follows a simple lifecycle:

DETECTED
   ↓
SURFACED
   ↓
VIEWED
   ↓
REVIEWED

If a previously reviewed event changes materially, it can become relevant again.

This prevents Pulse from repeatedly surfacing the same unchanged information while still allowing genuinely new developments to regain attention.

Market Replay

Pulse includes a deterministic market replay designed to demonstrate the complete intelligence pipeline.

The replay feeds simulated market observations through the same processing flow used by the application:

Market Data
     ↓
Normalization
     ↓
Snapshots
     ↓
Signal Calculation
     ↓
Significance Engine
     ↓
Attention Event
     ↓
Pulse UI

Example HDFC Bank scenario:

09:30   Normal trading
10:32   Corporate event appears
10:42   Volume begins accelerating
11:03   Price declines
11:24   Supporting information arrives
12:15   Move reaches -2.4%
14:30   Move reaches approximately -3.2%

This allows the entire product story to be demonstrated in minutes rather than waiting for a live market event.

Architecture
┌─────────────────────────────────────┐
│              Next.js                │
│         TypeScript + Tailwind       │
└──────────────────┬──────────────────┘
                   │ REST
                   ▼
┌─────────────────────────────────────┐
│          Spring Boot Backend        │
│              Java 21                │
│                                     │
│  Watchlist                          │
│  Market Data                        │
│  Snapshots                          │
│  Signals                            │
│  Significance                       │
│  Attention                          │
│  Explanations                       │
└───────────────┬───────────┬─────────┘
                │           │
                ▼           ▼
        ┌────────────┐  ┌───────────┐
        │ PostgreSQL │  │   Redis   │
        │            │  │           │
        │ Source of  │  │ Fast      │
        │ truth      │  │ access    │
        └────────────┘  └───────────┘

Pulse intentionally uses a modular monolith rather than microservices.

For a focused hackathon product, this provides:

simpler development
fewer deployment dependencies
easier debugging
transactional consistency
clear domain boundaries
faster iteration

The architecture can be split into services later if scale requires it.

Technology Stack
Frontend
Next.js
TypeScript
Tailwind CSS
React
Backend
Java 21
Spring Boot
Spring Data JPA
REST APIs
Data
PostgreSQL
Redis
Flyway migrations
Engineering
Maven Wrapper
Git
Docker Compose
Core Data Model

The system is organized around several core entities:

User
 │
 ├── Watchlist
 │      └── Watchlist Stocks
 │
 ├── User Checkpoint
 │
 └── Attention Events

Stock
 │
 ├── Market Snapshots
 ├── Market Signals
 └── Market Events

Important persisted concepts include:

users
watchlists
watchlist stocks
market snapshots
market events
market signals
attention events
user checkpoints
user preferences
Key API Endpoints
Watchlists
GET    /api/watchlists
POST   /api/watchlists
GET    /api/watchlists/{id}/stocks
POST   /api/watchlists/{id}/stocks/{symbol}
DELETE /api/watchlists/{id}/stocks/{symbol}
Pulse
GET  /api/pulse
POST /api/pulse/check
POST /api/pulse/reset
Signals
GET /api/signals/{symbol}/price-anomaly
GET /api/signals/{symbol}/volume-anomaly
GET /api/signals/{symbol}/relative-performance
GET /api/signals/{symbol}/signals
Significance
POST /api/significance/evaluate
POST /api/significance/evaluate/{symbol}
POST /api/significance/demo-minute/{minute}
Attention
GET  /api/attention
POST /api/attention/{id}/view
POST /api/attention/{id}/review
POST /api/attention/reset
Demo

The application includes a deterministic demo market so the complete experience can be reproduced consistently.

Start the infrastructure
docker compose up -d
Start the backend

Windows:

cd backend
.\mvnw.cmd spring-boot:run
Start the frontend
cd frontend
npm install
npm run dev

Open:

http://localhost:3000
Recommended Demo Flow
1. Reset

Click:

Reset demo

The Attention Center should show:

0 meaningful changes
2. Replay

Click:

Replay market

Pulse progressively processes the HDFC Bank scenario.

3. Surface the event

The Attention Center eventually shows:

HDFC Bank
SIGNIFICANT

-3.31%
12σ
2.69×
-3.81% vs market

Attention Score: 80
4. Investigate

Click:

Why this matters →

Review:

signal evidence
market context
divergence
explanation
change replay
5. Review

Click:

Review

The event moves through the attention lifecycle.

Why This Approach?

There are many ways to build a market monitoring system.

Pulse deliberately avoids turning the project into a collection of infrastructure technologies.

Instead, the architecture is optimized around the user problem:

Reduce the amount of market information a user has to process manually.

That leads to several design decisions.

Deterministic Significance

Market significance should be reproducible and testable.

AI as an Explanation Layer

AI is useful for translating structured evidence into natural language, but should not silently invent facts or make the core decision.

Precision Over Recall

A system that generates dozens of low-value alerts quickly becomes another source of noise.

User Checkpointing

"Something changed" only makes sense relative to what the user has already seen.

Replayable Intelligence

The same processing pipeline should work for live observations and deterministic demonstrations.

Engineering Trade-offs

This project was designed and implemented under a strict hackathon time constraint.

The following were intentionally kept out of the initial implementation:

Kafka
microservice decomposition
vector databases
RAG pipelines
ML-based personalization
complex notification infrastructure
brokerage integration

These can be added later without changing the core product concept.

The priority was:

Correct product loop
        >
Reliable intelligence
        >
Explainability
        >
Demoability
        >
Infrastructure complexity
Future Roadmap
P1
richer market context
more corporate event types
adaptive attention preferences
richer change replay
live news correlation
improved explanation generation
P2
push notifications
multiple watchlists
daily/weekly intelligence digest
personalized attention models
broader asset coverage
production-grade streaming infrastructure
Product Philosophy

Pulse is built around a simple principle:

Attention is scarce.

A market application shouldn't force users to inspect every movement just because the system can display it.

The goal is not to show more information.

The goal is to surface better information at the right time.

Closing

A traditional watchlist tells you what happened.

Pulse tells you:

what changed, whether it's unusual, why it might matter, and what evidence supports that conclusion.

And when nothing matters:

Pulse gets out of your way.

Built for Groww CODE 2026

PULSE — Your watchlist watches the market. You watch what matters.