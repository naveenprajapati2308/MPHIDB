# Option B — AI / Vision-Driven Autonomous Test Agent
**Status:** Future Project (Not Started) — Proposal Document
**Version:** 1.0
**Relationship to current framework:** Independent project. Does NOT modify or replace the existing Selenium + TestNG MPHIDB framework (`src/`, `pom.xml`, `pom-v2.xml` etc.). This document is purely a design/scoping reference for when work begins.

---

## 1. Background

The existing MPHIDB automation framework (Selenium 4 + TestNG + Page Object Model) is stable and already follows good resilience practices:

- Locators centralized in Page Object classes (`pageObjects/`)
- Config/credentials externalized (`testData/config.properties`)
- Data-driven test cases (`testDataProviders/`)
- Smart waits + JS-based clicks (`utilities/WaitUtils`, `utilities/ClickUtils`)

Even with these practices, the framework still breaks when the **dev team changes the underlying HTML/DOM structure** (renamed `id`/`name` attributes, restructured elements, new modals, etc.), because every locator is a fixed `@FindBy` string that has to be manually updated.

Two solutions were discussed to reduce this pain:

| | Option A — Healenium | Option B — This document |
|---|---|---|
| Approach | Self-healing Selenium locators (ML-based DOM similarity match) | AI/vision agent reads the live screen and decides actions each run |
| Scope of change handled | Minor attribute/structural changes | Minor **and** larger structural/flow changes |
| Effort to adopt | Low — wraps existing WebDriver, Page Objects mostly unchanged | High — new project, new execution model |
| Determinism / debuggability | High (still a fixed locator, just auto-repaired) | Lower (agent "interprets" the page each run) |
| Cost per run | Free | LLM API cost per action |
| Decision | **Accepted — to be applied to the current framework** | **Deferred — separate future project, documented here** |

This document scopes Option B as its own project, and separately answers: *can it still report into the same Automation Portal dashboard as the existing Selenium suite?* — Yes, via an adapter layer described in Section 5.

---

## 2. Problem Statement

Build an automation approach where:

1. Test intent is expressed as **business flow / instructions**, not fixed XPath/CSS locators.
2. At execution time, the agent reads the **current state of the page** (DOM/accessibility tree and/or screenshot) and decides what to click/type/select — the same way a human tester would, regardless of how the underlying HTML changed since the flow was written.
3. The agent can still be pointed at **specific business rules and credentials** (as provided by the QA/business team) to drive real scenarios — not just open-ended exploration.
4. Results plug into the existing **Automation Portal** (same execution history, reports, dashboards) alongside the current Selenium-based runs.

---

## 3. Proposed Architecture (high-level, to be finalized at kickoff)

```
┌─────────────────────────────┐
│   Flow / Business Rule       │   <- YAML/JSON/plain instructions + credentials
│   Definition (per scenario)  │
└───────────────┬──────────────┘
                │
                ▼
┌─────────────────────────────┐
│   AI Agent Runtime            │   <- Reads live page (DOM/accessibility tree +
│  (Playwright/Browser driver   │      screenshot), decides next action, executes it,
│   + LLM reasoning loop)       │      re-observes, repeats until flow step is done
└───────────────┬──────────────┘
                │  results / events / screenshots / logs
                ▼
┌─────────────────────────────┐
│   Option B "Runner Adapter"   │   <- Thin HTTP service exposing the SAME contract
│   (new lightweight service)   │      the Automation Portal already expects
└───────────────┬──────────────┘
                │
                ▼
      Automation Portal (existing, unchanged)
      backend + execution-manager + frontend
```

### Candidate tech stack (open for decision at kickoff)
- Browser driver: Playwright (best support for accessibility-tree extraction + screenshots)
- Reasoning layer: LLM-driven action loop (e.g. Claude with browser/computer tool-use, or an equivalent agent library such as Stagehand/browser-use)
- Runner language: Node.js or Python (whichever the reasoning library favors) — does **not** need to be Java, unlike the current framework

### What stays constant regardless of stack choice
- Flow definitions should stay in a simple, business-readable format (YAML/JSON), separate from the agent runtime, so business/QA can edit flows without touching agent code.
- Credentials/environment values stay externalized (same principle as `config.properties` today) — never hardcoded into a flow file.

---

## 4. Scope for v1.0 (proposed)

**In scope:**
- One pilot flow (to be chosen — e.g. Land Module login + one form step) automated end-to-end via the agent.
- Flow definition format finalized (fields: steps, expected outcomes, credentials reference).
- Pass/fail result produced locally (console/log), not yet wired to the Portal.

**Out of scope for v1.0 (later versions):**
- Portal integration (Section 5) — v1.1+
- Multi-flow parallel execution
- Full regression coverage parity with the existing Selenium suite
- Self-registration of new flows via a UI

*(This scope is a starting proposal — to be revised once the actual kickoff/requirements session happens.)*

---

## 5. Mapping to the Automation Portal (feasibility — confirmed possible)

The Automation Portal's contract with any execution runner is HTTP/JSON based, not Java-specific, which is why a non-Java agent project can still integrate. Three integration points:

1. **Trigger contract** — Execution Manager calls `POST {runnerUrl}/runner/run` with `{executionId, suiteXml, portalUrl, apiKey, openReport}`, and expects `/runner/health`, `/runner/status`, `/runner/cancel`, `/runner/suites`. Option B needs its own small adapter service implementing this same contract (mirrors what `framework-runner/` already does for the Maven/TestNG side, just launching the agent process instead of `mvn test`).
2. **Live event contract** — Whichever engine runs the flow must push lifecycle events to `POST {portalUrl}/api/events/execution` (`SUITE_STARTED`, `TEST_STARTED`, `TEST_PASSED`/`FAILED`/`SKIPPED`, `SCREENSHOT_CAPTURED`, `SUITE_COMPLETED`) in the same JSON shape the existing `PortalApiClient.java` sends. This is the primary source the Portal already uses for live test-case rows.
3. **Artifacts** — Screenshots/logs can reuse the existing generic folder-copy mechanism. The TestNG-XML-specific result enrichment (`testng-results.xml` parsing for params/tags/steps) is Java/TestNG-specific and won't apply to Option B; for this module type the Portal would rely on the live event stream only (slightly less structured detail, but functionally complete).

**Backend/Execution-Manager changes needed (moderate effort, additive only):**
- Route jobs to the correct runner based on module/technology (Execution Manager's `RunnerRegistry` already models multiple runners; today only one default is synced — needs a routing rule).
- Optionally tag `ModuleEntity` with a runner/technology type so the Portal UI can distinguish "Selenium suite" vs "AI Agent flow" modules.

**Conclusion:** Feasible without touching the existing Selenium pipeline. Effort is concentrated in building the new adapter + event client once Option B's core agent exists.

---

## 6. Risks / Tradeoffs to revisit before committing

- **Cost**: LLM calls per action → cost scales with number of steps × number of runs. Needs a cost estimate once a pilot flow is timed.
- **Speed**: Likely slower per test than direct Selenium calls (reasoning overhead per step).
- **Non-determinism**: Agent may occasionally misinterpret a page; needs retry/verification logic and clear failure reporting (not silent misclicks).
- **Debuggability**: Failures need enough logged context (what the agent "saw" and "decided") to be triageable — plain stack traces won't be enough.
- **Credential/data handling**: Since the agent reads the live screen, sensitive data (passwords, OTPs) must still flow through the same externalized-config discipline as today — never embedded in flow text sent to any external LLM API without review of data-handling/compliance implications.

---

## 7. Open Decisions (to resolve at project kickoff)

1. Final tech stack (agent framework + LLM provider).
2. Pilot flow selection.
3. Flow-definition file format (YAML vs JSON vs other).
4. Where the agent runtime executes (local machine / same Docker network as the Portal / separate host).
5. Cost budget and acceptable latency per test.
6. How much Portal integration (Section 5) is done in this same v1.0 milestone vs. deferred.

---

## Version History

| Version | Date | Notes |
|---|---|---|
| 1.0 | 2026-07-09 | Initial proposal document. No implementation started. Captures problem statement, architecture options, Portal integration feasibility, and open decisions for future kickoff. |
