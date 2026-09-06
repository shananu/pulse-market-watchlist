# PULSE

### Your watchlist watches the market. You watch what matters.

**PULSE** is a smart market watchlist that detects meaningful changes across your portfolio/watchlist, ranks what deserves attention, explains why it matters, and shows the evidence behind the conclusion.

Instead of making users repeatedly scan prices, PULSE turns a watchlist into an **attention system**.

> A traditional watchlist tells you what happened.  
> PULSE tells you what changed, whether it matters, and why.

---

## 🚀 Live Demo

**Production:**  
https://frontend-kappa-three-uzb7061grc.vercel.app/

**Backend:**  
https://pulse-backend-mnar.onrender.com/

---

# The Problem

A traditional watchlist is mostly a collection of numbers.

When a user returns after several hours, they have to manually determine:

- Which stocks actually moved?
- Was the movement unusual?
- Did the stock move differently from the market?
- Was volume unusually high?
- Is there a meaningful event?
- Which changes deserve attention?
- What can safely be ignored?

The problem is not a lack of market information.

The problem is **too much information competing for attention**.

PULSE is designed around one question:

> **"What deserves my attention right now?"**

---

# What PULSE Does

PULSE continuously turns market observations into prioritized attention.

### The core loop

```text
Detect
  ↓
Filter
  ↓
Correlate
  ↓
Rank
  ↓
Explain
  ↓
Provide Evidence
  ↓
Review
  ↓
Remember

The system intentionally supports an important outcome:

Nothing important happened.

If none of the stocks in a watchlist have a meaningful change, PULSE does not manufacture alerts.

Product Experience
1. Attention Center

The home screen is designed around attention rather than raw market data.

Instead of showing a wall of prices, it answers:

What deserves my attention?

Example:

3 meaningful changes

HDFC Bank
-3.31%
SIGNIFICANT

12.0σ price anomaly
2.69× volume
-3.81% vs market

Why this matters →

Below the important changes:

22 other stocks

Nothing material detected.

This keeps the user focused on signal instead of noise.

2. Smart Watchlist

Users can:

Create a watchlist
Add stocks
Remove stocks
Persist their watchlist
View their tracked securities

The current demo includes stocks such as:

HDFC Bank
Reliance
Infosys
TCS
ICICI Bank
SBI

The watchlist is not just a collection of symbols.

It is the input to the intelligence engine that determines what deserves attention.

3. Market Intelligence

PULSE evaluates multiple independent signals.

Signal families
Signal	What it detects
Price Anomaly	Unusual price movement relative to historical volatility
Volume Anomaly	Unusual trading volume
Relative Performance	Divergence from the market/benchmark
Event Relevance	Potentially meaningful corporate events
Persistence	Whether a move persists instead of being a transient spike

The system combines these signals into a single significance score.

Significance Engine

PULSE uses a deterministic scoring system rather than allowing an LLM to decide whether something is important.

This makes the system:

Explainable
Testable
Reproducible
Easier to tune
Safer for financial information
Score composition
Signal	Weight
Price anomaly	30%
Relative performance	25%
Event relevance	20%
Volume anomaly	15%
Persistence	10%

A convergence bonus is added when multiple independent signals agree.

3 strong signals → +10
4+ strong signals → +15

The final score is capped at 100.

Significance bands
Score	Band
0–29	Normal
30–49	Watch
50–69	Notable
70–100	Significant

This creates a clear distinction between:

"The stock moved."

and

"The stock moved in a way that is unusual enough to deserve attention."

Statistical Signals
Price Anomaly

Historical daily returns are calculated from a rolling historical window.

daily return =
(current price - previous price) / previous price

Historical volatility is calculated from historical returns.

The current move is then evaluated relative to that volatility:

z-score =
absolute(current return) / historical volatility

The resulting anomaly is converted into a normalized score.

Volume Anomaly

Current volume is compared against historical volume:

volume ratio =
current volume / historical average volume

Example:

1.0× → normal
1.5× → notable
2.0× → significant
3.0× → extreme
Relative Performance

PULSE compares the stock's return with a benchmark.

divergence =
stock return - benchmark return

This helps distinguish:

The entire market is falling

from:

This stock is falling significantly more than the market.
Persistence

A temporary price spike should not automatically become a major attention event.

PULSE therefore considers whether a move persists across multiple observations.

1 interval   → weak
2 intervals  → moderate
3+ intervals → strong

This reduces noisy one-off alerts.

Attention Lifecycle

PULSE treats attention as a stateful process.

DETECTED
   ↓
SURFACED
   ↓
VIEWED
   ↓
REVIEWED

If an already-reviewed event changes materially, it can become relevant again.

REVIEWED
   ↓
NEW MATERIAL CHANGE
   ↓
SURFACED AGAIN

This prevents the system from repeatedly showing the same event while still allowing genuinely new information to resurface.

Last Checked State

A key part of PULSE is understanding the user's checkpoint.

The system stores:

lastCheckedAt

When the user returns, PULSE compares the current state against that checkpoint.

This allows the product to answer:

"What changed since I last checked?"

rather than simply:

"What is the market doing right now?"

Stock Intelligence

Selecting an attention item opens a deeper intelligence view.

It answers:

What changed?

Price movement and anomaly metrics.

Does it matter?

Significance score and signal convergence.

Why?

Relative performance, volume and supporting context.

Why trust it?

Evidence from the underlying signals and market observations.

The goal is to let users move from:

Attention
   ↓
Understanding
   ↓
Evidence

without leaving the product.

Market Replay

PULSE includes a deterministic market replay mode for demonstrating how attention evolves over time.

The demo scenario progressively changes the HDFC Bank market state:

09:30
Normal

10:10
Initial movement

10:20
Unusual price movement

10:40
Volume acceleration

11:00
Multiple signals converge

12:00+
Significant attention event

The replay feeds the same pipeline used by the application:

Market Data
    ↓
Normalization
    ↓
Signal Calculation
    ↓
Significance Engine
    ↓
Attention Engine
    ↓
Pulse

This is important because the demo is not simply displaying a hardcoded final result.

It demonstrates how the system's state changes as new market observations arrive.

Explanation Layer

The explanation layer converts structured market evidence into a concise human-readable explanation.

For example:

HDFC Bank is showing an unusually large move
relative to its historical behavior.

The move is accompanied by elevated volume and
material divergence from the broader market.

Multiple independent signals agree that this
deserves attention.

The explanation is grounded in the signals already calculated by the system.

Design principle

The explanation layer should explain the evidence.

It should not invent evidence.

The current hackathon implementation uses deterministic structured explanations, while the architecture leaves room for a grounded LLM explanation service in a future version.

Financial Safety Philosophy

PULSE is designed as an information and attention tool.

It does not attempt to:

Give buy/sell recommendations
Predict guaranteed future prices
Replace financial judgment
Manufacture certainty from incomplete data

The system focuses on:

What changed?
Does it look unusual?
Why might it matter?
What evidence supports that conclusion?
Data Quality

Market information can become stale or inconsistent.

PULSE therefore treats freshness as part of the intelligence pipeline.

Observations contain information such as:

value
observedAt
receivedAt
source
freshness

The product distinguishes between:

Fresh
Delayed
Stale

A stale observation should reduce confidence rather than silently being treated as current.

This is particularly important for a market-monitoring product.

Architecture

PULSE uses a modular monolith architecture.

                    ┌─────────────────────┐
                    │      Next.js        │
                    │     Frontend        │
                    └──────────┬──────────┘
                               │
                               │ REST
                               ▼
                    ┌─────────────────────┐
                    │    Spring Boot      │
                    │      Backend        │
                    ├─────────────────────┤
                    │ Watchlist            │
                    │ Market               │
                    │ Snapshots            │
                    │ Signals              │
                    │ Significance         │
                    │ Attention            │
                    │ Explanations         │
                    └───────┬───────┬─────┘
                            │       │
                            ▼       ▼
                    ┌──────────┐ ┌──────────┐
                    │PostgreSQL│ │  Redis   │
                    └──────────┘ └──────────┘
Why a modular monolith?

The project was built within a 48-hour hackathon constraint.

Instead of introducing unnecessary distributed infrastructure, the system keeps domain boundaries clear inside a single backend.

This gives:

Faster development
Easier debugging
Lower operational complexity
Clear separation of concerns
A straightforward path to future service extraction
Technology Stack
Frontend
Next.js
React
TypeScript
Tailwind CSS
Backend
Java 21
Spring Boot
Spring Data JPA
Spring Web
Flyway
Data
PostgreSQL
Redis
Deployment
Vercel — Frontend
Render — Backend
Render PostgreSQL — Database
Data Model

Core entities include:

users
watchlists
watchlist_stocks
stock_master

market_snapshots
market_signals
market_events

attention_events
user_checkpoints
user_preferences

The model separates:

Market state

What is happening in the market.

Derived intelligence

What the system calculates from that market state.

User state

What the user has already seen or reviewed.

This separation allows market observations to evolve independently from user attention state.

API
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
GET  /api/significance/demo-minute
Attention
GET  /api/attention
POST /api/attention/{id}/view
POST /api/attention/{id}/review
POST /api/attention/reset
Production Deployment

The application is deployed as two services.

Vercel
  │
  │ HTTPS
  ▼
Render
Spring Boot API
  │
  ├── PostgreSQL
  │
  └── Redis

The frontend communicates with the backend through a configurable environment variable:

NEXT_PUBLIC_API_URL

The backend uses environment variables for production database configuration.

CORS is configured separately for the production frontend origin.

Local Development
Prerequisites
Node.js
Java 21
Docker
Git
Start infrastructure

From the project root:

docker compose up -d

This starts:

PostgreSQL → localhost:5433
Redis      → localhost:6379
Start backend
cd backend
./mvnw spring-boot:run

Windows:

cd backend
.\mvnw.cmd spring-boot:run

Backend:

http://localhost:8080
Start frontend
cd frontend
npm install
npm run dev

Frontend:

http://localhost:3000
Demo Flow

The recommended demonstration takes approximately three minutes.

00:00 — Start

Open PULSE.

Show:

Your watchlist watches the market.
00:15 — Attention Center

Show:

1 meaningful change

Then highlight HDFC Bank.

00:30 — Why does it matter?

Show:

-3.31%
12.0σ
2.69× volume
-3.81% vs market
80 / 100
SIGNIFICANT

Explain that multiple independent signals agree.

01:00 — Replay

Click:

Replay market

Show the system moving from normal conditions to a significant event.

01:30 — Evidence

Open:

Why this matters →

Walk through the underlying signals.

02:00 — Explanation

Show the structured explanation generated from the evidence.

02:20 — Watchlist

Demonstrate adding/removing a stock.

02:40 — Reset

Click:

Reset demo

Return to a clean state.

03:00 — Closing

"A traditional watchlist tells you what happened. PULSE tells you what changed, whether it matters, why it matters, and what evidence supports that conclusion."

And:

"When nothing matters, PULSE gets out of your way."

Engineering Tradeoffs
Why deterministic scoring instead of letting AI decide?

Because significance needs to be:

Explainable
Consistent
Testable
Reproducible

AI is better suited to explaining structured evidence than determining whether an event is objectively significant.

Why not Kafka?

Kafka would introduce operational complexity that is difficult to justify within a 48-hour hackathon.

The current architecture uses:

Scheduled jobs
Async execution
PostgreSQL
Redis
Modular domain boundaries

Kafka or CDC-based pipelines can be introduced if the system needs to scale to substantially larger event volumes.

Why PostgreSQL?

PostgreSQL provides:

Durable source of truth
Strong relational modeling
Transactional consistency
Flexible JSON/text metadata
Straightforward local and cloud deployment
Why Redis?

Redis is used as an acceleration layer rather than the source of truth.

PostgreSQL remains authoritative.

This makes cache invalidation and recovery simpler.

What Makes PULSE Different?

Most watchlists optimize for:

More information

PULSE optimizes for:

Better attention

A normal watchlist asks:

"What are the prices?"

PULSE asks:

"What changed since you last checked?"

Then:

"Does it matter?"

Then:

"Why?"

And finally:

"What evidence supports that?"

That is the product's central idea.

Future Roadmap

The architecture supports several natural extensions.

Personalized Attention

Allow users to define what matters most to them:

Price volatility
Sector divergence
Corporate events
Volume
Specific securities
Grounded AI Explanations

Introduce an LLM behind the existing explanation interface.

The model would receive only structured evidence produced by the intelligence engine.

Signals
   ↓
Evidence Object
   ↓
LLM
   ↓
Grounded Explanation

The LLM would not determine significance.

Market Context

Add broader context:

Stock
 ↓
Sector
 ↓
Benchmark
 ↓
Market

This allows users to understand whether a move is company-specific or market-wide.

Notifications

Surface only high-confidence significant events through:

Push notifications
Email
Daily digests

The goal would remain:

Fewer, better alerts.

Multiple Watchlists

Support different attention contexts such as:

My Stocks
Long Term
High Risk
Tech
Financials
Production Market Data

Replace the deterministic demo provider with a production market-data provider while keeping the same provider abstraction.

MarketDataProvider
       │
       ├── Demo Provider
       │
       └── Live Provider

This allows the intelligence engine to remain independent of the underlying market-data vendor.

Hackathon Context

PULSE was designed and implemented as a 72-hour Groww CODE 2026 hackathon project.

The primary engineering objective was not to maximize the number of technologies used.

It was to build a complete product loop:

Market Data
    ↓
Intelligence
    ↓
Attention
    ↓
Explanation
    ↓
User Review

while keeping the system:

Demonstrable
Explainable
Testable
Deployable
Extensible
Product Philosophy

Attention is scarce.

Markets produce an enormous amount of information.

The job of a smart watchlist should not be to show users everything.

It should help them identify what deserves their attention.

PULSE is built around that principle.

Detect less noise.
Surface more signal.
Explain the difference.
Built for Groww CODE 2026

PULSE

Your watchlist watches the market. You watch what matters.

Built with:

Next.js · TypeScript · Java · Spring Boot · PostgreSQL · Redis

Live Demo:

https://frontend-kappa-three-uzb7061grc.vercel.app/